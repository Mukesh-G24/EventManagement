package com.cts.dto;

import java.sql.Date;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventManagement {
	
	private int eventId;
	private String eventName;
	private String eventCategory;
	private String eventLocation;
	private Date eventDate;
	

}
