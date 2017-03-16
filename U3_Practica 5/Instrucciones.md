# Descripción de la Práctica 4 de la Unidad 3 (Telefonía  y redes)  (a.k.a. Práctica 11)
**Tema de interés: JSON Parsing**

El propósito de esta practica es que el alumno conozca la manera de trabajar con los datos de una respuesta en formato JSON de alguno servicio que proporcione un API REST tras haber realizado una petición (request) HTTP.  El objetivo es continuar desarrollando la aplicación de búsquedas en la base de datos de peliculas: https://www.themoviedb.org.

## Instrucciones de la práctica:
Obtener el código fuente base y modificarlo para:

 1. Actualizar el layout y referencias a las vistas para la MainActivity agregando un ListView
 2. Crear un layout para los Items que contendrá el ListView
 3. Incorporar GSON al proyecto mediante Gradle
 4. Crear las clases Model que soportarán la carga de datos desde JSON a través de GSON
 5. Crear un Adapter que nos permita popular con Items el ListView a partir del contenido del Model
 6. Actualizar la AsyncTask denominada MovieSearchTask para unir todas las piezas.

A continuación algo de código y su explicación para cada paso de la práctica:

##  1. Actualizar el layout y referencias a las vistas para la MainActivity agregando un ListView

Dado que con esta práctica se pretende dejar de presentar el resultado JSON y mas bien hacer Parsing de éste, debemos prescindir del TextView que nos permitia cargar este contenido textual y remplarzarlo por un ListView que nos permita posteriormente realizar la carga de datos mediante un Adaptador.  Es por esto que en este paso lo que se deberá realizar es actualizar nuestro layout para la MainActivity editando el archivo **activity_main.xml** para que incluya los elementos GUI requeridos.  El código aactualizado quedaría mas o menos como el siguiente:

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="fill_parent"
    android:layout_height="fill_parent"
    >

    <TextView android:id="@+id/tv_search"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Qué deseas buscar?"
        />

    <EditText android:id="@+id/et_search"
        android:text=""
        android:layout_width="fill_parent"
        android:layout_height="wrap_content"
        android:hint="Nombre de la peli, actor o dato a buscar..."
        />

    <Button
        android:id="@+id/bt_search"
        android:text="Buscar"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        />

    <!--Este sería el componente que remplaza al TextView previo-->
    <ListView
        android:id="@+id/lv_search_result"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        />

</LinearLayout>
```

Posteriormente, ya dentro de la clase MainActivity, deberás actualizar también las variables para referenciar al los elementos GUI utilizando el método **findViewById** como luce en el siguiente fragmento de código:

```java
...

private EditText mSearchEditText;
private Button mSearchButton;
private ListView mSearchResultListView; //Actualizada

@Override
public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mSearchEditText = (EditText) findViewById(R.id.et_search);
    mSearchButton = (Button) findViewById(R.id.bt_search);
    mSearchResultListView = (ListView) findViewById(R.id.lv_search_result); //Actualizada

...

}
...
```

Después de realizar lo anterior puedes llegar a tener un problema en el método **onPostExecute** de la clase MovieSearchTask, no te preocupes por el momento, se resolverá mas adelante.

##  2. Crear un layout para los Items que contendrá el ListView

Es bien conocido que un ListView puede contener Items, y es posible definir un layout al cual se apegarán los items que compondrán nuestro ListView.  En esta aplicación es deseable que cada Pelicula encontrada en los resultados se muestre como un *Item* y por el momento nos interesa que cada *item* muestre tres cosas: un número de posición, el título de la pelicula y su resumen (overview).  Para lograr esto debemos definir el layout de los items creando un archivo denominado **item_lista.xml** en la carpeta *layout* dentro de la carpeta *res* de nuestro proyecto.  El código para crear este layout se puede apreciar en el código a continuación:

```xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp">

    <TextView
        android:id="@+id/tv_position"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="#0000FF"
        android:textColor="#FFFFFF"
        android:layout_alignParentTop="true"
        android:textStyle="bold"
        android:paddingRight="10px"
        android:paddingLeft="10px"
        />

    <TextView
        android:id="@+id/tv_original_title"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="#0000FF"
        android:textColor="#FFFFFF"
        android:layout_toRightOf="@id/tv_position"
        android:textStyle="bold"
        />

    <TextView
        android:id="@+id/tv_overview"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_centerInParent="true"
        android:layout_below="@+id/tv_original_title"
        />

