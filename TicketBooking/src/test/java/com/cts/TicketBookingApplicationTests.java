package com.cts;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import com.cts.dto.UserRegistration;
import com.cts.exceptions.TicketNotFoundException;
import com.cts.feignclient.EventManagementClient;
import com.cts.feignclient.UserRegistrationClient;
import com.cts.model.TicketBooking;
import com.cts.model.TicketBooking.Status;
import com.cts.repository.TicketBookingRepository;
import com.cts.service.TicketBookingServiceImpl;

class TicketBookingServiceImplTest {

    @Mock
    private TicketBookingRepository repository;

    @Mock
    private EventManagementClient eventClient;

    @Mock
    private UserRegistrationClient userClient;

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private TicketBookingServiceImpl ticketBookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test Ticket Booking
     */
    @Test
    void testBookTicket() {
        TicketBooking ticket = new TicketBooking();
        ticket.setEventId(1);
        ticket.setUserId(100);

        UserRegistration user = new UserRegistration();
        user.setUserEmail("user@example.com");

        when(eventClient.getEventById(ticket.getEventId())).thenReturn(null);
        when(userClient.getUserById(ticket.getUserId())).thenReturn(user);
        when(repository.save(ticket)).thenReturn(ticket);

        TicketBooking bookedTicket = ticketBookingService.bookTicket(ticket);

        assertNotNull(bookedTicket);
        assertEquals(Status.BOOKED, bookedTicket.getTicketStatus());
    }

    /**
     * Test Fetching Ticket by ID (Success Case)
     */
    @Test
    void testGetTicketById_Success() throws TicketNotFoundException {
        TicketBooking ticket = new TicketBooking();
        ticket.setTicketId(1);

        when(repository.findById(1)).thenReturn(Optional.of(ticket));

        TicketBooking result = ticketBookingService.getTicketById(1);

        assertNotNull(result);
        assertEquals(1, result.getTicketId());
    }

    /**
     * Test Fetching Ticket by ID (Failure Case)
     */
    @Test
    void testGetTicketById_NotFound() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        TicketNotFoundException exception = assertThrows(TicketNotFoundException.class, () -> {
            ticketBookingService.getTicketById(1);
        });

        assertEquals("Ticket not found with id : 1. Enter a valid ticket Id.", exception.getMessage());
    }

    /**
     * Test Fetching All Tickets
     */
    @Test
    void testGetAllTickets() {
        TicketBooking ticket1 = new TicketBooking();
        TicketBooking ticket2 = new TicketBooking();

        when(repository.findAll()).thenReturn(Arrays.asList(ticket1, ticket2));

        assertEquals(2, ticketBookingService.getAllTickets().size());
    }

    /**
     * Test Fetching Tickets by User ID
     */
    @Test
    void testGetTicketsByUserId() {
        TicketBooking ticket1 = new TicketBooking();
        TicketBooking ticket2 = new TicketBooking();

        when(repository.findByUserId(100)).thenReturn(Arrays.asList(ticket1, ticket2));

        assertEquals(2, ticketBookingService.getTicketsByUserId(100).size());
    }

    /**
     * Test Cancelling Ticket
     */
    @Test
    void testCancelTicket() throws TicketNotFoundException {
        TicketBooking ticket = new TicketBooking();
        ticket.setTicketId(1);
        ticket.setEventId(5);
        ticket.setTicketStatus(Status.BOOKED);

        when(repository.findById(1)).thenReturn(Optional.of(ticket));
        when(repository.save(ticket)).thenReturn(ticket);

        String result = ticketBookingService.cancelTicket(1);

        assertEquals("Ticket cancelled", result);
        assertEquals(Status.CANCELLED, ticket.getTicketStatus());
    }

    /**
     * Test Sending Email
     */
    @Test
    void testSendMail() {
        String result = ticketBookingService.sendMail("user@example.com", "Subject", "Message Body");
        assertEquals("final", result);
    }
}
