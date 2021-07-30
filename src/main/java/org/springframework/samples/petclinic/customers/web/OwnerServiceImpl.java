package org.springframework.samples.petclinic.customers.web;

import java.util.List;

import org.springframework.samples.petclinic.customers.api.OwnerServiceApi;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRequest;
import org.springframework.stereotype.Service;

@Service
public class OwnerServiceImpl implements OwnerServiceApi {

	protected OwnerRepository ownerRepository;

	public OwnerServiceImpl(OwnerRepository ownerRepository) {
		this.ownerRepository = ownerRepository;
	}

	@Override
	public Owner createOwner(OwnerRequest ownerRequest) {
		return save(ownerRequest, null);
	}

	@Override
	public void updateOwner(OwnerRequest ownerRequest, int ownerId) {
		save(ownerRequest, ownerId);
	}

	protected Owner save(OwnerRequest ownerRequest, Integer ownerId) {
		Owner owner;
		if (ownerId != null) {
			owner = findOwnerByOwnerId(ownerId);
		} else {
			owner = new Owner();
		}
		owner.setFirstName(ownerRequest.getFirstName());
		owner.setLastName(ownerRequest.getLastName());
		owner.setAddress(ownerRequest.getAddress());
		owner.setCity(ownerRequest.getCity());
		owner.setTelephone(ownerRequest.getTelephone());
		return ownerRepository.save(owner);
	}

	@Override
	public Owner findOwnerByOwnerId(int ownerId) {
		return ownerRepository.findById(ownerId)
				.orElseThrow(() -> new ResourceNotFoundException("Owner " + ownerId + " is not found"));
	}

	@Override
	public List<Owner> findOwnerByLastName(String lastName) {
		return ownerRepository.findByLastName(lastName);
	}

}
