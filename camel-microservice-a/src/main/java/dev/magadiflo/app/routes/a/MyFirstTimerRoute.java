package dev.magadiflo.app.routes.a;

import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class MyFirstTimerRoute extends RouteBuilder {

    private final CurrentTimeBean currentTimeBean;

    @Override
    public void configure() throws Exception {
        from("timer:first-timer")
                .bean(this.currentTimeBean, "getCurrentTime")
                .to("log:first-timer");
    }
}

@Component
class CurrentTimeBean {
    public String getCurrentTime() {
        return "Time now is " + LocalDateTime.now();
    }
}