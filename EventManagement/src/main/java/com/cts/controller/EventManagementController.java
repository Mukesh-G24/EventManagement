package com.cts.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cts.exceptions.ErrorResponse;
import com.cts.exceptions.EventNotFoundException;
import com.cts.exceptions.InvalidDataException;
import com.cts.model.EventManagement;
import com.cts.service.EventManagementService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/event")
public class EventManagementController {
	
	private EventManagementService service;
	
	public EventManagementController(EventManagementService service) {
		this.service = service;
	}

	/**
     * Saves a new event.
     * @param event Event details to be saved.
     * @return The saved event.
     * @throws InvalidDataException if event data is invalid.
     */
	
	@PostMapping("/save")
	public EventManagement saveEvent(@RequestBody EventManagement event) throws InvalidDataException {
		return service.saveEvent(event);
	}

	/**
     * Updates an existing event.
     * @param eventId ID of the event to update.
     * @param updatedEvent Updated event details.
     * @return The updated event.
     * @throws EventNotFoundException if event not found.
     * @throws InvalidDataException if updated data is invalid.
     */
	
	@PutMapping("/update/{eventId}")
	public EventManagement updateEvent(@PathVariable int eventId, @RequestBody EventManagement updatedEvent) throws EventNotFoundException,InvalidDataException {
		return service.updateEvent(eventId, updatedEvent);

	}
	
	/**
     * Deletes an event by its ID.
     * @param eventId ID of the event to delete.
     * @return Confirmation message.
     * @throws EventNotFoundException if event not found.
     */
	
	@DeleteMapping("/delete/{eventId}")
	public String deleteEventById(@PathVariable int eventId) throws EventNotFoundException {
		return service.deleteEventById(eventId);
	}
	
	 /**
     * Fetches an event by its ID.
     * @param eventId ID of the event to fetch.
     * @return The requested event.
     * @throws EventNotFoundException if event not found.
     */
	
	@GetMapping("/fetch-by-id/{eventId}")
	public EventManagement getEventById(@PathVariable int eventId) throws EventNotFoundException {
		return service.getEventById(eventId);
	}
	
	/**
     * Fetches all events.
     * @return List of all events.
     */
	
	@GetMapping("/fetch-all")
	public List<EventManagement> getAllEvents() {
		return service.getAllEvents();
	}

	/**
     * Fetches events by category.
     * @param category Category of events.
     * @return List of events in the specified category.
     * @throws EventNotFoundException if no events found.
     */
	
	@GetMapping("/fetch-by-category/{category}")
	public List<EventManagement> getEventByCategory(@PathVariable String category) throws EventNotFoundException {
		return service.getEventByCategory(category);
	}
	
	/**
     * Decreases ticket count for an event.
     * @param eventId ID of the event.
     */
	
	@PostMapping("decreaseTicketCount/{eventId}")
	public void decreaseTicketCount(@PathVariable int eventId) {
		service.decreaseTicketCount(eventId);
	}

	/**
     * Increases ticket count for an event.
     * @param eventId ID of the event.
     */
	
	@PostMapping("increaseTicketCount/{eventId}")
	public void increaseTicketCount(@PathVariable int eventId) {
		service.increaseTicketCount(eventId);
	}

	 /**
     * Fetches events by location.
     * @param location Location of events.
     * @return List of events at the specified location.
     * @throws Exception if any unexpected error occurs.
     */
	
	@GetMapping("/fetch-by-location/{location}")
	public List<EventManagement> getEventByLocation(@PathVariable String location) throws Exception {
		return service.getEventByLocation(location);
	}

	/**
     * Fetches events by date.
     * @param date Date of events (format: yyyy-MM-dd).
     * @return List of events happening on the specified date.
     * @throws Exception if parsing or retrieval fails.
     */
	
	@GetMapping("/fetch-by-date/{date}")
	public List<EventManagement> getEventByDate(@PathVariable String date) throws Exception {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date formattedDate = formatter.parse(date);
		return service.getEventByDate(formattedDate);
		
	}
	
	/**
     * Handles EventNotFoundException globally.
     * @param e Exception object.
     * @param request HTTP request details.
     * @return Structured error response.
     */
	
	@ExceptionHandler(EventNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleException(EventNotFoundException e, HttpServletRequest request){	
		ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND.value(),e.getMessage(),LocalDateTime.now(),request.getRequestURI());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	
	/**
     * Handles InvalidDataException globally.
     * @param e Exception object.
     * @param request HTTP request details.
     * @return Structured error response.
     */
	
	@ExceptionHandler(InvalidDataException.class)
	public ResponseEntity<ErrorResponse> handleException(InvalidDataException e, HttpServletRequest request){	
		ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),e.getMessage(),LocalDateTime.now(),request.getRequestURI());
		return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	}

}
