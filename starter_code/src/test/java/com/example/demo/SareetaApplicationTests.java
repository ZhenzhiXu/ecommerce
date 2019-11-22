package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.controllers.ItemController;
import com.example.demo.controllers.OrderController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, classes = SareetaApplication.class)
@AutoConfigureMockMvc
public class SareetaApplicationTests extends TestCase {

	@Autowired
	private CartController cartController;

	@Autowired
	private ItemController itemController;

	@Autowired
	private OrderController orderController;

	@Autowired
	private UserController userController;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Before
	public void setup() throws Exception {
	}

	@Test
	public void contextLoads() {
		Assert.assertNotNull(cartController);
		Assert.assertNotNull(itemController);
		Assert.assertNotNull(orderController);
		Assert.assertNotNull(userController);
	}

	@Test
	public void whenValidInput_thenCreateUser() throws Exception {

		String username = feedSampleUser();
		List<User> found = userRepository.findAll();
		assertThat(found).extracting(User::getUsername).containsOnly(username);
	}

	@Test
	@WithMockUser
	public void getUserByName_thenStatus200() throws Exception {

		String username = feedSampleUser();
		mvc.perform(get("/api/user/" + username)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content()
						.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.username", is(username)));
	}

	@Test
	public void getUserByName_thenStatus403() throws Exception {

		String username = feedSampleUser();
		mvc.perform(get("/api/user/" + username)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is(HttpStatus.FORBIDDEN.value()));
	}

	private static byte[] toJson(Object object) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return mapper.writeValueAsBytes(object);
	}

	private String feedSampleUser() throws Exception {
		userRepository.deleteAll();

		Random random = new Random();
		String username = "admin" + random.nextInt();
		CreateUserRequest createUserRequest = new CreateUserRequest();
		createUserRequest.setUsername(username);
		createUserRequest.setPassword("12341234");
		createUserRequest.setConfirmPassword("12341234");
		mvc.perform(post("/api/user/create").contentType(MediaType.APPLICATION_JSON).content(toJson(createUserRequest)));

		return username;
	}
}
