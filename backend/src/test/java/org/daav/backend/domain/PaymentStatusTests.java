package org.daav.backend.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import org.junit.jupiter.api.Test;

class PaymentStatusTests {

	@Test
	void paymentMovesFromCreatedToApprovalToCaptureToCompleted() {
		var payment = samplePayment();

		payment.markApprovalPending("PAYPAL-ORDER-001");
		payment.markCapturePending();
		payment.markCompleted("PAYPAL-CAPTURE-001");

		assertThat(payment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
		assertThat(payment.getProviderOrderId()).isEqualTo("PAYPAL-ORDER-001");
		assertThat(payment.getProviderCaptureId()).isEqualTo("PAYPAL-CAPTURE-001");
	}

	@Test
	void paymentCannotCompleteDirectlyFromCreated() {
		var payment = samplePayment();

		assertThatThrownBy(() -> payment.markCompleted("PAYPAL-CAPTURE-002"))
				.isInstanceOf(InvalidStatusTransitionException.class);
	}

	@Test
	void providerOrderIdCannotBeChangedAfterAssignment() {
		var payment = samplePayment();
		payment.markApprovalPending("PAYPAL-ORDER-001");

		assertThatThrownBy(() -> payment.markApprovalPending("PAYPAL-ORDER-CHANGED"))
				.isInstanceOf(IllegalStateException.class);
	}

	private static Payment samplePayment() {
		var donation = new Donation(
				"DAAV-TEST-PAYMENT-001",
				"Asha Mrema",
				"asha@example.com",
				new BigDecimal("50000.00"),
				Currency.getInstance("TZS")
		);

		return new Payment(
				donation,
				PaymentProvider.PAYPAL,
				new BigDecimal("20.00"),
				Currency.getInstance("USD"),
				new BigDecimal("2500.000000"),
				"CONFIGURED_DEVELOPMENT_RATE",
				Instant.parse("2026-09-05T09:00:00Z")
		);
	}
}
