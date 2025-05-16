package com.cts.service;

import java.util.Date;
import java.util.List;

import com.cts.exceptions.EventNotFoundException;
import com.cts.exceptions.InvalidDataException;
import com.cts.model.EventManagement;

public interface EventManagementService {

	public abstract EventManagement saveEvent(EventManagement event) throws InvalidDataException;

	public abstract EventManagement getEventById(int id) throws EventNotFoundException;

	public abstract EventManagement updateEvent(int id, EventManagement updatedEvent) throws EventNotFoundException,InvalidDataException;

	public abstract String deleteEventById(int id) throws EventNotFoundException;
	
	public void decreaseTicketCount(int id);
	
	public void increaseTicketCount(int id);
	
	public abstract List<EventManagement> getAllEvents();

	public abstract List<EventManagement> getEventByCategory(String category) throws EventNotFoundException;
	
	public abstract List<EventManagement> getEventByLocation(String location) throws EventNotFoundException;
	
	public abstract List<EventManagement> getEventByDate(Date date) throws EventNotFoundException;
	
}
