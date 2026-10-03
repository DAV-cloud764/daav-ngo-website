package org.daav.backend.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
		name = "donations",
		uniqueConstraints = {
				@UniqueConstraint(name = "uk_donations_internal_reference", columnNames = "internal_reference")
		},
		indexes = {
				@Index(name = "idx_donations_status", columnList = "status"),
				@Index(name = "idx_donations_donor_email", columnList = "donor_email"),
				@Index(name = "idx_donations_created_at", columnList = "created_at")
		}
)
public class Donation {

	private static final Currency TZS = Currency.getInstance("TZS");

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@Column(name = "internal_reference", nullable = false, updatable = false, length = 48)
	private String internalReference;

	@Column(name = "donor_name", nullable = false, length = 120)
	private String donorName;

	@Column(name = "donor_email", nullable = false, length = 254)
	private String donorEmail;

	@Column(name = "original_amount", nullable = false, precision = 19, scale = 2)
	private BigDecimal originalAmount;

	@Column(name = "original_currency", nullable = false, length = 3)
	private Currency originalCurrency;

	@Enumerated(EnumType.STRING)
	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(name = "status", nullable = false, length = 32)
	private DonationStatus status;

	@OneToMany(
			mappedBy = "donation",
			cascade = { CascadeType.PERSIST, CascadeType.MERGE }
	)
	private List<Payment> payments = new ArrayList<>();

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	protected Donation() {
	}

	public Donation(
			String internalReference,
			String donorName,
			String donorEmail,
			BigDecimal originalAmount,
			Currency originalCurrency
	) {
		this.internalReference = requireText(internalReference, "internalReference");
		this.donorName = requireText(donorName, "donorName");
		this.donorEmail = requireText(donorEmail, "donorEmail").toLowerCase();
		this.originalAmount = requirePositive(originalAmount, "originalAmount");
		this.originalCurrency = requireTzs(originalCurrency);
		this.status = DonationStatus.CREATED;
	}

	void addPayment(Payment payment) {
		if (!payments.contains(payment)) {
			payments.add(payment);
		}
	}

	public void markPaymentPending() {
		transitionTo(DonationStatus.PAYMENT_PENDING);
	}

	public void markCompleted() {
		transitionTo(DonationStatus.COMPLETED);
	}

	public void markFailed() {
		transitionTo(DonationStatus.FAILED);
	}

	public void markCancelled() {
		transitionTo(DonationStatus.CANCELLED);
	}

	public void markExpired() {
		transitionTo(DonationStatus.EXPIRED);
	}

	private void transitionTo(DonationStatus targetStatus) {
		if (!status.canTransitionTo(targetStatus)) {
			throw new InvalidStatusTransitionException(status, targetStatus);
		}
		this.status = targetStatus;
	}

	@PrePersist
	void beforeInsert() {
		Instant now = Instant.now();
		createdAt = now;
		updatedAt = now;
	}

	@PreUpdate
	void beforeUpdate() {
		updatedAt = Instant.now();
	}

	public UUID getId() {
		return id;
	}

	public String getInternalReference() {
		return internalReference;
	}

	public String getDonorName() {
		return donorName;
	}

	public String getDonorEmail() {
		return donorEmail;
	}

	public BigDecimal getOriginalAmount() {
		return originalAmount;
	}

	public Currency getOriginalCurrency() {
		return originalCurrency;
	}

	public DonationStatus getStatus() {
		return status;
	}

	public List<Payment> getPayments() {
		return Collections.unmodifiableList(payments);
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	private static String requireText(String value, String fieldName) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(fieldName + " is required");
		}
		return value.trim();
	}

	private static BigDecimal requirePositive(BigDecimal value, String fieldName) {
		Objects.requireNonNull(value, fieldName + " is required");
		if (value.signum() <= 0) {
			throw new IllegalArgumentException(fieldName + " must be greater than zero");
		}
		return value;
	}

	private static Currency requireTzs(Currency currency) {
		Objects.requireNonNull(currency, "originalCurrency is required");
		if (!TZS.equals(currency)) {
			throw new IllegalArgumentException("Donation original currency must be TZS");
		}
		return currency;
	}
}
