package dbconverter.model.util;

import dbconverter.common.constant.DBTypes;
import dbconverter.model.vo.DBMetaFieldVO;
import dbconverter.model.vo.DBMetaTableVO;
import dbconverter.model.vo.ITableVO;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author daneelolivaw
 */
public class ConverterFirebird extends AbstractConverter
                               implements IConverter {

    public ConverterFirebird(int dbSourceType){
        super(dbSourceType);
    }

    //--------------------------------------------------------------------------
    // META DONNEES
    //--------------------------------------------------------------------------

    @Override
    protected DBMetaFieldVO manageFieldSpecificities(DBMetaFieldVO fieldToConvertVO,
                                                     DBMetaFieldVO convertedFieldVO){

        // gérer ici les clés étrangères, primaires, ...
        /*
        System.out.println("Gestion des spécificités propres à MySQL, FIELD "
                            + fieldToConvertVO);
        */

        convertedFieldVO.setFieldSize(fieldToConvertVO.getFieldSize());

        // pour l'instant, on ne dispose que du type commun => on convertit vers le type target
        convertedFieldVO.setFieldDataType(DBTypes.convertFromCommonType(convertedFieldVO.getFieldDataType(),
                                          DBTypes.Firebird));

        convertedFieldVO.setIsNullable(fieldToConvertVO.isIsNullable());

        System.out.println("-----> convertit = " + convertedFieldVO);

        String isNullable;

        // null / not null Firebird
        if(convertedFieldVO.isIsNullable() == 0){
            isNullable = "NOT NULL";
        }else{
            isNullable = "";
        }

        // Bug interprétation type TEXT JDBC ?
        if(convertedFieldVO.getFieldDataType().equals("VARCHAR")
                && convertedFieldVO.getFieldSize() > 255){
           //convertedFieldVO.setFieldDataType(DBTypes.dataTypes[][]);
           convertedFieldVO.setFieldDataType("BLOB SUB_TYPE TEXT");
        }

        // construction d'une String de création d'un field propre à MySQL
        convertedFieldVO.setFieldStructure(new StringBuilder());
        convertedFieldVO.getFieldStructure().append(convertedFieldVO.getFieldName());
        convertedFieldVO.getFieldStructure().append(" " + convertedFieldVO.getFieldDataType());

        // si date ou text pas besoin de size
        if(!convertedFieldVO.getFieldDataType().equals("DATE")
                && !convertedFieldVO.getFieldDataType().equals("BLOB SUB_TYPE TEXT")){
            convertedFieldVO.getFieldStructure().append("(" + convertedFieldVO.getFieldSize() + ")");
        }
        convertedFieldVO.getFieldStructure().append(" " + isNullable);

        return convertedFieldVO;
    }


    /**
     * S'il y a des spécificités propres aux tables, les gérer ici
     * @param convertTable
     * @return
     */
    @Override
    protected ITableVO manageTableSpecificities(ITableVO tableToConvertVO) {

        /*
        System.out.println("Gestion des spécificités propres à MySQL, TABLE "
                            + convertTable);
        */

        return tableToConvertVO;
    }

    @Override
    protected void writeOpenTableStructure(ITableVO tableVO) {

        String openTable = "CREATE TABLE "+tableVO.getTableName()+" (\n";

        tableVO.getTableStructure().append(openTable);
    }

    @Override
    protected void writeCloseTableStructure(ITableVO tableVO) {

        // !! le charset pourrait être différent (utf-8, ...)
        // le Engine également (ANSI, ...)
        // => il faudrait trouver le moyen de les détecter
        String closeTable = "\n);\nCOMMIT WORK;";

        tableVO.getTableStructure().append(closeTable);
    }

    @Override
    protected void writeOpenViewStructure(ITableVO tableVO) {

        /*
         * création d'un générateur de Views ???
         * => on recrée simplement une table ici, qu'on va peupler avec les data
         * => redondance, mais algo complexe à implémenter sinon
         * => si le temps
        String openView = "CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW \n";
        openView += "`"+tableVO.getTableName()+"` AS ..............\n";
        */
        String openView = "CREATE VIEW "+tableVO.getTableName()+" (\n";
        tableVO.getTableStructure().append(openView);

    }

    @Override
    protected void writeCloseViewStructure(ITableVO tableVO) {

        String closeTable = "\n);\nCOMMIT WORK;";

        tableVO.getTableStructure().append(closeTable);
    }

    @Override
    /**
     * Ecriture du alter en fonction du nombre de colonnes sur lesquelles
     * la clé primaire est définie
     */
    protected void definePrimaryKey(ITableVO tableVO,List<DBMetaFieldVO> PKList) {


        StringBuilder primaryKey = new StringBuilder();
        // DBMetaTableVO curTable = (DBMetaTableVO) tableVO;
        // String notProtected = "";

        //---- 1) clé primaire sur une colonne
        // ALTER TABLE `department` ADD PRIMARY KEY(`id`)
        if(PKList.size() == 1){

            DBMetaFieldVO PKField = (DBMetaFieldVO) PKList.get(0);

            primaryKey.append("/* ALTER TABLE ");
            primaryKey.append(tableVO.getTableName());
            primaryKey.append(" ADD PRIMARY KEY(`");
            primaryKey.append(PKField.getFieldName());
            primaryKey.append("`); */");

            // notProtected = primaryKey.toString().replaceAll("`", "");


        //-----2) clé primaire sur plus d'une colonne
        // TODO : même cas que les FK !
        }else{
            Iterator itPK = PKList.iterator();
            while(itPK.hasNext()){
                // mettre un délimiteur, tester si c'est le dernier, ...
                itPK.next();
            }
        }


        // primaryKey.append("/* ALTER TABLE PK " + tableVO.getTableName() + "; */");

        // si tableStructure, se fait directement après la création de la table
        // => ne fonctionne pas !, même problème de ";" que Firebird ?
        tableVO.getTableStructure().append(primaryKey);

        // => on le fait dans l'emplacement destiné au FK ?

    }

    @Override
    protected void defineForeignKeys(ITableVO tableVO,List<DBMetaFieldVO> FKList) {
        // System.out.println("ALTER CLES ETRANGERES");

        // StringBuilder créé ici et non dans la classe du valueObject,
        // vu que pas toujours nécessaire
        DBMetaTableVO curTable = (DBMetaTableVO) tableVO;

        StringBuilder foreignKeys = new StringBuilder();
        foreignKeys.append("/* ALTER TABLE FK " + tableVO.getTableName() + "; */");

        curTable.setForeignKeyStructure(new StringBuilder());
        curTable.getForeignKeyStructure().append(foreignKeys);
    }


}
