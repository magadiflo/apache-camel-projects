package dev.magadiflo.app.controller;

import dev.magadiflo.app.models.CurrencyExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping(path = "/api/v1/currencies")
public class CurrencyExchangeController {

    @GetMapping(path = "/currency-exchange/from/{from}/to/{to}")
    public ResponseEntity<CurrencyExchange> findConversionValue(@PathVariable String from, @PathVariable String to) {
        return ResponseEntity.ok(new CurrencyExchange(1003L, from, to, BigDecimal.TEN));
    }

}
