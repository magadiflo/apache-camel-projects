package dev.magadiflo.app.routes.patterns;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class EipPatternsRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("timer:multicast?period=10000")
                .multicast()
                .to("log:registro-01", "log:registro-02", "log:registro-03");
    }
}
