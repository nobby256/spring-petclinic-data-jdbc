package org.springframework.samples.petclinic.customers.web;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.samples.petclinic.customers.model.Owner;
import org.springframework.samples.petclinic.customers.model.OwnerRequest;

@DataJdbcTest
public class OwnerServiceImplTest {

	@Autowired
	OwnerRepository ownerRepository;

	OwnerServiceImpl target;

	@BeforeEach
	void setup() {
		target = new OwnerServiceImpl(ownerRepository);
	}

	@Test
	void findOwnerByOwnerTest() {
		Owner actual = target.findOwnerByOwnerId(1);
		assertThat(actual).isNotNull();
		assertThat(actual.getPets()).hasSizeGreaterThan(0);
	}

	@Test
	void findOwnerByLastNameTest() {
		List<Owner> actualOwners = target.findOwnerByLastName("");
		assertThat(actualOwners).hasSizeGreaterThan(0);
	}

	@Test
	void createOwnerTest() {
		OwnerRequest ownerRequest = new OwnerRequest();
		ownerRequest.setFirstName("たろう");
		ownerRequest.setLastName("やまだ");
		ownerRequest.setAddress("じゅうしょ");
		ownerRequest.setCity("どこかまち");
		ownerRequest.setTelephone("1234567890");

		Owner actual = target.createOwner(ownerRequest);

		assertThat(actual.getId()).isNotNull();
		assertThat(actual.getFirstName()).isEqualTo("たろう");
		assertThat(actual.getLastName()).isEqualTo("やまだ");
		assertThat(actual.getAddress()).isEqualTo("じゅうしょ");
		assertThat(actual.getCity()).isEqualTo("どこかまち");
		assertThat(actual.getTelephone()).isEqualTo("1234567890");
		assertThat(actual.getPets()).hasSize(0);

		Owner actual2 = target.findOwnerByOwnerId(actual.getId());

		assertThat(actual2.getId()).isEqualTo(actual.getId());
		assertThat(actual2.getFirstName()).isEqualTo(actual.getFirstName());
		assertThat(actual2.getLastName()).isEqualTo(actual.getLastName());
		assertThat(actual2.getAddress()).isEqualTo(actual.getAddress());
		assertThat(actual2.getCity()).isEqualTo(actual.getCity());
		assertThat(actual2.getTelephone()).isEqualTo(actual.getTelephone());
		assertThat(actual2.getPets().size()).isEqualTo(actual.getPets().size());
	}

	@Test
	public void updateOwnerTest() {
		Owner actual = target.findOwnerByOwnerId(1);
		OwnerRequest ownerRequest = new OwnerRequest();
		ownerRequest.setFirstName("たろう");
		ownerRequest.setLastName("やまだ");
		ownerRequest.setAddress("じゅうしょ");
		ownerRequest.setCity("どこかまち");
		ownerRequest.setTelephone("1234567890");

		target.updateOwner(ownerRequest, 1);

		Owner actual2 = target.findOwnerByOwnerId(1);
		assertThat(actual2.getId()).isEqualTo(actual.getId());
		assertThat(actual2.getFirstName()).isEqualTo(ownerRequest.getFirstName());
		assertThat(actual2.getLastName()).isEqualTo(ownerRequest.getLastName());
		assertThat(actual2.getAddress()).isEqualTo(ownerRequest.getAddress());
		assertThat(actual2.getCity()).isEqualTo(ownerRequest.getCity());
		assertThat(actual2.getTelephone()).isEqualTo(ownerRequest.getTelephone());
		assertThat(actual2.getPets()).isEqualTo(actual.getPets());
	}

}
