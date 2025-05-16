package com.cts.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cts.exceptions.UserNotFoundException;
import com.cts.model.UserRegistration;
import com.cts.repository.UserRegistrationRepository;

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {
    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationServiceImpl.class);

    private UserRegistrationRepository repository;
    public UserRegistrationServiceImpl(UserRegistrationRepository repository) {
		this.repository = repository;
	}

    @Override
    public List<UserRegistration> getAllUsers() {
        logger.info("Fetching all users");
        return repository.findAll();
    }

    @Override
    public UserRegistration getUserById(int id) throws UserNotFoundException {
        logger.info("Fetching user with ID: {}", id);
        Optional<UserRegistration> optional = repository.findById(id);
        if (optional.isPresent()) {
            logger.info("User found with ID: {}", id);
            return optional.get();
        } else {
            throw new UserNotFoundException("User not found with id : "+id+". Enter a valid user id..");
        }
    }

    @Override
    public UserRegistration saveUser(UserRegistration user) {
        logger.info("Saving user: {}", user);
        return repository.save(user);
    }

    @Override
    public String updateUser(int id, UserRegistration updatedUser) throws UserNotFoundException {
        logger.info("Updating user with ID: {}", id);
        Optional<UserRegistration> optionalUser = repository.findById(id);
        if (optionalUser.isPresent()) {
            UserRegistration existingUser = optionalUser.get();
            existingUser.setUserName(updatedUser.getUserName());
            existingUser.setUserEmail(updatedUser.getUserEmail());
            existingUser.setUserPassword(updatedUser.getUserPassword());
            existingUser.setUserContactNumber(updatedUser.getUserContactNumber());
            repository.save(existingUser);
            logger.info("User updated successfully with ID: {}", id);
            return "User updated successfully!";
        } else {
            logger.error("User not found with ID: {}", id);
            throw new UserNotFoundException("User not found with id : "+id+". Enter a valid user id..");
        }
    }

    @Override
    public void deleteUserById(int id) throws UserNotFoundException {
        logger.info("Deleting user with ID: {}", id);
        if (repository.existsById(id)) {
            repository.deleteById(id);
            logger.info("User deleted successfully with ID: {}", id);
        } else {
            logger.error("User not found with ID: {}", id);
            throw new UserNotFoundException("User not found with id : "+id+". Enter a valid user id..");
        }
    }

    @Override
    public boolean validateUser(String email, String password) {
        logger.info("Validating user with email: {}", email);
        UserRegistration user = repository.findByUserEmail(email);
        if (user != null && user.getUserPassword().equals(password)) {
            logger.info("User validated successfully with email: {}", email);
            return true;
        } else {
            logger.error("User validation failed with email: {}", email);
            return false;
        }
    }
}
