package org.springframework.samples.petclinic.service;

import org.springframework.samples.petclinic.customers.api.PetServiceApi;

//@FeignClient("customers-service")
public interface PetService extends PetServiceApi {

}
