package dev.magadiflo.app.routes.c;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ActiveMqSenderRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("file:files/json")
                .log("${body}")
                .to("activemq:my-activemq-queue");
    }
}
