package com.cts.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.cts.dto.EventManagement;
import com.cts.dto.UserRegistration;
import com.cts.exceptions.TicketNotFoundException;
import com.cts.feignclient.EventManagementClient;
import com.cts.feignclient.UserRegistrationClient;
import com.cts.model.TicketBooking;
import com.cts.model.TicketBooking.Status;
import com.cts.repository.TicketBookingRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class TicketBookingServiceImpl implements TicketBookingService {

	private static final Logger logger = LoggerFactory.getLogger(TicketBookingServiceImpl.class);

	private TicketBookingRepository repository;
	private EventManagementClient eventClient;
	private UserRegistrationClient userClient;
	private JavaMailSender javaMailSender;

	public TicketBookingServiceImpl(TicketBookingRepository repository, EventManagementClient eventClient,
			UserRegistrationClient userClient, JavaMailSender javaMailSender) {
		this.repository = repository;
		this.eventClient = eventClient;
		this.userClient = userClient;
		this.javaMailSender = javaMailSender;
	}

	@Override
	public TicketBooking bookTicket(TicketBooking ticket) {

		EventManagement event = eventClient.getEventById(ticket.getEventId());
		UserRegistration user = userClient.getUserById(ticket.getUserId());
		String email = user.getUserEmail();
		eventClient.decreaseTicketCount(ticket.getEventId());
		ticket.setTicketBookingDate(LocalDateTime.now());
		ticket.setTicketStatus(TicketBooking.Status.BOOKED);
		TicketBooking bookedTicket = repository.save(ticket);
		String subject = "Ticket booking status";
		String message = "Congratulations! Your ticket has been booked successfully."+ "\n" +"Ticket Details:\n" +
	               "User ID : " + bookedTicket.getUserId() + "\n" +
	               "Ticket ID : " + bookedTicket.getTicketId() + "\n" +
	               "Event ID : " + bookedTicket.getEventId() + "\n" +
	               "Event Name : " + event.getEventName().toUpperCase() + "\n" +
	               "Event Locatoin : "+ event.getEventLocation().toUpperCase() + "\n" +
	               "Event Date : "+ event.getEventDate();
		sendMail(email,subject,message);
		logger.info("Ticket booked successfully for event ID: {} and user ID: {}", ticket.getEventId(),
				ticket.getUserId());
		return bookedTicket;
	}

	@Override
	public TicketBooking getTicketById(int ticketId) throws TicketNotFoundException {
		logger.info("Fetching ticket with ID: {}", ticketId);
		Optional<TicketBooking> optional = repository.findById(ticketId);
		if (optional.isPresent()) {
			logger.info("Ticket found with ID: {}", ticketId);
			return optional.get();
		} else {
			logger.error("Ticket not found with ID: {}", ticketId);
			throw new TicketNotFoundException("Ticket not found with id : " + ticketId + ". Enter a valid ticket Id.");
		}
	}

	@Override
	public List<TicketBooking> getAllTickets() {
		logger.info("Fetching all tickets");
		return repository.findAll();
	}

	@Override
	public List<TicketBooking> getTicketsByUserId(int userId) {
		logger.info("Fetching tickets for user ID: {}", userId);
		return repository.findByUserId(userId);
	}

	@Override
	public List<TicketBooking> getTicketsByEventId(int eventId) {
		logger.info("Fetching tickets for event ID: {}", eventId);
		return repository.findByEventId(eventId);
	}

	@Override
	public String cancelTicket(int ticketId) throws TicketNotFoundException {
		logger.info("Cancelling ticket with ID: {}", ticketId);
		TicketBooking ticket = getTicketById(ticketId);
		ticket.setTicketStatus(Status.CANCELLED);
		eventClient.increaseTicketCount(ticket.getEventId());
		repository.save(ticket);
		logger.info("Ticket cancelled with ID: {}", ticketId);
		return "Ticket cancelled";
	}

	public String sendMail(String toAddress, String subject, String body) {
		System.out.println(toAddress+" "+subject+" "+body);
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(toAddress);
		message.setSubject(subject);
		message.setText(body);
		javaMailSender.send(message);
		return "Mail sent successfully...";
	}

}
