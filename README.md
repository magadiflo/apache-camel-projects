# [Learn Apache Camel Framework with Spring Boot](https://www.udemy.com/course/apache-camel-framework-with-spring-boot/)

Curso de `Udemy` tomado del instructor de `in28Minutes Official`.

---

## Crea dos microservicios

Creamos dos microservicios llamados `camel-microservice-a` y `camel-microservice-b`, ambos tendrán las siguientes
dependencias.

````xml
<!--Spring Boot 3.3.5-->
<!--Java 21-->
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.apache.camel.springboot</groupId>
        <artifactId>camel-spring-boot-starter</artifactId>
        <version>4.8.1</version>
    </dependency>

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
````

## ¿Qué son las rutas en Apache Camel?

Las rutas de `Camel` son la columna vertebral de `Apache Camel` y definen cómo los mensajes se mueven desde una fuente
`(endpoint)` hacia un destino, pasando por distintas transformaciones y procesamientos.

En una `ruta Camel`, especificas lo siguiente:

1. `Fuentes de entrada`: De dónde provienen los mensajes, como colas de mensajes, archivos, bases de datos, APIs, etc.

2. `Procesos intermedios`: Los componentes que transforman, filtran, enrutan o de alguna forma procesan los mensajes
   mientras pasan por la ruta.

3. `Destinos`: A dónde se envían los mensajes después de ser procesados.

En resumen, una `ruta de Camel` es una representación declarativa del `flujo de mensajes` dentro de tu aplicación.

## Paso 03. Creando tu primera ruta Apache Camel

A continuación realizaremos un ejemplo para ver nuestro primer acercamiento a las rutas de camel con spring boot. Así
que en nuestro microservicio `camel-microservice-a` creamos una clase llamada `MyFirstTimerRoute` e implementamos el
siguiente código.

````java

@Component
public class MyFirstTimerRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("timer:first-timer")
                .transform().constant("Time now is " + LocalDateTime.now())
                .to("log:first-timer");
    }
}
````

- La clase `MyFirstTimerRoute` extiende de `RouteBuilder`, que es la base para definir las `rutas en Camel`.
- `configure()`, es donde defines las rutas Camel.
- `from("timer:first-timer")`, esta es la fuente de la ruta. Aquí, estás utilizando un componente de temporizador
  `(timer)` llamado `first-timer` que dispara eventos en intervalos de tiempo definidos (por defecto, cada segundo).
- `.transform().constant("Time now is " + LocalDateTime.now())`, transforma el mensaje del temporizador en una cadena
  constante que muestra la hora actual.
- `.to("log:first-timer")`, envía el mensaje transformado a un componente de log `(log)`. Esto básicamente registra el
  mensaje en los logs con la categoría `first-timer`.

Al ejecutar nuestro `camel-microservice-a` veremos en consola la ejecución continua del siguiente log.

