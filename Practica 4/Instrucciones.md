#Descripción de la práctica 4 **Tema de interés: Uso de Notificaciones**

El propósito de esta practica es complementar la práctica 3 para que el alumno pueda lanzar notificaciones rumbo a la construcción de un Job Service que sea capaz de lanzar periodicamente estas notificación cuando el dispositivo se encuentre cargando bajo ciertas condiciones de funcionamiento. 

##Instrucciones:
Obtener el código fuente base y modificarlo para:

Fase 1: Preparar el entorno para lanzar Notificaciones
 1. Crear clase NotificationUtils 
 2. Modificar Manifiesto 

Fase 2: Agregar acciones específicas a las Notificaciones
 3. Actualizar el código de la clase ReminderTasks
 4. Actualizar el código de la clase NotificationUtils

A continuación algo de código y su explicación para cada paso de la práctica:

##1. Crear clase NotificationUtils. 
Para lograr este propósito el estudiante debe crear una clase denominada **NotificationUtils** preferentemente dentro del paquete denominado **utilities** dentro del paquete principal **com.example.android.background**.  El código a agregar en esta clase es mas o menos el siguiente:

```java
package com.example.android.background.utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.example.android.background.MainActivity;
import com.example.android.background.R;

public class NotificationUtils {

    /*
     * Esta constante se refuere al ID que puede ser utilizado para acceder a nuestra notificación una vez que 
     * ha sido mostrada.  Esto puede ser útil cuando se necesita cancelar la notificación o actualizarla. Este
     * es un número arbitrario y puede ser establecido a cualquier valor que gustes. 1138 no significa nada especial.
     */
    private static final int WATER_REMINDER_NOTIFICATION_ID = 1138;
    /**
     * Este ID es utilizado para identificar y referenciar de manera única al pending intent
     */
    private static final int WATER_REMINDER_PENDING_INTENT_ID = 3417;

    // Este método creará una notificaión cuando el dispositivo se cargue. Puede ser de utilidad para
    // comprender mejor echarle un vistazo a la guía del siguiente link para ver los ejemplos en los que
    // se basa el código: https://developer.android.com/training/notify-user/build-notification.html
    public static void remindUserBecauseCharging(Context context) {
        // - Tiene el color de R.colorPrimary - se utiliza ContextCompat.getColor para obtener un color compatible
        // - tiene la ic_drink_notification como el icono pequeño
        // - utiliza el icono devuelto por el método largeIcon helper como el icono grande
        // - establece el titulo al valor del String en recursos: charging_reminder_notification_title
        // - establece el texto al valor del String en recursos: charging_reminder_notification_body 
        // - establece el estilo a NotificationCompat.BigTextStyle().bigText(text)
        // - establece el default de notification a "vibrar"
        // - utiliza el content intent devuelto por el metodo auxiliar contentIntent para el contentIntent
        // - automaticamente cancela la notificación cuando se le da clic a esta
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_drink_notification)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.charging_reminder_notification_title))
                .setContentText(context.getString(R.string.charging_reminder_notification_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.charging_reminder_notification_body)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .setAutoCancel(true);

        // Si el build version es mayor que JELLY_BEAN, establece la prioridad de la notificación a PRIORITY_HIGH.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        // Obtiene un NotificationManager, utilizando context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Dispara la notificación invocando notify en el NotificationManager.
        // Pasa el ID que se definió para la notification y el notificationBuilder.build()
        notificationManager.notify(WATER_REMINDER_NOTIFICATION_ID, notificationBuilder.build());
    }

    // Este es un metodo auxiliar con un único parametro, el contexto, devuelve un PendingIntent
    // el cual es creado cuando la notificación es presionada.  Este pending intent debería abrir
    // nuevamente la MainActivity
    private static PendingIntent contentIntent(Context context) {
        // Se crea un intent para abrir la MainActivity
        Intent startActivityIntent = new Intent(context, MainActivity.class);
        // Se crea un PendingIntent utilizando getActivity que:
        // - Toma el contexto pasado como parametro
        // - toma el ID enero único definido anteriormente
        // - toma el intent para abrir la MainActivity que ha sido creada;
        // - Tiene la bandera FLAG_UPDATE_CURRENT, para que si el intent es creado nuevamente, 
        //   mantenga el intent pero actualice los datos
        return PendingIntent.getActivity(
                context,
                WATER_REMINDER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }


    // Metodo auxiliar que toma el contexto como parametro de entrada y devuelve Bitmap. 
    // Este metodo es necesario para decodificar el bitmap requerido por la notificación.
    private static Bitmap largeIcon(Context context) {
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.ic_local_drink_black_24px);
        return largeIcon;
    }
}
```

