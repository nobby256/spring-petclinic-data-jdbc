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
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.core.convert.ConversionService;
import org.springframework.samples.petclinic.customers.api.OwnerServiceApi;
import org.springframework.samples.petclinic.customers.api.PetServiceApi;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.visit.api.VisitServiceApi;
import org.springframework.samples.petclinic.visit.model.Visit;
import org.springframework.samples.petclinic.visit.model.VisitRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
	private final ConversionService convertionService;

	public VisitController(OwnerServiceApi owners, PetServiceApi pets, VisitServiceApi visits,
			ConversionService convertionService) {
		this.owners = owners;
		this.pets = pets;
		this.visits = visits;
		this.convertionService = convertionService;
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
	public String initNewVisitForm(VisitForm form) {
		Visit visit = new Visit();
		visit.setDate(getToday());
		copy(visit, form);
		return "pets/createOrUpdateVisitForm";
	}
	
	protected LocalDate getToday() {
		return LocalDate.now();
	}

	// Spring MVC calls method loadPetWithVisit(...) before processNewVisitForm is
	// called
	@PostMapping("/visits/new")
	public String processNewVisitForm(@Valid VisitForm form, BindingResult result, @PathVariable("petId") int petId) {
		if (result.hasErrors()) {
			return "pets/createOrUpdateVisitForm";
		}

		VisitRequest request = new VisitRequest();
		copy(form, request);
		visits.createVisits(request, petId);

		return "redirect:/owners/{ownerId}";
	}

	protected void copy(Visit visit, VisitForm form) {
		form.setCreate(visit.getId() == null ? true : false);
		form.setDate(convertionService.convert(visit.getDate(), String.class));
		form.setDescription(visit.getDescription());
	}

	protected void copy(VisitForm form, VisitRequest request) {
		request.setDate(convertionService.convert(form.getDate(), LocalDate.class));
		request.setDescription(form.getDescription());
	}
}
