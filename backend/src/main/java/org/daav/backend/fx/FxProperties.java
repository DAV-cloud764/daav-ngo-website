package org.daav.backend.fx;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "daav.fx")
public record FxProperties(
		@NotNull
		@DecimalMin(value = "0.000001", inclusive = true)
		BigDecimal tzsPerUsd,

		@NotBlank
		String source,

		@NotNull
		RoundingMode paypalRoundingMode,

		@Min(0)
		@Max(4)
		int paypalCurrencyScale
) {
}
