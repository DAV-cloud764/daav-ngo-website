package org.daav.backend.domain;

public class InvalidStatusTransitionException extends RuntimeException {

	public InvalidStatusTransitionException(Enum<?> currentStatus, Enum<?> targetStatus) {
		super("Invalid status transition from " + currentStatus + " to " + targetStatus);
	}
}
