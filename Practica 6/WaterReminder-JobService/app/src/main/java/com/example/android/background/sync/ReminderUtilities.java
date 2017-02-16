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
    private static final int REMINDER_INTERVAL_MINUTES = 15;
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