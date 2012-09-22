package dbconverter.model.vo;


import java.util.Collection;
import java.util.LinkedList;

/**
 * 
 * @author daneelolivaw
 */
public class DBMetaBaseVO implements ValueObject {

    /*
    private String DB_Driver = "com.mysql.jdbc.Driver";
    private String DB_Type = "mysql";
    private String DB_Server = "127.0.0.1";
    private String DB_Port = "8889";
    private String DB_Name  = "dbconvert01";
    private String DB_User = "root";
    private String DB_Password = "root";
    */

    // TODO Créer un hashmap avec le nom des tables comme clé p.ex.
    private Collection<DBMetaTableVO>  tables;
    private Collection<DBMetaViewVO>   views;

    public DBMetaBaseVO(){
        tables = new LinkedList<DBMetaTableVO>();
        views = new LinkedList<DBMetaViewVO>();
    }

    /**
     * Stocke les valueObject table ou vue en fonction de leu type
     * dans les collections adéquates
     * @param tableVO
     */
    public void addTableOrView(ITableVO tableVO) {
        
        // QUESTION : un autre moyen de faire celà ?
        // problème : rend un peu dommage le rôle de la factory
        
        if(tableVO.getClass().toString().equals("class dbconverter.model.vo.DBMetaTableVO")){
            System.out.println("--- TABLE ---");
            tables.add((DBMetaTableVO) tableVO);
        }else if(tableVO.getClass().toString().equals("class dbconverter.model.vo.DBMetaViewVO")){
            System.out.println("--- VIEW ---");
            views.add((DBMetaViewVO) tableVO);
        }

    }

    public void addTable(String tableName){
        DBMetaTableVO table = new DBMetaTableVO(tableName);
        tables.add(table);
    }

    public void addView(String viewName){
        DBMetaViewVO view = new DBMetaViewVO(viewName);
        views.add(view);
    }

    public void addTable(DBMetaTableVO table){
        tables.add(table);
    }

    public void addView(DBMetaViewVO view){
        views.add(view);
    }

    public Collection<DBMetaTableVO> getTables() {
        return tables;
    }

    public Collection<DBMetaViewVO> getViews() {
        return views;
    }

   
    

    
    
}
