# Descripción de la Práctica 2 de la Unidad 3 (Telefonía  y redes)  (a.k.a. Práctica 9)
**Tema de interés: Realización de llamadas telefónicas**

El propósito de esta practica es que el alumno conozca las distinas maneras de realizar llamadas telefónicas desde su aplicación.  El objetivo de la práctica es lograr que se desarrolle una breve aplicación que tenga la capacidad de realizar una llamada utilizando:
- Intents de tipo CALL action
- Intents de tipo DIAL action

![Pantalla de la Aplicación](Instrucciones_img/app.jpg?raw=true)

Prototipo de la App a desarrollar


## Instrucciones de la práctica:
Obtener el código fuente base y modificarlo para:

 1. Definir el layout y referencias a las vistas para la MainActivity
 2. Agregar clase auxiliar para monitorear el estatus de las llamadas
 3. Incorporar los métodos para realizar llamadas
 4. Agregar los permisos necesarios

A continuación algo de código y su explicación para cada paso de la práctica:

## 1. Definir el layout y referencias a las vistas para la MainActivity

Para lograr obtener la GUI requerida para la aplicación, a partir del código base hay que editar el layout correspondiente al MainActivity, es decir el archivo activity_main.xml, para que incluyas un los Buttons y TextEdit requeridos.  El código a escribir es mas o menos el siguiente:

![](Instrucciones_img/MainActivity_Layout.jpg?raw=true)

Posteriormente, ya dentro de la clase MainActivity, deberás agregar las variables para referenciar al los elementos GUI utilizando el método **findViewById** como luce en el siguiente fragmento de código:

```java
...

public class MainActivity extends AppCompatActivity {

    private Button mCallButton;
    private Button mDialAppButton;
    private EditText mNumberEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNumberEditText = (EditText) findViewById(R.id.et_phoneNumber);
        mCallButton = (Button) findViewById(R.id.bt_call);
        mDialAppButton = (Button) findViewById(R.id.bt_dial_app);
  }
}
```

## 2. Agregar clase auxiliar para monitorear el estatus de las llamadas

Una vez realizado el paso anterior, deberás agregar una inner class denominada MyPhoneListener, que nos servirá para monitorear el estatus de la llamada y mostrar algunos Toast informativos cuando realicemos el marcado y el colgado, esta clase es semejante a la realizada en la práctica 2 de la Unidad 2 en la que se desarrolló una CallLogApp, pero en este caso es mucho menos completa.  ATENCIÓN: Esta clase debe estar dentro de la clase MainActivity:

![](Instrucciones_img/MainActivity_InnerClass.jpg?raw=true)

## 3. Incorporar los métodos para realizar llamadas

Una vez realizado lo anterior, ahora debes proceder a incorporar el código necesario para realizar las llamadas, y se realizará de dos formas, una mediante un intent de tipo ACTION_CALL mediante el que se puede realizar la llamada directamente al destinatario y otra mediante un intent de tipo ACTION_DIAL en el que se despliega la aplicación predeterminada para llamadas.  Notose que también se creará y se configurará una instancia de la clase MyPhoneListener para monitorear el estatus de las llamadas y mostrar algunos Toast informativos. Esto se puede lograr agregando el siguiente código en la misma clase MainActivity dentro del método onCreate:  

![](Instrucciones_img/MainActivityMetodosRealizarLlamadas.jpg?raw=true)

## 4. Agregar los permisos necesarios

Finalmente para que la aplicación pueda tener funcionalidad, es necesario agregar los siguientes permisos en el archivo de manifiesto de la aplicación:

```xml
<uses-permission android:name="android.permission.CALL_PHONE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
```

**Listo, ahora puedes ejecutar y probar la aplicación de inmediato** iniciandola y escribiendo número y SMS para observar los resultados.

Por el momento, eso es todo amigos.
