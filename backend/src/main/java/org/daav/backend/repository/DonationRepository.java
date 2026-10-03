package org.daav.backend.repository;

import java.util.Optional;
import java.util.UUID;
import org.daav.backend.domain.Donation;
import org.daav.backend.domain.DonationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationRepository extends JpaRepository<Donation, UUID> {

	Optional<Donation> findByInternalReference(String internalReference);

	boolean existsByInternalReference(String internalReference);

	long countByStatus(DonationStatus status);
}
