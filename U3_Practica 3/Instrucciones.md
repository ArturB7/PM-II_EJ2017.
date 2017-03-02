# Descripción de la Práctica 3 de la Unidad 3 (Telefonía  y redes)  (a.k.a. Práctica 10)
**Tema de interés: Envío de correo electrónico**

El propósito de esta practica es que el alumno conozca la manera de realizar un envío de correo electrónico desde su aplicación.  El objetivo es lograr desarrollar una breve aplicación que tenga la capacidad de realizar el envío de un correo eletrónico. Para envíar un correo electrónico desde nuestra aplicación no es necesario implementar un cliente de correos desde cero, sino que es posible utilizar un cliente existente como la aplicación default de Email de Android, Gmail, Outlook, K-9, etc.  Para este proposito es necesario lanzar un Intent que muestre los clientes de email existentes.

![Pantalla de la Aplicación](Instrucciones_img/app.jpg?raw=true)

Prototipo de la App a desarrollar


## Instrucciones de la práctica:
Obtener el código fuente base y modificarlo para:

 1. Definir el layout y referencias a las vistas para la MainActivity
 2. Agregar el método sendEmail a la MainActivity
 3. Agregar el código necesario para invocar el metodo sendEmail a la MainActivity

A continuación algo de código y su explicación para cada paso de la práctica:

## 1. Definir el layout y referencias a las vistas para la MainActivity

Para lograr obtener la GUI requerida para la aplicación, a partir del código base hay que editar el layout correspondiente al MainActivity, es decir el archivo activity_main.xml, para que incluyas un los Buttons y TextEdit requeridos.  El código a escribir es mas o menos el siguiente:

![](Instrucciones_img/MainActivity_Layout.jpg?raw=true)

Posteriormente, ya dentro de la clase MainActivity, deberás agregar las variables para referenciar al los elementos GUI utilizando el método **findViewById** como luce en el siguiente fragmento de código:

```java
...

public class MainActivity extends AppCompatActivity {

    private EditText mRecipientEditText;
    private EditText mSubjectEditText;
    private EditText mBodyEditText;
    private Button mSendEmailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecipientEditText = (EditText) findViewById(R.id.et_recipient);
        mSubjectEditText = (EditText) findViewById(R.id.et_subject);
        mBodyEditText = (EditText) findViewById(R.id.et_body);

        mSendEmailButton = (Button) findViewById(R.id.bt_sendEmail);
}
```

## 2.  Agregar el método sendEmail a la MainActivity

Una vez realizado el paso anterior, como se habia mencionado, es necesario lanzar un Intent que muestre los clientes de Email que se encuentran instalados en el dispositivo.  Por esta razón se utilizará el action SENDTO en el Intent y especificandole el dato **mailto:**, además se establecerá el tipo de mensaje a **message/rfc822** de manera que se muesgtren solamente los clientes de Email. Android provee campos que pueden ser utilizados para establecer otros elementos del Email, estos campos pueden ser agregados al Intent como datos extra y son los siguientes:  

- EXTRA_BCC: direcciones email para "blind carbon copy"
- EXTRA_CC: direcciones email para "carbon copy"
- EXTRA_EMAIL: direcciones email para destinatarios
- EXTRA_HTML_TEXT: alternativa al uso de EXTRA_TEXT para utilizar formato HTML
- EXTRA_SUBJECT: el asunto del email
- EXTRA_TEXT: el mensaje o cuerpo del email

En esta práctica se utilizará EXTRA_EMAIL, EXTRA_SUBJECT and EXTRA_TEXT para llenar los campos apropiados en el cliente de Email, y esto se logrará agrgando el método sendEmail a nuestra MainActivity como se puede apreciar en el código a continuación:

![](Instrucciones_img/MainActivity_SendEmail.jpg?raw=true)

## 3. Agregar el código necesario para invocar el metodo sendEmail a la MainActivity

Finalmente para que la aplicación pueda tener la funcionalidad esperada, hay que agregar el código necesario para invocar el método sendEmail desde nuestro botón y para lograr esto tenemos que hacer lo siguiente:

![](Instrucciones_img/MainActivity.jpg?raw=true)


**Listo, ahora puedes ejecutar y probar la aplicación de inmediato**.

Por el momento, eso es todo amigos.
