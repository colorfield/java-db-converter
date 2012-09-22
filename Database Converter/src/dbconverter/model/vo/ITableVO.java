package dbconverter.model.vo;

import java.util.Collection;

/**
 *
 * @author daneelolivaw
 */
public interface ITableVO {

    public void addField(DBMetaFieldVO field);
    public void addField(String fieldName);
    public void addForeignKey(String fieldName,
                              String tableReference, String columnReference);
    public Collection<DBMetaFieldVO> getFields();
    public void setFields(Collection<DBMetaFieldVO> fields);
    public Collection<DBMetaFieldVO> getForeignKeys();
    public void setForeignKeys(Collection<DBMetaFieldVO> foreignKeys);
    public void addPrimaryKey(String fieldName);
    public Collection getPrimaryKeys();
    /*
    public DBMetaFieldVO getPrimaryKey();
    public void setPrimaryKey(DBMetaFieldVO primaryKey);
    public void setPrimaryKey(String primaryKey);
    */
    public String getTableName();
    public void setTableName(String tableName);

    public StringBuilder getTableStructure();
    public void setTableStructure(StringBuilder tableStructure);
    
    /* pas pour les vues => pas dans cette interface
    public StringBuilder getForeignKeyStructure();
    public void setForeignKeyStructure(StringBuilder foreignKeyStructure);
    */
}
