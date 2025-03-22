package org.lytvinenko.com.kastaparsinglytvinenko.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExchangeRate {
    @JsonProperty("ccy")
    private String currency;

    @JsonProperty("buy")
    private Double buy;

    @JsonProperty("sale")
    private Double sale;

    @JsonProperty("base_ccy")
    private String baseCurrency;
}
