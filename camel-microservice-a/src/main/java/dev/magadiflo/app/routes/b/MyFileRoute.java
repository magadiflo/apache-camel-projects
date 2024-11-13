package dev.magadiflo.app.routes.b;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MyFileRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("file:files/input")
                .routeId("files-input-route-id")
                .transform().body(String.class)
                //
                .choice()
                .when(simple("${file:ext} ends with 'xml'"))
                .log("XML FILE")
                .when(simple("${body} contains 'USD'"))
                .log("No es un archivo XML pero contiene USD")
                .otherwise()
                .log("No es un archivo XML")
                .end()
                //
                .log("${messageHistory} ${file:absolute.path}")
                .log("${file:name} ${file:name.ext} ${file:name.noext} ${file:onlyname}")
                .log("${file:onlyname.noext} ${file:parent} ${file:path} ${file:absolute}")
                .log("${file:size} ${file:modified}")
                .log("${routeId} ${camelId} ${body}")
                .to("file:files/output");
    }
}
