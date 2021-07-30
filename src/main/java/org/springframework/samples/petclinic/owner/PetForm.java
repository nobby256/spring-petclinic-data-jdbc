package org.springframework.samples.petclinic.owner;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PetForm {

	private boolean isCreate;

	@NotEmpty
	private String birthDate;

	@NotEmpty
	private String name;

	@NotNull
	private Integer typeId;

}