````bash
[camel-microservice-a] [r://first-timer] first-timer                              : Exchange[ExchangePattern: InOnly, BodyType: String, Body: Time now is 2024-11-03T12:59:41.942807200]
[camel-microservice-a] [r://first-timer] first-timer                              : Exchange[ExchangePattern: InOnly, BodyType: String, Body: Time now is 2024-11-03T12:59:41.942807200]
[camel-microservice-a] [r://first-timer] first-timer                              : Exchange[ExchangePattern: InOnly, BodyType: String, Body: Time now is 2024-11-03T12:59:41.942807200]
[camel-microservice-a] [r://first-timer] first-timer                              : Exchange[ExchangePattern: InOnly, BodyType: String, Body: Time now is 2024-11-03T12:59:41.942807200]
[camel-microservice-a] [r://first-timer] first-timer                              : Exchange[ExchangePattern: InOnly, BodyType: String, Body: Time now is 2024-11-03T12:59:41.942807200]
...
````

En resumen, este ejemplo configura una ruta que se activa por un temporizador, transforma el mensaje para incluir la
hora actual y lo registra en los logs.

## Paso 04. Usando Beans de Spring para la transformación en rutas de Camel

Podemos crear un bean de spring, por ejemplo una clase anotada con `@Component` para que realice la transformación,
es decir, tener la lógica separada y no hardcodeada como en el ejemplo anterior.

En el siguiente ejemplo creamos la clase `CurrentTimeBean` anotada con `@Component` para poder inyectarlo en la clase
`MyFirstTimerRoute`.

Si no hubiéramos inyectado el componente `CurrentTimeBean`, podríamos haber usado el nombre del bean dentro del
método, de la siguiente manera `.bean("currentTimeBean")`, pero según el instructor esa es una muy mala práctica, ya
que si por alguna razón cambia el bean, tendríamos que ir a buscar a los métodos `bean()` para hacer el cambio.

En nuestro ejemplo, estamos haciendo uso de la inyección de dependencia para usar el objeto inyectado dentro del
método `.bean(this.currentTimeBean, "getCurrentTime")`.

````java

@RequiredArgsConstructor
@Component
public class MyFirstTimerRoute extends RouteBuilder {

    private final CurrentTimeBean currentTimeBean;

    @Override
    public void configure() throws Exception {
        from("timer:first-timer")
                .bean(this.currentTimeBean, "getCurrentTime")
                .to("log:first-timer");
    }
}

@Component
class CurrentTimeBean {
    public String getCurrentTime() {
        return "Time now is " + LocalDateTime.now();
    }
}
````

Otro punto importante es observar que en el método `bean`, además de definir el objeto inyectado, estamos definiéndole
el nombre del método que ejecutaremos. Esto, solo si nuestro `CurrentTimeBean` tiene más de un método. En mi caso, lo
dejaré con el método explícitamente.

**Nota**

> Solo por tema de simplicidad es que creé la clase `CurrentTimeBean` dentro de la misma clase `MyFirstTimerRoute`,
> pero en un mundo real, todo debería estar separado, en su propio archivo.

Hay dos tipos de opciones que puede hacer dentro de un ruta específica:

1. `Procesamiento`, cuando recibo algún mensaje, quiero hacer alguna operación o algo que no suponga un cambio en el
   cuerpo del propio mensaje.

2. `Transformación`, cuando hacemos algo que cambia el cuerpo del mensaje.

Para este nuevo ejemplo vamos a crear un nuevo componente llamado `SimpleLoggingProceesing` y lo vamos a inyectar en
la clase `MyFirstTimerRoute`.

````java

@RequiredArgsConstructor
@Component
public class MyFirstTimerRoute extends RouteBuilder {

    private final CurrentTimeBean currentTimeBean;
    private final SimpleLoggingProceesing simpleLoggingProceesing;

    @Override
    public void configure() throws Exception {
        from("timer:first-timer")
                .log("${body}")
                .bean(this.currentTimeBean, "getCurrentTime")
                .log("${body}")
                .bean(this.simpleLoggingProceesing)
                .to("log:first-timer");
    }
}

// Transformación
@Component
class CurrentTimeBean {
    public String getCurrentTime() {
        return "Time now is " + LocalDateTime.now();
    }
}

//Procesamiento
@Slf4j
@Component
class SimpleLoggingProceesing {
    public void process(String message) {
        log.info("Message: {}", message);
    }
}
````

Entonces, cuando hablamos de `transformación`, nos referimos al cambio que vamos a aplicar al cuerpo del mensaje, tal
como se ve en el bean `CurrentTimeBean` donde retornamos una cadena en el método `getCurrentTime`. Sin embargo, en
la clase `SimpleLoggingProceesing` únicamente estamos imprimiendo en consola el mensaje que llega por parámetro, es
decir, no se está realizando ninguna transformación de los datos, sino más bien, estamos `procesando`.

Si ejecutamos la aplicación veremos que los siguientes registros se repiten constantemente.

````bash
[camel-microservice-a] [r://first-timer] route1                                   : null
[camel-microservice-a] [r://first-timer] route1                                   : Time now is 2024-11-03T20:23:18.672128400
[camel-microservice-a] [r://first-timer] d.m.a.routes.a.SimpleLoggingProceesing   : Message: Time now is 2024-11-03T20:23:18.672128400
[camel-microservice-a] [r://first-timer] first-timer                              : Exchange[ExchangePattern: InOnly, BodyType: String, Body: Time now is 2024-11-03T20:23:18.672128400]
````

La primera línea corresponde al primer `.log("${body}")`, la segunda línea corresponde al segundo `.log("${body}")`, la
tercera línea corresponde al procesamiento que se realiza en el bean `SimpleLoggingProceesing` en su método
`process()`. Finalmente, la cuarta línea corresponde al método `.to("log:first-timer")`.

En el primer bean `(CurrentTimeBean)` hay una `transformación`, mientras que en el segundo bean
`(SimpleLoggingProceesing)` hay un `procesamiento`.

## Paso 05. Procesamiento mediante procesadores Camel en rutas Camel

En este apartado vamos a utilizar el método `process()` de Camel, como una alternativa a realizar los procesamientos.
Así que, crearemos una clase llamada `SimpleLoggingProcessor` quien implementará la interfaz `Processor` y en el método
sobreescrito definiremos el procesamiento.

Posteriormente, en nuestra configuración principal de camel agregaremos `.process(new SimpleLoggingProcessor())` donde
hacemos referencia a la clase `SimpleLoggingProcessor`.

````java

@RequiredArgsConstructor
@Component
public class MyFirstTimerRoute extends RouteBuilder {

    private final CurrentTimeBean currentTimeBean;
    private final SimpleLoggingProceesing simpleLoggingProceesing;

    @Override
    public void configure() throws Exception {
        from("timer:first-timer")
                .log("${body}")                                     // (1)
                .bean(this.currentTimeBean, "getCurrentTime")
                .log("${body}")                                     // (2)
                .bean(this.simpleLoggingProceesing)                 // (3)
                .log("${body}")                                     // (4)
                .process(new SimpleLoggingProcessor())              // (5)
                .to("log:first-timer");                             // (6)
    }
}

// Transformación
@Component
class CurrentTimeBean {
    public String getCurrentTime() {
        return "Time now is " + LocalDateTime.now();
    }
}

//Procesamiento
@Slf4j
@Component
class SimpleLoggingProceesing {
    public void process(String message) {
        log.info("Message: {}", message);
    }
}

@Slf4j
@Component
class SimpleLoggingProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        log.info("Processor: {}", exchange.getMessage().getBody());
    }
}
````

Si luego ejecutamos la aplicación veremos en consola el siguiente resultado, donde el número de línea corresponde
a lo que se enumeró en el método `configure()`.

````bash
(1)[camel-microservice-a] [r://first-timer] route1                                   : null
(2)[camel-microservice-a] [r://first-timer] route1                                   : Time now is 2024-11-04T09:36:04.407947200
(3)[camel-microservice-a] [r://first-timer] d.m.a.routes.a.SimpleLoggingProceesing   : Message: Time now is 2024-11-04T09:36:04.407947200
(4)[camel-microservice-a] [r://first-timer] route1                                   : Time now is 2024-11-04T09:36:04.407947200
(5)[camel-microservice-a] [r://first-timer] d.m.app.routes.a.SimpleLoggingProcessor  : Processor: Time now is 2024-11-04T09:36:04.407947200
(6)[camel-microservice-a] [r://first-timer] first-timer                              : Exchange[ExchangePattern: InOnly, BodyType: String, Body: Time now is 2024-11-04T09:36:04.407947200]
````

**Resumen**

> En una ruta camel hay dos procesos intermedios que se pueden realizar: `transformación` o `procesamiento`.
>
> La `transformación`, se puede realizar con el método `transform()` o con un `bean()`.<br>
> El `procesamiento`, se puede realizar con el método `process()` o con un `bean()`.

## Paso 06. Creando una ruta de Camel para jugar con archivos

Como vamos a crear una nueva clase en este apartado, comentaré la anotación `@Component` de la clase
`MyFirstTimerRoute` para que no se ejecute en automático su código.

Ahora sí, crearemos la clase de componente `MyFileRoute` quien va a extender de la clase abstracta `RouteBuilder`.

````java

@Component
public class MyFileRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("file:files/input")
                .log("${file:name}")
                .to("file:files/output");
    }
}
````

- La clase `MyFileRoute` extiende `RouteBuilder`, lo cual te permite definir `rutas en Camel`.
- El método `configure()` permite definir las rutas.
- `from("file:files/input")`, esta es la fuente de la ruta. Indica que los archivos que se encuentran en la carpeta
  `files/input` serán procesados por esta ruta.
- `.log("${file:name}")`, esta línea registra `(log)` el nombre del archivo que está siendo procesado, utilizando la
  expresión `file:name` para obtener el nombre del archivo.
- `.to("file:files/output")`, esta es la ruta de destino. Indica que los archivos procesados se moverán a la carpeta
  `files/output`.

En resumen, este código toma archivos de la carpeta `files/input`, registra sus nombres en los logs, y luego los mueve
a la carpeta `files/output`.

Si es la primera vez que ejecutamos la aplicación y aún no tenemos el directorio `/files`, la aplicación lo creará
en la raíz de nuestro proyecto principal `apache-camel-projects`. Dentro de dicho directorio, inicialmente creará el
directorio `/input`. Cuando agreguemos algún archivo al directorio `/input`, automáticamente se creará el directorio
`/output` donde se moverá dicho archivo y en el directorio `/input` se creará un directorio oculto `.camel`.

El directorio `.camel` es utilizado por `Apache Camel` para gestionar el procesamiento de archivos. Cuando `Camel`
procesa archivos desde una carpeta de entrada, crea estos directorios ocultos para almacenar temporalmente los archivos
que está procesando. Esto ayuda a evitar la duplicación o el reprocesamiento de los mismos archivos si el proceso se
interrumpe o reinicia.

Así que básicamente, ese directorio oculto `.camel` actúa como un área de trabajo temporal para los archivos que `Camel`
mueve o procesa.

Ahora, agreguemos varios archivos al directorio `/input`, veremos que los nombres de dichos archivos se muestran en
consola y que además, los archivos son movidos al directorio `/output`.

````bash
[camel-microservice-a] [e://files/input] route1                                   : 1000.json
[camel-microservice-a] [e://files/input] route1                                   : 1000.xml
[camel-microservice-a] [e://files/input] route1                                   : 1001.json
[camel-microservice-a] [e://files/input] route1                                   : 1001.xml
[camel-microservice-a] [e://files/input] route1                                   : 1002.json
[camel-microservice-a] [e://files/input] route1                                   : 1002.xml
[camel-microservice-a] [e://files/input] route1                                   : data.csv
[camel-microservice-a] [e://files/input] route1                                   : single-line.csv
````
