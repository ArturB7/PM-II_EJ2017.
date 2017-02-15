package com.example.android.background.sync;

import android.content.Context;

import com.example.android.background.utilities.NotificationUtils;
import com.example.android.background.utilities.PreferenceUtilities;

public class ReminderTasks {

    //  Constante que nos servirá para indicarle a este task que se desea incrementar el contador de agua tomada
    public static final String ACTION_INCREMENT_WATER_COUNT = "increment-water-count";

    // constante publica denominada ACTION_DISMISS_NOTIFICATION que será utilizada para indicar que el usuario hizo caso omiso de la notificación
    public static final String ACTION_DISMISS_NOTIFICATION = "dismiss-notification";

    // Si la  acción es ACTION_INCREMENT_WATER_COUNT, incrementar el contador de agua
    public static void executeTask(Context context, String action) {
        if (ACTION_INCREMENT_WATER_COUNT.equals(action)) {
            incrementWaterCount(context);
        }else if (ACTION_DISMISS_NOTIFICATION.equals(action)) { // else para quitar la notificacón cuando el usuario ignore el recordatorio.
            NotificationUtils.clearAllNotifications(context);
        }

    }

    //incrementar el contador de agua mediante la clase PreferenceUtilities
    private static void incrementWaterCount(Context context) {
        PreferenceUtilities.incrementWaterCount(context);
        // Si el contador fue incrementado se quitar cualquier notificación, el método clearAllNotifications será creado en la clase NotificationUtils a continuación
        NotificationUtils.clearAllNotifications(context);
    }
}