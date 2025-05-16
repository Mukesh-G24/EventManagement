package com.cts.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cts.exceptions.ErrorResponse;
import com.cts.exceptions.TicketNotFoundException;
import com.cts.model.TicketBooking;
import com.cts.service.TicketBookingService;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/ticket")
public class TicketBookingController {

	private TicketBookingService ticketBookingService;
	
	public TicketBookingController(TicketBookingService ticketBookingService) {
		this.ticketBookingService = ticketBookingService;
	}

	/**
     * Books a new ticket.
     * @param ticket The ticket details to be booked.
     * @return The booked ticket.
     */
	
	@PostMapping("/book")
	public TicketBooking bookTicket(@RequestBody TicketBooking ticket) {

		return ticketBookingService.bookTicket(ticket);
	}

	/**
     * Fetches a ticket by its ID.
     * @param ticketId ID of the ticket to retrieve.
     * @return The requested ticket.
     * @throws TicketNotFoundException if the ticket is not found.
     */
	
	@GetMapping("fetch-by-id/{ticketId}")
	public TicketBooking getTicketById(@PathVariable int ticketId) throws TicketNotFoundException {
		return ticketBookingService.getTicketById(ticketId);
	}

	/**
     * Retrieves all tickets.
     * @return List of all booked tickets.
     */
	
	@GetMapping("/fetch-all")
	public List<TicketBooking> getAllTickets() {
		return ticketBookingService.getAllTickets();
	}

	/**
     * Retrieves all tickets booked by a specific user.
     * @param userId ID of the user.
     * @return List of tickets booked by the user.
     */
	
	@GetMapping("/fetch-by-user-id/{userId}")
	public List<TicketBooking> getTicketsByUserId(@PathVariable int userId) {
		return ticketBookingService.getTicketsByUserId(userId);
	}

	/**
     * Retrieves all tickets associated with a specific event.
     * @param eventId ID of the event.
     * @return List of tickets booked for the event.
     */
	
	@GetMapping("/fetch-by-event-id/{eventId}")
	public List<TicketBooking> getTicketsByEventId(@PathVariable int eventId) {
		return ticketBookingService.getTicketsByEventId(eventId);
	}

	 /**
     * Cancels a ticket based on its ID.
     * @param ticketId ID of the ticket to cancel.
     * @return Confirmation message after cancellation.
     * @throws TicketNotFoundException if the ticket is not found.
     */
	
	@DeleteMapping("/cancel/{ticketId}")
	public String cancelTicket(@PathVariable int ticketId) throws TicketNotFoundException {
		return ticketBookingService.cancelTicket(ticketId);    	
	}
	
	/**
     * Handles TicketNotFoundException globally.
     * @param e Exception instance.
     * @param request HTTP request details.
     * @return Structured error response.
     */
	
	@ExceptionHandler(TicketNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleException(TicketNotFoundException e, HttpServletRequest request){	
		ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND.value(),e.getMessage(),LocalDateTime.now(),request.getRequestURI());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	
	 /**
     * Handles FeignException when calling external microservices.
     * Extracts the error message from Feign exception response.
     * @param e FeignException instance.
     * @param request HTTP request details.
     * @param response HTTP response object.
     * @return Structured error response containing the extracted message.
     */
	
	@ExceptionHandler(FeignException.class)
	public ResponseEntity<ErrorResponse> handleException(FeignException e, HttpServletRequest request,
			HttpServletResponse response) {

		String message = e.getMessage();
		int start = message.indexOf("\"message\":\"") + 11; // Start after "message":""
		int end = message.indexOf("\",", start); // End before the next field
		String errorMessage = (start > 9 && end > start) ? message.substring(start, end) : "Unknown error";
		ErrorResponse res = new ErrorResponse(HttpStatus.NOT_FOUND.value(), errorMessage, LocalDateTime.now(),
				request.getRequestURI());
		return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
	}

}
