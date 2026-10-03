package org.daav.backend.domain;

public enum DonationStatus {
	CREATED,
	PAYMENT_PENDING,
	COMPLETED,
	FAILED,
	CANCELLED,
	EXPIRED;

	public boolean canTransitionTo(DonationStatus target) {
		if (target == null) {
			return false;
		}
		if (this == target) {
			return true;
		}

		return switch (this) {
			case CREATED -> target == PAYMENT_PENDING || target == CANCELLED || target == EXPIRED;
			case PAYMENT_PENDING -> target == COMPLETED
					|| target == FAILED
					|| target == CANCELLED
					|| target == EXPIRED;
			case FAILED, EXPIRED -> target == PAYMENT_PENDING;
			case COMPLETED, CANCELLED -> false;
		};
	}
}
