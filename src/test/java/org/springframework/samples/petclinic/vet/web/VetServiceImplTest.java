package org.springframework.samples.petclinic.vet.web;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.samples.petclinic.vet.model.VetDetail;

@DataJdbcTest
public class VetServiceImplTest {

	@Autowired
	VetRepository vetRepository;

	@Autowired
	SpecialtyRepository specialtyRepository;

	VetServiceImpl target;

	@BeforeEach
	void setup() {
		target = new VetServiceImpl(vetRepository, specialtyRepository);
	}

	@Test
	void getVetsTest() {
		List<VetDetail> actual = target.findAllVets();
		assertThat(actual).hasSize(6);
		assertThat(actual.get(0).getId()).isEqualTo(6);
		assertThat(actual.get(0).getSpecialties()).hasSize(0);
		assertThat(actual.get(1).getId()).isEqualTo(5);
		assertThat(actual.get(1).getSpecialties()).hasSize(1);
		assertThat(actual.get(2).getId()).isEqualTo(4);
		assertThat(actual.get(2).getSpecialties()).hasSize(1);
		assertThat(actual.get(3).getId()).isEqualTo(3);
		assertThat(actual.get(3).getSpecialties()).hasSize(2);
		assertThat(actual.get(4).getId()).isEqualTo(2);
		assertThat(actual.get(4).getSpecialties()).hasSize(1);
		assertThat(actual.get(5).getId()).isEqualTo(1);
		assertThat(actual.get(5).getSpecialties()).hasSize(0);
	}

}
