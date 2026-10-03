package org.daav.backend.fx;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Currency;
import org.junit.jupiter.api.Test;

class ConfiguredExchangeRateProviderTests {

	private static final Clock FIXED_CLOCK = Clock.fixed(
			Instant.parse("2026-09-04T10:00:00Z"),
			ZoneOffset.UTC
	);

	@Test
	void returnsConfiguredTzsToUsdRateWithAuditMetadata() {
		var properties = new FxProperties(
				new BigDecimal("2500"),
				"CONFIGURED_DEVELOPMENT_RATE",
				RoundingMode.HALF_UP,
				2
		);
		var provider = new ConfiguredExchangeRateProvider(properties, FIXED_CLOCK);

		var quote = provider.getExchangeRate(
				Currency.getInstance("TZS"),
				Currency.getInstance("USD")
		);

		assertThat(quote.sourceCurrency()).isEqualTo(Currency.getInstance("TZS"));
		assertThat(quote.targetCurrency()).isEqualTo(Currency.getInstance("USD"));
		assertThat(quote.sourceUnitsPerTargetUnit()).isEqualByComparingTo("2500");
		assertThat(quote.obtainedAt()).isEqualTo(Instant.parse("2026-09-04T10:00:00Z"));
		assertThat(quote.source()).isEqualTo("CONFIGURED_DEVELOPMENT_RATE");
	}

	@Test
	void rejectsUnsupportedCurrencyPairs() {
		var properties = new FxProperties(
				new BigDecimal("2500"),
				"CONFIGURED_DEVELOPMENT_RATE",
				RoundingMode.HALF_UP,
				2
		);
		var provider = new ConfiguredExchangeRateProvider(properties, FIXED_CLOCK);

		assertThatThrownBy(() -> provider.getExchangeRate(
				Currency.getInstance("USD"),
				Currency.getInstance("TZS")
		)).isInstanceOf(UnsupportedCurrencyPairException.class);
	}
}
