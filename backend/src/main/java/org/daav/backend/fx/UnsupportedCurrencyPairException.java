package org.daav.backend.fx;

import java.util.Currency;

public class UnsupportedCurrencyPairException extends RuntimeException {

	public UnsupportedCurrencyPairException(Currency sourceCurrency, Currency targetCurrency) {
		super("Unsupported currency pair: " + sourceCurrency.getCurrencyCode()
				+ " to " + targetCurrency.getCurrencyCode());
	}
}
