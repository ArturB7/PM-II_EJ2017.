# Descripción de la Práctica 1 de la Unidad 4 (Sensores)  (a.k.a. Práctica 14)
**Tema de interés: Reconocimiento de sensores**

El propósito de esta practica es que el alumno desarrolle una aplicación que detecte los sensores activos en el dispositivo y los diferentes cambios que en ellos se generan.  Una de las características que más llamó la atención desde el primer dispositivo Android, fue la implementación de diferentes tipos sensores, los cuales no parecen tener límite en los nuevos dispositivos.  Puedes consultar un poco de información sobre el [Framework de Sensores para android en su documentación online](https://developer.android.com/guide/topics/sensors/sensors_overview.html).

![Pantalla de la Aplicación](Instrucciones_img/app.png?raw=true)

Prototipo de la App a desarrollar


## Instrucciones de la práctica:
Obtener el código fuente base y modificarlo para:

 1. Definir el layout y referencias a las vistas para la MainActivity
 2. Detectar y cargar sensores disponibles
 3. Implementar la Interfaz SensorEventListener en nuestra Activity
 4. Registrar el manejador de eventos de sensores
 5. Agrega un nuevo sensor.

A continuación algo de código y su explicación para cada paso de la práctica:

## 1. Definir el layout y referencias a las vistas para la MainActivity

Para lograr obtener la GUI requerida para la aplicación, a partir del código base hay que editar el layout correspondiente a la activity principal para incorporar algunos elementos GUI en los que mostraremos la actividad de los sensores.  El código a escribir es mas o menos el siguiente:

![](Instrucciones_img/MainActivity_layout.png?raw=true)

Posteriormente, ya dentro de la clase MainActivity, deberás agregar las variables para referenciar al los elementos GUI utilizando el método **findViewById** como luce en el siguiente fragmento de código:

```java
...

    private TextView mAceleromentroTextView;
    private TextView mProximidadTextView;
    private TextView mLuzTextView;
    private TextView mTemperaturaTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAceleromentroTextView = (TextView) findViewById(R.id.tv_sensor_acel);
        mProximidadTextView = (TextView) findViewById(R.id.tv_sensor_proximidad);
        mLuzTextView = (TextView) findViewById(R.id.tv_sensor_luz);
        mTemperaturaTextView = (TextView) findViewById(R.id.tv_sensor_temperatura);

    } 
...
```

##  Detectar y cargar sensores disponibles

Para proceder a realizar la detección y carga de los sensores disponibles en nuestro dispositivo podemos definir las siguientes variables a nivel de clase dentro de la MainActivity, serán 5 variables del tipo **Sensor**, a través de las cuales se guardará una referencia a cada sensor que pueda ser detectado mediante el **SensorManager**.  El **SensorManager**, dentro de Android, es el responsable de permitir el acceso a los sensores del dispositivo, puedes encontrar mas [información sobre SensorManager en su documentación oficial.](https://developer.android.com/reference/android/hardware/SensorManager.html)

El código de las variables a agregar dentro de nuestra clase MainActivity es mas o menos el siguiente:

![](Instrucciones_img/Variables_Sensor.png?raw=true)

Para poder obtener las respectivas referencias de los sensores en el dispositivo Android, a continuación se utiliza el ya mencionado **SensorManager** a través de la llamada al método *getSystemService()* que nos retorna un servicio a nivel de sistema dependiendo del parámetro que le pasemos, en este caso **SENSOR_SERVICE**, pues queremos hacer uso de los sensores.
Una vez inicializado **SensorManager**, podemos hacer uso de este objeto para solicitar instancias de los diferentes tipo de sensores haciendo uso del método *getDefaultSensor()* añadiendo el tipo de sensor que queremos como parámetro. 

Para lograr esto agregaremos las llamadas respectivas ya mencionadas, dentro del método onCreate de la MainActivity para hacer referencia a los diversos sensores, de tal forma que nuestro código luzca como el siguiente:

![](Instrucciones_img/Sensor_Manager_Use.png?raw=true)

**En este momento ya puedes probar la aplicación en tu dispositivo** y esta deberá mostrarte en color verde las etiquetas de aquellos sensores que han sido detectados en tu dispositivo y en color rojo las de aquellos que no.  No obstante, aún falta realizar la lectura de valores de los mismos.

## 3. Implementar la Interfaz SensorEventListener en nuestra Activity

Para poder hacer la lectura de los valores de los sensores necesitamos una clase que implemente la la interfaz **SensorEventListener** y suscribirla al **SensorManager** vigente.  Para lograr esto lo primero que haremos sera convertir nuestra propia clase MainActivity en un **SensorEventListener** implementando esta interfaz. Para realizar la implementación de la interfaz **SensorEventListener** en nuestra clase MainActivity, que hacer es agregar el siguiente código a la firma de la clase:

![](Instrucciones_img/MainActivity_Implement_SensorEventListener.png?raw=true)

Posteriormente se puede apreciar que la interfaz **SensorEventListener** nos pide implementar 2 métodos:
 - *onSensorChanged(SensorEvent event)*, la cual nos permite codificar las acciones a realizar cuando un sensor registre un cambio en cualquiera de los sensores.
  - *onAccuracyChanged(Sensor sensor, int accuracy)*, en el cual no codificaremos pero se utiliza cuando se cambia la precisión "accuracy" de algún sensor.

En este caso se implementarán ambos métodos en nuestra clase pero solamente agregaremos código  al método *onSensorChanged* de manera que muestre en los elementos de GUI las variaciones registradas por los sensores.  Para tener una comprensión mas profunda de los valores que arroja cada sensor puedes consultar la [documentación sobre SensorEvent online](https://developer.android.com/reference/android/hardware/SensorEvent.html).  Lo haremos como se aprecia a continuación:

![](Instrucciones_img/onSensorChanged_method.png?raw=true)

## 4. Registrar el manejador de eventos de sensores

Para finalizar, se debe **resaltar** que el uso de los sensores requiere del uso de energía por parte de la aplicación que los utiliza a diferencia de las demás que no lo hacen, por lo que es recomendable liberar los manejador de eventos de sensor cuando se vaya a pausar la aplicación y registrarlos cuando la app sea nuevamente activa. 

También, existe la posibilidad de que nuestro dispositivo, no tenga todos los sensores que pensamos, por ejemplo podría ser que en algún dispositivo no exista un sensor de Temperatura, por lo que siempre es buena idea hacer un filtro que nos permita identificar con cuales si contamos y añadir el manejador de eventos de acuerdo a eso. 

Mediante el siguiente código, si el sensor existe, se registra el manejador de eventos correspondiente usando el método *registerListener()* del objeto **SensorManager**, al que se le pasa como parámetro, la clase que está implementando la interfaz **SensorEventListener** (**en este caso será nuestra propia MainActivity** y cuando la aplicación entra en pausa se remueve el registro. Esto se debe hacer sobreescribiendo los métodos onResume y onPause de nuestra MainActivity de la siguiente manera:

![](Instrucciones_img/Main_Activity_onResumeOnPause.png?raw=true)

**Listo, ahora puedes ejecutar y probar la aplicación de inmediato**

## 5. Agrega un nuevo sensor.

Para finalizar la práctica debes agregar un nuevo sensor por ti mismo. Elije cualquiera, recuerda que puedes encontrar más información sobre los sensores existentes en la documentación del [framework de sensores para android online](https://developer.android.com/guide/topics/sensors/sensors_overview.html).

**Suerte**
