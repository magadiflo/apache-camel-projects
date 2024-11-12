package dev.magadiflo.app.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class RestApiConsumerRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        restConfiguration()
                .host("localhost")
                .port(8000);

        from("timer:rest-api-consumer?period=10000")
                .setHeader("from", () -> "EUR")
                .setHeader("to", () -> "PEN")
                .log("${body}")
                .to("rest:get:/api/v1/currencies/currency-exchange/from/{from}/to/{to}")
                .log("${body}");
    }
}
