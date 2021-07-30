package org.springframework.samples.petclinic.customers.web;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.model.PetType;

@DataJdbcTest
public class PetServiceImplTest {

	@Autowired
	PetRepository petRepository;

	PetServiceImpl target;

	@BeforeEach
	void setup() {
		target = new PetServiceImpl(petRepository);
	}

	@Test
	void getPetTypesTest() {
		List<PetType> actual = target.getPetTypes();
		assertThat(actual).hasSizeGreaterThan(0);
	}

	@Test
	void findPetByPetIdTest() {
		Pet actual = target.findPetByPetId(2);
		assertThat(actual).isNotNull();
	}

}
