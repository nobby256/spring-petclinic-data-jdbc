package org.springframework.samples.petclinic.service;

import org.springframework.samples.petclinic.customers.api.OwnerServiceApi;

//@FeignClient("customers-service")
public interface OwnerService extends OwnerServiceApi {

}
