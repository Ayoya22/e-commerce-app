package com.example.demo.testForControllers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserLoginTests {


    @InjectMocks
    private UserController userController;


    @Mock
    private UserRepository userRepo;

    @Mock
    private CartRepository cartRepo;


    @Mock
    private BCryptPasswordEncoder encoder;


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private MockHttpServletRequest request;

    @Autowired
    private ObjectMapper objectMapper;


    CreateUserRequest userRequest;

    @BeforeEach
    public void initializationSetup() {
    	userRequest = new CreateUserRequest();
    	userRequest.setUsername("mert");
    	userRequest.setPassword("password");
    	userRequest.setConfirmPassword("password");
    }

    @Test
    public void testForUserCreation() throws Exception {
    	mockMvc.perform(
    		MockMvcRequestBuilders.post("/api/user/create").content(objectMapper.writeValueAsString(userRequest))
    			.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
    		.andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.username").exists())
    		.andReturn();
    }

    @Test   
    public void testCreateUserLoginEvent() throws Exception {
    	userRequest.setUsername("time");
    	mockMvc.perform(
    		MockMvcRequestBuilders.post("/api/user/create").content(objectMapper.writeValueAsString(userRequest))
    			.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
    		.andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.username").exists())
    		.andReturn();

    	MvcResult result = mockMvc
    		.perform(MockMvcRequestBuilders.post("/login").content(objectMapper.writeValueAsString(userRequest)))
    		.andExpect(status().isOk()).andReturn();
    	request.addParameter("Authorization", result.getResponse().getHeader("Authorization"));
    	assertNotNull(request.getParameter("Authorization"));

    	userRequest.setUsername("tikitaka");
    	mockMvc.perform(MockMvcRequestBuilders.post("/login").content(objectMapper.writeValueAsString(userRequest)))
    		.andExpect(status().isUnauthorized()).andReturn();

    	userRequest.setUsername("tikitaka");
    	userRequest.setPassword("testPassword");
    	mockMvc.perform(MockMvcRequestBuilders.post("/login").content(objectMapper.writeValueAsString(userRequest)))
    		.andExpect(status().isUnauthorized()).andReturn();
    }


    @Test
    public void testUnsuccessfulCreateUser() throws Exception {
    	userRequest.setUsername(null);
    	mockMvc.perform(
    		MockMvcRequestBuilders.post("/api/user/create").content(objectMapper.writeValueAsString(userRequest))
    			.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
    		.andExpect(status().isBadRequest()).andReturn();
    }


    @Test
    public void testUnsuccessfulCreateUser2() throws Exception {
    	userRequest.setPassword("make");
    	userRequest.setConfirmPassword("make");
    	mockMvc.perform(
    		MockMvcRequestBuilders.post("/api/user/create").content(objectMapper.writeValueAsString(userRequest))
    			.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
    		.andExpect(status().isBadRequest()).andReturn();
    }


    @Test
    public void testUnsuccessfulCreateUser4() throws Exception {
    	userRequest.setUsername("");
    	mockMvc.perform(
    		MockMvcRequestBuilders.post("/api/user/create").content(objectMapper.writeValueAsString(userRequest))
    			.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
    		.andExpect(status().isBadRequest()).andReturn();
    }
}
