package dbconverter.model.util.dao;

import dbconverter.common.constant.SQLCommand;
import dbconverter.model.vo.DBParamsVO;
import dbconverter.model.vo.ValueObject;
import java.sql.Connection; // non spécifique !
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe gérant la connexion et la déconnexion à une DB.
 *
 * Une méthode statique permet l'affichage des exceptions SQL
 *
 * La définition du type de DB (et du driver associé) se réalise sous
 * cdgen.common.PrivateParams
 * 
 * @author christophe
 */
public class SQLUtility {


   /**
    * Structure de la connectionString
    *
    * "jdbc:"+DefaultParams.DB_TYPE+"://"+DefaultParams.DB_SERVER+":"
    * +DefaultParams.DB_PORT+"/" + DefaultParams.DB_NAME ;
    *
    * jdbc:mysql://127.0.0.1:8889/dbconvert01
    */
    public String connectionString;
    public DBParamsVO dbParams;
    public Connection con = null;

    /**
     * @param dbParams
     */
    public SQLUtility(DBParamsVO dbParams){
        this.dbParams = dbParams;
        connectionString = dbParams.toString();
        System.out.println(connectionString);
    }


    /**
     * Ouverture de la connexion
     * @throws ClassNotFoundException
     */
    public void openConnection() throws ClassNotFoundException{
        // déjà connecté ?
        if (con == null) {
            try {
                // Enregistre le driver JDBC
                Class.forName(dbParams.getDb_driver());
                
                con = DriverManager.getConnection(connectionString,
                                                  dbParams.getUser(),
                                                  dbParams.getPassword());
            } catch (Exception ex) {
                Logger.getLogger(SQLUtility.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Fermeture de la connexion
     */
    public void closeConnection(){
        try {
            if (con != null) con.close();
        } catch (SQLException ex) {
            Logger.getLogger(SQLUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Retourne l'objet connection
     * @return
     */
    public Connection getConnection(){
        return con;
    }

    /**
     * Affichage de l'arbre d'erreurs SQL
     * @param e
     * @return
     */
    public static String showSQLException(SQLException e){

        SQLException nextException = e;
        StringBuilder msg = new StringBuilder();
        msg.append("\n============ SQL EXCEPTION ==============\n");

        while(nextException != null){
            msg.append(nextException.getMessage() + "\n");
            msg.append("Code erreur \t: " + nextException.getErrorCode() + "\n");
            msg.append("SQL state \t: " + nextException.getSQLState() + "\n");

            nextException = nextException.getNextException();
        }
        msg.append("\n=========================================\n");
        System.out.println(msg);

        return msg.toString();
    }
    
    // se limiter à la connexion / déconnexion ?
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------
    //--------------------------------------------------------------------------


    /**
     * Méthode de base pour l'exécution d'une requête SQL
     * @param vo
     * @param command
     * @param table
     * @return Object
     */
    public Object executeSQLQuery(ValueObject vo, SQLCommand command){

        Object result = null;

        try {
            openConnection();
            //result = getSQLResult(vo, command);
            closeConnection();

        } catch (Exception e) {
            // TODO : gestion de l'exception => p.ex. rollBack, ...
        }

        return result;
    }


    /**
     * Méthode abstraite devant être implémentée.
     * L'appel par la classe cliente de executeSQLQuery appelera la méthode
     * getSQLResult via la classe d'implémentation.
     * @param vo
     * @param command
     * @param table
     * @return
     */
    // public abstract Object getSQLResult(ValueObject vo, SQLCommand command);


    



}
