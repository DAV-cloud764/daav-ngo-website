package org.daav.backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.daav.backend.domain.Payment;
import org.daav.backend.domain.PaymentProvider;
import org.daav.backend.domain.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

	List<Payment> findByDonationId(UUID donationId);

	Optional<Payment> findByProviderAndProviderOrderId(PaymentProvider provider, String providerOrderId);

	Optional<Payment> findByProviderAndProviderCaptureId(PaymentProvider provider, String providerCaptureId);

	long countByStatus(PaymentStatus status);
}
