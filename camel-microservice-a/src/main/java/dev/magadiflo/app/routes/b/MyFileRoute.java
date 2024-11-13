package dev.magadiflo.app.routes.b;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Body;
import org.apache.camel.Headers;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class MyFileRoute extends RouteBuilder {

    private final DeciderBean deciderBean;

    @Override
    public void configure() throws Exception {
        from("file:files/input")
                .routeId("files-input-route-id")
                .transform().body(String.class)
                //
                .choice()
                .when(simple("${file:ext} ends with 'xml'"))
                .log("XML FILE")
                .when(method(this.deciderBean, "isThisConditionMethod2"))
                .log("No es un archivo XML pero contiene USD")
                .otherwise()
                .log("No es un archivo XML")
                .end()
                //
                .to("file:files/output");
    }
}

@Slf4j
@Component
class DeciderBean {
    public boolean isThisConditionMethod(String body) {
        log.info("{}", body);
        return true;
    }

    public boolean isThisConditionMethod2(@Body String body, @Headers Map<String, String> headers) {
        log.info("{}", body);
        log.info("{}", headers);
        return true;
    }
}