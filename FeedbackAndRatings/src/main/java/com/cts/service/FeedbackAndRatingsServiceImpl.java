package com.cts.service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cts.dto.EventManagement;
import com.cts.dto.UserRegistration;
import com.cts.exceptions.FeedbackAndRatingsNotFoundException;
import com.cts.feignclient.EventManagementClient;
import com.cts.feignclient.UserRegistrationClient;
import com.cts.model.FeedbackAndRatings;
import com.cts.repository.FeedbackAndRatingsRepository;

@Service
public class FeedbackAndRatingsServiceImpl implements FeedbackAndRatingsService {

    // Logger instance for logging
    private static final Logger logger = LoggerFactory.getLogger(FeedbackAndRatingsServiceImpl.class);

    
    FeedbackAndRatingsRepository repository;

    EventManagementClient eventClient;
    
    UserRegistrationClient userClient;
    
    public FeedbackAndRatingsServiceImpl(FeedbackAndRatingsRepository repository,EventManagementClient eventClient,UserRegistrationClient userClient) {
    	
    	this.repository = repository;
    	this.eventClient = eventClient;
    	this.userClient = userClient;
    }

    @Override
    public FeedbackAndRatings saveFeedback(FeedbackAndRatings feedback) {
        logger.info("Saving feedback for event ID: {} and user ID: {}", feedback.getEventId(), feedback.getUserId());
        eventClient.getEventById(feedback.getEventId());
        userClient.getUserById(feedback.getUserId());
        feedback.setSubmittedTimestamp(LocalDateTime.now());
        return repository.save(feedback);
    }


    @Override
    public FeedbackAndRatings updateFeedback(int feedbackId, FeedbackAndRatings feedback)
            throws FeedbackAndRatingsNotFoundException {
        logger.info("Updating feedback with ID: {}", feedbackId);
        Optional<FeedbackAndRatings> existingFeedback = repository.findById(feedbackId);
        if (existingFeedback.isPresent()) {
            FeedbackAndRatings updatedFeedback = existingFeedback.get();
            updatedFeedback.setRating(feedback.getRating());
            updatedFeedback.setComments(feedback.getComments());
           return repository.save(updatedFeedback);

        } else {
            logger.error("Feedback not found with ID: {}", feedbackId);
            throw new FeedbackAndRatingsNotFoundException("Feedback Not found with feedbakc Id : "+feedbackId+". Enter a valid feedback Id.");
        }
    }


    @Override
    public String deleteFeedback(int feedbackId) throws FeedbackAndRatingsNotFoundException {
        logger.info("Deleting feedback with ID: {}", feedbackId);
        getFeedbackById(feedbackId);
        repository.deleteById(feedbackId);
        logger.info("Feedback deleted successfully with ID: {}", feedbackId);
        return "Feedback deleted successfully";
    }


    @Override
    public FeedbackAndRatings getFeedbackById(int feedbackId) throws FeedbackAndRatingsNotFoundException {
        logger.info("Fetching feedback with ID: {}", feedbackId);
        Optional<FeedbackAndRatings> optional = repository.findById(feedbackId);
        if (optional.isPresent()) {
            logger.info("Feedback found with ID: {}", feedbackId);
            return optional.get();
        } else {
            logger.error("Feedback not found with ID: {}", feedbackId);
            throw new FeedbackAndRatingsNotFoundException("Feedback Not found with feedbakc Id : "+feedbackId+". Enter a valid feedback Id.");
        }
    }


    @Override
    public List<FeedbackAndRatings> getAllFeedbacksByUser(int userId) {
        logger.info("Fetching all feedbacks for user ID: {}", userId);
        return repository.findByUserId(userId);
    }


    @Override
    public List<FeedbackAndRatings> getAllFeedbacksByEvent(int eventId) {
        logger.info("Fetching all feedbacks for event ID: {}", eventId);
        return repository.findByEventId(eventId);
    }


    @Override
    public Double getAverageRatingForEvent(int eventId) {
        logger.info("Fetching average rating for event ID: {}", eventId);
        Double avgRating =  repository.findAverageRatingByEventId(eventId);
        return (avgRating != null) ? avgRating : 0.0;
    }

  
    @Override
    public List<FeedbackAndRatings> getAllfeedbacks() {
        logger.info("Fetching all feedbacks");
        return repository.findAll();
    }
    
}
