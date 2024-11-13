package dev.magadiflo.app.routes.b;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MyFileRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("file:files/input")
                .routeId("files-input-route-id")
                //
                .choice()
                .when(simple("${file:ext} ends with 'xml'"))
                .log("XML FILE")
                .otherwise()
                .log("No es un archivo XML")
                .end()
                //
                .log("${file:name}")
                .to("file:files/output");
    }
}
