package dev.magadiflo.app.routes.patterns;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Body;
import org.apache.camel.ExchangeProperties;
import org.apache.camel.Headers;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;


@RequiredArgsConstructor
@Component
public class EipPatternsRoute extends RouteBuilder {

    private final DynamicRouterBean dynamicRouterBean;

    @Override
    public void configure() throws Exception {
        from("timer:dynamicRouter?period=10000")
                .transform().constant("Mi mensaje está hardcodeado")
                .dynamicRouter(method(this.dynamicRouterBean, "decideTheNextEndpoint"));

        from("direct:endpoint1")
                .to("log:direct-endpoint1");

        from("direct:endpoint2")
                .to("log:direct-endpoint2");

        from("direct:endpoint3")
                .to("log:direct-endpoint3");
    }
}

@Slf4j
@Component
class DynamicRouterBean {

    private int invocations;

    public String decideTheNextEndpoint(@ExchangeProperties Map<String, String> properties,
                                        @Headers Map<String, String> headers,
                                        @Body String body) {
        log.info("properties: {}", properties);
        log.info("headers: {}", headers);
        log.info("body: {}", body);
        this.invocations++;

        if (this.invocations % 3 == 0) return "direct:endpoint1";
        if (this.invocations % 3 == 1) return "direct:endpoint2,direct:endpoint3";

        return null;
    }
}