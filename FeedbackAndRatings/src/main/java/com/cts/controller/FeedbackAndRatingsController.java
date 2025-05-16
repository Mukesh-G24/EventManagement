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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cts.exceptions.ErrorResponse;
import com.cts.exceptions.FeedbackAndRatingsNotFoundException;
import com.cts.model.FeedbackAndRatings;
import com.cts.service.FeedbackAndRatingsService;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/feedbackandrating")
public class FeedbackAndRatingsController {
	
	private FeedbackAndRatingsService service;

	public FeedbackAndRatingsController(FeedbackAndRatingsService service) {
		this.service = service;
	}

	/**
     * Saves a new feedback entry.
     * @param feedback The feedback details to be saved.
     * @return The saved feedback object.
     */
	
	@PostMapping("/save")
	public FeedbackAndRatings saveFeedback(@RequestBody FeedbackAndRatings feedback) {
		return service.saveFeedback(feedback);
	}

	 /**
     * Updates an existing feedback entry.
     * @param feedbackId ID of the feedback to update.
     * @param feedback Updated feedback details.
     * @return The updated feedback object.
     * @throws FeedbackAndRatingsNotFoundException if feedback is not found.
     */
	
	@PutMapping("/update/{feedbackId}")
	public FeedbackAndRatings updateFeedback(@PathVariable int feedbackId, @RequestBody FeedbackAndRatings feedback)
			throws FeedbackAndRatingsNotFoundException {
		return service.updateFeedback(feedbackId, feedback);
	}

	 /**
     * Deletes a feedback entry by its ID.
     * @param feedbackId ID of the feedback to delete.
     * @return Confirmation message upon deletion.
     * @throws FeedbackAndRatingsNotFoundException if feedback is not found.
     */
	
	@DeleteMapping("/delete/{feedbackId}")
	public String deleteFeedback(@PathVariable int feedbackId) throws FeedbackAndRatingsNotFoundException {
		return service.deleteFeedback(feedbackId);
	}

	 /**
     * Fetches feedback by its ID.
     * @param feedbackId ID of the feedback to retrieve.
     * @return The requested feedback object.
     * @throws FeedbackAndRatingsNotFoundException if feedback is not found.
     */
	
	@GetMapping("fetch-by-feedbackid/{feedbackId}")
	public FeedbackAndRatings getFeedbackById(@PathVariable int feedbackId) throws FeedbackAndRatingsNotFoundException {
		return service.getFeedbackById(feedbackId);
	}

	/**
     * Retrieves all feedback entries for a specific user.
     * @param userId The user's ID.
     * @return List of feedback entries associated with the user.
     */
	
	@GetMapping("/fetch-by-user-id/{userId}")
	public List<FeedbackAndRatings> getAllFeedbacksByUser(@PathVariable int userId) {
		return service.getAllFeedbacksByUser(userId);
	}

	 /**
     * Retrieves all feedback entries for a specific event.
     * @param eventId The event ID.
     * @return List of feedback entries associated with the event.
     */
	
	@GetMapping("/fetch-by-event-id/{eventId}")
	public List<FeedbackAndRatings> getAllFeedbacksByEvent(@PathVariable int eventId) {
		return service.getAllFeedbacksByEvent(eventId);
	}

	 /**
     * Calculates the average rating for a given event.
     * @param eventId ID of the event.
     * @return The average rating of the event.
     */
	
	@GetMapping("/fetch-event-avg-rating/{eventId}")
	public Double getAverageRatingForEvent(@PathVariable int eventId) {
		return service.getAverageRatingForEvent(eventId);
	}

	/**
     * Retrieves all feedback entries.
     * @return List of all feedback entries.
     */
	
	@GetMapping("/fetch-all")
	public List<FeedbackAndRatings> getAllfeedbacks() {
		return service.getAllfeedbacks();

	}

	/**
     * Exception handler for FeedbackAndRatingsNotFoundException.
     * @param e Exception instance.
     * @param request HTTP request details.
     * @return Structured error response.
     */
	
	@ExceptionHandler(FeedbackAndRatingsNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleException(FeedbackAndRatingsNotFoundException e,
			HttpServletRequest request) {
		ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage(), LocalDateTime.now(),
				request.getRequestURI());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}

	/**
     * Exception handler for FeignException when calling external microservices.
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
		int start = message.indexOf("\"message\":\"") + 11; 
		int end = message.indexOf("\",", start); 
		String errorMessage = (start > 9 && end > start) ? message.substring(start, end) : "Unknown error";
		ErrorResponse res = new ErrorResponse(HttpStatus.NOT_FOUND.value(), errorMessage, LocalDateTime.now(),
				request.getRequestURI());
		return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
	}

}
