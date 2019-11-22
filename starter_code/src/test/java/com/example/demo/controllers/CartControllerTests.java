package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTests {

  private CartController cartController;

  private UserRepository userRepository = mock(UserRepository.class);

  private ItemRepository itemRepository = mock(ItemRepository.class);

  private CartRepository cartRepository = mock(CartRepository.class);

  @Before
  public void setUp() {
    cartController = new CartController();
    TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    TestUtils.injectObjects(cartController, "userRepository", userRepository);
    TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
  }

  @Test
  public void addToCartMissingUser_thenNotFound() {
    String username = "test";
    when(userRepository.findByUsername(username)).thenReturn(null);
    final ResponseEntity<Cart> response = cartController.addTocart(new ModifyCartRequest());
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void addToCartMissingItem_thenNotFound() {
    User user = sampleUser();
    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
    when(itemRepository.findById(10L)).thenReturn(null);
    ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
    modifyCartRequest.setItemId(10L);
    final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void addToCart_thenOk() {
    User user = sampleUser();
    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
    when(itemRepository.findById(3L)).thenReturn(java.util.Optional.ofNullable(sampleItem(3L, "item3", BigDecimal.valueOf(30))));
    ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
    modifyCartRequest.setItemId(3L);
    modifyCartRequest.setQuantity(1);
    modifyCartRequest.setUsername(user.getUsername());
    final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(3, response.getBody().getItems().size());
    assertEquals(BigDecimal.valueOf(80), response.getBody().getTotal());
  }

  @Test
  public void removeFromCartMissingUser_thenNotFound() {
    String username = "test";
    when(userRepository.findByUsername(username)).thenReturn(null);
    final ResponseEntity<Cart> response = cartController.removeFromcart(new ModifyCartRequest());
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void removeFromcartMissingItem_thenNotFound() {
    User user = sampleUser();
    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
    when(itemRepository.findById(10L)).thenReturn(null);
    ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
    modifyCartRequest.setItemId(10L);
    final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void removeFromCart_thenOk() {
    User user = sampleUser();
    when(userRepository.findByUsername(user.getUsername())).thenReturn(user);
    when(itemRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(sampleItem(1L, "item3", BigDecimal.valueOf(30))));
    ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
    modifyCartRequest.setItemId(1L);
    modifyCartRequest.setUsername(user.getUsername());
    modifyCartRequest.setQuantity(1);
    final ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().getItems().size());
    assertEquals(BigDecimal.valueOf(20), response.getBody().getTotal());
  }

  private User sampleUser() {
    String username = "test";
    User user = new User();
    user.setUsername(username);
    user.setPassword("hashed");

    Item item = sampleItem(1L, "item1", BigDecimal.valueOf(30));

    Item item2 = sampleItem(2L, "item2", BigDecimal.valueOf(20));

    Cart cart = new Cart();
    cart.addItem(item);
    cart.addItem(item2);
    user.setCart(cart);

    return user;
  }

  private Item sampleItem(long id, String name, BigDecimal price) {
    Item item = new Item();
    item.setPrice(price);
    item.setId(id);
    item.setName(name);
    return item;
  }
}
