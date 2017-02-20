#Descripción de la práctica 6 
**Tema de interés: Uso de BroadcastReceivers**

El propósito de esta practica es que el alumno pueda conocer y aplicar el concepto de **Broadcast Receiver** el único componente de Android que faltaba por abordar. Puedes encontrar el contenido utilizado durante clase explicando claramente el concepto en el siguiente [video](https://www.youtube.com/watch?v=DTOwxXnDz9U)
También puedes aprender mas sobre este concepto en la [documentación de android sobre Broadcasts](https://developer.android.com/guide/components/broadcasts.html). 

El objetivo de la práctica es lograr que nuestra aplicación Hydratation Reminder pueda actualizar el icono que indica si el dispositivo se encuentra conectado a la red electrica para ser cargado o no. Para lograr esto se hará la configuración y uso de un *Broadcast Receiver* **dinámico**.

##Instrucciones de la práctica:
Obtener el código fuente base y modificarlo para:

 1. Crear un método que cambie de color el ícono del plug 
 2. Definir el Intent Filter atento al estatus de carga del dispositivo
 3. Crear un Broadcast Receiver que mande actualizar el color del ícono del plug
 4. Registrar el Broadcast Receiver con el Intent Filter definido
 5. Actualizar el estatus del ícono del plug cuando la aplicación vuelva a ser activa.
 
A continuación algo de código y su explicación para cada paso de la práctica:

##1. Crear un método que cambie de color el ícono del plug

Para lograr esto, dentro de la clase MainActivity, hay que crear un método denominado showCharging que reciba como argumento un boleano que indique si el dispositivo esta cargando o no y en función de esto coloque el ícono correspondiente en la vista del plug.

```java
...

    // Este método debería cambiar de imagen de mChargingImageView a ic_power_pink_80px si el argumento
	// boleano es true, o en caso contrario colocar la imagen a ic_power_grey_80px.  Ambas imagenes se
    // encuentran en R.drawable.  Este método actualizará la GUI  cuando nuestro broadcast receiver
    // sea disparado durante los cambios de estado provocados por conectar y desconectar el dispositivo 
	// de la corriente electrica.
    private void showCharging(boolean isCharging){
        if (isCharging) {
            mChargingImageView.setImageResource(R.drawable.ic_power_pink_80px);
        } else {
            mChargingImageView.setImageResource(R.drawable.ic_power_grey_80px);
        }
    }

...
```

##2. Definir el Intent Filter atento al estatus de carga del dispositivo

Despues de crear un método que sea capaz de actualizar nuestra GUI ante algún evento, debemos comenzar a trabajar para crear nuestro BroadcastReceiver, y lo primero que hay que hacer es configurar un IntentFilter atento a los eventos en los que deseamos tomar acción. Para lograr esto se deberán realizar las siguientes **2 modificaciones** en la clase MainActivity 

```java
...
public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private TextView mWaterCountDisplay;
    private TextView mChargingCountDisplay;
    private ImageView mChargingImageView;

    private Toast mToast;

	//1. Declarar una variable para contener nuestro Intent Filter
	IntentFilter mChargingIntentFilter;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWaterCountDisplay = (TextView) findViewById(R.id.tv_water_count);
        mChargingCountDisplay = (TextView) findViewById(R.id.tv_charging_reminder_count);
        mChargingImageView = (ImageView) findViewById(R.id.iv_power_increment);

        updateWaterCount();
        updateChargingReminderCount();

        ReminderUtilities.scheduleChargingReminder(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);

		//2. Crear la instancia del Intent Filter e indicarle que este atento al 
		// momento en que se conecta y se desconecta el cargador
        mChargingIntentFilter = new IntentFilter();
        mChargingIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED); 
        mChargingIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED); 
	}

    ...
	...
```

##3. Crear un Broadcast Receiver que mande actualizar el color del ícono del plug

Una vez creado y configurado el IntentFilter, ahora corresponde crear el BroadcastReceiver que lo utilice, para lograr esto hay que crear una **inner class** denominada ChargingBroadcastReceiver dentro de la clase MainActivity, para que pueda invocar el método creado en el paso 1 cuando el IntentFilter creado en el paso 2 determine que ha ocurrido uno de los eventos configurados.  Esta nueva clase deberá heredar de la clase BroadcastReceiver. **OJO: Esta nueva clase es una inner class, y se debe colocar dentro y prácticamente al final de la clase MainActivity**

```java
...
public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {
	...
	...
	...
	...

    //Crear la inner class denominada ChargingBroadcastReceiver que hereda de BroadcastReceiver
    private class ChargingBroadcastReceiver extends BroadcastReceiver {
        // El método onReceive se sobreesribe para recibir el intent que fue previamente creado 
		// y revisar cual de las acciones para las que fue configurado es la que ha ocurrido
		// en nuestro caso ACTION_POWER_CONNECTED o ACTION_POWER_DISCONNECTED. 
        @Override
        public void onReceive(Context context, Intent intent) {            
			// se obtiene la Accion del Intent recibido y se almacena el resultado en la variable 
			// isCharging verdadero o falso según sea el caso de la acción recibida.
			String action = intent.getAction();
            boolean isCharging = (action.equals(Intent.ACTION_POWER_CONNECTED));

            // Se pasa esta variable isCharging al método anteriormente creado para actualizar la GUI
            showCharging(isCharging);
        }
	}
} //Fin de la clase MainActivity
```

Una vez creada la **inner class** ***ChargingBroadcastReceiver*** debemos crear una variable y una instancia para la misma. Para llevar a cabo esto hay que actualizar nuevamente nuestra clase MainActivity realizando los siguientes **2 cambios**. 

```java
...
public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private TextView mWaterCountDisplay;
    private TextView mChargingCountDisplay;
    private ImageView mChargingImageView;

    private Toast mToast;

	IntentFilter mChargingIntentFilter;
	// 1. Declarar una variable para contener nuestro nuevo ChargingBroadcastReceiver
    ChargingBroadcastReceiver mChargingReceiver;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWaterCountDisplay = (TextView) findViewById(R.id.tv_water_count);
        mChargingCountDisplay = (TextView) findViewById(R.id.tv_charging_reminder_count);
        mChargingImageView = (ImageView) findViewById(R.id.iv_power_increment);

        updateWaterCount();
        updateChargingReminderCount();

        ReminderUtilities.scheduleChargingReminder(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
		
        mChargingIntentFilter = new IntentFilter();
        mChargingIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED); 
        mChargingIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED); 

		// 2. Crear la respectiva instancia de nuestro ChargingBroadcastReceiver. 
        mChargingReceiver = new ChargingBroadcastReceiver();
		
	}
	
	...
    ...
	...
```

##4. Registrar el Broadcast Receiver con el Intent Filter definido

Una vez que se han creado tanto el IntentFilter como el BroadcastReceiver que nos permitirán monitorear el cambio entre celular conectado y desconectado de la red eléctrica, debemos ponerlos a trabajar y esto se logra enlazandolos y registrandolos cuando la aplicación se vuelva activa (onResume), además debemos hacer lo contrario "des-registrarlos" cuando la aplicación se vuelva inactiva, creando con esto un BroadcastReceiver dinámico.  Para lograr esto hay que realizar nuevamente sobre la clase MainActivity los **2 cambios** siguientes:

```java
...
public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private TextView mWaterCountDisplay;
    private TextView mChargingCountDisplay;
    private ImageView mChargingImageView;

    private Toast mToast;

	IntentFilter mChargingIntentFilter;
    ChargingBroadcastReceiver mChargingReceiver;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWaterCountDisplay = (TextView) findViewById(R.id.tv_water_count);
        mChargingCountDisplay = (TextView) findViewById(R.id.tv_charging_reminder_count);
        mChargingImageView = (ImageView) findViewById(R.id.iv_power_increment);

        updateWaterCount();
        updateChargingReminderCount();

        ReminderUtilities.scheduleChargingReminder(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
		
        mChargingIntentFilter = new IntentFilter();
        mChargingIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED); 
        mChargingIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED); 

        mChargingReceiver = new ChargingBroadcastReceiver();
		
	}

    // 1. Agregar la sobreescritura del metodo onResume de la MainActivity esto para configurar 
	// que mediante el método registerReceiver sean atados nuestro ChargingBroadcastReceiver y 
	// el IntentFilter creados, y que esto pase solamente cuando nuestra aplicación se vuelva activa.
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mChargingReceiver, mChargingIntentFilter);
    }

    // 2. Agregar la sobreescritura del metodo onPause de la MainActivity para hacer unregister
	// de nuestro ChargingBroadcastReceiver cuando la aplicación se vuelva inactiva.
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mChargingReceiver);
    }
	
	...
    ...
	...
```


**Listo, ahora puedes ejecutar y probar la aplicación**, pero... deberás notar que aún tiene un pequeño **bug** que ocurre cuando conectas el cargador de corriente mientras la aplicación esta activa y lo desconectas cuando la aplicación se encuentra inactiva, provocando que el icono del plug no se actualice correctamente.  

Si te detienes a analizar, esto ocurre porque nuestro BroadcastReceiver es dinámico y como debes saberlo solamente trabaja cuando la aplicación esta activa, asi que cualquier cambio que ocurra mientras la aplicación se encuentra inactiva no será contemplado.  No obstante, este problema se resolverá a continuación.  

#5. Actualizar el estatus del ícono del plug cuando la aplicación vuelva a ser activa.

Hay dos maneras de resolver el bug planteado en el paso anterior dependiendo de si el dispositivo tiene o no el API de nivel 23(o superior).

Si el dispositivo del usuario tiene el API nivel 23 (o superior) para obtener el estatus actual de la batería basta con utilizar el servicio de "battery manager system" de esta forma:

```java
BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
boolean isCharging = batteryManager.isCharging();
```

Si el dispositivo del usuario no tiene API nivel 23 (o superior) el camino es un poco mas largo pues debe utilizarse un Sticky Intent (Intent Pegajoso)

Antes de Android 23 era necesario usar un Sticky Intents para obtener el estatus de la bateria. Un Intent normal es lanzado como broadcast y posiblemente capturado por un IntentFilter para ser procesado y entonces desaparecer, a diferencia de este, un Sticky Intent, permanece capturado y permite que nuestra aplicación acceda a este en cualquier momento para obtener cualquier tipo de información que contenga.  En Android un Sticky Intent es el lugar en el es almacenado el estatus de la bateria de manera constante.

Para usar un Sticky Intent no es necesario un BroadcastReceiver, pero se utiliza codigo similar para registrarlo en la aplicación:

```java
IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
Intent batteryStatus = context.registerReceiver(null, ifilter);
```

Notese que también un IntentFilter es creado y configurado, y que es utilizado el método **registerReceiver** como si fuese a registrarse un BroadcastReceiver, pero en este caso el primer argumento es **nulo**.  El método **registerReceiver** devuelve un Intent, y es este nuestro Sticky Intent que tendrá la información sobre los cambios en el estatus de la batería. 

Ahora, este Sticky Intent puede ser utilizado de la siguiente manera:

```java
boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
```

Puedes ver el siguiente link para obtener mayor información sobre [como controlar y monitorear el nivel de bateria y el estatus de carga.](https://developer.android.com/training/monitoring-device-state/battery-monitoring.html)
 
Con esta información ahora deberías ser capaz de corregir el bug presentado al final del ejercicio. Dejo a tu entendimiento el que ya debes saber donde agregar este código, pero to puede ayudar un poco indicandote que el código sería mas o menos el siguiente:

```java
    @Override
    protected void onResume() {
        super.onResume();

        /** Determinar el estatus actual de carga de batería **/
        // Revisar si estamos en Android M (API 23) o posterior, si es asi...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Obtener una instancia de BatteryManager utilizando getSystemService()
            BatteryManager batteryManager = (BatteryManager) getSystemService(BATTERY_SERVICE);
            // Invocar el método isCharging en el BatteryManager y pasar el resultado al método 
			// showCharging para actualizar la GUI
            showCharging(batteryManager.isCharging());
        } else {
            // Si estamos en una versión anterior a Android M (API 23), entonces...
            // Crear y registrar un IntentFilter para la acción ACTION_BATTERY_CHANGED. 
			// Este será un Sticky Intent que contendrá mucha información sobre el estatus 
			// de la bateria.
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent currentBatteryStatusIntent = registerReceiver(null, ifilter);
            // Obtener el valor entero de BatteryManager.EXTRA_STATUS y revisar si hace match
            // con BatteryManager.BATTERY_STATUS_CHARGING o BatteryManager.BATTERY_STATUS_FULL. 
			// Esto significaría que la batería esta actualmente siendo cargada.
            int batteryStatus = currentBatteryStatusIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = (batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING ||
                    batteryStatus == BatteryManager.BATTERY_STATUS_FULL);
            // Actualizar la GUI utilizando el método showCharging.
            showCharging(isCharging);
        }

        /** Registrar un BroadcastReceiver para monitorear el estatus de carga de batería **/
        registerReceiver(mChargingReceiver, mChargingIntentFilter);
    }
```


Por el momento, eso es todo amigos.
