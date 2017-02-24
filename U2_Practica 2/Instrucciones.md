# Descripción de la Práctica 2 de la Unidad 2 (BroadcastReceivers)  (a.k.a. Práctica 7)
**Tema de interés: Uso de un BroadcastReceiver para escuchar el estatus de las llamadas**

El propósito de esta practica es que el alumno aplique nuevamente el concepto de **Broadcast Receiver** pero ahora con el propósito de llevar un registro (Log) de eventos de las llamadas telefónicas de su dispositivo móvil. El concepto de BroadcastReceiver ya se había tratado en la práctica anterio pero puedes consultarlo nuevamente en la [documentación de android sobre Broadcasts](https://developer.android.com/guide/components/broadcasts.html).

El objetivo de la práctica es lograr que se desarrolle una aplicación que tenga la capacidad de llevar un Log de las llamadas telefónicas salientes, entrantes y perdidas, registrando la fecha y hora en que ocurren los eventos y los números telefónicos correspondientes.  Para lograr esto se realizará la configuración y uso de un *Broadcast Receiver* **estatico**.

![Pantalla de la Aplicación](Instrucciones_img/app.jpg?raw=true)

Prototipo de la App a desarrollar


## Instrucciones de la práctica:
Obtener el código fuente base y modificarlo para:

 1. Definir el layout y referencias a las vistas para la MainActivity
 2. Crear la clase abstracta PhoneCallsReceiver que herede de BroadcastReceiver
 3. Definir el IntentFilter atento al estatus de llamadas del dispositivo
 4. Instanciar el BroadcastReceiver
 5. Registrar el Broadcast Receiver con el Intent Filter definido

A continuación algo de código y su explicación para cada paso de la práctica:

## 1. Definir el layout y referencias a las vistas para la MainActivity

Para lograr obtener la GUI requerida para la aplicación, a partir del código base hay que editar el layout correspondiente al MainActivity, es decir el archivo activity_main.xml, para que incluyas un TextView denominado "tv_call_log" dentro de un ScrollView que a su vez esta contenido en un LinearLayout de orientación vertical.  El código a escribir es mas o menos el siguiente:

![](Instrucciones_img/MainActivity_Layout.jpg?raw=true)

Posteriormente, ya dentro de la clase MainActivity, deberás agregar una variable para referenciar al TextView creado y cargarla mediante el metodo **findViewById** como luce en el siguiente fragmento de código:

```java
...

public class MainActivity extends AppCompatActivity {

    TextView mCallLogTextView; //Variable para referencia al TextView de Log de Llamadas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Obtener referencia al TextView CallLog
        mCallLogTextView = (TextView) findViewById(R.id.tv_call_log);
    }
}
```

## 2. Crear la clase abstracta PhoneCallsReceiver que herede de BroadcastReceiver

Una vez realizado el paso anterior, ahora si procedemos a elaborar la estructura de nuestro **BroadcastReceiver**. Para esto deberemos crear una clase denominada **PhoneCallsReceiver** que heredará de la clase **BroadcastReceiver** y que implementará toda la funcionalidad que deberá ser ejecutada cuando se den las "señales" pertinentes para que este entre en acción. **Nótese** que esta es una clase ***abstracta*** con la intensión de dejar varios **"métodos pendientes"** (*abstractos*) que puedan ser implementados al momento de crear una instancia de la clase más adelante por quien vaya a utilizarla. Por el momento, hay que crear esta clase en un nuevo paquete denominado *receivers*, siguiendo el código siguiente:

![](Instrucciones_img/PhoneCallsReceiver.jpg?raw=true)

## 3. Definir el Intent Filter atento al estatus de llamadas del dispositivo

Antes de poder utilizar el **BroadcastReceiver** recien creado, debemos primeramente crear el **IntentFilter** que colaborará con este para darle aviso cuando ocurran los cambios de estado esperados. En este caso el **IntentFilter** deberá estar atento a recibir notificación cuando ocurran cambios de estado respecto a las llamadas telefónicas entrantes (dados por ***TelephonyManager.ACTION_PHONE_STATE_CHANGED***) y llamadas salientes (dado por ***Intent.ACTION_NEW_OUTGOING_CALL***), de manera que el código a agregar sería sobre la clase MainActivity para crear el **IntentFilter** quedaría mas o menos como sigue:  

![](Instrucciones_img/MainActivity_IntentFilter.jpg?raw=true)

Para que el IntentFilter pueda recibir las notificaciones para las que fue suscrito, es necesario otorgar permisos a la aplicación y para esto debe ser modificado el archivo del *manifiesto* agregando los permisos requeridos como se ve en el siguiente código:

![](Instrucciones_img/Manifest.jpg?raw=true)

## 4. Instanciar el BroadcastReceiver para indicar las acciones a realizar

Ahora que ya se tiene el IntentFilter preparado, el siguiente paso es crear la instancia nuestra clase **PhoneCallsReceiver** dentro de nuestra MainActivity e implementar los **"métodos pendientes"** (*abstractos*) para indicar lo que debe hacer cuando estos ocurran.  Para lograr esto, el código sería mas o menos el siguiente:

![](Instrucciones_img/MainActivity.jpg?raw=true)

## 5. Registrar el Broadcast Receiver con el Intent Filter definido

Una vez que se han creado tanto el IntentFilter como el BroadcastReceiver, debemos ponerlos a trabajar en conjunto registrandolos en la aplicación, que como serán estáticos podríamos hacerlo en el Manifiesto o directamente en el método onCreate de la clase MainActivity.  De hecho en la última parte del código del paso anterior, ya hiciste este registro y puedes apreciarlo si notas la línea de código que corresponde a:

```java
...
//Registrar el BroadcastReceiver en este caso como un BroadcastReceiver estatico.
registerReceiver(phoneCallBroadcastReceiver,iFilter);
...
```

**Listo, ahora puedes ejecutar y probar la aplicación de inmediato** iniciandola y llamando o pidiendo que te llamen para observar los resultados.

Por el momento, eso es todo amigos.
