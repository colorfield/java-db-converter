package dbconverter;

import dbconverter.view.ui.MainWindow;

/**
 * Scope d'application :
 * - Tables et vues
 * - PrimaryKey, ForeignKey
 * - varchar, int, date
 *
 * D'abord tout convertir en String puis passer à la classe qui va convertir
 *  => établir langage commun
 *
 * -----------------------------------------------------------------------------
 * 
 * DEMO
 * =============================================================================
 * Derby -> MySQL : 
 * vider MySQL.dbconvert02
 * dbconvert03 / app / app -> dbconvert02 / root / root
 *
 * MySQL -> Derby : 
 * vider Derby.dbconvert04
 * dbconvert01 / root / root -> dbconvert04 / app / app
 *
 * -----------------------------------------------------------------------------
 *
 * Firebird -> MySQL
 * vider MySQL.dbconvert02
 * dbconvert01 / sysdba / masterkey -> dbconvert02 / root / root
 *
 * MySQL -> Firebird
 * vider Firebird.dbconvert02
 * 
 *
 * 
 * @author daneelolivaw
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
        
    }

}
