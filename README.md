# MISW4203-2026-12-ing-sw-aplicacións-moviles

## Tabla de contenido

1. [Instalación](#instalación)
2. [Ejecución de la aplicación](#ejecución-de-la-aplicación)
3. [Ejecución de pruebas](#ejecución-de-pruebas)

## Instalación

Para instalar la aplicación en su máquina, siga las siguientes instrucciones:

1\. Descargue el código fuente de este proyecto.

2\. Abra Android Studio.

3\. Dé click en File > Open y abra la carpeta que descargó.

## Ejecución de la aplicación

Para ejecutar la aplicación tiene dos opciones: localmente con un emulador, o mediante la apk. A continuación explicamos ambas.

### Ejecución local

Para ejecutar la aplicación localmente, siga las siguientes instrucciones:

1. Encienda el backend. Si no lo tiene instalado, vaya a [BackVinyls](https://github.com/TheSoftwareDesignLab/BackVynils), descargue el proyecto y construya la imagen con Docker. Una vez construida, el back debería correr en el puerto 3000.

2. Abra el proyecto en Android Studio y asegúrese de tener un dispositivo de emulación corriendo. Para ello, diríjase a Device Manager.

3. Diríjase a la parte de arriba y dé click en el triángulo verde de ejecución.

    <img width="401" height="36" alt="image" src="https://github.com/user-attachments/assets/4c8c35ca-b4f3-4984-a685-0621a71f0080" /> <p></p>

4. Listo! La aplicación ya debería estar corriendo en su emulador.

    <img height="400" alt="image" src="https://github.com/user-attachments/assets/088f60c3-1cde-42f3-a08c-166897fc4ee4" /> <p></p>

### Ejecución con apk

1. Encienda el back. BackVinyls se encuentra desplegado en Render. Debido al plan gratuito que se usa, el servidor toma un tiempo en despertarse. Para evitar respuestas demoradas en la aplicación, diríjase a [este enlace](https://vinilos-backend-6ydt.onrender.com) y espere a que vea lo siguiente: 

    <img width="515" height="120" alt="image" src="https://github.com/user-attachments/assets/d6115466-51a1-4b0a-9c3d-f96b76454930" /> <p></p>

2. Con el back corriendo en Render, descargue la apk. Para ello, diríjase al release más reciente y descargue el archivo .apk.

3. Si descargó la apk en su computador, envíela a su dispositivo Android (puede usar Drive para esto).

4. Si descargó la apk en su dispositivo Android, o si la descargó mediante Drive al habérsela enviado, vaya a Descargas e instale la aplicación.

5. Si es necesario, otorgue los permisos para instalar aplicaciones de orígenes desconocidos. No se preocupe, nuestra aplicación no tiene contenido malicioso.

6. Abra la aplicación tocando el ícono.

    <img height="200" alt="image" src="https://github.com/user-attachments/assets/24624d11-3518-485b-b748-656a61a2ce8a" /> <p></p>

7. Listo! Puede disfrutar de la aplicación en su propio dispositivo.

## Ejecución de pruebas

1. Abra el proyecto en Android Studio.

2. Encienda el backend. Si no lo tiene instalado, vaya a [BackVinyls](https://github.com/TheSoftwareDesignLab/BackVynils), descargue el proyecto y construya la imagen con Docker. Una vez construida, el back debería correr en el puerto 3000.

3. Diríjase a kotlin+java > com.misw.app (androidTest).

4. Dé click derecho en com.misw.app (androidTest) y seleccione la opción Run 'Tests' in 'com.misw.a...'
    
5. Listo, puede ver el resultado de las pruebas en la parte del IDE:

    <img height="200" alt="image" src="https://github.com/user-attachments/assets/ca050b1d-2581-42ed-95d6-25c4902ad7ad" />
