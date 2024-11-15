package dev.magadiflo.app.routes.patterns;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class EipPatternsRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("file:files/csv")
                .unmarshal().csv()
                .split(body())
                .to("log:split-files");
    }
}
