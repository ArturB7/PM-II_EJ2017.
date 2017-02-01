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

    /**
     * El método QUERY se hace cargo de las peticiones query de los clientes. Se utilizará este método para 
     * hacer query de todos los estudiantes así como de algun estudiante con ID particular (número de control)
     * correspondiente a las URIs definidas para este propósito.
     *
     * @param uri           La URI para hacer query
     * @param projection    La lista de columnas que se colocarán en el cursor. Si es null, todas las columnas son incluidas.
     * @param selection     Criterio de selección para filtrar registros. Si es null, todos los registros son incluidos. 
     * @param selectionArgs Puedes incluir ?s en el selection, y serán remplazado por los valores en selectionArgs en el órden de aparición.
     * @param sortOrder     Como los registros del cursor deberían ser ordenados.
     * @return Un Cursorque contiene el resultado del query.
     */
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor;

        /*
         * Mediante este switch, dada una URI, se determina el tipo de petición que el query esta haciendo.
         */
        switch (sUriMatcher.match(uri)) {
            /*
             * Cuando el metodo match del sUriMatcher es invocado con una URI que luce así:
             *
             *      content://com.example.lenovo.students_contenproviderfoundation/students/147221
             *
             * el metodo match del sUriMatcher devolverá el código CODE_STUDENTS_WITH_ID que indica 
             * que debemos devolver el estudiante con el ID señalado al final de la URI.
             * En este caso particular el cursor devolberá únicamente un solo registro correspondiente
             * al estudiante con tal ID.
             */
            case CODE_STUDENTS_WITH_ID: {
                /*
                 * Para determinar el ID en el URI, se debe buscar el segmento final del path
                 * En el comentario de arriba el último segmento del path es 147221 y representa
                 * el número de control del estudiante.
                 */
                String numControl = uri.getLastPathSegment();

                /*
                 * El método query acepta un arreglo de string de argumentos, se aprovechará esta
                 * característica para agregar un argumento que permita realizar el filtrado.
                 */
                String[] selectionArguments = new String[]{numControl};

                cursor = db.query(
                        /* Tabla que vamos a consultar */
                        StudentsContract.StudentEntry.TABLE_NAME,
                        /*
                         * Una projection que designa las columnas que queremos devolver en el Cursor.
                         * Si es null devuelve todas las columnas, aunque es buena práctica indicar 
                         * cuales son aquellas que realmente necesitamos.
                         */
                        projection,
                        /*
                         * Para especificar el registro que queremos filtrar en el cursor utilizamos
                         * un signo de interrogación que será remplazado por el valor indicado en 
                         * selectionArguments, estos argumentos son insertados en la sentencia SQL 
                         * por SQLite tras bambalinas.
                         */
                        StudentsContract.StudentEntry.COLUMN_ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);

                break;
            }

            /*
             * Cuando el método match del sUriMatcher sea invocado con una URI que luzca EXACTAMENTE como esta
             *
             *      content://com.example.lenovo.students_contenproviderfoundation/students/
             *
             * El método match del sUriMatcher devolverá el código CODE_STUDENTS que indica que debemos
             * devolver un cursor con todos los estudiantes en nuestra tabla de students.
             *
             */
            case CODE_STUDENTS: {
                cursor = db.query(
                        StudentsContract.StudentEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("URI desconocida: " + uri);
        }

        //Call setNotificationUri on the cursor and then return the cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    /**
     * Elimina los datos dada una URI con argumentos opcionales para precisar el borrado selectivo.
     *
     * @param uri           El URI a alterar.
     * @param selection     Restricciones opcionales para aplicar durante el borrado de registros.
     * @param selectionArgs Utilizada en conjunto con la sentencia selection.
     * @return La cantidad de registros eliminados
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        throw new RuntimeException("You need to implement this method!");
    }

    /**
     * En esta app, no se utiliza este método.
     * Normalmente, este método maneja peticiones para conocer el MIME type
     * de los datos en una URI específica.  Por ejemplo si nuestra app provee imagenes
     * en una URI particular entonces deberíamos devolver image URI desde este metodo.
     *
     * @param uri El URI a consultar.
     * @return nada en esta app, pero normalmente una cadena MIME type, o null si no hay MIME type.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("We are not implementing getType in this app.");
    }

    /**
     * Método Insert en este ContentProvider.
     *
     * @param uri    El URI para la petición de Insert.
     * @param values Un conjunto de pares column_name/value para agregar en la base de datos.
     * @return nada en esta app, pero normalmente el URI del nuevo item insertado.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new RuntimeException(
                "You need to implement this method.");
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException("You need to implement this method.");
    }

    /**
     * Este método no necesita ser invocado. Esta diseñado para asistir a 
     * los frameworks de testing. Puedes aprender mas en:
     * http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
     */
    @Override
    @TargetApi(11)
    public void shutdown() {
        db.close();
        super.shutdown();
    }

}

```
##3. Registrar Content Provider en el Manifest
Para que el content provider sea valido debe registrarse en el archivo manifest agregando unas líneas mas o menos como las que siguen:

```xml
...
        <provider
            android:name=".data.StudentsContentProvider"
            android:authorities="@string/content_authority"
            android:exported="false"/>
    </application>
...
```

##4.Utilizar el Content Provider en lugar de acceder directamente a la BD en la app
Para probar rápidamente el funcionamiento del conten provider sin tener que crear una aplicacion adicional, la **MainActivity** de nuestra App actual puede ser modificado de manera que no utilice directamente SQLITE sino que realice las operaciones mediante el content provider, al menos las correspondientes a VIEW y VIEW ALL.  Para lograr esto se deberá modificar el código de la clase **MainActivity** mas o menos así:

Para el caso: if(view== mViewButton)
```java
    ...
    //Cursor c=db.rawQuery("SELECT * FROM student WHERE num_control='"+ mNoControlEditText.getText()+"'", null);
    ContentResolver resolver = getContentResolver();

    Cursor c = resolver.query(
    StudentsContract.StudentEntry.buildStudentsUriWithId(mNoControlEditText.getText().toString()),null,null,null,null);
    ...
```

Para el caso: if(view== mViewAllButton)
```java
    ...
    //Cursor c=db.rawQuery("SELECT * FROM student", null);

    ContentResolver resolver = getContentResolver();
    Cursor c = resolver.query(StudentsContract.StudentEntry.CONTENT_URI,null,null,null,null);
    ...
```

##(opcional) Utilizar el content provider desde otra aplicación.

Opcionalmente y por algunos puntos extras de aprendizaje podría crear una aplicación externa y utilizar nuestro nuevo content provider para hacer uso de los datos.

Eso es todo amigos.
