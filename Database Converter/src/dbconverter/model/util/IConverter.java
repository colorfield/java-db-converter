package dbconverter.model.util;

import dbconverter.model.vo.DBMetaBaseVO;
import dbconverter.model.vo.DBMetaFieldVO;

/**
 *
 * @author daneelolivaw
 */
public interface IConverter {

    public DBMetaBaseVO convertMetaData(DBMetaBaseVO dbMeta);
    public void convertData(DBMetaFieldVO fieldVO);

}
