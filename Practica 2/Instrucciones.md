#Descripción de la práctica

El propósito de esta practica es que el alumno pueda crear su propio Content Provider a partir de una aplicación básica con persistencia en base de datos.

##Instrucciones:
Hacer fork de la carpeta Students_ContenProviderFoundation que contiene el código fuente base y modificarlo para:

 1. Crear clase Contract
 2. Crear clase ContentProvider
 3. Registrar Content Provider en Manifest
 4. Utilizar el Content Provider en lugar de acceder directamente a la BD en la app
 5. (opcional) Utilizar el content provider desde otra aplicación.

A continuación algo de código y su explicación para cada paso de la práctica:

##1. Crear clase Contract. 
Para lograr este propósito el estudiante debe crear una clase denominada **StudentsContract** preferentemente en un nuevo paquete denominado **data** dentro del paquete principal **com.example.lenovo.students_contenproviderfoundation**.  El código a agregar es mas o menos el siguiente:

```java
package com.example.lenovo.students_contenproviderfoundation.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Define el nombre de la tabla y columnas de la base de datos de estudiates.  Esta clase no es 
 * estrictamente necesaria pero facilita las cosas.
 */
public class StudentsContract {

    /*
     * El "Content authority" es el nombre para el nombre completo para el content provide, es similar a la
     * relacion entre el nombre de dominio y su sitio web.  Una cadena conveniente que puede utilizarse para el 
     * content autority es el nombre del paquete de la aplicacón que garantiza ser único en la Play Store.
     */
    public static final String CONTENT_AUTHORITY = "com.example.lenovo.students_contenproviderfoundation";

    /*
     * Usa CONTENT_AUTHORITY para crear la base de todas las URI's que las Apps utilizarán para contactar
     * con el content provider de esta App.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
     * Paths posibles que pueden ser agregados al BASE_CONTENT_URI para formar un URI's valido que la App
     * pueda manejar.      
     */
    public static final String PATH_STUDENTS = "students";

    /* Inner class que define los contenidos de la tabla student */
    public static final class StudentEntry implements BaseColumns {

        /* El CONTENT_URI base es utilizado para hacer query de la tabla de students en el content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_STUDENTS)
                .build();

        /* Utilizada internamente para saber el nombre de la tabla de estudiantes*/
        public static final String TABLE_NAME = "student";

        /* Representa el campo que será considarado como ID */
        public static final String COLUMN_ID = "num_control";

        /* Constantes para identificar las otras columnas*/
        public static final String COLUMN_NOMBRE = "nombre";
        public static final String COLUMN_PUNTOS_EXTRA = "puntos_extra";

        /**
         * Este método construye una URI que agraga el numero de control del estudiante al final del URI path.
         * Este es utilizado para hacer query  sobre un estudiante con un ID particular.
         *
         * @param num_control El numero de control del estudiante
         * @return Uri para hacer query de un estudiante en particular
         */
        public static Uri buildStudentsUriWithId(String num_control) {
            return CONTENT_URI.buildUpon()
                    .appendPath(num_control)
                    .build();
        }

    }
}
```

##2. Crear clase ContentProvider

De  manera semejante al paso anterior, se debe crear una clase denominada **StudentsContentProvider** en el nuevo paquete anteriormente creado denominado **data** dentro del paquete principal **com.example.lenovo.students_contenproviderfoundation**.  El código a agregar es mas o menos el siguiente:

