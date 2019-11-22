package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTests {

  private ItemController itemController;

  private ItemRepository itemRepository = mock(ItemRepository.class);;

  @Before
  public void setUp() {
    itemController = new ItemController();
    TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
  }

  @Test
  public void getItemById_thenOk() throws Exception {
    Item item = new Item();
    item.setId(1L);
    item.setName("test item");
    when(itemRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(item));
    final ResponseEntity<Item> response = itemController.getItemById(1L);
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1L, response.getBody().getId().longValue());
  }

  @Test
  public void getItemByName_thenOk() throws Exception {
    String itemName = "test item";
    Item item = new Item();
    item.setId(1L);
    item.setName(itemName);
    when(itemRepository.findByName(itemName)).thenReturn(Collections.singletonList(item));
    final ResponseEntity<List<Item>> response = itemController.getItemsByName(itemName);
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody().size());
    assertTrue(response.getBody().contains(item));
  }

  @Test
  public void getItemByNameNonExist_thenNotFound() throws Exception {
    String itemName = "test item";
    when(itemRepository.findByName(itemName)).thenReturn(null);
    final ResponseEntity<List<Item>> response = itemController.getItemsByName(itemName);
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void getItems_thenOK() throws Exception {
    Item item = new Item();
    item.setId(1L);
    item.setName("test item");
    Item item2 = new Item();
    item.setId(1L);
    item.setName("test item2");
    when(itemRepository.findAll()).thenReturn(Arrays.asList(item, item2));
    final ResponseEntity<List<Item>> response = itemController.getItems();
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(2, response.getBody().size());
  }
}

