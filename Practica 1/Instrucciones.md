#Descripción de la práctica 1 
**Tema de interés: Uso de ContentProviders del sistema**

El propósito de esta practica es que el alumno pueda comprender mejor el concepto de ContentProvider y pueda utilizar un ContentProvider existente en el sistema.  El alumnó creará una aplicación que muestre el listado de Contactos en el dispositivo, su nombre, número telefónico y tipo de cuenta (google, whatsapp, sim, etc.) a la que corresponde el contacto, comprendiendo con esto adicionalmente la función de los Contracts en el contexto de ContentProviders.

##Instrucciones:
Utilizar el código fuente base y modificarlo para realizar lo siguiente:

 1. Configurar los views necesarios para mostrar un listado de contenido 
 2. Cargar el ContentProvider de Contactos mediante un Content Resolver
 3. Agregar los permisos para el uso del ContentProvider de contactos en el Manifest 

A continuación algo de código y su explicación para cada paso de la práctica:

## 1. Configurar los views necesarios para mostrar un listado de contenido. 

Para lograr este propósito el estudiante debe modificar el layout de la aplicación editando el archivo activity_main.xml, en este, trabajando sobre un LinearLayout, deberá agregar un Button que permitirá solicitar el listado de Contactos a un ContentProvider y estos serán cargados en un TextView contenido en un ScrollView de manera que sea posible agregar contenido mediante saltos de linea y tengamos un componente visual para cargar listado de contenido.

El código final del layout debería lucir mas o menos como el que sigue:

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout android:orientation="vertical" xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/activity_main"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:paddingBottom="@dimen/activity_vertical_margin"
    android:paddingLeft="@dimen/activity_horizontal_margin"
    android:paddingRight="@dimen/activity_horizontal_margin"
    android:paddingTop="@dimen/activity_vertical_margin"
    tools:context="com.example.lenovo.ejercicio1.MainActivity">

    <Button
        android:id="@+id/bt_cargar_contactos"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Cargar Contactos"/>

    <ScrollView
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
        <TextView
            android:id="@+id/tv_contactos"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:padding="8dp"
            android:textSize="18sp"
            android:text="Contacto1\n\nContacto2\n\nContacto3\n\nContactoN" />
    </ScrollView>

</LinearLayout>
```

## 2. Cargar el ContentProvider de Contactos mediante un Content Resolver

Una vez que se tenga un botón para disparar la carga de contactos, y un listado donde mostrar los mismos, lo que sigue es programar tales eventos para que esto ocurra conforme lo contemplado, para esto se editará la clase MainActivity realizando los siguientes 3 cambios:

```java
...

public class MainActivity extends AppCompatActivity {

	//1. Definir las variables necesarias que permitiran acceder a los views creados.
    private TextView mContactos;
    private Button mCargarContactos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		//2. Cargar una referencia a los views mediante el metodo findViewById
        mContactos = (TextView) findViewById(R.id.tv_contactos);
        mCargarContactos = (Button) findViewById(R.id.bt_cargar_contactos);

		//3. Configurar el evento Click del Boton de cargar contactos para que...
        mCargarContactos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContactos.setText(""); //Limpie el contenido previo del TextView que mostrará el listado...
                int counter = 0; //Reinicie el contador que permitira saber cuantos contactos se ha recuperado...
                ContentResolver resolver = getContentResolver(); //Obtener una instancia del ContenResolver...
                
				//Utilizar el ContenResolver para ejecutar un query sobre el ContentProvider de Contactos,
				//este ContentProvider se encuentra ubicado en la URI señalada en su respectiva clase de
				//contrato y se piden los datos sin establecer algun filtrado, ordenación u otro. 
				Cursor contactos = resolver.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,null,null, null);

				//Iterar sobre el cursor recuperado tras la consulta para...
                while (contactos.moveToNext())
                {
					//Recuperar nombre, numero telefónico y tipo de cuenta.
                    String nombre = contactos.getString(
                            contactos.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String numeroTel = contactos.getString(
                            contactos.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String tipoCuenta = contactos.getString(
                            contactos.getColumnIndex(ContactsContract.RawContacts.ACCOUNT_TYPE));

					//Agregar estos datos al TextView que mostrará el listado de contenido, a la vez que se incrementa el contador de contactos recuperados.
                    mContactos.append(++counter + ". " +  nombre
                            + "(" + numeroTel + ")\n" + tipoCuenta + "\n\n");
                }
                contactos.close(); //Cerrar el cursor una vez que se ha utilizado.
				
				//Mostrar un mensaje indicando la cantidad total de contactos recuperados.
                Toast.makeText(getApplicationContext(),
                        counter + " telefonos recuperados",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}

```

## 3. Agregar los permisos para el uso del ContentProvider de contactos en el Manifest 

Antes de proceder a probar el funcionamiento de la aplicacion, es necesario agregar en el Manifest de la aplicación el permiso correspondiente al uso de datos de contactos (en este caso solamente para leer datos), siendo la línea de código a agregar la siguiente:   

```xml
...
<manifest xmlns:android="http://schemas.android.com/apk/res/android" package="com.example.lenovo.ejercicio1">

    <!-- Permiso para leer datos de Contactos del dispositivo -->
    <uses-permission android:name="android.permission.READ_CONTACTS" />

    <application
	...

```

Listo a probar la aplicación.

Eso es todo amigos.
