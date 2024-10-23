package com.org.plane;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.org.plane.model.Ship;
import com.org.plane.repository.ShipRepository;
import com.org.plane.service.impl.ShipServiceImpl;

@SpringBootTest
@ActiveProfiles("test")
class PlaneStarApplicationTests {

	@Mock
	private ShipRepository shipRepository;

	@InjectMocks
	private ShipServiceImpl shipService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testFindById() {
		Mockito.when(shipRepository.findById(anyLong())).thenReturn(Optional.of(new Ship()));
		assertTrue(shipService.findById(1L).isPresent());
	}

}
