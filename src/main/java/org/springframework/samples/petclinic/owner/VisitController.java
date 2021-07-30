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

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.samples.petclinic.customers.api.OwnerServiceApi;
import org.springframework.samples.petclinic.customers.api.PetServiceApi;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.visit.api.VisitServiceApi;
import org.springframework.samples.petclinic.visit.model.VisitRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
 * @author Michael Isvy
 * @author Dave Syer
 * @author Maciej Walkowiak
 */
@Controller
@RequestMapping("/owners/{ownerId}/pets/{petId}")
class VisitController {

	private final OwnerServiceApi owners;
	private final PetServiceApi pets;
	private final VisitServiceApi visits;

	public VisitController(OwnerServiceApi owners, PetServiceApi pets, VisitServiceApi visits) {
		this.owners = owners;
		this.pets = pets;
		this.visits = visits;
	}

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	/**
	 * Called before each and every @RequestMapping annotated method. 2 goals: -
	 * Make sure we always have fresh data - Since we do not use the session scope,
	 * make sure that Pet object always has an id (Even though id is not part of the
	 * form fields)
	 */
	@ModelAttribute
	public void loadPetWithVisit(@PathVariable("ownerId") int ownerId, @PathVariable("petId") int petId, Model model) {
		Owner owner = this.owners.findOwnerByOwnerId(ownerId);
		model.addAttribute("owner", owner);
		Pet pet = owner.getPets().stream().filter(p -> p.getId().equals(petId)).findFirst().get();
		model.addAttribute("pet", pet);
		Map<Integer, String> petTypeMap = new HashMap<>();
		pets.getPetTypes().stream().forEach(petType -> petTypeMap.put(petType.getId(), petType.getName()));
		model.addAttribute("petTypeMap", petTypeMap);
		model.addAttribute("petVisits", this.visits.findVisitByPetId(petId));
	}

	// Spring MVC calls method loadPetWithVisit(...) before initNewVisitForm is
	// called
	@GetMapping("/visits/new")
	public String initNewVisitForm(@PathVariable("petId") Integer petId, Model model) {
		VisitRequest visitRequest = new VisitRequest();
		visitRequest.setNew(true);
		model.addAttribute("visit", visitRequest);
		return "pets/createOrUpdateVisitForm";
	}

	// Spring MVC calls method loadPetWithVisit(...) before processNewVisitForm is
	// called
	@PostMapping("/visits/new")
	public String processNewVisitForm(@Valid VisitRequest visitRequest, BindingResult result,
			@PathVariable("petId") int petId, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("visit", visitRequest);
			return "pets/createOrUpdateVisitForm";
		} else {
			this.visits.createVisits(visitRequest, petId);
			return "redirect:/owners/{ownerId}";
		}
	}

}
