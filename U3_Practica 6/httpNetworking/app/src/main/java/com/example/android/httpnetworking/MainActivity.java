package com.example.android.httpnetworking;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.httpnetworking.http.HttpRetriever;
import com.example.android.httpnetworking.model.Results;
import com.example.android.httpnetworking.adapters.MoviesAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MainActivity extends AppCompatActivity {

    private EditText mSearchEditText;
    private Button mSearchButton;
    private ListView mSearchResultListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Recuperar los views
        mSearchEditText = (EditText) findViewById(R.id.et_search);
        mSearchButton = (Button) findViewById(R.id.bt_search);
        mSearchResultListView = (ListView) findViewById(R.id.lv_search_result);

        //Configurar el listener para el clic del botón de búsqueda
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = mSearchEditText.getText().toString();
                //Mandar ejecutar la tarea en background
                MovieSearchTask task = new MovieSearchTask();
                task.execute(query);
            }
        });

    }

    //Método para enviar un Toast con el mensaje indicado como argumento
    public void longToast(CharSequence message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }


    /**
     * Clase para ejecutar una tarea en background que permita ir a recuperar datos a Internet
     * acorde a una búsqueda por Pelicula utilizando el api.themoviedb.org
     */
    private class MovieSearchTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Deshabilitar elementos GUI mientras se realiza la búsqueda
            mSearchButton.setEnabled(false);
            mSearchEditText.setEnabled(false);
            longToast("Buscando en Internet, paciencia...");

        }

        @Override
        protected String doInBackground(String... params) {
            String result = null;
            String query;
            String apiKey = "40f11090bae0134d6cbf32392c2d9697"; //Obtenida de www.themoviedb.org
            try {
                query = params[0]; //Recuperar el primer parametro, el query.

                //Mandar realizar la búsqueda.
                HttpRetriever http = new HttpRetriever();
                result = http.retrieve("https://api.themoviedb.org/3/search/movie?api_key="
                        + apiKey + "&query=" + query);
            }catch (Exception ex){
                Log.e("DIBG",ex.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {

                //Utilizar la libreria Gson para parsear los resultados JSON hacia objetos java.
                Gson gson = new GsonBuilder().create();
                Results queryResults = gson.fromJson(result, Results.class);

                //Cargar el ListView mediante el adaptador creado.
                mSearchResultListView.setAdapter(new MoviesAdapter(getBaseContext(),queryResults.results));

                longToast(queryResults.total_results + " resultados encontrados.");
            }else
                longToast("Error al procesar la petición");

            //Habilitar los elementos GUI previamente deshabilitados.
            mSearchButton.setEnabled(true);
            mSearchEditText.setEnabled(true);
        }
    }

}
