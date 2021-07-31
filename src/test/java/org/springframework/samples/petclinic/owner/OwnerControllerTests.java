package org.springframework.samples.petclinic.owner;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.util.Lists;
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
import org.springframework.samples.petclinic.visit.model.Visit;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Test class for {@link OwnerController}
 *
 * @author Colin But
 */
@WebMvcTest(OwnerController.class)
public class OwnerControllerTests {

	private static final int TEST_OWNER_ID = 1;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OwnerServiceApi owners;

	@MockBean
	private PetServiceApi pets;

	@MockBean
	private VisitServiceApi visits;

	private Owner george;

	@BeforeEach
	public void setup() {
		Pet leo = new Pet();
		leo.setId(1);
		leo.setName("Leo");

		george = new Owner();
		george.setId(TEST_OWNER_ID);
		george.setFirstName("George");
		george.setLastName("Franklin");
		george.setAddress("110 W. Liberty St.");
		george.setCity("Madison");
		george.setTelephone("6085551023");
		george.getPets().add(leo);
		given(this.owners.findOwnerByOwnerId(TEST_OWNER_ID)).willReturn(george);

		Owner joe = new Owner();
		joe.setId(2);
		joe.setFirstName("Joe");
		joe.setLastName("Bloggs");
		joe.setAddress("123 Caramel Street");
		joe.setCity("London");
		joe.setTelephone("01316761638");
		given(this.owners.createOwner(any())).willReturn(joe);

		Stream.Builder<PetType> petTypeStream = Stream.builder();
		petTypeStream.add(new PetType(1, "cat"));
		petTypeStream.add(new PetType(2, "dog"));
		petTypeStream.add(new PetType(3, "lizard"));
		petTypeStream.add(new PetType(4, "snake"));
		petTypeStream.add(new PetType(5, "bird"));
		petTypeStream.add(new PetType(6, "hamster"));
		List<PetType> petTypes = petTypeStream.build().collect(Collectors.toList());
		given(this.pets.getPetTypes()).willReturn(petTypes);

		Map<Integer, List<Visit>> visitMap = new HashMap<>();
		given(this.visits.findVisitByPetIds(any())).willReturn(visitMap);
	}

	@Test
	public void testInitCreationForm() throws Exception {
		mockMvc.perform(get("/owners/new"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("ownerForm"))
				.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	public void testProcessCreationFormSuccess() throws Exception {
		mockMvc.perform(post("/owners/new")
				.param("firstName", "Joe")
				.param("lastName", "Bloggs")
				.param("address", "123 Caramel Street")
				.param("city", "London")
				.param("telephone", "01316761638"))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/2"));
	}

	@Test
	public void testProcessCreationFormHasErrors() throws Exception {
		mockMvc.perform(post("/owners/new")
				.param("create", "true")
				.param("firstName", "Joe")
				.param("lastName", "Bloggs")
				.param("city", "London"))
				.andExpect(status().isOk())
				.andExpect(model().attributeHasErrors("ownerForm"))
				.andExpect(model().attributeHasFieldErrors("ownerForm", "address"))
				.andExpect(model().attributeHasFieldErrors("ownerForm", "telephone"))
				.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	public void testInitFindForm() throws Exception {
		mockMvc.perform(get("/owners/find"))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("findForm"))
				.andExpect(view().name("owners/findOwners"));
	}

	@Test
	public void testProcessFindFormSuccess() throws Exception {
		given(this.owners.findOwnerByLastName("")).willReturn(Lists.newArrayList(george, new Owner()));
		mockMvc.perform(get("/owners"))
				.andExpect(status().isOk())
				.andExpect(view().name("owners/ownersList"));
	}

	@Test
	public void testProcessFindFormByLastName() throws Exception {
		given(this.owners.findOwnerByLastName(george.getLastName())).willReturn(Lists.newArrayList(george));
		mockMvc.perform(get("/owners").param("lastName", "Franklin"))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/" + TEST_OWNER_ID));
	}

	@Test
	public void testProcessFindFormNoOwnersFound() throws Exception {
		mockMvc.perform(get("/owners").param("lastName", "Unknown Surname"))
				.andExpect(status().isOk())
				.andExpect(model().attributeHasFieldErrors("findForm", "lastName"))
				.andExpect(model().attributeHasFieldErrorCode("findForm", "lastName", "notFound"))
				.andExpect(view().name("owners/findOwners"));
	}

	@Test
	public void testInitUpdateOwnerForm() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}/edit", TEST_OWNER_ID)).andExpect(status().isOk())
				.andExpect(model().attributeExists("ownerForm"))
				.andExpect(model().attribute("ownerForm", hasProperty("lastName", is("Franklin"))))
				.andExpect(model().attribute("ownerForm", hasProperty("firstName", is("George"))))
				.andExpect(model().attribute("ownerForm", hasProperty("address", is("110 W. Liberty St."))))
				.andExpect(model().attribute("ownerForm", hasProperty("city", is("Madison"))))
				.andExpect(model().attribute("ownerForm", hasProperty("telephone", is("6085551023"))))
				.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	public void testProcessUpdateOwnerFormSuccess() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID)
				.param("firstName", "Joe")
				.param("lastName", "Bloggs")
				.param("address", "123 Caramel Street")
				.param("city", "London")
				.param("telephone", "01616291589"))
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/owners/{ownerId}"));
	}

	@Test
	public void testProcessUpdateOwnerFormHasErrors() throws Exception {
		mockMvc.perform(post("/owners/{ownerId}/edit", TEST_OWNER_ID).param("firstName", "Joe")
				.param("lastName", "Bloggs").param("city", "London")).andExpect(status().isOk())
				.andExpect(model().attributeHasErrors("ownerForm"))
				.andExpect(model().attributeHasFieldErrors("ownerForm", "address"))
				.andExpect(model().attributeHasFieldErrors("ownerForm", "telephone"))
				.andExpect(view().name("owners/createOrUpdateOwnerForm"));
	}

	@Test
	public void testShowOwner() throws Exception {
		mockMvc.perform(get("/owners/{ownerId}", TEST_OWNER_ID))
				.andExpect(status().isOk())
				.andExpect(model().attribute("owner", hasProperty("lastName", is("Franklin"))))
				.andExpect(model().attribute("owner", hasProperty("firstName", is("George"))))
				.andExpect(model().attribute("owner", hasProperty("address", is("110 W. Liberty St."))))
				.andExpect(model().attribute("owner", hasProperty("city", is("Madison"))))
				.andExpect(model().attribute("owner", hasProperty("telephone", is("6085551023"))))
				.andExpect(model().attribute("owner", hasProperty("telephone", is("6085551023"))))
				.andExpect(model().attributeExists("petTypeMap"))
				.andExpect(model().attributeExists("visitMap"))
				.andExpect(view().name("owners/ownerDetails"));
	}

}
