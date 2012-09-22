package dbconverter.model.vo;


import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Représentation en RAM d'une table
 * @author daneelolivaw
 */
public class DBMetaTableVO implements ITableVO {

    
    private StringBuilder tableStructure;
    private StringBuilder foreignKeyStructure;

    private String tableName;
    private Collection<DBMetaFieldVO> fields;
    // ne fonctionne que pour les clés primaires à une colonne => non  !
    // private DBMetaFieldVO primaryKey;
    private Collection<DBMetaFieldVO> primaryKeys;
    private Collection<DBMetaFieldVO> foreignKeys;

    public DBMetaTableVO(String tableName) {
        this.tableName = tableName;
        fields = new LinkedList<DBMetaFieldVO>();
        primaryKeys = new LinkedList<DBMetaFieldVO>();
        foreignKeys = new LinkedList<DBMetaFieldVO>();
    }

    public StringBuilder getTableStructure() {
        return tableStructure;
    }

    public void setTableStructure(StringBuilder tableStructure) {
        this.tableStructure = tableStructure;
    }

    public StringBuilder getForeignKeyStructure() {
        return foreignKeyStructure;
    }

    public void setForeignKeyStructure(StringBuilder foreignKeyStructure) {
        this.foreignKeyStructure = foreignKeyStructure;
    }

    


    /**
     * Ajout d'un objet field
     * @param field
     */
    public void addField(DBMetaFieldVO field){
        if(field != null){
            fields.add(field);
        }
    }

    /**
     * Ajout d'un objet field via son nom
     * @param fieldName
     */
    public void addField(String fieldName){
        if(fieldName != null){
            fields.add(new DBMetaFieldVO(fieldName));
        }
    }

    /**
     * Ajout d'une clé étrangère :
     * référence à la collection de fields + modification des propriétés
     * du champ concerné
     * @param fieldName
     * @param tableReference
     * @param columnReference
     */
    public void addForeignKey(String fieldName,
                              String tableReference, String columnReference){

        DBMetaFieldVO field = searchFieldByName(fieldName);
        if(field != null){
            field.setIsForeign(true);
            field.setTableReference(tableReference);
            field.setColumnReference(columnReference);
            foreignKeys.add(field);
        }
    }

    /**
     * Ajout des clés primaires (liste car possibilité qu'elle soit
     * définie sur plusieurs colonnes)
     * Référence à la collection de fields + modification des propriétés
     * du champ concerné
     * @param fieldName
     */
    public void addPrimaryKey(String fieldName){

        DBMetaFieldVO field = searchFieldByName(fieldName);
        if(field != null){
            field.setIsPrimary(true);
            primaryKeys.add(field);
        }
    }


    public Collection getPrimaryKeys(){
        return this.primaryKeys;
    }

    public Collection getForeignKeys(){
        return this.foreignKeys;
    }



    /**
     * Méthode utilisée par addForeignKey et setPrimatyKey(str)
     * @param fieldName
     * @return
     */
    private DBMetaFieldVO searchFieldByName(String fieldName){

        Iterator it = fields.iterator();
        boolean founded = false;
        DBMetaFieldVO curField = null;

        while(it.hasNext() && !founded){
            curField = (DBMetaFieldVO)it.next();
            if(curField.getFieldName().equals(fieldName)){
                founded = true;
            }
        }

        DBMetaFieldVO result = null;
        if(founded){
            result = curField;
        }
        return result;
    }



    private String enumerateCollection(Collection c){
        
        String str = "";
        Iterator it = c.iterator();
        
        while(it.hasNext())
            str += it.next().toString();

        return str;
    }




    @Override
    public String toString(){

        String str = "Table ";
        str += "[Name = " + tableName + "]";
        str += "\n>>> Primary\n";
        str += enumerateCollection(primaryKeys);
        str += "\n>>> Foreign\n";
        str += enumerateCollection(foreignKeys);

        return str;
    }


    //--------------------------------------------------------------------------
    // GETTER/SETTER
    //--------------------------------------------------------------------------

    /**
     * Définit la clé primaire en fonction du nom du champ
     * @param primaryKey
     */
    /*
    public void setPrimaryKey(String primaryKeyName) {
        // this.primaryKey = searchFieldByName(primaryKeyName);
        DBMetaFieldVO field = searchFieldByName(primaryKeyName);
        field.setIsPrimary(true);
        this.primaryKey = field;
    }
    
    public void setPrimaryKey(DBMetaFieldVO primaryKey) {
        this.primaryKey = primaryKey;
        primaryKey.setIsPrimary(true);
    }
    

    public DBMetaFieldVO getPrimaryKey() {
        return primaryKey;
    }
    */

    public Collection<DBMetaFieldVO> getFields() {
        return fields;
    }

    public void setFields(Collection<DBMetaFieldVO> fields) {
        this.fields = fields;
    }


    public void setForeignKeys(Collection<DBMetaFieldVO> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }


    

}
