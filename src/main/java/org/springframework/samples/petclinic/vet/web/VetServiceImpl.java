package org.springframework.samples.petclinic.vet.web;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.samples.petclinic.vet.api.VetServiceApi;
import org.springframework.samples.petclinic.vet.model.Specialty;
import org.springframework.samples.petclinic.vet.model.Vet;
import org.springframework.samples.petclinic.vet.model.VetDetail;
import org.springframework.stereotype.Service;

@Service
public class VetServiceImpl implements VetServiceApi {

	protected VetRepository vetRepository;

	protected SpecialtyRepository specialtyRepository;

	public VetServiceImpl(VetRepository vetRepository, SpecialtyRepository specialtyRepository) {
		this.vetRepository = vetRepository;
		this.specialtyRepository = specialtyRepository;
	}

	@Override
	public List<VetDetail> findAllVets() {
		return vetRepository.findAll().stream().map(this::vetToVetDetail).collect(Collectors.toList());
	}

	protected List<VetDetail> vetToVetDetail(List<VetDetail> details) {
		return details.stream().map(this::vetToVetDetail).collect(Collectors.toList());
	}

	protected VetDetail vetToVetDetail(Vet vet) {
		List<Specialty> specialities = vet.getSpecialtyRefs().stream().map(specialtyRef -> {
			return specialtyRepository.findById(specialtyRef.getSpecialty());
		}).collect(Collectors.toList());
		return new VetDetail(vet, specialities);
	}

}
