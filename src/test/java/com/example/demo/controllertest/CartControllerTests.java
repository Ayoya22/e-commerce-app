package com.example.demo.controllertest;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.ApplicationUser;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class CartControllerTests {
    
	/** The cart controller. */
    @InjectMocks
    private CartController cartController;

    /** The user repo. */
    @Mock
    private UserRepository userRepo;

    /** The cart repo. */
    @Mock
    private CartRepository cartRepo;

    /** The item repo. */
    @Mock
    private ItemRepository itemRepo;

    /** The mock mvc. */
    @Autowired
    private MockMvc mockMvc;

    /** The object mapper. */
    @Autowired
    private ObjectMapper objectMapper;

    /** The request. */
    @Autowired
    private MockHttpServletRequest request;

    /** The user. */
    private ApplicationUser user;

    /** The user request. */
    private CreateUserRequest userRequest;

    /**
     * Inits the.
     *
     * @throws JsonProcessingException the json processing exception
     * @throws Exception               the exception
     */
    @BeforeEach
    public void initialization() throws JsonProcessingException, Exception {
		userRequest = new CreateUserRequest();
		userRequest.setUsername("ravi");
		userRequest.setPassword("password");
		userRequest.setConfirmPassword("password");

		MvcResult entity = mockMvc.perform(
			MockMvcRequestBuilders.post("/api/user/create").content(objectMapper.writeValueAsString(userRequest))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk()).andReturn();
		user = objectMapper.readValue(entity.getResponse().getContentAsString(), ApplicationUser.class);

		MvcResult result = mockMvc
			.perform(MockMvcRequestBuilders.post("/login").content(objectMapper.writeValueAsString(userRequest)))
			.andExpect(status().isOk()).andReturn();
		request.addParameter("Authorization", result.getResponse().getHeader("Authorization"));
    }

    /**
     * Test cart controller.
     *
     * @throws JsonProcessingException the json processing exception
     * @throws Exception               the exception
     */
    @Test
    public void testAddAndRemoveFromCart() throws JsonProcessingException, Exception {
		when(userRepo.findByUsername(Mockito.anyString())).thenReturn(user);

		// test the addToCart method positive and negative flows

		ModifyCartRequest addToCartRequest = new ModifyCartRequest();
		addToCartRequest.setItemId(1L);
		addToCartRequest.setQuantity(10);
		addToCartRequest.setUsername("ravi");

		mockMvc.perform(MockMvcRequestBuilders.post("/api/cart/addToCart")
			.content(objectMapper.writeValueAsString(addToCartRequest)).contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON).header("Authorization", request.getParameter("Authorization")))
			.andExpect(status().isOk());

		mockMvc.perform(
			MockMvcRequestBuilders.post("/api/cart/addToCart").content(objectMapper.writeValueAsString(addToCartRequest))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isUnauthorized());

		addToCartRequest.setUsername("ravi1");
		mockMvc.perform(MockMvcRequestBuilders.post("/api/cart/addToCart")
			.content(objectMapper.writeValueAsString(addToCartRequest)).contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON).header("Authorization", request.getParameter("Authorization")))
			.andExpect(status().isNotFound());

		// test the removeFromCart method positive and negative flows

		ModifyCartRequest removeFromCartRequest = new ModifyCartRequest();
		removeFromCartRequest.setItemId(1L);
		removeFromCartRequest.setQuantity(4);
		removeFromCartRequest.setUsername("ravi");

		mockMvc.perform(MockMvcRequestBuilders.post("/api/cart/removeFromCart")
			.content(objectMapper.writeValueAsString(removeFromCartRequest)).contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON).header("Authorization", request.getParameter("Authorization")))
			.andExpect(status().isOk());

		mockMvc.perform(MockMvcRequestBuilders.post("/api/cart/removeFromCart")
			.content(objectMapper.writeValueAsString(removeFromCartRequest)).contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());

		removeFromCartRequest.setUsername("ravi1");
		mockMvc.perform(MockMvcRequestBuilders.post("/api/cart/removeFromCart")
			.content(objectMapper.writeValueAsString(removeFromCartRequest)).contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON).header("Authorization", request.getParameter("Authorization")))
			.andExpect(status().isNotFound());
    }
}
