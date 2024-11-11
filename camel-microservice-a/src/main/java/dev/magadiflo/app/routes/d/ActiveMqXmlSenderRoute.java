package dev.magadiflo.app.routes.d;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ActiveMqXmlSenderRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("file:files/xml")
                .log("${body}")
                .to("activemq:my-activemq-xml-queue");
    }
}
