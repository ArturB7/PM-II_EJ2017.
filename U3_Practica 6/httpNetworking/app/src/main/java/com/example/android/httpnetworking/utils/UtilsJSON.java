package com.example.android.httpnetworking.utils;

/***
 * Clase que contiene métodos utilitarios par el tratamiento de JSON
 */
public class UtilsJSON {
    /**
     * Este método devuelve una representación "identada" de un texto JSON
     * @param text el texto a identar
     * @return una cadena con el texto identado
     */
    public static String formatString(String text){
        StringBuilder json = new StringBuilder();
        String indentString = "";

        for (int i = 0; i < text.length(); i++) {
            char letter = text.charAt(i);
            switch (letter) {
                case '{':
                case '[':
                    json.append("\n" + indentString + letter + "\n");
                    indentString = indentString + "\t";
                    json.append(indentString);
                    break;
                case '}':
                case ']':
                    indentString = indentString.replaceFirst("\t", "");
                    json.append("\n" + indentString + letter);
                    break;
                case ',':
                    json.append(letter + "\n" + indentString);
                    break;

                default:
                    json.append(letter);
                    break;
            }
        }
        return json.toString();
    }
}