##2. Modificar Manifiesto

Actualizar el manifiesto agregando lo siguiente:

a) Agregar el permiso para vibrar
```xml
...
    <uses-permission android:name="android.permission.VIBRATE" />
...
```
b) Agregar el atributo launchMode a "singleTop" sobre la MainActivity para que cuando esta sea abierta utilizando una notificación no sea generada una nueva sino que se abra la existente.
```xml
...
        <activity
            android:name=".MainActivity"
            android:launchMode="singleTop"
            android:screenOrientation="portrait">
            ...
```


Hasta este momento **ya deberías ser capaz de lanzar una notificación**, si deseas probar el código (esto es opcional pero deseable pues te servirá para los próximos 2 pasos) puedes agregar un botón en el layout que dispare el lanzamiento de nuestra notificación.  Solo tienes que hacer lo siguiente:

a) Al final del archivo del layout **activity_main.xml** agrega un botón que responda al onClick de la siguiente manera:
```xml
    ...
    </RelativeLayout>

    <Button
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:onClick="testNotification"
        android:text="Probar Notificación" />

</LinearLayout>
```

b) En algun lugar dentro de la clase de **MainActivity** agrega el método **testNotification** correspondiente de la siguiente forma:
``` java
    ...
    
    public void testNotification(View view) {
        NotificationUtils.remindUserBecauseCharging(this);
    }
    
    ...
```

Ejecuta tu aplicación y prueba haciendo clic en el botón "Probar Notificación" y observa el resultado.  **No olvides** que este botón es solamente para hacer pruebas, más adelante se te pedirá remover este botón del layout y el método testNotification de la MainActivity.

##3. Actualizar el código de la clase ReminderTasks
Realizar los siguientes 3 cambios a la clase ReminderTask:

```java
...
public class ReminderTasks {

    public static final String ACTION_INCREMENT_WATER_COUNT = "increment-water-count";
    // 1. Agregar una constante publica denominada ACTION_DISMISS_NOTIFICATION que será utilizada para indicar que el usuario hizo caso omiso de la notificación
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";

    public static void executeTask(Context context, String action) {
        if (ACTION_INCREMENT_WATER_COUNT.equals(action)) {
            incrementWaterCount(context);
        } else if (ACTION_DISMISS_NOTIFICATION.equals(action)) {
            NotificationUtils.clearAllNotifications(context);
        }
        // 2. Agregar un else para quitar la notificacón cuando el usuario ignore el recordatorio, 
        //    el método clearAllNotifications será creado en la clase NotificationUtils a continuación
    }

    private static void incrementWaterCount(Context context) {
        PreferenceUtilities.incrementWaterCount(context);
        // 3. Agregar que si el contador fue incrementado se quite cualquier notificación
        NotificationUtils.clearAllNotifications(context);
    }
}
```

##4. Actualizar el código de la clase NotificationUtils
Realizar los siguientes 6 cambios a la clase NotificationUtils:

