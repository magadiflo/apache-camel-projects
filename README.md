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

