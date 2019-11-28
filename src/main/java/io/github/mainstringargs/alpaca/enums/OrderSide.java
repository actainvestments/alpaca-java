package io.github.mainstringargs.alpaca.enums;

import io.github.mainstringargs.abstracts.enums.APIName;

/**
 * The Enum OrderSide.
 */
public enum OrderSide implements APIName {

    /** The buy. */
    BUY("buy"),

    /** The sell. */
    SELL("sell");

    /** The api name. */
    String apiName;

    /**
     * Instantiates a new order side.
     *
     * @param apiName the api name
     */
    OrderSide(String apiName) {
        this.apiName = apiName;
    }

    @Override
    public String getAPIName() {
        return apiName;
    }
}