```java
...
//1. Agregar específicamente el siguiente import, pues existe un gran riesgo de confusión sobre que tipo de Action utilizar: 
import android.support.v4.app.NotificationCompat.Action;
...
public class NotificationUtils {

    private static final int WATER_REMINDER_NOTIFICATION_ID = 1138;
    private static final int WATER_REMINDER_PENDING_INTENT_ID = 3417;
    
    // 2. Agregar estas nuevas constantes para identificar los intents cuando el usuario atienda la notificación y cuando la ignore.
    private static final int ACTION_DRINK_PENDING_INTENT_ID = 1;
    private static final int ACTION_IGNORE_PENDING_INTENT_ID = 14;

    // 3. Crear el método al que se refería el paso anterior para quitar todas las notificaciones
    public static void clearAllNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static void remindUserBecauseCharging(Context context) {

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_drink_notification)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.charging_reminder_notification_title))
                .setContentText(context.getString(R.string.charging_reminder_notification_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.charging_reminder_notification_body)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                // 4. Agregar este par de nuevas acciones a la notificación indicando sus métodos correspondientes
                .addAction(drinkWaterAction(context))
                .addAction(ignoreReminderAction(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        }

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(WATER_REMINDER_NOTIFICATION_ID, notificationBuilder.build());
    }
    
    // 5. Agregar un método estático denominado ignoreReminderAction que...
    private static Action ignoreReminderAction(Context context) {
        // crea un Intent para lanzar el WaterReminderIntentService...
        Intent ignoreReminderIntent = new Intent(context, WaterReminderIntentService.class);
        // configura la acción del intent para designar que se ignoró la notificación...
        ignoreReminderIntent.setAction(ReminderTasks.ACTION_DISMISS_NOTIFICATION);
        // crea un PendingIntent desde el intent para lanzar el WaterReminderIntentService...
        PendingIntent ignoreReminderPendingIntent = PendingIntent.getService(
                context,
                ACTION_IGNORE_PENDING_INTENT_ID,
                ignoreReminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // crea un Action para que el usuario ignore la notificación (y la descarte)...
        Action ignoreReminderAction = new Action(R.drawable.ic_cancel_black_24px,
                "No, gracias.",
                ignoreReminderPendingIntent);
        // devuelve tal accion.
        return ignoreReminderAction;
    }

    // 6. Agregar un método estatico denominado drinkWaterAction que...
    private static Action drinkWaterAction(Context context) {
        // crea un Intent para lanzar el WaterReminderIntentService
        Intent incrementWaterCountIntent = new Intent(context, WaterReminderIntentService.class);
        // configura la acción del intent para designar que se incrementó el contador de water count...
        incrementWaterCountIntent.setAction(ReminderTasks.ACTION_INCREMENT_WATER_COUNT);
        // crea un PendingIntent desde el intent para lanzar el WaterReminderIntentService...
        PendingIntent incrementWaterPendingIntent = PendingIntent.getService(
                context,
                ACTION_DRINK_PENDING_INTENT_ID,
                incrementWaterCountIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        // crea una Action para que el usuario nos indique que ha tomado un vaso de agua...
        Action drinkWaterAction = new Action(R.drawable.ic_local_drink_black_24px,
                "Ya tome!",
                incrementWaterPendingIntent);
        // devuelve tal accion.
        return drinkWaterAction;
    }

    private static PendingIntent contentIntent(Context context) {
        Intent startActivityIntent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(
                context,
                WATER_REMINDER_PENDING_INTENT_ID,
                startActivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static Bitmap largeIcon(Context context) {
        Resources res = context.getResources();
        Bitmap largeIcon = BitmapFactory.decodeResource(res, R.drawable.ic_local_drink_black_24px);
        return largeIcon;
    }
}
```

Si anteriormente habías agregado el botón para realizar pruebas en este momento **ya deberías ser capaz de lanzar una notificación con un par de acciones**, si ya tomaste agua puedes indicarle que ya lo hiciste y esto incrementará el contador, si deseas omitir la notificación esta es la segunda acción.  Si aún no haz agregado el botón para probar y deseas ver el código en acción (esto es opcional otra vez) puedes agregar el botón en el layout que dispara el lanzamiento de nuestra notificación como se había mencionado anteriormente. 

Ahora finalmente viene la parte interesante sobre el Job Service los veo en la siguiente práctica...
