package com.cts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cts.exceptions.UserNotFoundException;
import com.cts.model.UserRegistration;
import com.cts.repository.UserRegistrationRepository;
import com.cts.service.UserRegistrationServiceImpl;

@ExtendWith(MockitoExtension.class)
 class UserRegistrationApplicationTests {

    @InjectMocks
    private UserRegistrationServiceImpl service;

    @Mock
    private UserRegistrationRepository repository;

    private UserRegistration user;

    @BeforeEach
    void setup() {
        user = new UserRegistration();
        user.setId(1);
        user.setUserName("John Doe");
        user.setUserEmail("john@example.com");
        user.setUserPassword("secure123");
        user.setUserContactNumber("9876543210");
    }

    @Test
    void testGetAllUsers() {
        when(repository.findAll()).thenReturn(Arrays.asList(user));

        List<UserRegistration> users = service.getAllUsers();
        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
    }

    @Test
    void testGetUserById_Success() throws UserNotFoundException {
        when(repository.findById(1)).thenReturn(Optional.of(user));

        UserRegistration foundUser = service.getUserById(1);
        assertNotNull(foundUser);
        assertEquals("John Doe", foundUser.getUserName());
    }

    @Test
    void testGetUserById_NotFound() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> service.getUserById(1));
        assertEquals("Invalid ID", exception.getMessage());
    }

    @Test
    void testSaveUser() {
        when(repository.save(any(UserRegistration.class))).thenReturn(user);

        UserRegistration savedUser = service.saveUser(user);
        assertNotNull(savedUser);
        assertEquals("John Doe", savedUser.getUserName());
    }

    @Test
    void testUpdateUser_Success() throws UserNotFoundException {
        when(repository.findById(1)).thenReturn(Optional.of(user));
        when(repository.save(any(UserRegistration.class))).thenReturn(user);

        String response = service.updateUser(1, user);
        assertEquals("User updated successfully!", response);
    }

    @Test
    void testUpdateUser_NotFound() {
        when(repository.findById(1)).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> service.updateUser(1, user));
        assertEquals("User not found with id: 1", exception.getMessage());
    }

    @Test
    void testDeleteUserById_Success() throws UserNotFoundException {
        when(repository.existsById(1)).thenReturn(true);
        doNothing().when(repository).deleteById(1);

        service.deleteUserById(1);
        verify(repository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteUserById_NotFound() {
        when(repository.existsById(1)).thenReturn(false);

        Exception exception = assertThrows(UserNotFoundException.class, () -> service.deleteUserById(1));
        assertEquals("User not found with id: 1", exception.getMessage());
    }

    @Test
    void testValidateUser_Success() {
        when(repository.findByUserEmail("john@example.com")).thenReturn(user);

        boolean isValid = service.validateUser("john@example.com", "secure123");
        assertTrue(isValid);
    }

    @Test
    void testValidateUser_Failure() {
        when(repository.findByUserEmail("john@example.com")).thenReturn(user);

        boolean isValid = service.validateUser("john@example.com", "wrongpass");
        assertFalse(isValid);
    }
}
