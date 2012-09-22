package dbconverter.model.util;

import dbconverter.common.constant.DBTypes;
import dbconverter.model.vo.DBMetaBaseVO;
import dbconverter.model.vo.DBMetaFieldVO;
import dbconverter.model.vo.ITableVO;
import java.util.List;

/**
 *
 * @author daneelolivaw
 */
public class ConverterDerby extends AbstractConverter implements IConverter {

    public ConverterDerby(int dbSourceType){
        super(dbSourceType);
    }

    //--------------------------------------------------------------------------
    // META DONNEES
    //--------------------------------------------------------------------------

    @Override
    protected DBMetaFieldVO manageFieldSpecificities(DBMetaFieldVO fieldToConvertVO,
                                                     DBMetaFieldVO convertedFieldVO){

        convertedFieldVO.setFieldSize(fieldToConvertVO.getFieldSize());

        // pour l'instant, on ne dispose que du type commun => on convertit vers le type target
        convertedFieldVO.setFieldDataType(DBTypes.convertFromCommonType(convertedFieldVO.getFieldDataType(),
                                          DBTypes.Derby));

        convertedFieldVO.setIsNullable(fieldToConvertVO.isIsNullable());

        System.out.println("-----> convertit = " + convertedFieldVO);

        String isNullable;

        // null / not null Derby
        if(convertedFieldVO.isIsNullable() == 0){
            isNullable = "NOT NULL";
        }else{
            // si un champ est nullable, contrairement à MySQL
            // on ne peut pas le dire explicitement...

            // isNullable = "NULL";
            isNullable = "";
        }

        // Bug interprétation type "TEXT" JDBC => en Derby LONG VARCHAR
        if(convertedFieldVO.getFieldDataType().equals("VARCHAR")
                && convertedFieldVO.getFieldSize() > 255){
            convertedFieldVO.setFieldDataType("LONG VARCHAR");
        }

        // construction d'une String de création d'un field propre à MySQL
        convertedFieldVO.setFieldStructure(new StringBuilder());
        convertedFieldVO.getFieldStructure().append(convertedFieldVO.getFieldName());
        convertedFieldVO.getFieldStructure().append(" " + convertedFieldVO.getFieldDataType());

        // si date ou text pas besoin de size
        // Derby ne veut pas de size pour INT et INTEGER également
        if(!convertedFieldVO.getFieldDataType().equals("DATE")
                && !convertedFieldVO.getFieldDataType().equals("TEXT")
                && !convertedFieldVO.getFieldDataType().equals("INT")
                && !convertedFieldVO.getFieldDataType().equals("INTEGER")
                && !convertedFieldVO.getFieldDataType().equals("LONG VARCHAR")){
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

        StringBuilder openTable = new StringBuilder();
        // on supprime le "IF NOT EXISTS"... Derby ne comprends pas
        openTable.append("CREATE TABLE ");
        openTable.append(tableVO.getTableName());
        openTable.append(" (\n");

        tableVO.getTableStructure().append(openTable);
    }

    @Override
    protected void writeCloseTableStructure(ITableVO tableVO) {

        // on ne mentionne pas de référence à Engine, default charset, ...
        // pas de terminateur ";"
        String closeTable = "\n)\n";

        tableVO.getTableStructure().append(closeTable);
        
        // Derby ne supporte pas les caractères de protection "`"
        // nettoyage des protections du nom de la table et des champs
        String cleanProtected = tableVO.getTableStructure().toString().replaceAll("`", "");
        StringBuilder cleaned = new StringBuilder();
        cleaned.append(cleanProtected);
        tableVO.setTableStructure(cleaned);

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

        // pas de terminateur ;
        String closeTable = ")";

        tableVO.getTableStructure().append(closeTable);

       
    }

    @Override
    protected void definePrimaryKey(ITableVO tableVO,List<DBMetaFieldVO> PKList) {
        System.out.println("ALTER CLE PRIMAIRE");
    }

    @Override
    protected void defineForeignKeys(ITableVO tableVO,List<DBMetaFieldVO> FKList) {
        System.out.println("ALTER CLES ETRANGERES");
    }

    //--------------------------------------------------------------------------
    // DONNEES
    //--------------------------------------------------------------------------


}
