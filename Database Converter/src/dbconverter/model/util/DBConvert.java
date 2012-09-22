package dbconverter.model.util;

import dbconverter.model.util.dao.DBDump;
import dbconverter.model.util.dao.DBReadDump;
import dbconverter.model.vo.DBParamsVO;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * -----------------------------------------------------------------------------
 * METHODOLOGIE
 * -----------------------------------------------------------------------------
 * 
 * 1. Recevoir d'une factory le convertisseur nécessaire pour la cible
 * 2. Extraire les métadonnées de la source (tables, views)
 * 3. Convertir les métadonnées
 * 4. Créer la métabase dans la cible
 * 5. Extraire les données table par table, record par record
 * 6. Convertir les données table par table, record par record
 * 7. Insérer les données table par table, record par record
 *
 * Il n'y a donc pas un schéma simpliste extraire - convertir - insérer :
 * vu que le volume d'une db peut rapidement faire dépasser les capacités en RAM
 * on procède au coup par coup (record par record).
 *
 * Ceci nécessite que les objets DBDump et DBReadDump possèdent une référence
 * vers le convertisseur.
 *
 * @author daneelolivaw
 */
public class DBConvert {

    
    private DBParamsVO dbSourceParams;
    private DBParamsVO dbTargetParams;

    private DBDump dbExtract;
    private IConverter dbConverter;
    private DBReadDump  dbInsert;
    
    public DBConvert(DBParamsVO dbSourceParams,DBParamsVO dbTargetParams){

        this.dbSourceParams = dbSourceParams;
        this.dbTargetParams = dbTargetParams;

        dbConverter = ConverterFactory.create(dbTargetParams.getDb_typeId(),dbSourceParams.getDb_typeId());

        // l'extracteur doit connaître l'inséreur pour pouvoir travailler
        // par pool d'INSERT au lieu de tout charger en RAM
        dbInsert = new DBReadDump(dbTargetParams, dbConverter);
        dbExtract = new DBDump(dbSourceParams, dbConverter, dbInsert);
        
        //================================= 1. META-DONNEES
        // enchainement de 3 méthodes :
        // récupération de la metabase dans un objet DBMetabaseVO
        // et conversion de celle-ci dans un nouvel objet DBMetabaseVO
        // puis création de celle-ci via cet objet

        // test unitaire
        //dbExtract.getDBMetaData();

        
        dbInsert.createMetaData(dbConverter.convertMetaData(dbExtract.getDBMetaData()));
        
        try {
            //================================= 2. DONNEES
            // schéma :
            // l'extracteur extrait ligne par ligne (ou par pool de n lignes)
            // à chaque ligne, il passe au convertisseur
            // puis le convertisseur passe le résultat à l'inséreur
            // idée de solution plus élégante / moins couplée :
            // travailler avec des foncteurs
            // ce qu'il y ade particulier avec cette solution :
            // convertData fait tout le boulot
            dbExtract.convertData();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBConvert.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }


    public IConverter getConverter(){
        return dbConverter;
    }
    

}
