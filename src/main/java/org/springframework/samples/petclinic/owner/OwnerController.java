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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.samples.petclinic.customers.api.OwnerServiceApi;
import org.springframework.samples.petclinic.customers.api.PetServiceApi;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRequest;
import org.springframework.samples.petclinic.visit.api.VisitServiceApi;
import org.springframework.samples.petclinic.visit.model.Visit;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author Juergen Hoeller
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Michael Isvy
 * @author Maciej Walkowiak
 */
@Controller
class OwnerController {

	private static final String VIEWS_OWNER_CREATE_OR_UPDATE_FORM = "owners/createOrUpdateOwnerForm";
	private final OwnerServiceApi owners;
	private final PetServiceApi pets;
	private final VisitServiceApi visits;

	public OwnerController(OwnerServiceApi owners, PetServiceApi pets, VisitServiceApi visits) {
		this.owners = owners;
		this.pets = pets;
		this.visits = visits;
	}

	@GetMapping("/owners/find")
	public String initFindForm(FindForm form) {
		return "owners/findOwners";
	}

	@GetMapping("/owners")
	public String processFindForm(FindForm form, BindingResult result, Model model) {
		if (form.getLastName() == null) {
			form.setLastName("");
		}
		// find owners by last name
		Collection<Owner> results = owners.findOwnerByLastName(form.getLastName());
		if (results.isEmpty()) {
			// no owners found
			result.rejectValue("lastName", "notFound", "not found");
			return "owners/findOwners";
		} else if (results.size() == 1) {
			// 1 owner found
			int ownerId = results.iterator().next().getId();
			return "redirect:/owners/" + ownerId;
		} else {
			// multiple owners found
			model.addAttribute("selections", results);
			return "owners/ownersList";
		}
	}

	@GetMapping("/owners/new")
	public String initCreationForm(@ModelAttribute(binding = false) OwnerForm form) {
		copy(new Owner(), form);

		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/new")
	public String processCreationForm(@Valid OwnerForm form, BindingResult result) {
		if (result.hasErrors()) {
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}

		OwnerRequest request = new OwnerRequest();
		copy(form, request);
		int ownerId = owners.createOwner(request).getId();

		return "redirect:/owners/" + ownerId;
	}

	@GetMapping("/owners/{ownerId}/edit")
	public String initUpdateOwnerForm(@ModelAttribute(binding = false) OwnerForm form,
			@PathVariable("ownerId") int ownerId) {
		Owner owner = owners.findOwnerByOwnerId(ownerId);
		copy(owner, form);

		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/{ownerId}/edit")
	public String processUpdateOwnerForm(@Valid OwnerForm form, BindingResult result,
			@PathVariable("ownerId") int ownerId) {
		if (result.hasErrors()) {
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		}

		OwnerRequest request = new OwnerRequest();
		copy(form, request);
		owners.updateOwner(request, ownerId);

		return "redirect:/owners/{ownerId}";
	}

	/**
	 * Custom handler for displaying an owner.
	 *
	 * @param ownerId the ID of the owner to display
	 * @return a ModelMap with the model attributes for the view
	 */
	@GetMapping("/owners/{ownerId}")
	public String showOwner(@PathVariable("ownerId") int ownerId, ModelMap model) {
		Owner owner = owners.findOwnerByOwnerId(ownerId);
		model.addAttribute("owner", owner);

		Map<Integer, String> petTypeMap = new HashMap<>();
		pets.getPetTypes().stream().forEach(petType -> petTypeMap.put(petType.getId(), petType.getName()));
		model.addAttribute("petTypeMap", petTypeMap);

		List<Integer> petIds = owner.getPets().stream().map(pet -> pet.getId()).collect(Collectors.toList());
		Map<Integer, List<Visit>> visitMap = petIds.isEmpty() ? new HashMap<>() : visits.findVisitByPetIds(petIds);
		model.addAttribute("visitMap", visitMap);

		return "owners/ownerDetails";
	}

	protected void copy(Owner owner, OwnerForm form) {
		form.setCreate(owner.getId() == null ? true : false);
		form.setFirstName(owner.getFirstName());
		form.setLastName(owner.getLastName());
		form.setAddress(owner.getAddress());
		form.setCity(owner.getCity());
		form.setTelephone(owner.getTelephone());
	}

	protected void copy(OwnerForm form, OwnerRequest request) {
		request.setFirstName(form.getFirstName());
		request.setLastName(form.getLastName());
		request.setAddress(form.getAddress());
		request.setCity(form.getCity());
		request.setTelephone(form.getTelephone());
	}
}
