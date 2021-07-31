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

import java.time.LocalDate;
import java.util.Collection;

import javax.validation.Valid;

import org.springframework.core.convert.ConversionService;
import org.springframework.samples.petclinic.customers.api.OwnerServiceApi;
import org.springframework.samples.petclinic.customers.api.PetServiceApi;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.model.PetRequest;
import org.springframework.samples.petclinic.customers.model.PetType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
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
	private final ConversionService convertionService;

	public PetController(PetServiceApi pets, OwnerServiceApi owners, ConversionService convertionService) {
		this.pets = pets;
		this.owners = owners;
		this.convertionService = convertionService;
	}

	@ModelAttribute("types")
	public Collection<PetType> populatePetTypes() {
		return pets.getPetTypes();
	}

	@ModelAttribute("owner")
	public Owner findOwner(@PathVariable("ownerId") int ownerId) {
		return owners.findOwnerByOwnerId(ownerId);
	}

	@GetMapping("/pets/new")
	public String initCreationForm(@ModelAttribute(binding = false) PetForm form) {
		copy(new Pet(), form);
		return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/pets/new")
	public String processCreationForm(@Valid PetForm form, BindingResult result, @PathVariable("ownerId") int ownerId) {
		if (result.hasErrors()) {
			return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
		}
		if (!pets.findPetByOwnerIdAndPetName(ownerId, form.getName()).isEmpty()) {
			result.rejectValue("name", "duplicate", "already exists");
		}

		PetRequest request = new PetRequest();
		copy(form, request);
		pets.createPet(request, ownerId);

		return "redirect:/owners/{ownerId}";
	}

	@GetMapping("/pets/{petId}/edit")
	public String initUpdateForm(@ModelAttribute(binding = false) PetForm form, Owner owner,
			@PathVariable("petId") int petId) {
		Pet pet = owner.getPets().stream().filter(p -> p.getId().equals(petId)).findFirst().get();
		copy(pet, form);

		return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/pets/{petId}/edit")
	public String processUpdateForm(@Valid PetForm form, BindingResult result, @PathVariable("ownerId") int ownerId,
			@PathVariable("petId") int petId) {
		if (result.hasErrors()) {
			return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
		}

		PetRequest request = new PetRequest();
		copy(form, request);
		pets.updatePet(request, ownerId, petId);

		return "redirect:/owners/{ownerId}";
	}

	protected void copy(Pet pet, PetForm form) {
		form.setCreate(pet.getId() == null ? true : false);
		form.setBirthDate(convertionService.convert(pet.getBirthDate(), String.class));
		form.setName(pet.getName());
		form.setTypeId(pet.getTypeId());
	}

	protected void copy(PetForm form, PetRequest request) {
		request.setBirthDate(convertionService.convert(form.getBirthDate(), LocalDate.class));
		request.setName(form.getName());
		request.setTypeId(form.getTypeId());
	}
}
