package com.cts.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketBooking {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int ticketId;
	@NotNull(message = "Event ID cannot be null")
	private int eventId;
	@NotNull(message = "User ID cannot be null")
	private int userId;
	private LocalDateTime ticketBookingDate;
	
	@Enumerated(EnumType.STRING)
	private Status ticketStatus;
	
	@NotNull
	@Min(value = 1, message = "Minimum ticket count should be 1")
	private int noOfTickets;
	
	public enum Status {
		BOOKED, CANCELLED;
	}

}
