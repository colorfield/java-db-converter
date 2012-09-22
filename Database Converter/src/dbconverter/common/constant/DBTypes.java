package dbconverter.common.constant;


/**
 *
 * @author daneelolivaw
 */
public class DBTypes {

  public static final int TABLE_TABLE       = 0;
  public static final int TABLE_VIEW        = 1;
  
  // dans la matrice dbTypes, frontière entre les types numériques et non numériques
  private static final int NUMERIC_BORDER   = 10;

  // Liste des SGBDR supportés
  // TODO : les définir à partir des constantes puis modifier la lecture des combobox
  public static final String [] supportedSGBDR = new String [] {"MySQL","Access","Derby","Firebird"};

  // Id local des SGDB,  correspond également aux colonnes de la matrice dataTypes
  
  public static final int JDBC          = 1; // le type commun utilisé par AbstractConverter
  public static final int MySQL         = 2;
  public static final int PostgreSQL    = 3;
  public static final int Oracle        = 4;
  public static final int Derby         = 5;
  public static final int MSSQL         = 6;
  public static final int SapDB         = 7;
  public static final int DB2           = 8;
  public static final int HSQL          = 9;
  public static final int PointBase     = 10;
  public static final int Access        = 11;
  public static final int Firebird      = 12;

  /**
   * Sources
   * http://docs.codehaus.org/display/CASTOR/Type+Mapping
   * http://download.oracle.com/javase/1.3/docs/guide/jdbc/getstart/mapping.html
   * http://www.firebirdsql.org/manual/migration-mssql-data-types.html
   */

