package com.cts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.model.TicketBooking;

public interface TicketBookingRepository extends JpaRepository<TicketBooking,Integer> {

	List<TicketBooking> findByUserId(int userId);

	List<TicketBooking> findByEventId(int eventId);
}
