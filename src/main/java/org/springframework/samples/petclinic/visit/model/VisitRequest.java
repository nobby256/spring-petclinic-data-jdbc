package org.springframework.samples.petclinic.visit.model;

import java.time.LocalDate;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class VisitRequest {

	private boolean isNew;
	
	private LocalDate date;

	@NotEmpty
	private String description;

}
