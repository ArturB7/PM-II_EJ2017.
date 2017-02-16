package com.example.android.background.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.RetryStrategy;

// WaterReminderFirebaseJobService debe heredar de JobService de firebase
public class WaterReminderFirebaseJobService extends JobService {

    // Esta variable almacenará la tarea asincrona (es decir para ejecutar en background)
    // que se creará y disparará cuando el Job sea iniciado.
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

        // Aqui se crea la AsyncTask anonima sobreescribiendo sus metodos indicando el trabajo a
        // realizar en background.
        mBackgroundTask = new AsyncTask() {

            // Esto se debe realizar en background
            @Override
            protected Object doInBackground(Object[] params) {
                // Se utiliza el metodo executeTask de la clase ReminderTask para mandar realizar
                // la accion programada para el recordatorio.
                Context context = WaterReminderFirebaseJobService.this;
                ReminderTasks.executeTask(context, ReminderTasks.ACTION_CHARGING_REMINDER);
                return null;
            }

            //Este metodo se invoca una vez que el AsyncTask ha terminado de ejecutarse en
            // background
            @Override
            protected void onPostExecute(Object o) {
                // Para indicar al JobManager de android que todo salio bien, se debe invocar
                // el metodo jobFinished, con los parametros que se habian pasado al Job y el
                // argumento de falso para indicarle que todo salio bien y que no es necesario
                // volver a ejecutar el Job.
                jobFinished(jobParameters, false);
            }
        };

        // Aqui se manda ejecutar el AsyncTask recien creado
        mBackgroundTask.execute();

        // Se devuelve true pues se ha iniciado el Job satiscatoriamente
        return true;
    }

    /**
     * Este metodo es invocado cuando el motor de calendarización interrumpe la ejecución de un job
     * que en ese momento esta corriendo, por ejemplo si se crease un job para sincronizar archivos
     * locales hacia la nube y de repente se pierde la conexion a internet totalmente el job no
     * podría continuar en ese momento es disparado este metodo onStopJob.
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