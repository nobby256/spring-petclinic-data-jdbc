package org.springframework.samples.petclinic.visit.api;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.visit.model.Visit;
import org.springframework.samples.petclinic.visit.model.VisitRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface VisitServiceApi {

	@PostMapping("owners/{ownerId}/pets/{petId}/visits")
	@ResponseStatus(HttpStatus.CREATED)
	Visit createVisits(@Valid @RequestBody VisitRequest visitRequest, @PathVariable("petId") int petId);

	@GetMapping("owners/*/pets/{petId}/visits")
	List<Visit> findVisitByPetId(@PathVariable("petId") int petId);

	@GetMapping("pets/visits")
	Map<Integer, List<Visit>> findVisitByPetIds(@RequestParam("petId") List<Integer> petIds);

	@GetMapping("pets/visits2")
	List<Visit> visitsMultiGet(@RequestParam("petId") List<Integer> petIds);

}
