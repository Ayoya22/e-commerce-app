package com.example.demo.testForControllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
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
import com.example.demo.model.persistence.AppUser;
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
    

    @InjectMocks
    private CartController cartController;


    @Mock
    private UserRepository userRepo;


    @Mock
    private CartRepository cartRepo;


    @Mock
    private ItemRepository itemRepo;


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;


    @Autowired
    private MockHttpServletRequest request;


    private AppUser user;


    private CreateUserRequest userRequest;


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
		user = objectMapper.readValue(entity.getResponse().getContentAsString(), AppUser.class);

		MvcResult result = mockMvc
			.perform(MockMvcRequestBuilders.post("/login").content(objectMapper.writeValueAsString(userRequest)))
			.andExpect(status().isOk()).andReturn();
		request.addParameter("Authorization", result.getResponse().getHeader("Authorization"));
    }


    @Test
    public void testToCheckRemoveFromCart() throws JsonProcessingException, Exception {
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
