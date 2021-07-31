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
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for the {@link PetController}
 *
 * @author Colin But
 */
@WebMvcTest(value = PetController.class)
public class PetControllerTests {

	private static final int TEST_OWNER_ID = 1;
	private static final int TEST_PET_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private PetServiceApi pets;

	@MockBean
	private OwnerServiceApi owners;

	@BeforeEach
	public void setup() {
		Pet leo = new Pet();
		leo.setId(1);
		leo.setName("Leo");
		given(this.pets.findPetByPetId(TEST_PET_ID)).willReturn(leo);

		Owner george = new Owner();
		george.setId(1);
		george.setFirstName("George");
		george.getPets().add(leo);
		given(this.owners.findOwnerByOwnerId(TEST_OWNER_ID)).willReturn(george);

		Stream.Builder<PetType> petTypeStream = Stream.builder();
		petTypeStream.add(new PetType(1, "cat"));
		petTypeStream.add(new PetType(2, "dog"));
		petTypeStream.add(new PetType(3, "lizard"));
		petTypeStream.add(new PetType(4, "snake"));
		petTypeStream.add(new PetType(5, "bird"));
		petTypeStream.add(new PetType(6, "hamster"));
		List<PetType> petTypes = petTypeStream.build().collect(Collectors.toList());
		given(this.pets.getPetTypes()).willReturn(petTypes);
	}

	@Test
	public void testInitCreationForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/new", TEST_OWNER_ID)).andExpect(status().isOk())
				.andExpect(view().name("pets/createOrUpdatePetForm")).andExpect(model().attributeExists("petForm"));
	}

	@Test
	public void testProcessCreationFormSuccess() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
				.param("create", "true")
				.param("name", "Betty")
				.param("typeId", "1")
				.param("birthDate", "2015-02-12"))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	public void testProcessCreationFormHasErrors() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/new", TEST_OWNER_ID)
				.param("create", "true")
				.param("name", "Betty")
				.param("birthDate","2015-02-12"))
				.andExpect(model().attributeHasErrors("petForm"))
				.andExpect(model().attributeHasFieldErrors("petForm", "typeId"))
				.andExpect(model().attributeHasFieldErrorCode("petForm", "typeId", "NotNull"))
				.andExpect(status().isOk())
				.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	public void testInitUpdateForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("petForm"))
				.andExpect(model().attributeExists("owner"))
				.andExpect(model().attributeExists("types"))
				.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

	@Test
	public void testProcessUpdateFormSuccess() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
				.param("create", "false")
				.param("name", "Betty")
				.param("typeId", "1")
				.param("birthDate", "2015-02-12"))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	public void testProcessUpdateFormHasErrors() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/pets/{petId}/edit", TEST_OWNER_ID, TEST_PET_ID)
				.param("create", "false")
				.param("name", "Betty")
				.param("birthDate", "2015-02-12"))
				.andExpect(model().attributeHasErrors("petForm"))
				.andExpect(model().attributeHasFieldErrors("petForm", "typeId"))
				.andExpect(model().attributeHasFieldErrorCode("petForm", "typeId", "NotNull"))
				.andExpect(status().isOk())
				.andExpect(view().name("pets/createOrUpdatePetForm"));
	}

}
