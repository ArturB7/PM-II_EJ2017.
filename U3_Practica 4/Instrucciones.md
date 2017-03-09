# Descripción de la Práctica 4 de la Unidad 3 (Telefonía  y redes)  (a.k.a. Práctica 11)
**Tema de interés: Petciones HTTP sobre Internet**

El propósito de esta practica es que el alumno conozca la manera de trabajar con datos de Internet realizando una petición (request) HTTP para trabajar con los datos de respuesta desde su aplicación.  El objetivo es comenzar a desarrollar una breve aplicación que tenga la capacidad de realizar búsquedas en una base de datos de peliculas, en este caso: https://www.themoviedb.org, quienes proporcionan una API REST para el uso de sus datos mediante peticiones HTTP entregando una respuesta en formato JSON.

![Pantalla de la Aplicación](Instrucciones_img/app.png?raw=true)

Prototipo de la App a desarrollar


## Instrucciones de la práctica:
Obtener el código fuente base y modificarlo para:

 1. Definir el layout y referencias a las vistas para la MainActivity
 2. Crear la clase para manejo de HTTP denominada HttpRetriever
 3. Crear la clase auxiliar para "identar" código JSON denominada UtilsJSON
 4. Crear la inner class MovieSearchTask como una AsyncTask
 5. Agregar el código necesario para invocar la búsqueda desde la MainActivity
 6. Agregar los permisos necesarios

A continuación algo de código y su explicación para cada paso de la práctica:

##  1. Definir el layout y referencias a las vistas para la MainActivity

Para lograr obtener la GUI requerida para la aplicación, a partir del código base hay que editar el layout correspondiente al MainActivity, es decir el archivo activity_main.xml, para que incluyas un los elementos GUI requeridos.  El código a escribir es mas o menos el siguiente:

![](Instrucciones_img/MainActivity_Layout.png?raw=true)

Posteriormente, ya dentro de la clase MainActivity, deberás agregar las variables para referenciar al los elementos GUI utilizando el método **findViewById** como luce en el siguiente fragmento de código:

```java
...

private EditText mSearchEditText;
private Button mSearchButton;
private TextView mSearchResultTextView;

@Override
public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    mSearchEditText = (EditText) findViewById(R.id.et_search);
    mSearchButton = (Button) findViewById(R.id.bt_search);
    mSearchResultTextView = (TextView) findViewById(R.id.tv_search_result);

}

...
```
Enseguida hay que aprovechar que tenemos la clase MainActivity en edición, para crear un método que estaremos utilizando más adelante para facilitar la presentación de Toast en pantalla.  Hay que agregarlo como método de la MainActivity y el código es el siguiente:

![](Instrucciones_img/LongToast.png?raw=true)

##  2. Crear la clase para manejo de HTTP denominada HttpRetriever

Una vez realizado el paso anterior deberás proceder a crear una nueva clase que tenga el propósito y responsabilidad de agrupar los métodos que nos ayudarán a obtener resultados desde Internet especificando direcciones URL.  Para comenzar vamos a crear una clase nueva denominada **HttpRetriever** dentro de un nuevo paquete denominado ***http***  y esto se logrará escribiendo el código que se puede apreciar en el código a continuación:

![](Instrucciones_img/HttpRetriever.png?raw=true)

## 3. Crear la clase auxiliar para "identar" código JSON denominada UtilsJSON

Como se había mencionado anteriormente, las peticiones HTTP con las que estaríamos lidiando nos entregarán un resultado en formato JSON.  Cabe mencionar que el resultado es entregado en un formato lineal (sin identación (tabulación) y saltos de línea), y como en por el momento se desea presentar tal cual el resultado JSON en un TextView de resultados, el método que crearemos en esta clase podriamos considerarlo opcional, pues por el momento solamente nos apoyará a tener una vista "más legible" de JSON pero en realidad este tipo de resultado debería ser "parseado" y entregado en una vista más completa que se desarrollará más adelante. Por el momento para lograr esto crea una nueva clase denominada **UtilsJSON** dentro de un nuevo paquete denominado ***utils*** mediante el código siguiente:

```java
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
```

## 4. Crear la inner class MovieSearchTask como una AsyncTask
Ahora que ya tenemos las clases que nos apoyarán con las peticiones HTTP y con el tratamiento del resultado en formato JSON, se debe proceder a desarrollar el propósito de la práctica, relizar búsquedas en la base de datos de películas **themoviedb.org**, para esto debemos conocer un poco la API de esta base de datos, sobre todo lo referente a la búsqueda de datos y esto lo puedes consultar en [su documentación y ejemplos](https://developers.themoviedb.org/3/getting-started/search-and-query-for-details) en donde podrás corroborar que para realizar una consulta hacia **themoviedb.org** basta realizar una petición HTTP pasando los argumentos necesarios y **themoviedb.org** nos entregará el resultado esperado.  

Es **relevante** notar que para realizar una consulta utilizando esta API es requerida una **API KEY**, y esta debería ser tramitada personalmente por ti registrandote en el sitio de **themoviedb.org** y solicitandola para desarrollar una aplicación móvil, no obstante por el momento (y mientras funcione) puedes utilizar la que se ve en el código.  

Antes de comenzar a codificar, por otra parte, es muy **importante** mencionar que en Android resulta extremadamente conveniente manejar cualquier peticion HTTP fuera del hilo principal de la aplicación, pues dependiendo de la velocidad de la red podremos acceder rápida o muy lentamente al contenido deseado y esto puede provocar un rendimiento variable de nuestra aplicación e incluso que esta pueda quedar bloqueada. Es por esto, que se sugiere encarecidamente realizar cualquier operación de este tipo como una tarea en background.  

En este caso se utilizará una tarea en background creada mediante una ***AsyncTask*** [ver documentación](https://developer.android.com/reference/android/os/AsyncTask.html) que pueda ejecutarse desde nuestra MainActivity y a la vez configurar los elementos GUI correspondientemente para que sean deshabilitados mientras se realiza la búsqueda y sean rehabilitados una vez que se tenga un resultado.   Para lograr esto se deberá crear una ***clase interna*** dentro de la clase **MainActivity** a la que denominaremos **MovieSearchTask** y que será la encargada de realizar las peticiones HTTP mediante el **HttpRetriever** anteriormente creado. El código que se puede apreciar a continuación:

![](Instrucciones_img/MovieSearchTask.png?raw=true)

## 5. Agregar el código necesario para invocar la búsqueda desde la MainActivity
Una vez realizado el paso anterior deberemos poner a trabajar la MovieSearchTask en el momento oportuno, esto es cuando el usuario presione el botón buscar.  Es entonces el momento de pasarle como argumento lo que el usuario escriba en pantalla y delegarle el trabajo para que realice la búsqueda.  Esto se logrará escribiendo el código que se puede apreciar en el código a continuación:

![](Instrucciones_img/MainActivity_Buscar.png?raw=true)

## 6. Agregar los permisos necesarios
Finalmente y antes de terminar, es muy **importente** agregar el permiso para uso de INTERNET en nuestro archivo de manifiesto.  El permiso es el siguiente:

```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

**Listo, ahora puedes ejecutar y probar la aplicación de inmediato, si cuentas con una conexión a INTERNET**

![Pantalla de la Aplicación](Instrucciones_img/app_final.png?raw=true)
