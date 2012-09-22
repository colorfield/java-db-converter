package dbconverter.model.vo;

/**
 *
 * @author daneelolivaw
 */
public class TableFactory {

    public static final int TABLE = 0;
    public static final int VIEW  = 1;

    public static ITableVO create(String tableType, String tableName){

        ITableVO result = null;
        int tableTypeId = 0;

        if(tableType.equals("TABLE")){
            tableTypeId = 0;
        }else if(tableType.equals("VIEW")){
            tableTypeId = 1;
        }

        
        switch(tableTypeId){
            case TableFactory.TABLE:
                result = (ITableVO) new DBMetaTableVO(tableName);
                break;
            case TableFactory.VIEW:
                result = (ITableVO) new DBMetaViewVO(tableName);
                break;
        }

        return result;
        
    }


    /**
     * QUESTION : comment remplacer cette horrible comparaison de String ?
     * Si on doit dupliquer une table en fonction de son type initial
     * @param tableVO
     * @param tableName
     * @return
     */
    public static ITableVO create(ITableVO tableVO, String tableName){

        ITableVO result = null;
        if(tableVO.getClass().toString().equals("class dbconverter.model.vo.DBMetaTableVO")){
            result = create("TABLE", tableName);
        }else if(tableVO.getClass().toString().equals("class dbconverter.model.vo.DBMetaViewVO")){
            result = create("VIEW", tableName);
        }

        return result;

    }



}
