package com.example.demo.controllertest;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.ApplicationUser;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class OrderControllerTest.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTests {

	/** The order controller. */
	@InjectMocks
	private OrderController orderController;

	/** The user repo. */
	@Mock
	private UserRepository userRepo;

	/** The order repo. */
	@Mock
	private OrderRepository orderRepo;

	/** The request. */
	@Autowired
	private MockHttpServletRequest request;

	/** The mock mvc. */
	@Autowired
	private MockMvc mockMvc;

	/** The object mapper. */
	@Autowired
	private ObjectMapper objectMapper;

	/** The user request. */
	CreateUserRequest userRequest;

	/** The user. */
	ApplicationUser user = new ApplicationUser();

	/**
	 * Inits the user creation.
	 *
	 * @throws Exception the exception
	 */
	@BeforeEach
	public void initialization() throws Exception {
		//create and login user to get bearer token
		userRequest = new CreateUserRequest();
		userRequest.setUsername("ravi3");
		userRequest.setPassword("password");
		userRequest.setConfirmPassword("password");

		MvcResult entityResult = mockMvc.perform(
				MockMvcRequestBuilders.post("/api/user/create").content(objectMapper.writeValueAsString(userRequest))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		user = objectMapper.readValue(entityResult.getResponse().getContentAsString(), ApplicationUser.class);

		MvcResult result = mockMvc
				.perform(MockMvcRequestBuilders.post("/login").content(objectMapper.writeValueAsString(userRequest)))
				.andExpect(status().isOk()).andReturn();
		request.addParameter("Authorization", result.getResponse().getHeader("Authorization"));

		//add items in user's cart.
		ModifyCartRequest cartRequest = new ModifyCartRequest();
		cartRequest.setItemId(1L);
		cartRequest.setQuantity(8);
		cartRequest.setUsername("ravi3");

		mockMvc.perform(MockMvcRequestBuilders.post("/api/cart/addToCart")
				.content(objectMapper.writeValueAsString(cartRequest)).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON).header("Authorization", request.getParameter("Authorization")))
				.andExpect(status().isOk());
	}

	/**
	 * Test order creation and history.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testSubmitOrderAndHistoryApis() throws Exception {
		//test submit order api positive and negative flows
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/order/submit/{username}", "Tarun4")
				.accept(MediaType.APPLICATION_JSON).header("Authorization", request.getParameter("Authorization")))
				.andExpect(status().isOk()).andReturn();
		assertNotNull(result.getResponse().getContentAsString());

		mockMvc.perform(MockMvcRequestBuilders.post("/api/order/submit/{username}", "testValue")
				.accept(MediaType.APPLICATION_JSON).header("Authorization", request.getParameter("Authorization")))
				.andExpect(status().isNotFound());

		mockMvc.perform(MockMvcRequestBuilders.post("/api/order/submit/{username}", "Tarun4")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());

		//test order history api positive and negative flows
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/order/history/{username}", "Tarun4")
				.accept(MediaType.APPLICATION_JSON).header("Authorization", request.getParameter("Authorization")))
				.andExpect(status().isOk()).andReturn();
		assertNotNull(mvcResult.getResponse().getContentAsString());

		mockMvc.perform(MockMvcRequestBuilders.get("/api/order/history/{username}", "testValue")
				.accept(MediaType.APPLICATION_JSON).header("Authorization", request.getParameter("Authorization")))
				.andExpect(status().isNotFound());

		mockMvc.perform(MockMvcRequestBuilders.get("/api/order/history/{username}", "Tarun4")
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
	}
}