```java
package com.example.lenovo.students_contenproviderfoundation.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

public class StudentsContentProvider extends ContentProvider {

    /* Estas constantes enteras sirven para identificar las URIs que este ContentProvider puede manejar
     * Estas serán utilizadas para hacer match de las URIs con los datos que corresponden. Se tomará
     * ventaja de la clase UriMatcher para hacer el proceso de matching mucho mas sencillo en lugar de hacerlo
     * a mano mediante expresiones regulares.  Es importante como desarrollador no volver a inventar la rueda
     * el UriMatcher ya hace bien el trabajo que te ahorrará invertir esfuerzos en expresiones regulares. 
     */
    public static final int CODE_STUDENTS = 100;
    public static final int CODE_STUDENTS_WITH_ID = 101;

    /*
     * El URI Matcher utilizado por este content provider. La "s" al inicio del nombre de la variable
     * significa que este UriMatcher es un miembro estatico y es una convención común de google para Android.
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    /*
     * Esta variable permitirá mantener el acceso a la base de datos de la aplicación cuando así 
     * sea requerido al momento de trabajar con los datos.
     */
    private SQLiteDatabase db;

    /**
     * Crea el UriMatcher que hará match para cada URI correspondiente a las constantes CODE_STUDENTS 
     * y CODE_STUDENTS_WITH_ID definidas arriba.
     *
     * @return Un UriMatcher que hace match correctamente con las constantes CODE_STUDENTS y CODE_STUDENTS_WITH_ID
     */
    public static UriMatcher buildUriMatcher() {

        /*
         * Todos los paths agregados a el UriMatcher tienen un codigo de retorno cuando el match ocurre.
         * El código que es pasado al constructor del UriMatcher aquí representa el código de retorno 
         *para la URI raíz.  Es común utilizar NO_MATCH como el código para este caso.
         */
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = StudentsContract.CONTENT_AUTHORITY;

        /*
         * Para cada URI se requiere crear el codigo correspondiente. Preferentemente estos son
         * campos constantes en la clase, así que puedes utilizarlos a lo ancho y largo de la clase
         * y estos no cambiarán. En este ejemplo, se utilizan solamente CODE_STUDENTS y CODE_STUDENTS_WITH_ID.
         */
         
        /* Aquí se agrega la URI fija para consultar todos los estudiantes que es:
         * content://com.example.lenovo.students_contenproviderfoundation/students/
         * esto le indica al UriMatcher que si se envía ese path el matcher debe 
         * devolver el código correspondiente a CODE_STUDENTS
         */
        matcher.addURI(authority, StudentsContract.PATH_STUDENTS, CODE_STUDENTS);

        /*
         * Acá se agrega la URI que permite consultar un estudiante en particular por su ID y
         * que podría lucir algo así como:
         * content://com.example.lenovo.students_contenproviderfoundation/students/147221
         * Donde el "/#" indica al UriMatcher que si al PATH_STUDENTS le sigue CUALQUIER numero,
         * debería devolver el código de: CODE_STUDENTS_WITH_ID
         */
        matcher.addURI(authority, StudentsContract.PATH_STUDENTS + "/#", CODE_STUDENTS_WITH_ID);

        return matcher;
    }

    /**
     * En el método onCreate, se inicializa nuestro content provider. Este metodo es invocado para 
     * todos los content providers registrados en el hilo principal de la aplicación una vez que 
     * la aplicación es lanzada, por lo que no debería desempeñar operaciones largas o que puedan
     * retrasen el inicio de la aplicación.
     *
     * Inicializaciones no triviales (como actualizar o escanear
     * de bases de datos) deberían esperar hasta que el content provider sea utilizado.
     *
     * Una inicialización diferida mantendrá el inicio de la aplicación rápido y evitará 
     * trabajo innecesario si el content provider no es utilizado, o  la base de datos arroja un
     * un error tal como disco lleno evitando que la aplicación se inicie adecuadamente.
     *
     * @return true si el proveedor ha sido exitosamente cargado, false si no
     */
    @Override
    public boolean onCreate() {
        /*
         * Como se comentó anteriormente onCreate is ejecutado en el hilo principal, y
         * se deben evitanr operaciones que puedan causar un lag en nuestra app. El constructir de SQLITE 
         * es muy liviano y podemos colocar su inicialización aquí.
         */
        db = getContext().openOrCreateDatabase("StudentDB", Context.MODE_PRIVATE, null);

        return true;
    }



```