  // !! INT (MySQL) ne s'y retrouvait pas => modifié !!

  
  public static final Object [][] dataTypes = {
   // colonnes : JDBC MySQL PostgreSQL Oracle  Derby  MSSQL SapDB DB2 HSQL PointBase Access Firebird
   { 1, "BIT", "TINYINT(1)", "BOOLEAN", "BOOLEAN", "CHAR FOR BIT DATA", "BIT", "BOOLEAN", "", "BIT", "BOOLEAN", "bit", "CHAR(1)" },
   { 2, "TINYINT", "TINYINT", "SMALLINT", "SMALLINT", "SMALLINT", "TINYINT", "SMALLINT", "SMALLINT", "TINYINT", "SMALLINT", "TINYINT","SMALLINT" },
   { 3, "SMALLINT", "SMALLINT", "SMALLINT", "SMALLINT", "SMALLINT", "SMALLINT", "SMALLINT", "SMALLINT", "SMALLINT", "SMALLINT", "SMALLINT","SMALLINT" },
   { 4, "INTEGER", "INT", "INTEGER", "INTEGER", "INTEGER", "INTEGER", "INTEGER", "INTEGER", "INTEGER", "INTEGER", "INT","INTEGER","INT" },
   { 5, "BIGINT", "BIGINT", "BIGINT", "NUMERIC", "BIGINT", "BIGINT", "INTEGER", "BIGINT", "BIGINT", "NUMERIC" , "bigint","INT64"  },
   { 6, "FLOAT", "FLOAT", "DOUBLE PRECISION", "FLOAT", "FLOAT", "FLOAT", "FLOAT", "FLOAT", "FLOAT", "FLOAT", "FLOAT","FLOAT"  },
   { 7, "DOUBLE", "DOUBLE", "DOUBLE PRECISION", "DOUBLE PRECISION", "DOUBLE", "DOUBLE PRECISION", "DOUBLE PRECISION", "DOUBLE", "DOUBLE PRECISION", "DOUBLE PRECISION", "DOUBLE","DOUBLE"  },
   { 8, "REAL", "REAL", "REAL", "REAL", "REAL", "REAL", "DOUBLE PRECISION", "REAL", "REAL", "REAL", "REAL","DOUBLE"  },
   { 9, "NUMERIC", "NUMERIC", "NUMERIC", "NUMERIC", "NUMERIC", "NUMERIC", "NUMERIC", "NUMERIC", "NUMERIC", "NUMERIC", "CURRENCY","NUMERIC"  },
   { 10, "DECIMAL", "DECIMAL", "NUMERIC", "DECIMAL", "DECIMAL", "DECIMAL", "DECIMAL", "DECIMAL", "DECIMAL", "DECIMAL", "DECIMAL","DECIMAL"  },
   { 11, "CHAR", "CHAR", "CHAR", "CHAR", "CHAR", "CHAR", "CHAR", "CHAR", "CHAR", "CHAR", "CHAR" ,"CHAR" },
   { 12, "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR2", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR", "VARCHAR","VARCHAR"  },
   { 13, "LONGVARCHAR", "TEXT", "VARCHAR", "LONG", "LONG VARCHAR", "TEXT", "LONG", "LONG VARCHAR", "LONGVARCHAR", "CLOB", "ntext","BLOB SUB_TYPE 1"  },
   { 14, "DATE", "DATE", "DATE", "DATE", "DATE", "DATETIME", "DATE", "DATE", "DATE ", "DATE", "DATETIME" ,"DATE" },
   { 15, "TIME", "TIME", "TIME", "DATE", "TIME", "DATETIME", "TIME", "TIME", "TIME ", "TIME", "DATETIME" ,"TIMESTAMP" },
   { 16, "TIMESTAMP", "TIMESTAMP", "TIMESTAMP", "TIMESTAMP", "TIMESTAMP", "TIMESTAMP", "TIMESTAMP", "TIMESTAMP", "TIMESTAMP", "TIMESTAMP", "DATETIME","TIMESTAMP"  },
   { 17, "BINARY", "BINARY", "BYTEA", "RAW", "CHAR [n] FOR BIT DATA", "BINARY", "BLOB", "CHAR [n] FOR BIT DATA", "BINARY", "BLOB", "binary","CHAR"  },
   { 18, "VARBINARY", "VARBINARY", "BYTEA", "LONG RAW", "VARCHAR [] FOR BIT DATA", "VARBINARY", "BLOB", "VARCHAR [] FOR BIT DATA", "VARBINARY", "BLOB", "VARBINARY","CHAR"  },
   { 19, "LONGVARBINARY", "VARBINARY", "BYTEA", "LONG RAW", "LONG VARCHAR FOR BIT DATA", "IMAGE", "BLOB", "LONG VARCHAR FOR BIT DATA", "LONGVARBINARY", "BLOB", "LONGBINARY","BLOB"  },
   { 20, "OTHER", "BLOB", "BYTEA", "BLOB", "BLOB", "IMAGE", "BLOB", "BLOB", "OTHER", "BLOB", "BLOB","BLOB"  },
   { 21, "JAVA_OBJECT", "BLOB", "BYTEA", "BLOB", "BLOB", "IMAGE", "BLOB", "BLOB", "OBJECT", "BLOB", "BLOB","BLOB"  },
   { 22, "BLOB", "BLOB", "BYTEA", "BLOB", "BLOB", "IMAGE", "BLOB", "BLOB ", "OBJECT", "BLOB", "BLOB","BLOB"  },
   { 23, "CLOB", "TEXT", "TEXT", "CLOB", "CLOB", "TEXT", "CLOB", "CLOB", "OBJECT", "CLOB", "BLOB SUB_TYPE TEXT" ,"BLOB SUB_TYPE TEXT" },
  };


  /**
   * Définit si un type est "numérique" ou non
   * (s'il doit être englobé par '' dans un INSERT)
   *
   * On recherche dans la matrice datatTypes
   * 
   * @param strType
   * @return
   */
  public static boolean isNumericType(String fieldType,int dbType){

         // indices du tableau de 1 à 10 = numériques
         // indices suivants = ''

         // dbType permet d'aller chercher uniquement dans la bonne colonne
         //System.out.println("TYPE db = " + dbType);
         //System.out.println("TYPE field = " + fieldType);


         boolean founded = false;
         int curIdx = 0;

         // le test de curIdx est là en tant que contrôle ("garde fou" si fieldType inexsitant)
         // la première évaluation étant normalement suffisante,
         // en termes de performance, pas d'incidence vu l'évaluation optimisée
         // pas nécessaire d'aller plus loin que 10 (NUMERIC_BORDER) -> si pas trouvé avant, pas numérique
         while(!founded && curIdx < NUMERIC_BORDER /*dataTypes.length*/){ //
             if(fieldType.equals(dataTypes[curIdx][dbType])){
                 founded = true;
                 // System.out.println("TYPE champ trouvé = " + dataTypes[curIdx][dbType]);
             }
             ++curIdx;
         }


         return founded;

  }


  /**
   * La DB doit-elle être référencée sous la forme d'un fichier ?
   * @param dbType
   * @return
   */
  public static boolean isFile(int dbType){

      boolean result = false;

      switch(dbType){
          case DBTypes.Access: result = true;break;
          case DBTypes.MySQL: result = false;break;
          case DBTypes.Derby: result = false;break;
          case DBTypes.Firebird: result = true;break;
      }

      return result;
      
  }



  

  /**
   * Retourne l'ID (local à cette application)
   * du type de DB en fonction de son nom
   *
   * Remarque : on aurait pu prendre l'index du ComboBox vu qu'il
   * s'agit tout de même du model de celle-ci (setSelectedIndex)
   * toutefois l'objectif est de fournir l'id local au cas où
   * on brancherait un outil CLI p.ex. (=> pas de ComboBox !)
   *
   * @return DBType local constant
   */
  public static int getDBTypeID(String dbStr){
      // TODO : compléter la liste
      int result = 0; // par défaut Access

      if(dbStr.equals("Access")){
          result = DBTypes.Access;
          //System.out.println("---> Access");
      }else if(dbStr.equals("MySQL")){
          result = DBTypes.MySQL;
          //System.out.println("---> MySQL");
      }else if(dbStr.equals("Firebird")){
          result = DBTypes.Firebird;
      }else if(dbStr.equals("Derby")){
          result = DBTypes.Derby;
      }
      
      return result;
  }

  /**
   * Retourne le nom du sous-protocole en fonction du type de DB
   * @param dbStr
   * @return
   */
  public static String getDBSubProtocol(int dbType){
      // TODO : compléter la liste
      String result = "";

      switch(dbType){
          case DBTypes.Access: result = "odbc";break;
          case DBTypes.MySQL: result = "mysql";break;
          case DBTypes.Derby: result = "derby";break;
          case DBTypes.Firebird: result = "firebirdsql";break;
      }

      return result;

  }

  /**
   * Retourne le nom du driver en fonction du type de DB
   * @param dbStr
   * @return
   */
  public static String getDBDriver(int dbType){
      // TODO : compléter la liste
      String result = "";

      switch(dbType){
          case DBTypes.Access: result = "sun.jdbc.odbc.JdbcOdbcDriver";break;
          case DBTypes.MySQL: result = "com.mysql.jdbc.Driver";break;
          case DBTypes.Derby: result = "org.apache.derby.jdbc.ClientDriver";break;
          case DBTypes.Firebird: result = "org.firebirdsql.jdbc.FBDriver";break;
      }

      return result;

  }

  /**
   * Tentative de définition du port par défaut...
   * TODO : à refléter dans la view
   * @param dbType
   * @return
   */
  public static int getDBDefaultPort(int dbType){
      // TODO : compléter la liste
      int result = 0;

      switch(dbType){
          case DBTypes.Access: result = 1433;break;
          case DBTypes.MySQL: result = 8889;break; // avec MAMP, si LAMP 3306...
          case DBTypes.Derby: result = 1527;break;
          case DBTypes.Firebird: result = 3050;break;
      }

      return result;

  }



  /**
   * Convertit le type SGBD source en type JDBC
   * @param sourceType
   * @param dbType
   * @return
   */
  public static String convertToCommonType(String sourceType, int dbType){

      String commonType = null;

      System.out.println("SOURCE = " + sourceType);

      int iTypes = 0;
      boolean founded = false;

      // boucle sur les types de données
       
      while((!founded) && (iTypes < dataTypes.length)){

          if(sourceType.equals(dataTypes[iTypes][dbType])){
              commonType = (String) dataTypes[iTypes][1]; // indice 1 = JDBC
              founded = true;
          }

          ++iTypes;
      }

      System.out.println("COMMUN = " + commonType);
      
      return commonType;
  }



  /**
   * Convertit du type commun vers le type cible
   * TODO : optimisation : ne plus passer que l'indice du commun
   * au convertisseur => plus de recherche, deux indices
   * @param commonType
   * @param dbType
   * @return
   */
  public static String convertFromCommonType(String commonType, int dbType){

      String targetType = "";

      System.out.println("COMMUN = " + commonType);
      
      int iTypes = 0;
      boolean founded = false;

      // boucle sur les types de données

      while((!founded) && (iTypes < dataTypes.length)){

          if(commonType.equals(dataTypes[iTypes][1])){ // indice 1 = JDBC
              targetType = (String) dataTypes[iTypes][dbType]; // indice dbType = SGBD target
              founded = true;
          }

          ++iTypes;
      }

      System.out.println("CIBLE = " + targetType);


      return targetType;


  }




}
