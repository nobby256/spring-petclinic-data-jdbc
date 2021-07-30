package org.springframework.samples.petclinic.vet.web;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.Repository;
import org.springframework.samples.petclinic.vet.model.Specialty;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Maciej Walkowiak
 */
public interface SpecialtyRepository extends Repository<Specialty, Integer> {

	@Transactional(readOnly = true)
	@Cacheable("specialty")
	Specialty findById(Integer id);
}
