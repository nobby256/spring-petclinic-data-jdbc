package org.springframework.samples.petclinic.customers.model;

import java.time.LocalDate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class PetRequest {
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	@NotNull
	private LocalDate birthDate;

	@NotEmpty
	private String name;

	@NotNull
	private Integer typeId;

}
