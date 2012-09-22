package dbconverter.model.vo;

/**
 * Représentation en RAM d'une View
 * @author daneelolivaw
 */
public class DBMetaViewVO extends DBMetaTableVO implements ITableVO {

    public DBMetaViewVO(String viewName) {
        super(viewName);
    }

    // TODO : gérer ici les différences entre les views et les tables
    
}
