package org.springframework.samples.petclinic.owner;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.samples.petclinic.customers.api.OwnerServiceApi;
import org.springframework.samples.petclinic.customers.api.PetServiceApi;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.model.PetType;
import org.springframework.samples.petclinic.visit.api.VisitServiceApi;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for {@link VisitController}
 *
 * @author Colin But
 */
@WebMvcTest(VisitController.class)
public class VisitControllerTests {

	private static final int TEST_OWNER_ID = 1;
	private static final int TEST_PET_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private VisitServiceApi visits;

	@MockBean
	private PetServiceApi pets;

	@MockBean
	private OwnerServiceApi owners;

	@BeforeEach
	public void init() {
		Pet leo = new Pet();
		leo.setId(1);
		leo.setName("Leo");

		Owner george = new Owner();
		george.setId(1);
		george.setFirstName("George");
		george.getPets().add(leo);

		Stream.Builder<PetType> petTypeStream = Stream.builder();
		petTypeStream.add(new PetType(1, "cat"));
		petTypeStream.add(new PetType(2, "dog"));
		petTypeStream.add(new PetType(3, "lizard"));
		petTypeStream.add(new PetType(4, "snake"));
		petTypeStream.add(new PetType(5, "bird"));
		petTypeStream.add(new PetType(6, "hamster"));
		List<PetType> petTypes = petTypeStream.build().collect(Collectors.toList());

		given(this.owners.findOwnerByOwnerId(TEST_OWNER_ID)).willReturn(george);
		given(this.pets.findPetByPetId(TEST_PET_ID)).willReturn(leo);
		given(this.pets.getPetTypes()).willReturn(petTypes);
	}

	@Test
	public void testInitNewVisitForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID))
				.andExpect(status().isOk()).andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

	@Test
	public void testProcessNewVisitFormSuccess() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID)
				.param("date", "2021-01-01").param("description", "Visit Description"))
				.andExpect(status().is3xxRedirection()).andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	public void testProcessNewVisitFormHasErrors() throws Exception {
		mockMvc.perform(
				post("/owners/{ownerId}/pets/{petId}/visits/new", TEST_OWNER_ID, TEST_PET_ID).param("name", "George"))
				.andExpect(model().attributeHasErrors("visitForm")).andExpect(status().isOk())
				.andExpect(view().name("pets/createOrUpdateVisitForm"));
	}

}
