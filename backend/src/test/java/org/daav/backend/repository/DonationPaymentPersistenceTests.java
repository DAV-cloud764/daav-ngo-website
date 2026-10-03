package org.daav.backend.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import org.daav.backend.domain.Donation;
import org.daav.backend.domain.DonationStatus;
import org.daav.backend.domain.Payment;
import org.daav.backend.domain.PaymentProvider;
import org.daav.backend.domain.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class DonationPaymentPersistenceTests {

	@Autowired
	private DonationRepository donationRepository;

	@Autowired
	private PaymentRepository paymentRepository;

	@Autowired
	private EntityManager entityManager;

	@Test
	void persistsDonationAndPaymentWithSeparateCurrencyAmountsAndFxSnapshot() {
		var donation = sampleDonation("DAAV-REF-001");
		donation.markPaymentPending();

		var payment = samplePayment(donation);
		payment.markApprovalPending("PAYPAL-ORDER-001");

		donationRepository.saveAndFlush(donation);
		entityManager.clear();

		var savedDonation = donationRepository.findByInternalReference("DAAV-REF-001").orElseThrow();
		var savedPayment = paymentRepository
				.findByProviderAndProviderOrderId(PaymentProvider.PAYPAL, "PAYPAL-ORDER-001")
				.orElseThrow();

		assertThat(savedDonation.getOriginalAmount()).isEqualByComparingTo("50000.00");
		assertThat(savedDonation.getOriginalCurrency()).isEqualTo(Currency.getInstance("TZS"));
		assertThat(savedDonation.getStatus()).isEqualTo(DonationStatus.PAYMENT_PENDING);

		assertThat(savedPayment.getProviderAmount()).isEqualByComparingTo("20.00");
		assertThat(savedPayment.getProviderCurrency()).isEqualTo(Currency.getInstance("USD"));
		assertThat(savedPayment.getExchangeRate()).isEqualByComparingTo("2500.000000");
		assertThat(savedPayment.getExchangeRateSource()).isEqualTo("CONFIGURED_DEVELOPMENT_RATE");
		assertThat(savedPayment.getExchangeRateObtainedAt()).isEqualTo(Instant.parse("2026-09-05T09:00:00Z"));
		assertThat(savedPayment.getDonation().getId()).isEqualTo(savedDonation.getId());
		assertThat(savedPayment.getStatus()).isEqualTo(PaymentStatus.APPROVAL_PENDING);
		assertThat(savedDonation.getCreatedAt()).isNotNull();
		assertThat(savedDonation.getUpdatedAt()).isNotNull();
		assertThat(savedPayment.getCreatedAt()).isNotNull();
		assertThat(savedPayment.getUpdatedAt()).isNotNull();
	}

	@Test
	void rejectsDuplicateInternalDonationReferences() {
		donationRepository.saveAndFlush(sampleDonation("DAAV-REF-DUPLICATE"));

		assertThatThrownBy(() -> donationRepository.saveAndFlush(sampleDonation("DAAV-REF-DUPLICATE")))
				.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	void rejectsDuplicateProviderOrderForSameProvider() {
		var firstDonation = sampleDonation("DAAV-REF-ORDER-001");
		var firstPayment = samplePayment(firstDonation);
		firstPayment.markApprovalPending("PAYPAL-ORDER-DUPLICATE");
		donationRepository.saveAndFlush(firstDonation);

		var secondDonation = sampleDonation("DAAV-REF-ORDER-002");
		var secondPayment = samplePayment(secondDonation);
		secondPayment.markApprovalPending("PAYPAL-ORDER-DUPLICATE");

		assertThatThrownBy(() -> donationRepository.saveAndFlush(secondDonation))
				.isInstanceOf(DataIntegrityViolationException.class);
	}

	private static Donation sampleDonation(String internalReference) {
		return new Donation(
				internalReference,
				"Asha Mrema",
				"asha@example.com",
				new BigDecimal("50000.00"),
				Currency.getInstance("TZS")
		);
	}

	private static Payment samplePayment(Donation donation) {
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
