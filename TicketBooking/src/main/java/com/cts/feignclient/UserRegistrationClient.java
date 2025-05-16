package com.cts.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.cts.dto.UserRegistration;

@FeignClient(name = "USERREGISTRATION", path = "/user")
public interface UserRegistrationClient {
	@GetMapping("/fetch-by-id/{userId}")
	public UserRegistration getUserById(@PathVariable int userId);

}
