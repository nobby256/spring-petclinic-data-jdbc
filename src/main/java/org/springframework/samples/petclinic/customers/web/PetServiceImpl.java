package org.springframework.samples.petclinic.customers.web;

import java.util.List;

import org.springframework.samples.petclinic.customers.api.PetServiceApi;
import org.springframework.samples.petclinic.customers.model.Pet;
import org.springframework.samples.petclinic.customers.model.PetRequest;
import org.springframework.samples.petclinic.customers.model.PetType;
import org.springframework.stereotype.Service;

@Service
public class PetServiceImpl implements PetServiceApi {

	protected PetRepository petRepository;

	public PetServiceImpl(PetRepository petRepository) {
		this.petRepository = petRepository;
	}

	@Override
	public List<PetType> getPetTypes() {
		return petRepository.findPetTypes();
	}

	@Override
	public Pet createPet(PetRequest petRequest, int ownerId) {
		return save(petRequest, ownerId, null);

	}

	@Override
	public void updatePet(PetRequest petRequest, int ownerId, int petId) {
		save(petRequest, ownerId, petId);
	}

	protected Pet save(PetRequest petRequest, int ownerId, Integer petId) {
		Pet pet;
		if (petId != null) {
			pet = findPetByPetId(petId);
		} else {
			pet = new Pet();
		}
		pet.setName(petRequest.getName());
		pet.setBirthDate(petRequest.getBirthDate());
		pet.setOwnerId(ownerId);
		petRepository.findPetType(petRequest.getTypeId()).ifPresent(petType -> pet.setTypeId(petType.getId()));
		return petRepository.save(pet);
	}

	@Override
	public Pet findPetByPetId(int petId) {
		return petRepository.findById(petId)
				.orElseThrow(() -> new ResourceNotFoundException("Pet " + petId + " is not found"));
	}

	@Override
	public List<Pet> findPetByOwnerIdAndPetName(int ownerId, String petName) {
		return petRepository.findByOwnerIdAndName(ownerId, petName);
	}

	@Override
	public List<Pet> findPetByOwnerId(int ownerId) {
		return petRepository.findByOwnerId(ownerId);
	}

}
