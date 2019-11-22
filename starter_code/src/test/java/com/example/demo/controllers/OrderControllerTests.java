package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTests {

  private OrderController orderController;

  private UserRepository userRepository = mock(UserRepository.class);

  private OrderRepository orderRepository = mock(OrderRepository.class);

  @Before
  public void setUp() {
    orderController = new OrderController();
    TestUtils.injectObjects(orderController, "userRepository", userRepository);
    TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
  }

  @Test
  public void submit_thenOk() {
    User user = sampleUser();
    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
    final ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(2, response.getBody().getItems().size());
    assertEquals(BigDecimal.valueOf(50), response.getBody().getTotal());
  }

  @Test
  public void submitMissingUser_thenNotFound() {
    User user = sampleUser();
    when(userRepository.findByUsername(user.getUsername())).thenReturn(null);
    final ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void findOrder_thenOk() {
    User user = sampleUser();
    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
    when(orderRepository.findByUser(user)).thenReturn(Collections.singletonList(UserOrder.createFromCart(user.getCart())));
    final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(2, response.getBody().get(0).getItems().size());
    assertEquals(BigDecimal.valueOf(50), response.getBody().get(0).getTotal());
  }

  @Test
  public void findOrderMissingUser_thenNotFound() {
    User user = sampleUser();
    when(userRepository.findByUsername(user.getUsername())).thenReturn(null);
    final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  private User sampleUser() {
    String username = "test";
    User user = new User();
    user.setUsername(username);
    user.setPassword("hashed");

    Item item = new Item();
    item.setId(1L);
    item.setName("test item");
    item.setPrice(BigDecimal.valueOf(20));

    Item item2 = new Item();
    item2.setId(2L);
    item2.setName("test item2");
    item2.setPrice(BigDecimal.valueOf(30));

    Cart cart = new Cart();
    cart.addItem(item);
    cart.addItem(item2);
    user.setCart(cart);

    return user;
  }

}
