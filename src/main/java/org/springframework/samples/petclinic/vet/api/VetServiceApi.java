package org.springframework.samples.petclinic.vet.api;

import java.util.List;

import org.springframework.samples.petclinic.vet.model.VetDetail;
import org.springframework.web.bind.annotation.GetMapping;

public interface VetServiceApi {

	@GetMapping("/vets")
    List<VetDetail> findAllVets();

}
