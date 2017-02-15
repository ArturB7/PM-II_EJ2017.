#Descripción de la práctica

El propósito de esta practica es que el alumno pueda crear un JobService que complemente la aplicación de la práctica 3 para finalmente lograr ejecutar una tarea en background que constantemente le recuerde tomar agua cuando el dispositivo se encuentre cargando.
Para crear nuestro Service Job es necesario utilizar un Job Scheduler.  Existen dos enfoques para crear un Job Scheduler, el enfoque normal que utiliza una clase JobScheduler tal cual o el enfoque sugerido por Google que utiliza la clase FireBaseJobDispatcher. La ventaja de FireBaseJobDispatcher es que ofrece soporte de compatibilidad hasta el API 9 a diferencia del JobScheduler que solo ofrece hasta el API 21. El único prerequisito para utilizar el FireBaseJobDispatcher es que tengas Google Play Services en tu dispositivo con al menos el API 9 (Gingerbread).  
Para comprender mejor la decisión de utilizar el FireBaseJobDispatcher revisa este video sobre [Scheduling Jobs](https://www.youtube.com/watch?v=bTFIr9pWnCg)
Puedes aprender mas sobre esto en la documentación de [FireBaseJobDispatcher](https://github.com/firebase/firebase-jobdispatcher-android). 

##Instrucciones:
Obtener el código fuente base y modificarlo para:

 1. Incluir una dependencia en Gradle hacia el FirebaseJobDispatcher 
 2. Crear una nueva Task en la clase ReminderTask
 3. Crear un nuevo Service que herede de JobService
 4. Agregar el Service al manifest
 5. Calendarizar el Service con FireBaseJobDispatcher

A continuación algo de código y su explicación para cada paso de la práctica:

##1. Incluir una dependencia hacia el Firebase Job Dispatcher

Para lograr esto hay que agregar la dependencia en el archivo build.gradle (Module:app)

```gradle
...
dependencies {
    compile fileTree(include: ['*.jar'], dir: 'libs')
    compile 'com.android.support:appcompat-v7:25.0.1'
    // Se agrega la dependencia de Gradle para el Firebase Job Dispatcher
    compile 'com.firebase:firebase-jobdispatcher:0.5.0'
}
...
```
##2. Crear una nueva Task en la clase ReminderTask

Para lograr esto se tendran que realizar 3 cambios en la clase ReminderTask con la finalidad de que ahora esta también sea capaz de manejar la tarea de incrementar el contador de recordatorios y mostrar la notificación correspondiente cuando sea necesario:

```java
...

public class ReminderTasks {
    public static final String ACTION_INCREMENT_WATER_COUNT = "increment-water-count";
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";
	//1. Crear una nueva constante que sirva para identificar la accion de recordatorio.
    static final String ACTION_CHARGING_REMINDER = "charging-reminder";

    public static void executeTask(Context context, String action) {
        if (ACTION_INCREMENT_WATER_COUNT.equals(action)) {
            incrementWaterCount(context);
        } else if (ACTION_DISMISS_NOTIFICATION.equals(action)) {
            NotificationUtils.clearAllNotifications(context);
        } else if (ACTION_CHARGING_REMINDER.equals(action)) {
            issueChargingReminder(context);
        }
		//2. Agregar el 'else' correspondiente a la nueva acción para que cuando esta ocurra se invoque a un método que realice lo necesario.
    }

    private static void incrementWaterCount(Context context) {
        PreferenceUtilities.incrementWaterCount(context);
        NotificationUtils.clearAllNotifications(context);
    }

	//3. Agregar el nuevo método invocado desde el else recien agregado el cual realizará dos cosas:...			 
    private static void issueChargingReminder(Context context) {
        //primero, incrementara el contador de la cantidad de recordatorios mostrados (nota: este es diferente al contador de vasos tomados)... 	
        PreferenceUtilities.incrementChargingReminderCount(context);
		//segundo, mostrar la notificación al usuario. 
        NotificationUtils.remindUserBecauseCharging(context);
    }
}
```

##3. Crear un nuevo Service que herede de JobService

Para lograr esto hay que crear una nueva clase denominada **WaterReminderFirebaseJobService** preferentemente dentro del paquete sync, nótese que esta clase heredará de JobService una clase compatible con el FireBaseJobDispatcher.  Este clase de servicio puede programarse para ejecutarse bajo ciertas condiciones mediante un FireBaseJobDispatcher (condiciones como: si el dispositivo se encuentra cargando, si se tiene red wifi, en un marco de tiempo de espera, etc ), es decir, justo lo que se necesita para programar un recordatorio como el que se plantea en la práctica.  Esta clase contiene las instrucciones que se deberán ejecutar cuando las condiciones se cumplan y en los siguientes pasos se utilizará para ser programado a través de un FireBaseJobDispatcher.

```java
package com.example.android.background.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.RetryStrategy;

// WaterReminderFirebaseJobService debe heredar de JobService de firebase
public class WaterReminderFirebaseJobService extends JobService {

	//Esta variable almacenará la tarea asincrona (es decir para ejecutar en background) que se creará y disparará cuando el Job sea iniciado.
    private AsyncTask mBackgroundTask;

    /**
     * Este método debe sobreescribirse para el JobService actual, y en este se especifica el 
	 * trabajo a realizar cuando el Job sea iniciado.  Es relevante conocer que este Job se ejecuta
	 * en el hilo principal de la aplicación y por tanto es conveniente descargar el trabjo en un
	 * AsyncTask que pueda seguir su propio hilo en background.
     *
     * @return verdadero si se ha iniciado el trabajo satiscatoriamente.
     */
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        // Aqui se crea la AsyncTask anonima sobreescribiendo sus metodos indicando el trabajo a realizar en background.
        mBackgroundTask = new AsyncTask() {

            // Esto se debe realizar en background
            @Override
            protected Object doInBackground(Object[] params) {
                // Se utiliza el metodo executeTask de la clase ReminderTask para mandar realizar la accion programada para el recordatorio.
                Context context = WaterReminderFirebaseJobService.this;
                ReminderTasks.executeTask(context, ReminderTasks.ACTION_CHARGING_REMINDER);
                return null;
            }

			//Este metodo se invoca una vez que el AsyncTask ha terminado de ejecutarse en background
            @Override
            protected void onPostExecute(Object o) {
				// Para indicar al JobManager de android que todo salio bien, se debe invocar el metodo jobFinished, con los parametros que se habian pasado al Job y el argumento de falso para indicarle que todo salio bien y que no es necesario volver a ejecutar el Job.
                jobFinished(jobParameters, false);
            }
        };

        // Aqui se manda ejecutar el AsyncTask recien creado
        mBackgroundTask.execute();
 
		// Se devuelve true pues se ha iniciado el Job satiscatoriamente
		return true;
    }

    /**
     * Este metodo es invocado cuando el motor de calendarización interrumpe la ejecución de un job que
	 * en ese momento esta corriendo, por ejemplo si se crease un job para sincronizar archivos locales
	 * hacia la nube y de repente se pierde la conexion a internet totalmente el job no podría continuar
	 * en ese momento es disparado este metodo onStopJob.
     *
     * @return verdadero si se debe reintentar ejecutar el job.
     */
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        // Para nuestro caso si el mBackgroundTask esta activo, habría que cancelarlo solamente
        // Se devuelve verdadero para que se vuelva a intentar ejecutar el job.
        if (mBackgroundTask != null) mBackgroundTask.cancel(true);
        return true;
    }
}
```
##4. Agregar el Service al manifest

Hay que agregar el WaterReminderFirebaseJobService en el manifiesto. Notese que este debe ser de alcance local y tener un intent filter para la acción de com.firebase.jobdispatcher.ACTION_EXECUTE.

```xml
...
        <service
            android:name=".sync.WaterReminderIntentService"
            android:exported="false"/>

        <service
            android:name=".sync.WaterReminderFirebaseJobService"
            android:exported="false">
            <intent-filter>
                <action android:name="com.firebase.jobdispatcher.ACTION_EXECUTE"/>
            </intent-filter>
        </service>

    </application>
...
```

Hasta este punto ya se tiene el servicio completamente desarrollado y en posibilidad de ser programado para ejecución.  En el siguiente paso se utilizará finalmente el FireBaseJobDispatcher para programar la ejecución de este servicio.

##5. Calendarizar el Service con FireBaseJobDispatcher

Para lograr calendarizar nuestro servicio recien creado de manera que pueda ejecutarse bajo las condiciones que se contemplan (cuando el dispositivo se encuentre cargando, que se repita cada 15 minutos, etc.) se realizará lo siguiente:

Primero, para tener todo mas organizado, se creará una clase denominada ReminderUtilities, de preferencia en el paquete sync.  Esta clase tiene la responsabilidad de crear y configurar un FireBaseJobDispatcher que pueda ser lanzado posteriormente desde la MainActivity.  El código es mas o menos el siguiente: 
```java
package com.example.android.background.sync;

import android.content.Context;
import android.support.annotation.NonNull;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class ReminderUtilities {

    //  - REMINDER_INTERVAL_SECONDS es un entero que almacena la cantidad de segundos que tienen 15 minutos puesto que el FireBaseJobDispatcher trabaja en unidades de tiempo a nivel de segundos.
    //  - SYNC_FLEXTIME_SECONDS tambien un entero que almacena la cantidad de segundos, en este caso indicarán el tiempo que se debe esperar entre notificación y notificación 
    //  - REMINDER_JOB_TAG Constante de tipo String, para almacenar algo como "hydration_reminder_tag"
    //  - sInitialized es boleana e indica si el Job ha sido inicializado ya (true) o todavia no (false).
    private static final int REMINDER_INTERVAL_MINUTES = 2;
    private static final int REMINDER_INTERVAL_SECONDS = (int) (TimeUnit.MINUTES.toSeconds(REMINDER_INTERVAL_MINUTES));
    private static final int SYNC_FLEXTIME_SECONDS = REMINDER_INTERVAL_SECONDS;
    private static final String REMINDER_JOB_TAG = "hydration_reminder_tag";
    private static boolean sInitialized;

    // Notese que este metodo es synchronized pues se desea que solamente se ejecute una vez a la vez, 
	// Aquí será creado el FirebaseJobDispatcher para calendarizar el job que se repetirá cada
	// REMINDER_INTERVAL_SECONDS cuando el dispositivo se encuentre cargando. 
	// el FireBaseJobDispatcher disparará nuestro servicio WaterReminderFirebaseJobService
    synchronized public static void scheduleChargingReminder(@NonNull final Context context) {

        // En caso de que el job ya haya sido inicializado, se termina el metodo.
        if (sInitialized) return;

		// En caso de que haya que inicializarlo se deberá:...
        // Crear una instancia de GooglePlayDriver (FireBaseJobDispatcher trabaja bajo GooglePlay y actualmente la gran mayoria de los dispositivos cuentan con esta aplicación instalada de manera predeterminada.)
        Driver driver = new GooglePlayDriver(context);
        // Se crea el FirebaseJobDispatcher mediante el driver
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

		/* Finalmente se crea el Job que periodicamente cree recordatorios para tomar agua */
        // Se construye el Job mediante el metodo newJobBuilder del FireBaseJobDispatcher indicando:
        // - que se tiene que ejecutar el servicio WaterReminderFirebaseJobService
        // - que tiene por identificador la etiqueta REMINDER_JOB_TAG
        // - que solamente se ejecute cuando el dispositivo se encuentre cargando
        // - que su tiempo de vida es perpetuo, nunca muere (inclusive si el dispositivo es reiniciado)
        // - que es recurrente, debe ocurrir repetidamente
        // - que debe ocurrir cada 15 minutes con un marco de tiempo de 15 minutes
        // - que el job actual sea remplazado si actualmente se encuentra en ejecución
        Job constraintReminderJob = dispatcher.newJobBuilder()
                .setService(WaterReminderFirebaseJobService.class)
                .setTag(REMINDER_JOB_TAG)
                .setConstraints(Constraint.DEVICE_CHARGING)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        REMINDER_INTERVAL_SECONDS,
                        REMINDER_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS))
                .setReplaceCurrent(true)
                .build();

        /* Una vez construido el Job se calendariza mediante el FireBaseJobDispatcher */
        dispatcher.schedule(constraintReminderJob);

        /* El Job ha sido inicializado */
        sInitialized = true;
    }
}
```

Una vez creado el Job para ejecutar el Service y programado mediante el FireBaseJobDispatcher dentro de la clase ReminderUtilities, simplemente basta con utilizarla desde nuestro MainActivity para completar el trabajo.  El lugar mas apropiado para realizar esto es el método onCreate de la MainActivity, así que bastará con agregar el siguiente código mas o menos de esta manera:

```java
...
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWaterCountDisplay = (TextView) findViewById(R.id.tv_water_count);
        mChargingCountDisplay = (TextView) findViewById(R.id.tv_charging_reminder_count);
        mChargingImageView = (ImageView) findViewById(R.id.iv_power_increment);

        updateWaterCount();
        updateChargingReminderCount();
        // Esto mandará programar nuestra notificación periodicamente a manera de recordatorio.
        ReminderUtilities.scheduleChargingReminder(this);
        ...
```

Listo para ejecutar la aplicación, pero antes de que hagas eso, hay algunos detalles que atender, primero, para realizar las pruebas tal vez sea conveniente cambiar el intervalo de espera de 15 minutos a 1 o 2, pues si lo dejamos en 15 tendrías que esperar 15 minutos antes de ver el resultado (espero que identifiques la línea de código en la que debes realizar este cambio).  Segundo, como la aplicación considera el estatus del dispositivo en cuanto a si esta cargado o no, y no tienes un cargador a la mano para realizar pruebas veridicas, tal vez desees quitar esa restriccion por un momento, o puedes intentar utilizar algunos comandos ADB como los que se mencionan en (este sitio)[https://stanfy.com/blog/android-shell-part-1-mocking-battery-status/] para tener el control del dispositivo y cambiar su estatus de carga.   

Finalmente, una vez realizadas las pruebas y para que la aplicación quede lista para llevar, elimina el código utilizado que se creó para probar la notificación y listo.

Por el momento, eso es todo amigos.
