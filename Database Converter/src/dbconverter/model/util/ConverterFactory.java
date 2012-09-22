package dbconverter.model.util;

import dbconverter.common.constant.DBTypes;

/**
 * Factory retournant le convertisseur correspondant au type de DB
 * @author daneelolivaw
 */
public class ConverterFactory {

    public static IConverter create(int dbTargetType, int dbSourceType){

        IConverter dbConverter = null;

        switch(dbTargetType){
            case DBTypes.Access:
                dbConverter = new ConverterAccess(dbSourceType);
                break;
            case DBTypes.Derby:
                dbConverter = new ConverterDerby(dbSourceType);
                break;
            case DBTypes.MySQL:
                dbConverter = new ConverterMySQL(dbSourceType);
                break;
        }
        
        return dbConverter;
        
    }
}
