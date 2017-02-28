# Descripción de la Práctica 1 de la Unidad 3 (Telefonía  y redes)  (a.k.a. Práctica 8)
**Tema de interés: Envío de SMS**

El propósito de esta practica es que el alumno conozca las distinas maneras de enviar mensajes de tipo SMS desde el dispositivo movil.  El objetivo de la práctica es lograr que se desarrolle una breve aplicación que tenga la capacidad de enviar SMS utilizando:
- La clase SmsManager
- Intents de tipo SENDTO action
- Intents de tipo VIEW action

El uso de la clase SmsManager facilita el trabajo de envío de SMS porque provee la oportunidad de personalizar su funcionalidad dentro de nuestra propia Activity ver [Documentación sobre SmsManager](https://developer.android.com/reference/android/telephony/SmsManager.html). No obstante, el uso de Intents implica que se utilicen las aplicaciones para manejo de SMS que ya estan integradas en el dispositivo para delegarles el trabajo.

![Pantalla de la Aplicación](Instrucciones_img/app.jpg?raw=true)

Prototipo de la App a desarrollar


## Instrucciones de la práctica:
Obtener el código fuente base y modificarlo para:

 1. Definir el layout y referencias a las vistas para la MainActivity
 2. Incorporar los métodos para envío de sms
 3. Vincular los métodos con las vistas
 4. Agregar los permisos necesarios

A continuación algo de código y su explicación para cada paso de la práctica:

## 1. Definir el layout y referencias a las vistas para la MainActivity

Para lograr obtener la GUI requerida para la aplicación, a partir del código base hay que editar el layout correspondiente al MainActivity, es decir el archivo activity_main.xml, para que incluyas un los Buttons y TextEdit requeridos.  El código a escribir es mas o menos el siguiente:

![](Instrucciones_img/MainActivity_Layout.jpg?raw=true)

Posteriormente, ya dentro de la clase MainActivity, deberás agregar una variable para referenciar al los elementos GUI utilizando el método **findViewById** como luce en el siguiente fragmento de código:

```java
...

public class MainActivity extends AppCompatActivity {

  //Variables para referenciar a los views en GUI
  private EditText mPhoneNumberEditText;
  private EditText mSmsBodyEditText;
  private Button mSmsManagerButton;
  private Button mSmsSendToButton;
  private Button mSmsViewButton;

  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);

      //Obtener la referencia de los views en GUI
      mPhoneNumberEditText = (EditText) findViewById(R.id.phoneNumber);
      mSmsBodyEditText = (EditText) findViewById(R.id.smsBody);
      mSmsManagerButton = (Button) findViewById(R.id.smsManager);
      mSmsSendToButton = (Button) findViewById(R.id.smsSIntent);
      mSmsViewButton = (Button) findViewById(R.id.smsVIntent);
  }
}
```

## 2. Incorporar los métodos para envío de sms

Una vez realizado el paso anterior, ahora si procedemos a desarrollar los varios métodos dentro de nuestra clase MainActivity que nos servirán para uno solo y el mismo propósito, enviar un sms, no obstante, de diferente manera. Los tres métodos a desarrollar toman como argumento 2 cosas: el número telefónico y el cuerpo del mensaje.  Así que dentro de la clase MainActivity hay que codificar estos métodos mas o menos como se puede a preciar en el siguiente código:

![](Instrucciones_img/MainActivityMetodosParaEnvioSMS.jpg?raw=true)

## 3. Vincular los métodos con las vistas

Una vez creados los métodos anteriores bastará con vincularlos al botón correspondiente, esto se puede lograr agregando el siguiente código en la misma clase MainActivity:  

```java
...

public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    //Obtener la referencia de los views en GUI
    mPhoneNumberEditText = (EditText) findViewById(R.id.phoneNumber);
    mSmsBodyEditText = (EditText) findViewById(R.id.smsBody);
    mSmsManagerButton = (Button) findViewById(R.id.smsManager);
    mSmsSendToButton = (Button) findViewById(R.id.smsSIntent);
    mSmsViewButton = (Button) findViewById(R.id.smsVIntent);

    //Establecer listeners para el evento OnClick de cada Button
    OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            //Bajar el dato del teléfono y cuerpo del mensaje desde los views
            String phoneNumber = mPhoneNumberEditText.getText().toString();
            String smsBody = mSmsBodyEditText.getText().toString();
            //Invocar el método dependiendo del botón presionado
            if (view.equals(mSmsManagerButton))
                sendSmsByManager(phoneNumber,smsBody);
            else if (view.equals(mSmsSendToButton))
                sendSmsBySIntent(phoneNumber,smsBody);
            else if (view.equals(mSmsViewButton))
                sendSmsByVIntent(phoneNumber,smsBody);
        }
    };

    mSmsManagerButton.setOnClickListener(clickListener);
    mSmsSendToButton.setOnClickListener(clickListener);
    mSmsViewButton.setOnClickListener(clickListener);
}

...
```

## 4. Agregar los permisos necesarios

Finalmente para que la aplicación pueda tener funcionalidad a través del SmsManager, es necesario agregar el siguiente permiso en el archivo de manifiesto de la aplicación:

```xml
    <uses-permission android:name="android.permission.SEND_SMS" />
```

**Listo, ahora puedes ejecutar y probar la aplicación de inmediato** iniciandola y escribiendo número y SMS para observar los resultados.

Por el momento, eso es todo amigos.
