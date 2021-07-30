/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.owner;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.samples.petclinic.customers.api.OwnerServiceApi;
import org.springframework.samples.petclinic.customers.api.PetServiceApi;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.model.PetRequest;
import org.springframework.samples.petclinic.customers.model.PetType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Maciej Walkowiak
 */
@Controller
@RequestMapping("/owners/{ownerId}")
class PetController {

	private static final String VIEWS_PETS_CREATE_OR_UPDATE_FORM = "pets/createOrUpdatePetForm";
	private final PetServiceApi pets;
	private final OwnerServiceApi owners;

	public PetController(PetServiceApi pets, OwnerServiceApi owners) {
		this.pets = pets;
		this.owners = owners;
	}

	@ModelAttribute("types")
	public Collection<PetType> populatePetTypes() {
		return this.pets.getPetTypes();
	}

	@ModelAttribute("owner")
	public Owner findOwner(@PathVariable("ownerId") int ownerId) {
		return this.owners.findOwnerByOwnerId(ownerId);
	}

	@InitBinder("owner")
	public void initOwnerBinder(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@InitBinder("pet")
	public void initPetBinder(WebDataBinder dataBinder) {
		dataBinder.setValidator(new PetValidator());
	}

	@GetMapping("/pets/new")
	public String initCreationForm(Owner owner, ModelMap model) {
		PetRequest petRequest = new PetRequest();
		petRequest.setNew(true);
		model.put("pet", petRequest);
		return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/pets/new")
	public String processCreationForm(@PathVariable("ownerId") int ownerId, @Valid PetRequest petRequest,
			BindingResult result, ModelMap model) {
		if (!pets.findPetByOwnerIdAndPetName(ownerId, petRequest.getName()).isEmpty()) {
			result.rejectValue("name", "duplicate", "already exists");
		}
		if (result.hasErrors()) {
			model.put("pet", petRequest);
			return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
		} else {
			this.pets.createPet(petRequest, ownerId);
			return "redirect:/owners/{ownerId}";
		}
	}

	@GetMapping("/pets/{petId}/edit")
	public String initUpdateForm(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId,
			ModelMap model) {
		Owner owner = (Owner) model.getAttribute("owner");
		Pet pet = owner.getPets().stream().filter(p -> p.getId().equals(petId)).findFirst().get();

		PetRequest petRequest = new PetRequest();
		petRequest.setBirthDate(pet.getBirthDate());
		petRequest.setName(pet.getName());
		petRequest.setTypeId(pet.getTypeId());
		model.put("pet", petRequest);

		return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/pets/{petId}/edit")
	public String processUpdateForm(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId,
			@Valid PetRequest petRequest, BindingResult result, ModelMap model) {
		if (result.hasErrors()) {
			model.put("pet", petRequest);
			return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
		} else {
			this.pets.updatePet(petRequest, ownerId, petId);
			return "redirect:/owners/{ownerId}";
		}
	}

}
