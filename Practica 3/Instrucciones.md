#Descripción de la práctica

El propósito de esta practica es que el alumno pueda crear un IntentService para ejecutar una tarea en background.  La implementación surge a partir de una aplicación básica que tiene el propósito de llevar la cuenta de vasos que ha bebido y recordar al usuario beber agua mientras su dispositivo se encuentra cargando batería, pues según los cientificos este es el mejor momento para re-hidratar el cerebro. El código base actualmente solo presenta una interfaz gráfica de usuario que muestra un toast al momento de hacer clic sobre la imagen de un vaso.  Se pretende implementar un IntentService que simule la escritura en background de datos hacia un proveedor de persistencia en la nube, no obstente  en realidad por el momento solamente se hará uso de un archivo [SharedPreferences](https://developer.android.com/training/basics/data-storage/shared-preferences.html) para persistir los datos que nos permitan llevar la cuenta de los vasos que ha bebido y de la cantidad de veces que el usuario ha sido recordado :P

##Instrucciones:
Obtener el código fuente base y modificarlo para:

 1. Crear clase ReminderTasks 
 2. Crear clase WaterReminderIntentService
 3. Registrar el Service en el Manifest
 4. Utilizar el Service para mandar persistir los datos de manera asincrona

A continuación algo de código y su explicación para cada paso de la práctica:

##1. Crear clase ReminderTasks. 
Para lograr este propósito el estudiante debe crear una clase denominada **ReminderTasks** preferentemente en un nuevo paquete denominado **sync** dentro del paquete principal **com.example.android.background**.  El código a agregar en esta clase es mas o menos el siguiente:

```java

```

##2. Crear clase WaterReminderIntentService

De manera semejante al paso anterior, se debe crear una clase denominada **WaterReminderIntentService** en el nuevo paquete anteriormente creado denominado **sync**.  El código a agregar en esta nueva clase es mas o menos el siguiente:

```java

```
##3. Registrar el Service en el Manifest
Para que el Service sea valido debe registrarse en el archivo manifest agregando unas líneas mas o menos como las que siguen:

```xml
...

...
```

##4.Utilizar el Service para mandar persistir los datos de manera asincrona
Para probar rápidamente el funcionamiento del nuevo service y lograr que se mande escribir hacia SharedPreferences de manera asincrona como ya se tiene programado,  habrá que modificar la **MainActivity** de nuestra App actual.  Para lograr esto se deberá modificar el código de esta clase **MainActivity** mas o menos así:

```java

```

Eso es todo amigos.
