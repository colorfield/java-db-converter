package dbconverter.model.util.dao;

import dbconverter.common.constant.ViewConstant;
import dbconverter.model.util.IConverter;
import dbconverter.model.vo.DBMetaBaseVO;
import dbconverter.model.vo.DBMetaTableVO;
import dbconverter.model.vo.DBParamsVO;
import dbconverter.model.vo.ITableVO;
import java.sql.SQLException;
import java.util.Iterator;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reçoit les paramètres de la DB cible et
 *
 * 1. Crée les tables
 * 2. Crée les vues
 * 3. Insère les data /// ??? => cf. DBDump.getDBData
 * 
 * @author christophe
 */
public class DBReadDump {

    // nombre d'insert traités en une fois
    private static final int MAX_QUERIES = 100;

    private IConverter dbConverter;

    // value objects
    private DBParamsVO dbParams;

    // sql objects
    private SQLUtility sqlUtil;
    private Statement st;
    private Connection con;
    // insert en attente
    private Collection<String> waitingQueries;
    

    public DBReadDump(DBParamsVO dbParams, IConverter dbConverter){
        
        this.dbConverter = dbConverter;
        this.dbParams = dbParams;
        sqlUtil = new SQLUtility(dbParams);

        waitingQueries = new LinkedList<String>();

        try {
            sqlUtil.openConnection();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBReadDump.class.getName()).log(Level.SEVERE, null, ex);
        }

        con = (Connection) sqlUtil.getConnection();
    }


    //--------------------------------------------------------------------------
    // META DONNEES
    //--------------------------------------------------------------------------

    public void createMetaData(DBMetaBaseVO dbMeta) {

        System.out.println(ViewConstant.DOUBLE_LINE + 
                           "3) CREATION DES META DONNEES" +
                           ViewConstant.DOUBLE_LINE);

        
        // sélection des données table par table
        Iterator itTable = dbMeta.getTables().iterator();
        Iterator itView = dbMeta.getViews().iterator();

        while(itTable.hasNext()){
            createTable((ITableVO) itTable.next());
        }
        
        // une fois qu'on a crée toutes les tables, on peut créer les clés étrangères
        // (les clés primaires sont crées directement après la table avec un alter)
        Iterator itFK = dbMeta.getTables().iterator();
        while(itFK.hasNext()){
            DBMetaTableVO curTable = (DBMetaTableVO) itFK.next();
            if(!curTable.getForeignKeys().isEmpty())
                createForeignKeys(curTable);
        }


        while(itView.hasNext()){
            createView((ITableVO) itView.next());
        }

        // pas ici, on en a encore besoin pour l'insert des data...
        // sqlUtil.closeConnection();
    }

    /**
     * Création d'une table à partir de la String du valueObject Table convertie
     * @param tableOrView
     */
    private void createTable(ITableVO table){
      
        // => la chaîne est construite ailleurs, 
        // en fonction du dialecte SQL cible.
        // Cette chaîne est donc construite via l'instance particulière
        // du convertisseur (ex. ConverterMySQL) dans le valueObject table
        // puis exécutée par cette méthode !!

        System.out.println("Création de la table : " + table);
        System.out.println("===> structure = " + table.getTableStructure());
        try {
            st = (Statement) con.createStatement();
            st.executeUpdate(table.getTableStructure().toString());
        } catch (SQLException ex) {
            SQLUtility.showSQLException(ex);
            Logger.getLogger(DBReadDump.class.getName()).log(Level.SEVERE, null, ex);
        }
 

    }

    private void createForeignKeys(DBMetaTableVO tableVO){

        // System.out.println("------ CREATION DES CLES ETRANGERES (table "+ tableVO.getTableName() +"-----");
        System.out.println(tableVO.getForeignKeyStructure());
    }


    /**
     * Création des views
     * @param view
     */
    private void createView(ITableVO view){
        // TODO 
        System.out.println("Création de la view : " + view);
        System.out.println("===> structure = " + view.getTableStructure());
    }

    //--------------------------------------------------------------------------
    // DONNEES
    //--------------------------------------------------------------------------


    /**
     * Accumulation des queries avant d'exécuter
     * 
     * @param qry
     */
    public void addDataToInsert(String qry){

        System.out.println("Accumulation => " + qry);

        if(waitingQueries.size() < MAX_QUERIES)
        this.waitingQueries.add(qry);

        
    }


    /**
     * TODO : gérer la réouverture de la connexion si nécessaire (+ de 100)
     *
     * Insertion des data : appellé dans deux cas :
     * <ol>
     *  <li>Lorsque MAX_QUERIES est atteint</li>
     *  <li>Lorsque l'extracteur de données a fini son travail</li>
     * </ol>
     */
    public void insertData() throws ClassNotFoundException{
        try {
            // sqlUtil.openConnection();
            // con = (Connection) sqlUtil.getConnection();
           
            // a cause de Derby, il a fallu placer le statement 
            // dans la boucle au lieu de pouvoir le créer et le fermer en dehors

            System.out.println("\n---- DEBUT D'EXECUTION (insert) ----");

            Iterator it = waitingQueries.iterator();

            while (it.hasNext()){
                st = (Statement) con.createStatement();
                String qry = (String) it.next();
                // System.out.println("Exécution => " + qry.replaceAll(";", ""));
                System.out.println("Exécution => " + qry);
                st.executeUpdate(qry);
                st.close();
            }

            /*
            String[] queries = (String[]) waitingQueries.toArray();
            st = (Statement) con.createStatement();
            st.executeUpdate("INSERT",queries);
            st.close();
            */
            
            sqlUtil.closeConnection();
        } catch (SQLException ex) {
            SQLUtility.showSQLException(ex);
            Logger.getLogger(DBReadDump.class.getName()).log(Level.SEVERE, null, ex);
            
        }
    }

}
