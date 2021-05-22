package jpabook.jpashop.service;

import jpabook.jpashop.domain.Item.Album;
import jpabook.jpashop.domain.Item.Book;
import jpabook.jpashop.domain.Item.Movie;
import jpabook.jpashop.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemService itemService;

    Book book;
    Album album;
    Movie movie;
    @BeforeEach
    void beforeEach() {
        new Book()
    }

    @Test
    void saveItem() {
    }

    @Test
    void updateItem() {
    }

    @Test
    void findItems() {
    }

    @Test
    void findOne() {
    }
}