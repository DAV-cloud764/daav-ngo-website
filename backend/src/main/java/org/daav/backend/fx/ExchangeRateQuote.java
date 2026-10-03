package org.daav.backend.fx;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.Objects;

public record ExchangeRateQuote(
		Currency sourceCurrency,
		Currency targetCurrency,
		BigDecimal sourceUnitsPerTargetUnit,
		Instant obtainedAt,
		String source
) {

	public ExchangeRateQuote {
		Objects.requireNonNull(sourceCurrency, "sourceCurrency is required");
		Objects.requireNonNull(targetCurrency, "targetCurrency is required");
		Objects.requireNonNull(sourceUnitsPerTargetUnit, "sourceUnitsPerTargetUnit is required");
		Objects.requireNonNull(obtainedAt, "obtainedAt is required");
		Objects.requireNonNull(source, "source is required");

		if (sourceUnitsPerTargetUnit.signum() <= 0) {
			throw new IllegalArgumentException("Exchange rate must be greater than zero");
		}

		source = source.trim();
		if (source.isEmpty()) {
			throw new IllegalArgumentException("Exchange rate source is required");
		}
	}
}
