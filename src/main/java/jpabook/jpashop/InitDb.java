package jpabook.jpashop;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.Item.Book;
import jpabook.jpashop.domain.Item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;


/**
 * 총 주문 2개
 * userA jpa book1 jpa book 2
 * userb spring 1 spring 2
 */
@Component
@RequiredArgsConstructor
public class InitDb {
    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
        initService.dbInit2();
    }

    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;

        public void dbInit1() {
            Member member = createMember("userA", "서울", "1", "11111");
            em.persist(member);

            Item book1 = createBook("jpa1", 10000, 100);
            em.persist(book1);
            Item book2 = createBook("jpa2", 20000, 100);
            em.persist(book2);
            OrderItem orderItem1 = OrderItem.createOrderItem(book1,10000,1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2,20000,2);

            Delivery delivery = createDelivery(member);

            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        private Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }


        public void dbInit2() {
            Member member = createMember("userB", "대구", "2", "2222");
            em.persist(member);

            Item book1 = createBook("spring1", 15000, 200);
            em.persist(book1);
            Item book2 = createBook("spring2", 30000, 400);
            em.persist(book2);
            OrderItem orderItem1 = OrderItem.createOrderItem(book1,15000,3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2,30000,4);

            Delivery delivery = createDelivery(member);

            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }
        private Item createBook(String name, int price, int stockQuantity) {
            Book book1 = new Book();
            book1.setName(name);
            book1.setPrice(price);
            book1.setStockQuantity(stockQuantity);
            return book1;
        }
        private Member createMember(String name, String city, String street, String zipcode) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address(city, street, zipcode));
            return member;
        }
    }
}


