package com.cts.service;

import java.util.List;

import com.cts.exceptions.FeedbackAndRatingsNotFoundException;
import com.cts.model.FeedbackAndRatings;

public interface FeedbackAndRatingsService {
public abstract FeedbackAndRatings saveFeedback(FeedbackAndRatings feedback);
    
    public abstract FeedbackAndRatings updateFeedback(int feedbackId, FeedbackAndRatings feedback) throws FeedbackAndRatingsNotFoundException;
    
    public abstract String deleteFeedback(int feedbackId) throws FeedbackAndRatingsNotFoundException;
    
    public abstract FeedbackAndRatings getFeedbackById(int feedbackId) throws FeedbackAndRatingsNotFoundException;
    
    public abstract List<FeedbackAndRatings> getAllFeedbacksByUser(int userId);
    
    public abstract List<FeedbackAndRatings> getAllfeedbacks();
    
    public abstract List<FeedbackAndRatings> getAllFeedbacksByEvent(int eventId);
    
    public abstract Double getAverageRatingForEvent(int eventId);
}
