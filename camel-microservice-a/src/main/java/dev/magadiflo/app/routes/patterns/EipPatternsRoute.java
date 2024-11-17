package dev.magadiflo.app.routes.patterns;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class EipPatternsRoute extends RouteBuilder {

    private final SplitterComponent splitterComponent;

    @Override
    public void configure() throws Exception {
        from("file:files/csv")
                .convertBodyTo(String.class)
                .split(method(this.splitterComponent))
                .to("log:split-files");
    }
}

@Slf4j
@Component
class SplitterComponent {
    public List<String> splitInput(String body) {
        log.info("body: {}", body);
        return List.of("abc", "def", "ghi");
    }
}
