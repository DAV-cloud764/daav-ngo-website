package org.daav.backend.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;
import java.util.Objects;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
		name = "payments",
		uniqueConstraints = {
				@UniqueConstraint(
						name = "uk_payments_provider_order",
						columnNames = { "provider", "provider_order_id" }
				),
				@UniqueConstraint(
						name = "uk_payments_provider_capture",
						columnNames = { "provider", "provider_capture_id" }
				)
		},
		indexes = {
				@Index(name = "idx_payments_donation_id", columnList = "donation_id"),
				@Index(name = "idx_payments_status", columnList = "status"),
				@Index(name = "idx_payments_provider_status", columnList = "provider,status"),
				@Index(name = "idx_payments_created_at", columnList = "created_at")
		}
)
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", nullable = false, updatable = false)
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(
			name = "donation_id",
			nullable = false,
			foreignKey = @ForeignKey(name = "fk_payments_donation")
	)
	private Donation donation;

	@Enumerated(EnumType.STRING)
	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(name = "provider", nullable = false, length = 32)
	private PaymentProvider provider;

	@Column(name = "provider_order_id", length = 128)
	private String providerOrderId;

	@Column(name = "provider_capture_id", length = 128)
	private String providerCaptureId;

	@Column(name = "provider_amount", nullable = false, precision = 19, scale = 2)
	private BigDecimal providerAmount;

	@Column(name = "provider_currency", nullable = false, length = 3)
	private Currency providerCurrency;

	@Column(name = "exchange_rate", nullable = false, precision = 19, scale = 6)
	private BigDecimal exchangeRate;

	@Column(name = "exchange_rate_source", nullable = false, length = 120)
	private String exchangeRateSource;

	@Column(name = "exchange_rate_obtained_at", nullable = false)
	private Instant exchangeRateObtainedAt;

	@Enumerated(EnumType.STRING)
	@JdbcTypeCode(SqlTypes.VARCHAR)
	@Column(name = "status", nullable = false, length = 32)
	private PaymentStatus status;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private Instant updatedAt;

	protected Payment() {
	}

	public Payment(
			Donation donation,
			PaymentProvider provider,
			BigDecimal providerAmount,
			Currency providerCurrency,
			BigDecimal exchangeRate,
			String exchangeRateSource,
			Instant exchangeRateObtainedAt
	) {
		this.donation = Objects.requireNonNull(donation, "donation is required");
		this.provider = Objects.requireNonNull(provider, "provider is required");
		this.providerAmount = requirePositive(providerAmount, "providerAmount");
		this.providerCurrency = Objects.requireNonNull(providerCurrency, "providerCurrency is required");
		this.exchangeRate = requirePositive(exchangeRate, "exchangeRate");
		this.exchangeRateSource = requireText(exchangeRateSource, "exchangeRateSource");
		this.exchangeRateObtainedAt = Objects.requireNonNull(
				exchangeRateObtainedAt,
				"exchangeRateObtainedAt is required"
		);
		this.status = PaymentStatus.CREATED;
		donation.addPayment(this);
	}

	public void markApprovalPending(String providerOrderId) {
		assignProviderOrderId(providerOrderId);
		transitionTo(PaymentStatus.APPROVAL_PENDING);
	}

	public void markCapturePending() {
		transitionTo(PaymentStatus.CAPTURE_PENDING);
	}

	public void markCompleted(String providerCaptureId) {
		assignProviderCaptureId(providerCaptureId);
		transitionTo(PaymentStatus.COMPLETED);
	}

	public void markFailed() {
		transitionTo(PaymentStatus.FAILED);
	}

	public void markCancelled() {
		transitionTo(PaymentStatus.CANCELLED);
	}

	public void markExpired() {
		transitionTo(PaymentStatus.EXPIRED);
	}

	private void assignProviderOrderId(String value) {
		String newValue = requireText(value, "providerOrderId");
		if (providerOrderId != null && !providerOrderId.equals(newValue)) {
			throw new IllegalStateException("providerOrderId cannot be changed once assigned");
		}
		providerOrderId = newValue;
	}

	private void assignProviderCaptureId(String value) {
		String newValue = requireText(value, "providerCaptureId");
		if (providerCaptureId != null && !providerCaptureId.equals(newValue)) {
			throw new IllegalStateException("providerCaptureId cannot be changed once assigned");
		}
		providerCaptureId = newValue;
	}

	private void transitionTo(PaymentStatus targetStatus) {
		if (!status.canTransitionTo(targetStatus)) {
			throw new InvalidStatusTransitionException(status, targetStatus);
		}
		status = targetStatus;
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

	public Donation getDonation() {
		return donation;
	}

	public PaymentProvider getProvider() {
		return provider;
	}

	public String getProviderOrderId() {
		return providerOrderId;
	}

	public String getProviderCaptureId() {
		return providerCaptureId;
	}

	public BigDecimal getProviderAmount() {
		return providerAmount;
	}

	public Currency getProviderCurrency() {
		return providerCurrency;
	}

	public BigDecimal getExchangeRate() {
		return exchangeRate;
	}

	public String getExchangeRateSource() {
		return exchangeRateSource;
	}

	public Instant getExchangeRateObtainedAt() {
		return exchangeRateObtainedAt;
	}

	public PaymentStatus getStatus() {
		return status;
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
}
