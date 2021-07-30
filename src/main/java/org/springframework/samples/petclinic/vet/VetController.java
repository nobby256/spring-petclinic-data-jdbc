/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.vet;

import java.util.List;

import org.springframework.samples.petclinic.vet.api.VetServiceApi;
import org.springframework.samples.petclinic.vet.model.VetDetail;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Ken Krebs
 * @author Arjen Poutsma
 * @author Maciej Walkowiak
 */
@Controller
class VetController {

	private final VetServiceApi vets;

	public VetController(VetServiceApi vets) {
		this.vets = vets;
	}

	@ModelAttribute("vets")
	public List<VetDetail> populateVets() {
		return vets.findAllVets();
	}

	@GetMapping("/vets")
	public String showVetList() {
		return "vets/vetList";
	}

}
