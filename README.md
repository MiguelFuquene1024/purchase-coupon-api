
# purchase-coupon-api
Api de compra de cupones(Nivel 1)

## Autor

- Miguel Angel Fuquene Arias

## Contrucción

- IntelliJ-Editor de código y ambiente de desarrollo(IDE)
- Maven-gestor de dependencias
- SpringBoot- Framework creación de APIs
- MySql- Base de datos
- Postman- Herramienta para envio de solicitudes a servidores web y recibir las respuestas correspondientes.
- Docker - Plataforma de contenedores para desplegar y prueba del proyecto como segunda opcion.
- Amazon Web Services(Servidor para despliegue y configuracion de servicios).
- Amazon DynamoDB(Db para gestion de información)


## Requerimientos
- Java 17 o superior
- Tener un Ide como IntelliJ, Eclipse o STS
- Spring framework
- Maven 3.0 o superior
- Tener instalado Postman para envio de peticiones


- En su directorio de preferencia ejecute el comando git clone https://github.com/MiguelFuquene1024/purchase-coupon-api.git
- Acceda a su IDE por ejemplo IntelliJ, busque la ruta donde clono el repositorio y abra el proyecto.
- Abra la terminal y ejecute los siguientes comandos
  . mvn clean
  . mvn compile
- Luego corra la aplicación,deberá verse como la imagen a continuación:

![]()


- Esta Api tendrá una funcionalidades principal:
- Puede maximizar a partir de un array de productos favoritos y un monto de cupón,la cantidad de productos que puede canjear con dicho cupon.
- A partir de estos productos se guardaran en una db cada uno de estos con la cantidad de veces que este ha sigo canjeado.


## Pasos para ejecucion

- si ya compilo el proyecto, el siguiente paso es ejecutarlo con mvn spring-boot:run o simplemente darle click derecho en la clase main(CouponApplication) y darle run en caso de intellij.

## Prueba local
- Ahora para probar el servicio ingrese a postman , cree una nueva petición de tipo POST, pegue la siguiente url (http://localhost:8080/coupon) y en el body haga un JSON con un arreglo de nombres de productos y el monto del cupón como se ve la siguiente imagen.
- La respuesta tambien se evidenciara en la imagen:

![]()

## Prueba por el navegador

- Para probar el servicio expuesto en AWS cambie o cree un nuevo request con la siguiente url (http://lb-aws-api-1655684714.us-east-1.elb.amazonaws.com/coupon) y envie en el body nuevamente un json con al misma estructura anterior.
- Debe responder como se muestra en la imagen.

![]()


