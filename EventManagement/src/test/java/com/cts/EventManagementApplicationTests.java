package com.cts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cts.exceptions.EventNotFoundException;
import com.cts.exceptions.InvalidDataException;
import com.cts.model.EventManagement;
import com.cts.repository.EventManagementRepository;
import com.cts.service.EventManagementServiceImpl;

@ExtendWith(MockitoExtension.class)
 class EventManagementApplicationTests {

    private static final Logger logger = LoggerFactory.getLogger(EventManagementApplicationTests.class);

    @InjectMocks
    private EventManagementServiceImpl eventService;

    @Mock
    private EventManagementRepository repository;

    private EventManagement event;

    @BeforeEach
    void setup() {
        event = new EventManagement();
        event.setId(1);
        event.setEventName("Tech Conference");
        event.setEventCategory("Technology");
        event.setEventLocation("New York");
        event.setEventOrganizerId(101);
        event.setTicketCount(100);
        event.setEventDate(new Date(0));
    }

    @Test
    void testSaveEvent_Success() throws InvalidDataException {
        when(repository.save(any(EventManagement.class))).thenReturn(event);

        EventManagement savedEvent = eventService.saveEvent(event);
        assertNotNull(savedEvent);
        assertEquals("Tech Conference", savedEvent.getEventName());

        logger.info("Test: Event saved successfully");
    }

    @Test
    void testGetEventById_Success() throws EventNotFoundException {
        when(repository.findById(1)).thenReturn(Optional.of(event));

        EventManagement foundEvent = eventService.getEventById(1);
        assertNotNull(foundEvent);
        assertEquals("Technology", foundEvent.getEventCategory());

        logger.info("Test: Event fetched successfully");
    }

    @Test
    void testGetEventById_NotFound() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(EventNotFoundException.class, () -> eventService.getEventById(1));
        assertEquals("Event not found with id 1. Enter a valid event id..", exception.getMessage());

        logger.error("Test: Event not found");
    }

    @Test
    void testUpdateEvent_Success() throws EventNotFoundException, InvalidDataException {
        when(repository.findById(1)).thenReturn(Optional.of(event));
        when(repository.save(any(EventManagement.class))).thenReturn(event);

        EventManagement updatedEvent = eventService.updateEvent(1, event);
        assertEquals("Tech Conference", updatedEvent.getEventName());

        logger.info("Test: Event updated successfully");
    }

    @Test
    void testDeleteEventById_Success() throws EventNotFoundException {
        when(repository.findById(1)).thenReturn(Optional.of(event));
        doNothing().when(repository).deleteById(1);

        String result = eventService.deleteEventById(1);
        assertEquals("Event deleted successfully.", result);

        logger.info("Test: Event deleted successfully");
    }

    @Test
    void testDecreaseTicketCount_Success() {
        when(repository.findById(1)).thenReturn(Optional.of(event));
        eventService.decreaseTicketCount(1);

        assertEquals(99, event.getTicketCount());
        logger.info("Test: Ticket count decreased successfully");
    }

    @Test
    void testIncreaseTicketCount_Success() {
        when(repository.findById(1)).thenReturn(Optional.of(event));
        eventService.increaseTicketCount(1);

        assertEquals(101, event.getTicketCount());
        logger.info("Test: Ticket count increased successfully");
    }
}
