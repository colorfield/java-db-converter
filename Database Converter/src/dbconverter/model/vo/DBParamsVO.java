package dbconverter.model.vo;

import dbconverter.common.constant.DBTypes;
import dbconverter.common.constant.DefaultParams;


public class DBParamsVO implements ValueObject {

    private String user = DefaultParams.DB_USR;
    private String password = DefaultParams.DB_PWD;
    private String server = DefaultParams.DB_SERVER;

    private String db_typeName; // définit par la view
    private int    db_typeId;   // constantes de DBTypes
    private String db_driver; // définit en fonction du type
    private String db_subProtocol; // définit en fonction du type
    private String db_name;
    private String db_localFile;

    private int    port;

    private String connectionString;

 
    public DBParamsVO(String server, int port, String db_typeName,
                    String db_name, String user, String password){

        this.db_typeName = db_typeName;
        this.server = server;
        this.port = port;
        this.db_name = db_name;
        this.user = user;
        this.password = password;

        // Définit le driver et le sous-protocole
        // en fonction du nom du type de la base de données
        setDriverAndSubProtocol();
        setConnectionString();
    }


    /**
     * Définit le driver et le sous-protocole associé au SGBD
     * Final pour éviter les override 
     * vu qu'appellé dans un constructeur
     */
    public final void setDriverAndSubProtocol(){
        db_typeId = DBTypes.getDBTypeID(db_typeName);
        db_driver = DBTypes.getDBDriver(db_typeId);
        db_subProtocol = DBTypes.getDBSubProtocol(db_typeId);
    }

    /**
     * Construction de la connectionString
     */
    private void setConnectionString() {

        switch(this.db_typeId){
            case DBTypes.Access:
                // TODO
                break;

            /* Syntaxe firebird
             *
             * jdbc:firebirdsql:localhost/3050:/Users/daneelolivaw/Dropbox/EPFC/ANCA/db/dbconvert01.gdb
             *
             */
            case DBTypes.Firebird:
                this.connectionString = "jdbc:"+db_subProtocol+":";
                this.connectionString += server;
                this.connectionString += "/"+port+":";
                this.connectionString += "/" + db_name;
                break;

            case DBTypes.MySQL:
            case DBTypes.Derby:
                this.connectionString = "jdbc:"+db_subProtocol+"://";
                this.connectionString += server;
                this.connectionString += ":"+port;
                this.connectionString += "/" + db_name;
                break;
        }
    }


    @Override
    public String toString(){

        // TODO : les valeurs ne se mettent pas à jour !
        System.out.println(connectionString);

        return connectionString;

    }

    //--------------------------------------------------------------------------
    //  GETTER/SETTER
    //--------------------------------------------------------------------------

    public String getDb_driver() {
        return db_driver;
    }

    /**
     * Si le nom du SGBD a changé, on doit aussi changer
     * le driver et le sous-protocole
     */
    public void setDb_typeName(String db_typeName) {
        this.db_typeName = db_typeName;
        setDriverAndSubProtocol();
        setConnectionString();
    }

    public String getDb_typeName() {
        return db_typeName;
    }

    public void setServer(String server) {
        this.server = server;
        setConnectionString();
    }

    public String getServer() {
        return server;
    }

    public void setPort(int port) {
        this.port = port;
        setConnectionString();
    }

    public int getPort() {
        return port;
    }

    /**
     * A ne pas confondre avec le nom de la db (nom du type de db / nom de la db)
     */
    public void setDb_name(String db_name) {
        this.db_name = db_name;
        setConnectionString();
    }
    

    public String getDb_name() {
        return db_name;
    }


    public int getDb_typeId() {
        return db_typeId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDb_localFile() {
        return db_localFile;
    }

    public void setDb_localFile(String db_localFile) {
        this.db_localFile = db_localFile;
        setConnectionString();
    }


    
    
}
