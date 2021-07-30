package org.springframework.samples.petclinic.customers.api;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

public interface OwnerServiceApi {

	@PostMapping(path = "/owners")
	@ResponseStatus(HttpStatus.CREATED)
	Owner createOwner(@Valid @RequestBody OwnerRequest ownerRequest);

	@GetMapping(path = "/owners/{ownerId}")
	Owner findOwnerByOwnerId(@PathVariable("ownerId") int ownerId);

	@GetMapping(path = "/owners", params = { "lastName" })
	List<Owner> findOwnerByLastName(@RequestParam(name = "lastName", required = false) String lastName);

	@PutMapping(path = "/owners/{ownerId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void updateOwner(@Valid @RequestBody OwnerRequest ownerRequest, @PathVariable("ownerId") int ownerId);

}
