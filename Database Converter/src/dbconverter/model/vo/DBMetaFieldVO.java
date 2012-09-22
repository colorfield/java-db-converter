package dbconverter.model.vo;

/**
 * Représentation en RAM d'un champ d'une table ou d'une vue
 * @author daneelolivaw
 */
public class DBMetaFieldVO {

    private StringBuilder fieldStructure;

    private String fieldName;
    private int fieldSize;
    private int isNullable;
    private String fieldDataType; // le principal enjeu...
    
    private boolean isPrimary;
    private boolean isForeign;

    
    // uniquement pour les clés étrangères
    private String tableReference;
    private String columnReference;

    public DBMetaFieldVO(String fieldName, int fieldSize, int isNullable, String fieldDataType) {
        this.fieldName = fieldName;
        this.fieldSize = fieldSize;
        this.isNullable = isNullable;
        this.fieldDataType = fieldDataType;
    }

    public DBMetaFieldVO(String fieldName) {
        this.fieldName = fieldName;
    }

    public StringBuilder getFieldStructure() {
        return fieldStructure;
    }

    public void setFieldStructure(StringBuilder fieldStructure) {
        this.fieldStructure = fieldStructure;
    }

    @Override
    public String toString(){
        String str = "Field ";
        str += "[Name = " + fieldName;
        str += " - Type = " + fieldDataType;
        str += " - Size = " + fieldSize;
        str += " - isNullable = "+ isNullable + "]";

        return str;
    }

    public String getColumnReference() {
        return columnReference;
    }

    public void setColumnReference(String columnReference) {
        this.columnReference = columnReference;
    }

    public String getTableReference() {
        return tableReference;
    }

    public void setTableReference(String tableReference) {
        this.tableReference = tableReference;
    }

    public String getFieldDataType() {
        return fieldDataType;
    }

    public void setFieldDataType(String fieldDataType) {
        this.fieldDataType = fieldDataType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public int getFieldSize() {
        return fieldSize;
    }

    public void setFieldSize(int fieldSize) {
        this.fieldSize = fieldSize;
    }

    public boolean isIsForeign() {
        return isForeign;
    }

    public void setIsForeign(boolean isForeign) {
        this.isForeign = isForeign;
    }

    public int isIsNullable() {
        return isNullable;
    }

    public void setIsNullable(int isNullable) {
        this.isNullable = isNullable;
    }

    public boolean isIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    

}
