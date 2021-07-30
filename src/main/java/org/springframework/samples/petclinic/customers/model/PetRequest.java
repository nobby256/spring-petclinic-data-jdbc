package org.springframework.samples.petclinic.customers.model;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class PetRequest {

	private boolean isNew;
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate birthDate;

	@Size(min = 1)
	private String name;

	@NotNull
	private Integer typeId;

}