</RelativeLayout>
```

##  3. Incorporar GSON al proyecto mediante Gradle

Realizar el Parsing de un resultado JSON puede resultar complicado si se realiza manualmente, es por esto que la gente de Google creó GSON, la librería para realizar la serialización/desserialización de JSON hacia java, y que actualmente es enormemente utilizada en el ambito profesional.  Puedes aprender más sobre esto en el (repositorio GitHub de GSON)[https://github.com/google/gson] o en la web como el sitio (Leveraging the Gson Library)[https://guides.codepath.com/android/Leveraging-the-Gson-Library] en donde se proporciona guia y herramientas muy utiles para trabajar con esta librería.  

Como se había mencionado anteriormente, el resultado que habiamos obtenido tras la petición HTTP sobre la API de **themoviedb.org**  es de tipo JSON y este resultado debería ser "parseado" y entregado en una vista más completa. Anteriormente, se había creado la clase **UtilsJSON** dentro del paquete denominado ***utils***, ahora ya puedes eliminarla si lo deseas.  Pues en lugar de esto utilizaremos GSON para realizar nuestro Parsing.

Para poder utilizar GSON lo único que debes hacer, gracias a Gradle, es agregar la dependencia *compile 'com.google.code.gson:gson:2.8.0'* en el archivo **build.gradle** (Module:app).  El archivo quedaría mas o menos como sigue:  

```
dependencies {
    compile fileTree(dir: 'libs', include: ['*.jar'])
    androidTestCompile('com.android.support.test.espresso:espresso-core:2.2.2', {
        exclude group: 'com.android.support', module: 'support-annotations'
    })
    compile 'com.android.support:appcompat-v7:25.1.1'
    testCompile 'junit:junit:4.12'

    compile 'com.google.code.gson:gson:2.8.0' //Dependencia agregada
}
```

Recuerda realizar SYNC del proyecto una vez agregada esta línea, para lo que es neceario que tengas conexión a INTERNET pues Gradle se encargará de descargar las librerias necesarias para que GSON funcione adecuadamente.

## 4. Crear las clases Model que soportarán la carga de datos desde JSON a través de GSON
Como pudiste haber leido en la documentación  del (repositorio GitHub de GSON)[https://github.com/google/gson] o en la web del sitio (Leveraging the Gson Library)[https://guides.codepath.com/android/Leveraging-the-Gson-Library] GSON nos permite hacer la traducción, parsing o serialización/deserialización entre el formato JSON y un conjunto de clases java que correspondan al *modelo de datos* capaz de almacenar la información en formato JSON. Es por esto que es requerido crear este conjunto de clases que albergarán el contenido de JSON pero ya en el lado de Java. Una herramienta muy util para generar estas clases del *modelo* la puedes encontrar en el sitio http://www.jsonschema2pojo.org/, esta herramienta genera un código completisimo utilizando anotaciones GSON y generando getters y setters para cada atributo.

No obstante, por motivos de aprendizaje y simplicidad, nosotros crearemos nuestro propio modelo que sea capaz de contener los resultados que nos arroja la API de **themoviedb.org** cuando realizamos un query.  Si pusiste atención, el resultado que nos arroja la API nos entrega un JSON como el siguiente:

```json
{
"page":1,
"results":
	[
		{
		 "poster_path":"\/zjqInUwldOBa0q07fOyohYCWxWX.jpg",
		 "adult":false,
		 "overview":"A tale which follows the comedic and eventful journeys of two fish, the fretful Marlin and his young son Nemo, who are separated from each other in the Great Barrier Reef when Nemo is unexpectedly taken from his home and thrust into a fish tank in a dentist's office overlooking Sydney Harbor. Buoyed by the companionship of a friendly but forgetful fish named Dory, the overly cautious Marlin embarks on a dangerous trek and finds himself the unlikely hero of an epic journey to rescue his son.",
		 "release_date":"2003-05-30",
		 "genre_ids":[16,10751],
		 "id":12,
		 "original_title":"Finding Nemo",
		 "original_language":"en",
		 "title":"Finding Nemo",
		 "backdrop_path":"\/n2vIGWw4ezslXjlP0VNxkp9wqwU.jpg",
		 "popularity":8.573358,
		 "vote_count":4125,
		 "video":false,
		 "vote_average":7.5
		},

		{
		 "poster_path":"\/z09QAf8WbZncbitewNk6lKYMZsh.jpg",
		 "adult":false,
		 "overview":"Dory is reunited with her friends Nemo and Marlin in the search for answers about her past. What can she remember? Who are her parents? And where did she learn to speak Whale?",
		 "release_date":"2016-06-16",
		 "genre_ids":[16,10751],
		 "id":127380,
		 "original_title":"Finding Dory",
		 "original_language":"en",
		 "title":"Finding Dory",
		 "backdrop_path":"\/iWRKYHTFlsrxQtfQqFOQyceL83P.jpg",
		 "popularity":93.678281,
		 "vote_count":2569,
		 "video":false,
		 "vote_average":6.7
		}
	],
"total_results":2,
"total_pages":1}
```  

En el código JSON anterior, arrojado de una búsqueda de "finding+nemo", podemos observar que la API entrega los resultados en función de *páginas* de resultados, y cada página tiene los resultados, "peliculas", como un subconjunto de *results* y cada película tiene sus propios atributos: *poster_path*, *adult*, *overview*, etc. y al final nos arroja el total de resultados y el total de páginas.  Para soportar este modelo en Java deberemos crear un par de clases que colocaremos en un nuevo paquete denominado *model* y serán las siguientes:

Clase Results:
```java
package com.example.android.httpnetworking.model;

