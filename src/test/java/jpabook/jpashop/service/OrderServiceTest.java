package jpabook.jpashop.service;

import jpabook.jpashop.repository.OrderRepository;
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
    @Autowired
    OrderRepository orderRepository;
    /**
     *
     테스트 요구사항
     상품 주문이 성공해야 한다.
     상품을 주문할 때 재고 수량을 초과하면 안 된다.
     주문 취소가 성공해야 한다.
     */

}