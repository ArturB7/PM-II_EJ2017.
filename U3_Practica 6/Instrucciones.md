# Descripción de la Práctica 6 de la Unidad 3 (Telefonía  y redes)  (a.k.a. Práctica 11)
**Tema de interés: Procesamiento de imagenes desde HTTP**

El propósito de esta practica es que el alumno conozca la manera de trabajar con datos de Internet correspondientes a *imagenes* tras haber realizando una petición (request) HTTP para trabajar con los datos de respuesta desde su aplicación.  El objetivo es continuar el desarrollo de la aplicación de búsquedas en una base de datos de peliculas https://www.themoviedb.org, ahora con la facultad de cargar las imagenes correspondientes al poster de las peliculas encontradas.

![Pantalla de la Aplicación](Instrucciones_img/app.png?raw=true)

Prototipo de la App a desarrollar


## Instrucciones de la práctica:
Obtener el código fuente base y modificarlo para:

 1. Redefinir el layout del item para el ListView
 2. Crear una inner class MoviePosterDownloadTask como AsyncTask del MovieAdapter
 3. Actualizar el MovieAdapter para que utilice el MoviePosterDownloadTask y el LruCache

A continuación algo de código y su explicación para cada paso de la práctica:

##  1. Redefinir el layout del item para el ListView

Para lograr obtener la GUI requerida para la aplicación, a partir del código base hay que editar el layout correspondiente al archivo item_lista.xml que es la plantilla con el que se cargan los elementos dentro del del ListView. Si lo recurdas no teniamos un **ImageView**, ahora hay que agregarlo para poder mostrar nuestro poster de la película.  El código a escribir es mas o menos el siguiente:

![](Instrucciones_img/item_lista_layout.png?raw=true)

##  2. Crear una inner class MoviePosterDownloadTask como AsyncTask del MovieAdapter

Si recuerdas, todas las operaciones que impliquen trafico de Internet en Android, deben ser realizadas en segundo plano.  Un caso especial y muy frecuente en ambiente de producción lo tenemos con la imagen correspondiente al poster de cada película.  Nuestro objeto JSON, parseado en la práctica anterior, contiene un atributo denominado **poster_path** este contiene un valor que representa al *nombre del archivo* que esta ubicado en alguna parte de la nube y que es el Poster de la película. Por ejemplo: */8uO0gUM8aNqYLs1OsTBQiXu0fEv.jpg*

Este nombre no nos dice mucho para poder encontrar el poster de la pelicula en Internet, a no ser de que tengamos la URL base en la que residan todos los posters de las películas.  Es entonces donde la gente de **www.themoviedb** proporcionan como parte de la (documentación de su API una especificación sobre como recuperar los posters de cada película)[https://developers.themoviedb.org/3/getting-started/images], que en resumen dicta lo siguiente:

- La URL base para obtener películas es: https://image.tmdb.org/t/p/[file_size] /[file_path] en donde puedes ver claramente que hacen falta dos argumentos *file_size* y *file_path*.  
- El *file_ path* ya lo tenemos en nuestro objeto parseado.
- Pero hay que saber los posibles valores para file_size o tamaño de la imagen.  Estos se encuentran definidos en un JSON de configuración, a saber en una URL a la que solo puedes acceder con tu API KEY, por ejemplo: https://api.themoviedb.org/3/configuration?api_key=40f11090bae0134d6cbf32392c2d9697 pero que en resumen son: "w92","w154","w185","w342","w500","w780" y "original".

En la práctica utilizaremos el tamaño w154.

Otra cosa especial que se esta utilizando en esta ocación es una clase denominada **LruCache**, que nos ayudará a mantener las imagenes descargadas de Internet en un cache de la aplicación para no cargarlas desde Internet cada vez que sean requeridas, sino que puedan tomarse desde el almacenamiento inmediato de memoria.  Este es un enfoque tradicional y cada vez menos utilizado, pues actualmente google promueve distintas librerias para el cargado de imagenes y uso de cache.  Puedes encontrar mas información sobre (LruCache en su documentación oficial)[https://developer.android.com/reference/android/util/LruCache.html] o sobre las (recomendaciones de Google para cargar y cachear imagenes aquí)[https://developer.android.com/topic/performance/graphics/cache-bitmap.html].

Con esto en mente, el código a programár enseguida basicamente hay que agregarlo dentro del cuerpo de la clase **MoviesAdapter**, tendremos una nueva variable de clase denominada **mMemoryCache** de tipo *LruCache* y una nueva Inner class denominada **MoviePosterDownloadTask**, esta clase nos apoyará para descargar las imagenes de Internet y cargarlas al cache cuando sea requerido y entonces el código a agregar dentro del cuerpo de la clase *MoviesAdapter* es el siguiente:

![](Instrucciones_img/MoviesPosterDownloadTask.png?raw=true)

IMPORTANTE: Para el LruCache se hace uso del import *android.support.v4.util.LruCache*.

## 3. Actualizar el MoviesAdapter para que utilice el MoviePosterDownloadTask y el LruCache

Finalmente, lo único que hay que hacer para que todo tenga sentido es actualizar el método *getView* de nuestra clase **MoviesAdapter** para que busque en el cache de posters el poster de cada película y de no encontrarlo, le encargue el trabajo de descargarla a una instancia de la clase **MoviePosterDownloadTask** para que esta realice el trabajo y coloque la imagen desacargada en el LruCache de la aplicación.

El codigo actualizado del método *getView* de nuestra clase **MoviesAdapter** es mas o menos el siguiente:

![](Instrucciones_img/MoviesAdapter.png?raw=true)

**Listo, ahora puedes ejecutar y probar la aplicación de inmediato, si cuentas con una conexión a INTERNET**
