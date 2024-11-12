package dev.magadiflo.app.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class KafkaReceiverRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("kafka:my-kafka-topic")
                .to("log:received-message-from-kafka");
    }
}
