package org.daav.backend.domain;

public enum PaymentStatus {
	CREATED,
	APPROVAL_PENDING,
	CAPTURE_PENDING,
	COMPLETED,
	FAILED,
	CANCELLED,
	EXPIRED;

	public boolean canTransitionTo(PaymentStatus target) {
		if (target == null) {
			return false;
		}
		if (this == target) {
			return true;
		}

		return switch (this) {
			case CREATED -> target == APPROVAL_PENDING || target == FAILED;
			case APPROVAL_PENDING -> target == CAPTURE_PENDING
					|| target == COMPLETED
					|| target == FAILED
					|| target == CANCELLED
					|| target == EXPIRED;
			case CAPTURE_PENDING -> target == COMPLETED || target == FAILED;
			case COMPLETED, FAILED, CANCELLED, EXPIRED -> false;
		};
	}
}
