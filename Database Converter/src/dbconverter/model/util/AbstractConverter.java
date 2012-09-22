package dbconverter.model.util;

import dbconverter.common.constant.DBTypes;
import dbconverter.common.constant.ViewConstant;
import dbconverter.common.utils.Texturize;
import dbconverter.model.vo.DBMetaBaseVO;
import dbconverter.model.vo.DBMetaFieldVO;
import dbconverter.model.vo.ITableVO;
import dbconverter.model.vo.TableFactory;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author daneelolivaw
 */
public abstract class AbstractConverter {

    private int dbSourceType;

    /**
     * Afin de ne pas devoir faire de recherches inutiles dans DBTypes.dataTypes,
     * à la création, on connait le type de db source
     * => on sait immédiatement à quel indice de colonne aller chercher
     */
    public AbstractConverter(int dbSourceType){
        this.dbSourceType = dbSourceType;
        System.out.println("DBSOURCE = " + dbSourceType);
    }
    
    //--------------------------------------------------------------------------
    // META DONNEES
    //--------------------------------------------------------------------------

    /**
     * Conversion de la métabase
     *
     * @param dbMeta source
     * @return dbMeta convertit
     */
    public DBMetaBaseVO convertMetaData(DBMetaBaseVO dbMeta) {

        System.out.println(ViewConstant.DOUBLE_LINE + "2) CONVERSION DES META DONNEES" + ViewConstant.DOUBLE_LINE);

        DBMetaBaseVO resultMetaBase = new DBMetaBaseVO();

        // conversion, principalement des types...
        Iterator itTable = dbMeta.getTables().iterator();
        Iterator itView = dbMeta.getViews().iterator();

        // tables : convertit et ajoute à la nouvelle méta-base
        while(itTable.hasNext()){

            resultMetaBase.addTableOrView(convertTable((ITableVO) itTable.next(),
                                          DBTypes.TABLE_TABLE));
        }

        // vues : convertit et ajoute à la nouvelle méta-base
        while(itView.hasNext()){
            
            resultMetaBase.addTableOrView(convertTable((ITableVO) itView.next(),
                                          DBTypes.TABLE_VIEW));
        }


        //------- La gestion des PK / FK se fait dans convertTable !

        // une fois qu'on a créé toutes les tables, on peut faire les alter
        // pour les foreign Keys, on fait les primary Key par la même occasion...

        // attention, ici on ne fait qu'écrire dans le script, l'exécution se fait
        // ailleurs => l'exécution doit également être réalisée ensuite

        // chaque implémentation fait un append
        // 1) dans la StringBuilder tableStructure
        // pour la primary key (si nécessaire), vu que celle-ci peut être déclarée
        // juste après la création de la table
        // 2) dans la StringBuilder foreignKeysStructure, qui elle sera exécutée
        // après la création des tables

        /*
        Iterator itKeys = resultMetaBase.getTables().iterator();

        while(itKeys.hasNext()){
            ITableVO tableVO = (ITableVO) itKeys.next();
            // getPrimaryKeys au pluriel car possibilité de PK sur deux colonnes
            if(! tableVO.getPrimaryKeys().isEmpty())
                definePrimaryKey(tableVO);
            if(! tableVO.getForeignKeys().isEmpty())
                defineForeignKeys(tableVO);
        }
        */

        return resultMetaBase;
    }


    //----------------------- TABLES


