package dev.magadiflo.app.routes.patterns;

import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class EipPatternsRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        String routingSlip = "direct:endpoint1,direct:endpoint3";

        from("timer:routingSlip?period=10000")
                .transform().constant("Mi mensaje está hardcodeado")
                .routingSlip(simple(routingSlip));

        from("direct:endpoint1")
                .to("log:direct-endpoint1");

        from("direct:endpoint2")
                .to("log:direct-endpoint2");

        from("direct:endpoint3")
                .to("log:direct-endpoint3");
    }
}