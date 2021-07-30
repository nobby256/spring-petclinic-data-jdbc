package org.springframework.samples.petclinic.service;

import org.springframework.samples.petclinic.vet.api.VetServiceApi;

//@FeignClient("vets-service")
public interface VetService extends VetServiceApi {

}
