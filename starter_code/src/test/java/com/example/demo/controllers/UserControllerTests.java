package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTests {

  private UserController userController;

  private UserRepository userRepository = mock(UserRepository.class);

  private CartRepository cartRepository = mock(CartRepository.class);

  private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

  @Before
  public void setUp() {
    userController = new UserController();
    TestUtils.injectObjects(userController, "userRepository", userRepository);
    TestUtils.injectObjects(userController, "cartRepository", cartRepository);
    TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
  }

  @Test
  public void whenValidInput_thenCreateUser() throws Exception {
    when(encoder.encode("12341234")).thenReturn("thisIsHashed");
    userRepository.deleteAll();

    Random random = new Random();
    String username = "admin" + random.nextInt();
    CreateUserRequest createUserRequest = new CreateUserRequest();
    createUserRequest.setUsername(username);
    createUserRequest.setPassword("12341234");
    createUserRequest.setConfirmPassword("12341234");
    final ResponseEntity<User> response = userController.createUser(createUserRequest);
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    User user = response.getBody();

    assertEquals(0, user.getId());
    assertEquals(username, user.getUsername());
    assertEquals("thisIsHashed", user.getPassword());
  }

  @Test
  public void whenUsernameUsed_thenBadRequest() throws Exception {
    userRepository.deleteAll();

    Random random = new Random();
    String username = "admin" + random.nextInt();
    CreateUserRequest createUserRequest = new CreateUserRequest();
    createUserRequest.setUsername(username);
    createUserRequest.setPassword("12341234");
    createUserRequest.setConfirmPassword("4321");
    User user = new User();
    user.setUsername(username);
    when(userRepository.findByUsername(username)).thenReturn(user);
    final ResponseEntity<User> response = userController.createUser(createUserRequest);
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  public void whenPasswordNotConfirmed_thenBadRequest() throws Exception {
    userRepository.deleteAll();

    Random random = new Random();
    String username = "admin" + random.nextInt();
    CreateUserRequest createUserRequest = new CreateUserRequest();
    createUserRequest.setUsername(username);
    createUserRequest.setPassword("12341234");
    createUserRequest.setConfirmPassword("4321");
    final ResponseEntity<User> response = userController.createUser(createUserRequest);
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  public void whenPasswordTooShort_thenBadRequest() throws Exception {
    userRepository.deleteAll();

    Random random = new Random();
    String username = "admin" + random.nextInt();
    CreateUserRequest createUserRequest = new CreateUserRequest();
    createUserRequest.setUsername(username);
    createUserRequest.setPassword("4321");
    createUserRequest.setConfirmPassword("4321");
    final ResponseEntity<User> response = userController.createUser(createUserRequest);
    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  public void whenfindByNonExistUserName_thenNotFound() throws Exception {
    String username = "test";
    when(userRepository.findByUsername(username)).thenReturn(null);

    final ResponseEntity<User> response = userController.findByUserName(username);
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void whenfindByExistUserName_thenOk() throws Exception {
    String username = "test";
    User user = new User();
    user.setUsername(username);
    user.setPassword("hashed");
    when(userRepository.findByUsername(username)).thenReturn(user);

    final ResponseEntity<User> response = userController.findByUserName(username);
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(username, response.getBody().getUsername());
  }

  @Test
  public void whenfindByExistId_thenOk() throws Exception {
    String username = "test";
    User user = new User();
    user.setId(1L);
    user.setUsername(username);
    user.setPassword("hashed");
    when(userRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(user));

    final ResponseEntity<User> response = userController.findById(1L);
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1L, response.getBody().getId());
  }
}
