package dev.magadiflo.app.routes.b;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

//@Component
public class MyFileRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("file:files/input")
                .log("${file:name}")
                .to("file:files/output");
    }
}
