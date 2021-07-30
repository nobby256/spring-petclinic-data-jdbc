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
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
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

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@GetMapping("/owners/new")
	public String initCreationForm(ModelMap model) {
		Owner owner = new Owner();
		model.addAttribute("owner", owner);
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/new")
	public String processCreationForm(@Valid OwnerRequest ownerRequest, BindingResult result) {
		if (result.hasErrors()) {
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		} else {
			Owner owner = this.owners.createOwner(ownerRequest);
			return "redirect:/owners/" + owner.getId();
		}
	}

	@GetMapping("/owners/find")
	public String initFindForm(ModelMap model) {
		model.put("owner", new Owner());
		return "owners/findOwners";
	}

	@GetMapping("/owners")
	public String processFindForm(@ModelAttribute Owner owner, BindingResult result, ModelMap model) {

		// allow parameterless GET request for /owners to return all records
		if (owner.getLastName() == null) {
			owner.setLastName(""); // empty string signifies broadest possible search
		}

		// find owners by last name
		Collection<Owner> results = this.owners.findOwnerByLastName(owner.getLastName());
		if (results.isEmpty()) {
			// no owners found
			result.rejectValue("lastName", "notFound", "not found");
			return "owners/findOwners";
		} else if (results.size() == 1) {
			// 1 owner found
			owner = results.iterator().next();
			return "redirect:/owners/" + owner.getId();
		} else {
			// multiple owners found
			model.put("selections", results);
			return "owners/ownersList";
		}
	}

	@GetMapping("/owners/{ownerId}/edit")
	public String initUpdateOwnerForm(@PathVariable("ownerId") int ownerId, ModelMap model) {
		Owner owner = this.owners.findOwnerByOwnerId(ownerId);
		model.addAttribute("owner", owner);
		return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
	}

	@PostMapping("/owners/{ownerId}/edit")
	public String processUpdateOwnerForm(@Valid OwnerRequest ownerRequest, BindingResult result,
			@PathVariable("ownerId") int ownerId) {
		if (result.hasErrors()) {
			return VIEWS_OWNER_CREATE_OR_UPDATE_FORM;
		} else {
			this.owners.updateOwner(ownerRequest, ownerId);
			return "redirect:/owners/{ownerId}";
		}
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

}
