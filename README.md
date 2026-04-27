# MISW4203-2026-12-ing-sw-aplicacións-moviles

## Tabla de contenido

1. [Instalación y ejecución de la aplicación]
2. [Ejeción de la aplicación](#ejecuión-de-la-aplicación)
3. [Ejecución de pruebas](#ejecución-de-pruebas)

## Instalación y ejecución de la aplicación

Para instalar la aplicación en su máquina, siga las siguientes instrucciones:

1. Descargue el código fuente de este proyecto.

2. Abra Android Studio.

3. Diríjase a Android Studio. Dé click en File -> Open y abra la carpeta que descargó.

## Ejecución de la aplicación

Para ejecutar la aplicaciones tiene dos opciones: localmente con un emulador, o mediante la apk. A continuación explicamos ambas.

### Ejecución local

Para ejecutar la aplicación localmente, siga las siguientes instrucciones:

1. Encienda el backend. Si no lo tiene instalado, vaya a [BackVinyls](https://github.com/TheSoftwareDesignLab/BackVynils), descargue el proyecto y construya la imagen con Docker. Una vez construida, el back debería correr en el puerto 3000.

2. Abra el proyecto en Android Studio y asegúrese de tener un dispositivo de emulación corriendo. Para ello, diríjase a Device Manager.

3. Diríjase a la parte de arriba y dé click en el triángulo de verde de ejecución.

[IMAGEN]

4. Listo, la aplicación ya debería estar corriendo en su emulador.

[IMAGEN] 

### Ejecución con apk

1. Encienda el back. BackVinyls se encuentra desplegado en Render. Debido al plan gratuito que se usa, el servidor toma un tiempo en despertarse. Para evitar respuestas demoradas en la aplicación, diríjase a [este enlace](https://vinilos-backend-6ydt.onrender.com) y espere a que vea lo siguiente: 

[IMAGEN]

2. Con el back corriendo en Render, descargue la apk. Puede usar [este enlace](ENLACE).

3. Si descargó la apk en su computador, envíela a su dispositivo Android (puede usar Drive para esto).

4. Si descargó la apk en su dispositivo Android, o si la descargó mediante Drive al habérsela enviado, vaya a Descargas e instale la aplicación.

5. Si es necesario, otorgue los permisos para instalar aplicaciones de orígenes desconocidos. No se preocupe, nuestra aplicación no tiene contenido malicioso.

6. Abra la aplicación haciendo tap en el ícono.

[IMAGEN]

7. Listo! Puede disfrutar de la aplicación en su propio dispositivo.

## Ejecución de pruebas

1. Abra el proyecto en Android Studio.

2. Encienda el backend. Si no lo tiene instalado, vaya a [BackVinyls](https://github.com/TheSoftwareDesignLab/BackVynils), descargue el proyecto y construya la imagen con Docker. Una vez construida, el back debería correr en el puerto 3000.

3. Diríjase a kotlin+java > com.misw.app (androidTest).

4. Dé click derecho en com.misw.app (androidTest) y seleccione la opción Run 'Tests' in 'com.misw.a...'

[IMAGEN]

5. Listo, puede ver el resultado de las pruebas en la parte del IDE:

[IMAGEN]