package com.example.demo.controllertest;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.repositories.ItemRepository;

/**
 * The Class ItemControllerTest.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTests {

	/** The item controller. */
	@InjectMocks
	private ItemController itemController;

	/** The item repo. */
	@Mock
	private ItemRepository itemRepo;

	/** The mock mvc. */
	@Autowired
	private MockMvc mockMvc;

	/**
	 * Test get all items api.
	 *
	 * @return the all item test
	 * @throws Exception the exception
	 */
	@Test
	public void testGetItemsApi() throws Exception {
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get("/api/item").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();

		assertNotNull(mvcResult.getResponse().getContentAsString());
	}

	/**
	 * Test get the item by name api.
	 *
	 * @return the item by name
	 * @throws Exception the exception
	 */
	@Test
	public void testGetItemByNameApi() throws Exception {
		MvcResult mvcResult = mockMvc.perform(
				MockMvcRequestBuilders.get("/api/item/name/{name}", "Round Widget").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		assertNotNull(mvcResult.getResponse().getContentAsString());

		mvcResult = mockMvc.perform(
				MockMvcRequestBuilders.get("/api/item/name/{name}", "Toys").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andReturn();

	}

	/**
	 * Test get item by id api.
	 *
	 * @return the item by id
	 * @throws Exception the exception
	 */
	@Test
	public void testGetItemByIdApi() throws Exception {
		MvcResult mvcResult = mockMvc
				.perform(MockMvcRequestBuilders.get("/api/item/{id}", 1).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		assertNotNull(mvcResult.getResponse().getContentAsString());

		mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/item/{id}", 57).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()).andReturn();
	}
}
