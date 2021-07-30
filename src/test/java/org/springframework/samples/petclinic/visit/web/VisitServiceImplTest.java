package org.springframework.samples.petclinic.visit.web;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.samples.petclinic.visit.model.Visit;

@DataJdbcTest
public class VisitServiceImplTest {

	@Autowired
	VisitRepository visitRepository;

	VisitServiceImpl target;

	@BeforeEach
	void setup() {
		target = new VisitServiceImpl(visitRepository);
	}

	@Test
	void findByPetIdTest() {
		List<Visit> actual = target.findVisitByPetId(7);
		assertThat(actual).hasSizeGreaterThan(0);
	}

	@Test
	void findVisitByPetIdsTest() {
		List<Integer> args = List.of(Integer.valueOf(7), Integer.valueOf(8));

		Map<Integer, List<Visit>> actual = target.findVisitByPetIds(args);

		assertThat(actual).hasSizeGreaterThan(0);
	}

	@Test
	void visitsMultiGetTest() {
		List<Integer> args = List.of(Integer.valueOf(7), Integer.valueOf(8));

		List<Visit> actual = target.visitsMultiGet(args);

		assertThat(actual).hasSizeGreaterThan(0);
	}

}
