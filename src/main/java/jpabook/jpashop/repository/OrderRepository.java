package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Item.Item;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    // 동적쿼리
    // 스트링 조합
    // jpa criteria -> jpql 을 코드로 짜는 것 sql이 보여지지 않아서 유지보수가 너 무 힘 듬
    // querydsl 을 공부하자
    public List<Order> findAll(OrderSearch orderSearch) {
        return em.createQuery("select o from Order o join o.member m", Order.class)
                .setMaxResults(1000) // 최대 1000건
                .getResultList();
    }

    public List<Order> findAllWithMemberDelivery() {
        // fetch join이 중요하다.
        return em.createQuery("select o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d",Order.class)
                .getResultList();
    }
}