import java.util.List;

public class Results {
    public int page;
    public List<Movie> results;
    public int total_results;
    public int total_pages;
}
```  

Clase Movie:
```java
package com.example.android.httpnetworking.model;

import java.util.List;

public class Movie {
    public String poster_path;
    public boolean adult;
    public String overview;
    public String release_date;
    public List<Integer> genre_ids;
    public int id;
    public String original_title;
    public String original_language;
    public String title;
    public String backdrop_path;
    public double popularity;
    public int vote_count;
    public boolean video;
    public double vote_average;
}
```  

Notese que no hacemos uso de anotaciones GSON sino que basta con tener atributos publicos con el nombre de cada uno de los elementos JSON en nuestro resultado a "parsear".

##  5. Crear un Adapter que nos permita popular con Items el ListView a partir del contenido del Model
Una vez realizado el paso anterior deberemos y ya rumbo a la solución final, ahora que ya tenemos el *modelo de datos* que soportará contener nuestros resultados, podemos crear el Adapter que nos permitirá poblar el ListView con los resultados, en este caso utilizaremos un ArrayAdapter, puedes (consultar su documentación para mayor información)[https://developer.android.com/reference/android/widget/ArrayAdapter.html], pero en escencia lo que permite es devolver a un ListView cada uno de los items que contendrá, apegados a un layout (en nuestro caso el item_lista.xml), a partir de una colección de objetos que le sea proporcionada.  Para lograr esto crearemos la clase denominada **MoviesAdapter** en un nuevo paquete denominado *adapters*, como se aprecia a continuación:

```java
package com.example.android.httpnetworking.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.httpnetworking.R;
import com.example.android.httpnetworking.model.Movie;

import java.util.List;

public class MoviesAdapter extends ArrayAdapter{

    public MoviesAdapter(Context context, List objects) {
        super(context,0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Obteniendo una instancia del inflater
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //Salvando la referencia del View de la fila
        View v = convertView;

        //Comprobando si el View no existe
        if (null == convertView) {
            //Si no existe, entonces inflarlo
            v = inflater.inflate(
                    R.layout.item_lista,
                    parent,
                    false);
        }

        //Obteniendo instancias de los elementos
        TextView mPositionTextView = (TextView) v.findViewById(R.id.tv_position);
        TextView mOriginalTitleTextView = (TextView) v.findViewById(R.id.tv_original_title);
        TextView mOverviewTextView = (TextView) v.findViewById(R.id.tv_overview);

        //Obteniendo instancia de la Movie en la posición actual
        Movie item = (Movie) getItem(position);
        mPositionTextView.setText(String.valueOf(position+1));
        mOriginalTitleTextView.setText(item.original_title);
        mOverviewTextView.setText(item.overview);

        return v;
    }
}
```  

## 6. Actualizar la AsyncTask denominada MovieSearchTask para unir todas las piezas.
Estamos a un paso de finalizar, ahora solamente debemos unir las piezas de manera que primero GSON cargue nuestras clase *modelo* "parseando" el resultado JSON, luego alimentar el *Adapter* con el *modelo*  y finalmente indicarle a nuestro *ListView* haga uso del *Adapter*.  Para lograr esto deberemos actualizar el método **onPostExecute** de la inner class *MovieSearchTask* dentro de nuestra clase MainActivity para que ahora luzca como sigue:

```java

...

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

...

```

**Listo, ahora puedes ejecutar y probar la aplicación de inmediato, si cuentas con una conexión a INTERNET**
