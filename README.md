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

1. Encienda el backend. Si no lo tiene instalado, vaya a [Vinilos-Backend](https://github.com/Laurarestrepo03/Vinilos-Backend), descargue el proyecto y construya la imagen con Docker. Una vez construida, el back debería correr en el puerto 3000.

    > ⚠️ ADVERTENCIA: Este backend NO es el mismo que [BackVinyls](https://github.com/TheSoftwareDesignLab/BackVynils), pues contiene un endpoint nuevo para los premios de un artista. Si desea consultar más detalles de los cambios implementados, vaya a los Pull Requests del repositorio de Vinilos-Backend.

2. Abra el proyecto en Android Studio y asegúrese de tener un dispositivo de emulación corriendo. Para ello, diríjase a Device Manager.

3. Diríjase a la parte de arriba y dé click en el triángulo verde de ejecución.

    <img width="401" height="36" alt="image" src="https://github.com/user-attachments/assets/4c8c35ca-b4f3-4984-a685-0621a71f0080" /> <p></p>

4. Listo! La aplicación ya debería estar corriendo en su emulador.

    <img height="400" alt="image" src="https://github.com/user-attachments/assets/b2ce5d4f-b783-46e3-93ae-609764e3ac55" /> <p></p>

### Ejecución con apk

1. Encienda el back. BackVinyls se encuentra desplegado en Render. Debido al plan gratuito que se usa, el servidor toma un tiempo en despertarse. Para evitar respuestas demoradas en la aplicación, diríjase a [este enlace](https://vinilos-backend-6ydt.onrender.com) y espere a que vea lo siguiente: 

    <img width="515" height="120" alt="image" src="https://github.com/user-attachments/assets/d6115466-51a1-4b0a-9c3d-f96b76454930" /> <p></p>

2. Con el back corriendo en Render, descargue la apk. Para ello, diríjase al release más reciente y descargue el archivo .apk. Si descargó la apk en su computador, envíela a su dispositivo Android (puede usar Drive para esto).

3. Una vez tenga el apk en su dispositivo Android, vaya a Descargas (o la ubicación en la que se encuentre el archivo) e instale la aplicación.

4. Si es necesario, otorgue los permisos para instalar aplicaciones de orígenes desconocidos. No se preocupe, nuestra aplicación no tiene contenido malicioso.

5. Abra la aplicación tocando el ícono.

    <img height="200" alt="image" src="https://github.com/user-attachments/assets/24624d11-3518-485b-b748-656a61a2ce8a" /> <p></p>

6. Listo! Puede disfrutar de la aplicación en su propio dispositivo.

## Ejecución de pruebas

Antes de ejecutar las pruebas e2e, es importante mencionar que ellas están desacopladas del backend, es decir, no debe tenerlo corriendo. Esto es porque usamos [Mockito](https://site.mockito.org/) para simular la recuperación de datos y códigos de respuesta.

1. Abra el proyecto en Android Studio.

2. Diríjase a kotlin+java > com.misw.app (androidTest).

3. Dé click derecho en com.misw.app (androidTest) y seleccione la opción Run 'Tests' in 'com.misw.a...'
    
4. Listo, puede ver el resultado de las pruebas en la parte de abajo del IDE:

    <img height="200" alt="image" src="https://github.com/user-attachments/assets/75e457d5-410f-4011-a23f-f5ba29167138" />

