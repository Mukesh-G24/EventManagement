package com.cts.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.cts.dto.EventManagement;

@FeignClient(name="EVENTMANAGEMENT",path="/event")
public interface EventManagementClient {
     
	@GetMapping("/fetch-by-id/{eventId}")
	public EventManagement getEventById(@PathVariable int eventId);
	
	@PostMapping("increaseTicketCount/{eventId}")
	public void increaseTicketCount(@PathVariable int eventId);
	
	@PostMapping("decreaseTicketCount/{eventId}")
	public void decreaseTicketCount(@PathVariable int eventId);
}
