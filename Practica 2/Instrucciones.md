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

