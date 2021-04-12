package jpabook.jpashop.service;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Item.Book;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
class OrderServiceTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    /**
     *
     테스트 요구사항
     상품 주문이 성공해야 한다.
     상품을 주문할 때 재고 수량을 초과하면 안 된다.
     주문 취소가 성공해야 한다.
     */

    @Test
    void 상품주문() {
        //given
        Member member = getMember();
        Book book = getBook("jpa", 10000, 10);
        int orderCount = 2;
        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.ORDER, getOrder.getStatus());
        assertEquals(1,getOrder.getOrderItems().size());
        assertEquals(10000*orderCount,getOrder.getTotalPrice());
        assertEquals(8,book.getStockQuantity());
    }

    @Test
    void 주문취소() {
        //given
        Member member = getMember();
        Book book = getBook("jpa", 10000, 10);
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.CANCLE, getOrder.getStatus(),"주문 취소상태로 변경되어야한다.");
        assertEquals(10,book.getStockQuantity(),"재고는 원복되어야한다.");

    }

    @Test
    void 상품주문_재고수량초과() {
        // given
        Member member = getMember();
        Book book = getBook("jpa", 10000, 10);

        int orderCount = 11;
        //when

        assertThrows(NotEnoughStockException.class
                , ()-> orderService.order(member.getId(),book.getId(),orderCount), "주문수량 테스트가 필요하다.");
    }

    private Book getBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member getMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울","기기","123"));
        em.persist(member);
        return member;
    }
}