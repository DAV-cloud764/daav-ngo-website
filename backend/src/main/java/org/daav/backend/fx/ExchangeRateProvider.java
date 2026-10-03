package org.daav.backend.fx;

import java.util.Currency;

public interface ExchangeRateProvider {

	ExchangeRateQuote getExchangeRate(Currency sourceCurrency, Currency targetCurrency);
}
