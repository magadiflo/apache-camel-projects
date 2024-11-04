package dev.magadiflo.app.routes.a;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class MyFirstTimerRoute extends RouteBuilder {

    private final CurrentTimeBean currentTimeBean;
    private final SimpleLoggingProceesing simpleLoggingProceesing;

    @Override
    public void configure() throws Exception {
        from("timer:first-timer")
                .log("${body}")
                .bean(this.currentTimeBean, "getCurrentTime")
                .log("${body}")
                .bean(this.simpleLoggingProceesing)
                .to("log:first-timer");
    }
}

// Transformación
@Component
class CurrentTimeBean {
    public String getCurrentTime() {
        return "Time now is " + LocalDateTime.now();
    }
}

//Procesamiento
@Slf4j
@Component
class SimpleLoggingProceesing {
    public void process(String message) {
        log.info("Message: {}", message);
    }
}