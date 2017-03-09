package com.example.android.httpnetworking.http;

import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class HttpRetriever {

    /**
     * Este método devuelve el resultado completo de un HTTP response.
     *
     * @param url La URL de la que se requiere recuperar el HTTP response.
     * @return El contenido del HTTP response.
     */
    public String retrieve(String url) {
        URL uUrl;
        HttpURLConnection urlConnection = null;
        InputStream in;
        try {
            uUrl = new URL(url);
            urlConnection = (HttpURLConnection) uUrl.openConnection();
            in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } catch(Exception ex){
            Log.e("EXCEPCION", "Método Retrieve" + ex.getMessage());
            return null;
        } finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }
    }
}
