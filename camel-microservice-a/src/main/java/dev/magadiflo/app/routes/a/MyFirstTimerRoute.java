package dev.magadiflo.app.routes.a;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyFirstTimerRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("timer:first-timer")
                .transform().constant("Time now is " + LocalDateTime.now())
                .to("log:first-timer");
    }
}
