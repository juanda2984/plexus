package com.org.plane;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.org.plane.util.KafkaProducer;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ShipControllerIntegrationTest {
	@Autowired
	private MockMvc mockMvc;

	private String token;
	
	@BeforeEach
	public void setUp() throws Exception {
		String json = "{\"username\":\"user\",\"password\":\"password\"}";

		token = "Bearer " + mockMvc.perform(post("/authenticate") 
				.contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isOk()).andReturn()
				.getResponse().getContentAsString();
	}

	@Test
	public void testSearchShips() throws Exception {
		mockMvc.perform(get("/ships/search?name=wing").header("Authorization", token))
				.andExpect(status().isOk());
	}
}
