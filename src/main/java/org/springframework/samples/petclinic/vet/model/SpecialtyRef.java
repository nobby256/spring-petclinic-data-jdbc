package org.springframework.samples.petclinic.vet.model;

import java.io.Serializable;

import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Reference between {@link Vet} and {@link Specialty} required to have many to
 * many relationships in Spring Data JDBC.
 *
 * @author Maciej Walkowiak
 */
@Table("vet_specialty")
@Data
@EqualsAndHashCode
@ToString
public class SpecialtyRef implements Serializable {
	private Integer specialty;

	public SpecialtyRef(Integer specialty) {
		this.specialty = specialty;
	}
}
