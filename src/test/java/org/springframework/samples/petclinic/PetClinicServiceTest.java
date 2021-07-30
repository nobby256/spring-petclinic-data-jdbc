package org.springframework.samples.petclinic;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.web.OwnerRepository;
import org.springframework.samples.petclinic.customers.web.OwnerServiceImpl;
import org.springframework.samples.petclinic.customers.web.PetRepository;
import org.springframework.samples.petclinic.customers.web.PetServiceImpl;
import org.springframework.samples.petclinic.vet.web.SpecialtyRepository;
import org.springframework.samples.petclinic.vet.web.VetRepository;
import org.springframework.samples.petclinic.vet.web.VetServiceImpl;
import org.springframework.samples.petclinic.visit.web.VisitRepository;
import org.springframework.samples.petclinic.visit.web.VisitServiceImpl;

@DataJdbcTest
@Import(OwnerServiceImpl.class)
public class PetClinicServiceTest {

	@Autowired
	OwnerRepository ownerRepository;
	@Autowired
	PetRepository petRepository;
	@Autowired
	VetRepository vetRepository;
	@Autowired
	SpecialtyRepository specialtyRepository;
	@Autowired
	VisitRepository visitRepository;

	PetClinicService target;

	@BeforeEach
	void setup() {
		// target = new PetClinicService(ownerService, petService, vetService,
		// visitService);
		target = new PetClinicService(new OwnerServiceImpl(ownerRepository), new PetServiceImpl(petRepository),
				new VetServiceImpl(vetRepository, specialtyRepository), new VisitServiceImpl(visitRepository));
	}

	@Test
	void findOwnerByIdWithVisitsTest() {
		OwnerWithVisits actual = target.findOwnerByIdWithVisits(1);
		assertThat(actual.getOwner()).isNotNull();
		for (Pet pet : actual.getOwner().getPets()) {
			assertThat(actual.getVisitMap().get(pet.getId()).stream()
					.allMatch(visit -> visit.getPetId().equals(pet.getId()))).isTrue();
		}
	}

	@Test
	void findPetByIdWithVisitsTest() {
		PetWithVisits actual = target.findPetByIdWithVisits(1);
		assertThat(actual.getPet()).isNotNull();
		assertThat(actual.getVisits().stream().allMatch(visit -> visit.getPetId().equals(actual.getPet().getId())))
				.isTrue();
	}

}
