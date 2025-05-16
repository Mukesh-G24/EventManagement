package com.cts.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cts.exceptions.EventNotFoundException;
import com.cts.exceptions.InvalidDataException;
import com.cts.model.EventManagement;
import com.cts.repository.EventManagementRepository;

@Service
public class EventManagementServiceImpl implements EventManagementService {

	private static final Logger logger = LoggerFactory.getLogger(EventManagementServiceImpl.class);

	private EventManagementRepository repository;

	public EventManagementServiceImpl(EventManagementRepository repository) {
		this.repository = repository;
	}

	@Override
	public EventManagement saveEvent(EventManagement event) throws InvalidDataException {
		logger.info("Saving event: {}", event);
		EventManagement eve = null;
		eve = repository.save(event);
		if (eve.getId() > 0) {
			return eve;
		} else {
			throw new InvalidDataException("Error in createing an event. Enter valid event details..");
		}
	}

	@Override
	public EventManagement getEventById(int eventId) throws EventNotFoundException {
		logger.info("Fetching event with ID: {}", eventId);
		Optional<EventManagement> event = repository.findById(eventId);
		if (event.isPresent()) {
			return event.get();
		} else {
			throw new EventNotFoundException("Event not found with id " + eventId + ". Enter a valid event id..");
		}
	}

	@Override
	public EventManagement updateEvent(int eventId, EventManagement event)
			throws EventNotFoundException, InvalidDataException {
		logger.info("Updating event with ID: {}", eventId);
		EventManagement eve = getEventById(eventId);
		EventManagement existingEvent = eve;
		existingEvent.setEventName(event.getEventName());
		existingEvent.setEventCategory(event.getEventCategory());
		existingEvent.setEventLocation(event.getEventLocation());
		existingEvent.setEventDate(event.getEventDate());
		existingEvent.setEventOrganizerId(event.getEventOrganizerId());
		saveEvent(existingEvent);
		return existingEvent;
	}

	@Override
	public String deleteEventById(int eventId) throws EventNotFoundException {
		logger.info("Deleting event with ID: {}", eventId);
		getEventById(eventId);
		repository.deleteById(eventId);
		return "Event deleted successfully.";
	}

	@Override
	public List<EventManagement> getAllEvents() {
		logger.info("Fetching all events");
		return repository.findAll();
	}

	@Override
	public List<EventManagement> getEventByCategory(String category) throws EventNotFoundException {
		logger.info("Fetching events by category: {}", category);
		List<EventManagement> events = repository.findByEventCategory(category);
		if (events.isEmpty()) {
			throw new EventNotFoundException(
					"Event not found with category " + category + ". Enter a valid category..");
		}
		return events;
	}

	@Override
	public void decreaseTicketCount(int eventId) {
		logger.info("Decreasing ticket count for event ID: {}", eventId);
		EventManagement event = repository.findById(eventId)
				.orElseThrow(() -> new RuntimeException("Event not found with Id: " + eventId));
		int currentCount = event.getTicketCount();
		if (currentCount > 0) {
			event.setTicketCount(currentCount - 1);
			repository.save(event);
			logger.info("Ticket count decreased for event ID: {}", eventId);
		} else {
			logger.error("No tickets available for event with ID: {}", eventId);
			throw new RuntimeException("No tickets available for event with Id: " + eventId);
		}
	}

	@Override
	public void increaseTicketCount(int eventId) {
		logger.info("Increasing ticket count for event ID: {}", eventId);
		EventManagement event = repository.findById(eventId)
				.orElseThrow(() -> new RuntimeException("Event not found with Id: " + eventId));
		int currentCount = event.getTicketCount();
		event.setTicketCount(currentCount + 1);
		repository.save(event);
		logger.info("Ticket count increased for event ID: {}", eventId);
	}

	@Override
	public List<EventManagement> getEventByLocation(String location) throws EventNotFoundException {
		logger.info("Fetching events by location: {}", location);
		List<EventManagement> events = repository.findByEventLocation(location);
		if (events.isEmpty()) {
			throw new EventNotFoundException(
					"Event not found with location " + location + ". Enter a valid location..");
		}
		return events;

	}

	@Override
	public List<EventManagement> getEventByDate(Date date) throws EventNotFoundException {
		logger.info("Fetching events by date: {}", date);
		List<EventManagement> events = repository.findByEventDate(date);
		if (events.isEmpty()) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			String dateStr = formatter.format(date);
			throw new EventNotFoundException("Event not found with date " + dateStr + ". Enter a valid date..");
		}
		return events;
	}
}
