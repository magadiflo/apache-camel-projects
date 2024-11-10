package dev.magadiflo.app.routes;

import dev.magadiflo.app.models.CurrencyExchange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ActiveMqReceiverRoute extends RouteBuilder {

    private final MyCurrencyExchangeProcessor myCurrencyExchangeProcessor;

    @Override
    public void configure() throws Exception {
        from("activemq:my-activemq-queue")
                .unmarshal().json(JsonLibrary.Jackson, CurrencyExchange.class)
                .bean(this.myCurrencyExchangeProcessor)
                .to("log:received-message-from-active-mq");
    }
}

@Slf4j
@Component
class MyCurrencyExchangeProcessor {

    public void processMessage(CurrencyExchange currencyExchange) {
        log.info("Realiza procesamiento con currencyExchange.getConversionMultiple() cuyo valor es {}", currencyExchange.getConversionMultiple());
    }

}
