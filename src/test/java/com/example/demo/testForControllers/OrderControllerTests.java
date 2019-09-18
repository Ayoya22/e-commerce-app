package com.example.demo.testForControllers;

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
import com.example.demo.model.persistence.AppUser;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import com.fasterxml.jackson.databind.ObjectMapper;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTests {


	@InjectMocks
	private OrderController orderController;

	@Mock
	private UserRepository userRepo;

	@Mock
	private OrderRepository orderRepo;

	@Autowired
	private MockHttpServletRequest request;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	CreateUserRequest userRequest;

	AppUser user = new AppUser();

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
		user = objectMapper.readValue(entityResult.getResponse().getContentAsString(), AppUser.class);

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
