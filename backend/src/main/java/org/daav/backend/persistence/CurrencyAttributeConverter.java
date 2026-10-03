package org.daav.backend.persistence;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Currency;

@Converter(autoApply = true)
public class CurrencyAttributeConverter implements AttributeConverter<Currency, String> {

	@Override
	public String convertToDatabaseColumn(Currency currency) {
		return currency == null ? null : currency.getCurrencyCode();
	}

	@Override
	public Currency convertToEntityAttribute(String currencyCode) {
		return currencyCode == null ? null : Currency.getInstance(currencyCode);
	}
}
