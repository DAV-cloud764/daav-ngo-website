package org.daav.backend.fx;

import java.time.Clock;
import java.time.Instant;
import java.util.Currency;
import org.springframework.stereotype.Component;

@Component
public class ConfiguredExchangeRateProvider implements ExchangeRateProvider {

	private static final Currency TZS = Currency.getInstance("TZS");
	private static final Currency USD = Currency.getInstance("USD");

	private final FxProperties fxProperties;
	private final Clock clock;

	public ConfiguredExchangeRateProvider(FxProperties fxProperties, Clock clock) {
		this.fxProperties = fxProperties;
		this.clock = clock;
	}

	@Override
	public ExchangeRateQuote getExchangeRate(Currency sourceCurrency, Currency targetCurrency) {
		if (!TZS.equals(sourceCurrency) || !USD.equals(targetCurrency)) {
			throw new UnsupportedCurrencyPairException(sourceCurrency, targetCurrency);
		}

		return new ExchangeRateQuote(
				sourceCurrency,
				targetCurrency,
				fxProperties.tzsPerUsd(),
				Instant.now(clock),
				fxProperties.source()
		);
	}
}
