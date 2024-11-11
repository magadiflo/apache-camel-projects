package dev.magadiflo.app.routes;

import dev.magadiflo.app.models.CurrencyExchange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@RequiredArgsConstructor
@Component
public class ActiveMqReceiverRoute extends RouteBuilder {

    private final MyCurrencyExchangeProcessor myCurrencyExchangeProcessor;
    private final MyCurrencyExchangeTransformer myCurrencyExchangeTransformer;

    @Override
    public void configure() throws Exception {
        from("activemq:my-activemq-xml-queue")
                .unmarshal().jacksonXml(CurrencyExchange.class)
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

@Slf4j
@Component
class MyCurrencyExchangeTransformer {

    public CurrencyExchange processMessage(CurrencyExchange currencyExchange) {
        BigDecimal multiply = currencyExchange.getConversionMultiple().multiply(BigDecimal.TEN);
        currencyExchange.setConversionMultiple(multiply);
        return currencyExchange;
    }
}
