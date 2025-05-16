package com.cts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cts.dto.EventManagement;
import com.cts.dto.UserRegistration;
import com.cts.exceptions.FeedbackAndRatingsNotFoundException;
import com.cts.feignclient.EventManagementClient;
import com.cts.feignclient.UserRegistrationClient;
import com.cts.model.FeedbackAndRatings;
import com.cts.repository.FeedbackAndRatingsRepository;
import com.cts.service.FeedbackAndRatingsServiceImpl;

@ExtendWith(MockitoExtension.class)
 class FeedbackAndRatingsApplicationTests {

    @InjectMocks
    private FeedbackAndRatingsServiceImpl service;

    @Mock
    private FeedbackAndRatingsRepository repository;

    @Mock
    private EventManagementClient eventClient;

    @Mock
    private UserRegistrationClient userClient;

    private FeedbackAndRatings feedback;

    @BeforeEach
    void setup() {
        feedback = new FeedbackAndRatings();
        feedback.setId(1);
        feedback.setEventId(100);
        feedback.setUserId(200);
        feedback.setRating(4.5);
        feedback.setComments("Great event!");
        feedback.setSubmittedTimestamp(LocalDateTime.now());
    }

    @Test
    void testSaveFeedback_Success() {
        when(eventClient.getEventById(feedback.getEventId())).thenReturn(new EventManagement());
        when(userClient.getUserById(feedback.getUserId())).thenReturn(new UserRegistration());
        when(repository.save(any(FeedbackAndRatings.class))).thenReturn(feedback);

        FeedbackAndRatings response = service.saveFeedback(feedback);
        assertEquals("Feedback saved successfully", response);
    }

    @Test
    void testSaveFeedback_InvalidEvent() {
        when(eventClient.getEventById(feedback.getEventId())).thenReturn(null);
        FeedbackAndRatings response = service.saveFeedback(feedback);
        assertEquals("Enter valid EventId", response);
    }

    @Test
    void testSaveFeedback_InvalidUser() {
        when(eventClient.getEventById(feedback.getEventId())).thenReturn(new EventManagement());
        when(userClient.getUserById(feedback.getUserId())).thenReturn(null);

        FeedbackAndRatings response = service.saveFeedback(feedback);
        assertEquals("Enter valid userId", response);
    }

    @Test
    void testGetFeedbackById_Success() throws FeedbackAndRatingsNotFoundException {
        when(repository.findById(1)).thenReturn(Optional.of(feedback));

        FeedbackAndRatings result = service.getFeedbackById(1);
        assertNotNull(result);
        assertEquals(4.5, result.getRating());
    }

    @Test
    void testGetFeedbackById_NotFound() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(FeedbackAndRatingsNotFoundException.class, () -> service.getFeedbackById(1));
        assertEquals("Feedback Not found", exception.getMessage());
    }

    @Test
    void testUpdateFeedback_Success() throws FeedbackAndRatingsNotFoundException {
        when(repository.findById(1)).thenReturn(Optional.of(feedback));
        when(repository.save(any(FeedbackAndRatings.class))).thenReturn(feedback);

        FeedbackAndRatings response = service.updateFeedback(1, feedback);
        assertEquals("Feedback updated successfully", response);
    }

    @Test
    void testUpdateFeedback_NotFound() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(FeedbackAndRatingsNotFoundException.class, () -> service.updateFeedback(1, feedback));
        assertEquals("Feedback Not found", exception.getMessage());
    }

    @Test
    void testDeleteFeedback_Success() throws FeedbackAndRatingsNotFoundException {
        doNothing().when(repository).deleteById(1);
        String response = service.deleteFeedback(1);
        assertEquals("Feedback deleted successfully", response);
    }

    @Test
    void testGetAllFeedbacksByUser() {
        when(repository.findByUserId(200)).thenReturn(Arrays.asList(feedback));

        List<FeedbackAndRatings> result = service.getAllFeedbacksByUser(200);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetAllFeedbacksByEvent() {
        when(repository.findByEventId(100)).thenReturn(Arrays.asList(feedback));

        List<FeedbackAndRatings> result = service.getAllFeedbacksByEvent(100);
        assertFalse(result.isEmpty());
    }

    @Test
    void testGetAverageRatingForEvent_Success() {
        when(repository.findAverageRatingByEventId(100)).thenReturn(4.2);

        Double avgRating = service.getAverageRatingForEvent(100);
        assertEquals(4.2, avgRating);
    }

    @Test
    void testGetAverageRatingForEvent_NoRatings() {
        when(repository.findAverageRatingByEventId(100)).thenReturn(null);

        Double avgRating = service.getAverageRatingForEvent(100);
        assertEquals(0.0, avgRating);
    }
}
