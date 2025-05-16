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
import com.cts.exceptions.UserNotFoundException;
import com.cts.model.UserRegistration;
import com.cts.service.UserRegistrationService;

import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/user")
public class UserRegistrationController {

    private UserRegistrationService userService;
    
    public UserRegistrationController(UserRegistrationService userService) {
		this.userService = userService;
	}

    /**
     * Registers a new user.
     * @param user The user details to be saved.
     * @return The saved user object.
     */
    
    @PostMapping("/signup")
    public UserRegistration signup(@RequestBody UserRegistration user) {
        return userService.saveUser(user);
    }

    /**
     * Retrieves all registered users.
     * @return List of all registered users.
     */
    
    @GetMapping("/fetch-all")
    public List<UserRegistration> getUsers() {
        return userService.getAllUsers();
    }

    /**
     * Fetches a user by their ID.
     * @param userId ID of the user to retrieve.
     * @return The requested user object.
     * @throws UserNotFoundException if the user is not found.
     */
    
    @GetMapping("/fetch-by-id/{userId}")
    public UserRegistration getUserById(@PathVariable int userId) throws UserNotFoundException {
        return userService.getUserById(userId);
    }

    /**
     * Updates an existing user's details.
     * @param userId ID of the user to update.
     * @param updatedUser Updated user details.
     * @return Confirmation message after updating.
     * @throws UserNotFoundException if the user is not found.
     */

    @PutMapping("/update/{userId}")
    public String updateUser(@PathVariable int userId, @RequestBody UserRegistration updatedUser) throws UserNotFoundException {
        return userService.updateUser(userId, updatedUser);
    }

    /**
     * Deletes a user by their ID.
     * @param userId ID of the user to delete.
     * @return Confirmation message upon deletion.
     * @throws UserNotFoundException if the user is not found.
     */
    
    @DeleteMapping("/delete/{userId}")
    public String deleteUserById(@PathVariable int userId) throws UserNotFoundException {
        userService.deleteUserById(userId);
        return "User deleted successfully";
    }
    
    /**
     * Handles UserNotFoundException globally.
     * @param e Exception instance.
     * @param request HTTP request details.
     * @return Structured error response.
     */
    
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleException(UserNotFoundException e, HttpServletRequest request){	
		ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND.value(),e.getMessage(),LocalDateTime.now(),request.getRequestURI());
		return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
	}
	
}
