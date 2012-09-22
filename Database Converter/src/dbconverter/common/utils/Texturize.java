package dbconverter.common.utils;

import java.text.Normalizer;
import java.util.StringTokenizer;

/**
 * Utilitaire de nettoyage de chaîne
 * @author daneelolivaw
 */
public class Texturize {

    /**
     * Suppression des caractères accentués, et espaces
     * @param str
     * @return
     */
    public static String cleanString(String str){

        return removeSpaces(removeAccents(str));

    }

    /**
     * Protection d'une chaîne (principalement pour utilisation des DB
     * @param str
     * @return
     */
    public static String protectString(String str){

        return "`"+ str + "`";
    }


    /**
     * Suppression des caractères accentués
     * @param s
     * @return
     */
    public static String removeAccents(String s) {

        // JAVA 6
        return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("[\u0300-\u036F]", "");


        // JAVA 5
        /*
        String temp = Normalizer.normalize(s, Normalizer.DECOMP, 0);
        return temp.replaceAll("[^\\p{ASCII}]","");
        */
    }


    /**
     * Suppression des espaces
     * @param s
     * @return
     */
    public static String removeSpaces(String s) {
      StringTokenizer st = new StringTokenizer(s," ",false);
      String t="";
      while (st.hasMoreElements()) t += st.nextElement();
      return t;
    }


}
