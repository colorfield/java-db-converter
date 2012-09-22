package dbconverter.model.util.dao;

import dbconverter.common.constant.DBTypes;
import dbconverter.common.constant.ViewConstant;
import dbconverter.model.util.IConverter;
import dbconverter.model.vo.DBMetaFieldVO;
import dbconverter.model.vo.DBMetaBaseVO;
import dbconverter.model.vo.DBParamsVO;
import dbconverter.model.vo.ITableVO;
import dbconverter.model.vo.TableFactory;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reçoit les paramètres de la DB source et
 * 1. Extrait la métabase (tables et vues pour faire simple)
 * 2. Extrait les données
 * @author christophe
 */
public class DBDump {

    private IConverter dbConverter;
    private DBReadDump dbInsert;
    
    // value objects
    private DBParamsVO dbParams;
    private DBMetaBaseVO dbMeta;
    
    // sql objects
    private SQLUtility sqlUtil;
    private Statement st;
    private Connection con;
    private DatabaseMetaData dbMetaData;

    public DBDump(DBParamsVO dbParams, IConverter dbConverter, DBReadDump dbInsert){

        this.dbConverter = dbConverter;
        this.dbInsert = dbInsert;
        this.dbParams = dbParams;
        sqlUtil = new SQLUtility(dbParams);

        // ----- La classe devient un outil appelé de l'extérieur
        // (par le convertisseur) vu qu'on ne réalise pas vraiment
        // une séquence extraire - convertir - insérer
        // voir commentaire "Méthodologie" dans DBConvert

        try {
            sqlUtil.openConnection();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBDump.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // dump des métadonnées
        /*
        dbMeta = new DBMetaVO();
        getDBMetaData();
        */
        
        // dump des données
        // on ne peut pas passer par un valueObject
        // imaginons le cas de grandes DB => pas de capacité en RAM !
        // le traitement doit donc se faire au fur et à mesure
        // ou écriture d'un fichier SQL
        // getDBData();
        
    }

    /**
     * Ouverture de la connexion
     */
    public void openConnection(){
        try {
            sqlUtil.openConnection();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBDump.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Fermeture de la connexion
     */
    public void closeConnection(){
        sqlUtil.closeConnection();
    }


    /**
     * Dump des méta-données
     */
    public DBMetaBaseVO getDBMetaData(){

        System.out.println(ViewConstant.DOUBLE_LINE + "1) EXTRACTION DES META DONNEES" + ViewConstant.DOUBLE_LINE);

        dbMeta = new DBMetaBaseVO();
        getTablesAndViews();
        //... et plus si affinité

        // à ce stade, on dispose des metadata modélisées en RAM
        // on peut donc convertir puis créer
        // le schéma correspondant dans la DB cible
        // (cf. DBConvert)

        return dbMeta;
    }

    /**
     * Dump des données
     */
    public void convertData() throws ClassNotFoundException{


        System.out.println(ViewConstant.DOUBLE_LINE + "4) EXTRACTION/CONVERSION/INSERTION DES DONNEES" + ViewConstant.DOUBLE_LINE);

        // sélection des données table par table
        Iterator itTable = dbMeta.getTables().iterator();
        Iterator itView = dbMeta.getViews().iterator();

        while(itTable.hasNext()){
            selectConvertInsertData((ITableVO) itTable.next());
        }

        /*
        while(itView.hasNext()){
            // TODO : vues
        }
        */
        
        // on a finit d'extraire la métabase et les données
        // fermeture de la connexion à la DB Source
        sqlUtil.closeConnection(); // ???
        try {
            con.close();
        } catch (SQLException ex) {
            SQLUtility.showSQLException(ex);
            Logger.getLogger(DBDump.class.getName()).log(Level.SEVERE, null, ex);
        }

        dbInsert.insertData();

    }


    //--------------------------------------------------------------------------
    // META DONNEES : méthodes private
    //--------------------------------------------------------------------------


    /**
     * Récupération des tables et des vues
     */
    private void getTablesAndViews(){

        try {
            // création de la connexion et du statement
            con = sqlUtil.getConnection();
            st = con.createStatement();

            // 1) Récupération des métadonnées
            dbMetaData = con.getMetaData();
            String tablesTypes [] = new String[] {"TABLE","VIEW"};

            ResultSet rs = dbMetaData.getTables(null, null, "%", tablesTypes);

            // boucle sur les tables et vues
            while (rs.next()) {

              String tableName = rs.getString("TABLE_NAME");
              String tableType = rs.getString("TABLE_TYPE");

              // création du valueObject Table ou View => factory
              ITableVO tableVO = TableFactory.create(tableType, tableName);

              dbMeta.addTableOrView(tableVO);

              // DEBUG
              // String str = "=========================================\n";
              // str += "[Name = " + tableName;
              // str += " - Type = " + tableType +  "]";
              // printLine();
              // System.out.println(str);
              // 2) Récupération de la structure pour une table
              getTableStructure(tableName, tableVO);
              
              // System.out.println("=========================================\n");

            }
            // fermeture du statement et de la connexion
            st.close();
            // con.close();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    

    /**
     * Récupération de la structure d'une table ou d'une vue
     * @param tableName
     */
    private void getTableStructure(String tableName, ITableVO tableVO){
        getTableFields(tableName, tableVO);
        getTableKeys(tableName, tableVO);
    }

    /**
     * Récupération des champs
     * @param tableName
     */
    private void getTableFields(String tableName, ITableVO tableVO){
        
        System.out.println("TABLE NAME = " + tableName);

        try {
            String sql = "select * from " + tableName;
            ResultSet rs = st.executeQuery(sql);
            ResultSetMetaData metaData = rs.getMetaData();
            int rowCount = metaData.getColumnCount();

            // DEBUG
            // System.out.println("Table Name : " + metaData.getTableName(2));
            // System.out.println("Field\tDataType\tSize\tNullable");
            
            DBMetaFieldVO field;

            for (int i = 0; i < rowCount; i++) {
                // String fieldName, int fieldSize, boolean isNullable, String fieldDataType
                field = new DBMetaFieldVO(metaData.getColumnName(i + 1).toString());
                field.setFieldDataType(metaData.getColumnTypeName(i + 1));
                field.setIsNullable(metaData.isNullable(i + 1));
                field.setFieldSize(metaData.getColumnDisplaySize(i + 1));

                tableVO.addField(field);
                System.out.println("--- " + field);

                // nom
                // System.out.print(metaData.getColumnName(i + 1) + "\t");
                // type
                // System.out.println(metaData.getColumnTypeName(i + 1) + "\t");
                // taille
                // System.out.print(metaData.getColumnDisplaySize(i + 1) + "\t");
                // nullable
                // System.out.println(metaData.isNullable(i + 1));
            }
            
        } catch (Exception ex) {
            Logger.getLogger(DBDump.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Récupération des clés primaires et étrangères
     * @param tableName
     */
    private void getTableKeys(String tableName, ITableVO tableVO){
        try {

            // clés primaires
            ResultSet pk = dbMetaData.getPrimaryKeys("", "", tableName);

            String pkStr = "";
            while(pk.next()) {
                pkStr += pk.getString("COLUMN_NAME");
            }

            tableVO.addPrimaryKey(pkStr);
            // tableVO.setPrimaryKey(pkStr);

            // DEBUG
            // System.out.println("Clé primaire : " + pkStr);
            // printLine();

            // Clés étrangères
            ResultSet fk = dbMetaData.getImportedKeys("", "", tableName);
            // String fkStr = "";

            while(fk.next()) {

                tableVO.addForeignKey(fk.getString("COLUMN_NAME"),
                                      fk.getString("PKTABLE_NAME"),
                                      fk.getString("PKCOLUMN_NAME"));

                // fkStr += fk.getString("COLUMN_NAME");
                // fkStr += " REFERENCES " + fk.getString("PKTABLE_NAME");
                // fkStr += " (" + fk.getString("PKCOLUMN_NAME") + ")";
            }
            // System.out.println("Clé(s) étrangère(s) : " + fkStr);

            System.out.println(tableVO);


        } catch (Exception ex) {
            // SQLUtility.showSQLException(e);
            Logger.getLogger(DBDump.class.getName()).log(Level.SEVERE, null, ex);
        }
        

    }

    //--------------------------------------------------------------------------
    // DONNEES : méthodes private
    //--------------------------------------------------------------------------


   
    /**
     *
     * @param table
     */
    private void selectConvertInsertData(ITableVO table){
        System.out.println("\n---- SELECT TABLE "+ table.getTableName() +" ----");
        try {
            // création de la connexion et du statement
            st = (Statement) con.createStatement();

            String sql = "SELECT * FROM " + table.getTableName();
            // se manipule comme un itérateur
            ResultSet resultSet = st.executeQuery(sql);
            // effet de bord : avance sur le prochain tuple
            // ---- TODO : n tuples puis convert et insert
            // ici, on fait tuple par tuple, ce qui est suffisant
            while (resultSet.next()) {

                Iterator it = table.getFields().iterator();
                StringBuilder qry = new StringBuilder();
                Boolean isNumeric = false;

                qry.append("INSERT INTO ");
                qry.append(table.getTableName());
                qry.append(" VALUES(");

                // System.out.println("DBType = " + dbParams.getDb_typeName() + " " + dbParams.getDb_typeId());

                while(it.hasNext()){
                    // récupère la valeur de chaque champ
                    DBMetaFieldVO curField = (DBMetaFieldVO) it.next();
                    
                    // teste si le champ est "numérique" : date, ...
                    isNumeric = DBTypes.isNumericType(curField.getFieldDataType(), dbParams.getDb_typeId());
                    //System.out.println("IS NUMERIC = " + isNumeric);
                    //System.out.print("fieldName = " + curField.getFieldName() + " - ");
                    String curFieldValue = resultSet.getString(curField.getFieldName());
                    //System.out.println("fieldValue = " + curFieldValue);

                    // construit la chaîne d'insertion pour chaque valeur
                    if(!isNumeric){
                        qry.append("'");
                        qry.append(curFieldValue);
                        qry.append("'");
                    }else{
                        qry.append(curFieldValue);
                    }
                    // rajoute un délimiteur si n'est pas la dernière value
                    if(it.hasNext())
                        qry.append(",");
                }

                qry.append(");");
                // System.out.println(qry);

                // --------------------- EMBRANCHEMENT ------------------------
                // s'il fallait définir des requêtes d'insertion particulières
                // pour chaque SGBD, il suffirait alors de construire
                // la chaîne qry construite ici
                // this.dbConverter.convertData(curField, curValue);

                // passe la qry à l'objet DBReadDump qui fait ensuite
                // ce que bon lui semble (accumuler n requêtes avant d'exécuter...)
                this.dbInsert.addDataToInsert(qry.toString());

                // une fois que tout a été traité, appel de
                // dbInsert.insertData() à la fin de convertData()

            }
            st.close();
            // con.close(); // ---> se fait ailleurs vu qu'on doit traiter
                            // toutes les tables => après la boucle de convertData
            
        } catch (SQLException ex) {
            SQLUtility.showSQLException(ex);
            Logger.getLogger(DBDump.class.getName()).log(Level.SEVERE, null, ex);
        }

        

    }
    

    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------




}