    /**
     * Conversion des tables
     * @param tableToConvertVO
     * @return
     */
    private ITableVO convertTable(ITableVO tableToConvertVO, int tableType){

        System.out.println("AbstractConverter TABLE = " + tableToConvertVO);

        // protection du nom des tables
        String cleanTableName = Texturize.protectString(Texturize.cleanString(tableToConvertVO.getTableName()));
        ITableVO tableVO = TableFactory.create(tableToConvertVO, cleanTableName);

        // si au passage il faut faire d'autres opérations que le Texturize...
        // pour peu qu'elles soient généralisables pour toutes les DB, les faire ici

        // ====> primary key au niveau table p.ex. ???
        // !! primary key, ... aussi géré au niveau field
        // => lors de la recréation de la méta-base dans le SGBD source on
        // partira plutôt sur cette base (surtout pour les foreign key dont on doit
        // récupérer la table et la colonne correspondante).


        // création de l'objet StringBuilder pour la construction de
        // la "tableStructure"
        tableVO.setTableStructure(new StringBuilder());

       
        // écriture de l'entête du dialecte SQL de création de la table/vue
        switch(tableType){
            case DBTypes.TABLE_TABLE:
                writeOpenTableStructure(tableVO);
                break;
            case DBTypes.TABLE_VIEW:
                writeOpenViewStructure(tableVO);
                break;
        }


        // ensuite on appelle la méthode abstraite, qui elle va gérer
        // les spécificités propres au SGBDR pour les champs
        Iterator it = tableToConvertVO.getFields().iterator();
        while(it.hasNext()){
            // on convertit et on ajoute à la nouvelle table
            DBMetaFieldVO convertedFieldVO = convertField((DBMetaFieldVO) it.next());

            // tableVO.addField(convertField((DBMetaFieldVO) it.next()));
            // on écrit la structure de création d'un field propre au SGBD cible
            tableVO.getTableStructure().append(convertedFieldVO.getFieldStructure());

            // s'il y a encore un field, on place un délimiteur
            if(it.hasNext()){tableVO.getTableStructure().append(", \n");}
        }

        // écriture du "footer" du dialecte SQL de création de la table/vue
        switch(tableType){
            case DBTypes.TABLE_TABLE:
                writeCloseTableStructure(tableVO);

                // la vérification du fait que le nom du field
                // a ou n'a pas été "nettoyé", "protégé", ...
                // se fait au niveau de chaque implémentation

                //---------- Primary Keys
                // getPrimaryKeys au pluriel car possibilité de PK sur deux colonnes
                if(! tableToConvertVO.getPrimaryKeys().isEmpty()){

                    Iterator itPK = tableToConvertVO.getPrimaryKeys().iterator();
                    List<DBMetaFieldVO> PKList = new LinkedList<DBMetaFieldVO>();
                    while(itPK.hasNext()){
                        DBMetaFieldVO curField = (DBMetaFieldVO) itPK.next();
                        PKList.add(curField);
                    }
                    definePrimaryKey(tableVO,PKList);
                }

                //---------- Foreign Keys
                if(! tableToConvertVO.getForeignKeys().isEmpty()){
                    Iterator itFK = tableToConvertVO.getForeignKeys().iterator();
                    List<DBMetaFieldVO> FKList = new LinkedList<DBMetaFieldVO>();
                    while(itFK.hasNext()){
                        DBMetaFieldVO curField = (DBMetaFieldVO) itFK.next();
                        FKList.add(curField);
                    }
                    defineForeignKeys(tableVO,FKList);
                }

                break;
            case DBTypes.TABLE_VIEW:
                writeCloseViewStructure(tableVO);
                break;
        }


        // s'il faut gérer d'autres particularités
        // on appellera une méthode abstraite complémentaire
        // (p.ex. Derby qui ne supporte pas les champs protégés, ...
        // ===> dans ce cas on les supprime...
        tableVO = manageTableSpecificities(tableVO);


        // puis on retourne la table entièrement convertie à la nouvelle méta-base
        return tableVO;
    }


    /**
     * Gestion des spécificités propres aux SGBD pour les tables (s'il y en a)
     * @param tableToConvertVO
     * @return
     */
    protected abstract ITableVO manageTableSpecificities(ITableVO tableToConvertVO);


    protected abstract void writeOpenTableStructure(ITableVO tableToConvertVO);
    protected abstract void writeCloseTableStructure(ITableVO tableToConvertVO);
    protected abstract void writeOpenViewStructure(ITableVO tableToConvertVO);
    protected abstract void writeCloseViewStructure(ITableVO tableToConvertVO);

    protected abstract void definePrimaryKey(ITableVO tableToConvertVO,List<DBMetaFieldVO> PKList);
    protected abstract void defineForeignKeys(ITableVO tableToConvertVO,List<DBMetaFieldVO> FKList);

    
    //----------------------- FIELDS

    /**
     * Protection du nom des champs et autres opérations communes si nécessaire
     * @param fieldToConvertVO
     * @return field convertit et protégé
     */
    private DBMetaFieldVO convertField(DBMetaFieldVO fieldToConvertVO){

        System.out.println("\n-----> original = " + fieldToConvertVO);

        // 1) on protège le nom
        String cleanFieldName = Texturize.protectString(
                                Texturize.cleanString(fieldToConvertVO.getFieldName()));

        DBMetaFieldVO convertedFieldVO = new DBMetaFieldVO(cleanFieldName);

        // 2) faire correspondre le type source à un type commun (JDBC)
        convertedFieldVO.setFieldDataType(
                         DBTypes.convertToCommonType(fieldToConvertVO.getFieldDataType()
                                                    ,dbSourceType));

        // 3) demande de la correspondance propre à chaque SGBD
        // on délègue alors enfin à la classe abstraite dont l'implémentation
        // sera choisie en fonction du type de DB Source
        convertedFieldVO = manageFieldSpecificities(fieldToConvertVO,convertedFieldVO);


        return convertedFieldVO;
    }

    /**
     * Gestion des spécificités propres aux SGBD pour les champs
     * @param fieldToConvertVO
     * @return field convertit
     */
    protected abstract DBMetaFieldVO manageFieldSpecificities(DBMetaFieldVO fieldToConvertVO,DBMetaFieldVO convertedField);

    //--------------------------------------------------------------------------
    // DONNEES
    //--------------------------------------------------------------------------

    /**
     * Conversion des données => pour l'instant, on construit la chaîne INSERT
     * dans DBDump (voir commentaire "EMBRANCHEMENT").
     *
     * On pourrait très bien choisir de construire ici ou dans une méthode
     * surchargée par les classes d'implémentation de cette classe abstraite.
     * ... selon les nécessités
     * 
     */
    public void convertData(DBMetaFieldVO fieldVO) {
        System.out.println("Conversion des données..........");
    }


    /* --- Pas la responsabilité de cet objet : uniquement un convertisseur
     * => doit être effectué par DBReadDump
    public void insertData(String qry){

    }
    */

    
}
