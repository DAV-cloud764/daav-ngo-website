package org.daav.backend.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.Currency;
import org.junit.jupiter.api.Test;

class DonationStatusTests {

	@Test
	void donationMovesThroughPendingToCompleted() {
		var donation = new Donation(
				"DAAV-TEST-DONATION-001",
				"Asha Mrema",
				"ASHA@example.com",
				new BigDecimal("50000.00"),
				Currency.getInstance("TZS")
		);

		donation.markPaymentPending();
		donation.markCompleted();

		assertThat(donation.getStatus()).isEqualTo(DonationStatus.COMPLETED);
		assertThat(donation.getDonorEmail()).isEqualTo("asha@example.com");
	}

	@Test
	void completedDonationCannotMoveToFailed() {
		var donation = new Donation(
				"DAAV-TEST-DONATION-002",
				"Asha Mrema",
				"asha@example.com",
				new BigDecimal("50000.00"),
				Currency.getInstance("TZS")
		);
		donation.markPaymentPending();
		donation.markCompleted();

		assertThatThrownBy(donation::markFailed)
				.isInstanceOf(InvalidStatusTransitionException.class);
	}

	@Test
	void donationRejectsNonTzsOriginalCurrency() {
		assertThatThrownBy(() -> new Donation(
				"DAAV-TEST-DONATION-003",
				"Asha Mrema",
				"asha@example.com",
				new BigDecimal("20.00"),
				Currency.getInstance("USD")
		)).isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("TZS");
	}
}
