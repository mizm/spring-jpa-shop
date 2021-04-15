package jpabook.jpashop.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.Item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

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
        QOrder order = QOrder.order;
        QMember member = QMember.member;

        JPAQueryFactory query = new JPAQueryFactory(em);


        return query.select(order)
                .from(order)
                .join(order.member, member)
                .where(statusEq(orderSearch.getOrderStatus()),
                        nameLike(orderSearch))
                .limit(1000)
                .fetch();
    }

    private BooleanExpression nameLike(OrderSearch orderSearch) {
        if (!StringUtils.hasText(orderSearch.getMemberName())) {
            return null;
        }
        return QMember.member.name.like(orderSearch.getMemberName());
    }

    private BooleanExpression statusEq(OrderStatus statusCond) {
        if(statusCond == null) {
            return null;
        }
        return QOrder.order.status.eq(statusCond);
    }

    public List<Order> findAllWithMemberDelivery() {
        // fetch join이 중요하다.
        return em.createQuery("select o from Order o" +
                " join fetch o.member m" +
                " join fetch o.delivery d",Order.class)
                .getResultList();
    }

    public List<Order> findAllWithItem(OrderSearch orderSearch) {
        //onetomany 조인을 하면 many의 갯수만큼 row결과가 나온다
        //distinct를 db쿼리에는 완벽히 동일한 것만 줄이기 때문에 지금은 상관없다.
        // jpa에서 distinct는 from Order 의 기준으로 동일한것을 줄여준다.
        // 단점 페이징 불가.
        // collection fetch 조인에서는 페이징 쿼리를 memory에서 페이징 처리를 한다.
        // 잘못하면 out of memory가 바로 터진다.
        // 단점 one to many 페치조인은 한개만 사용할 수 있다.
        // 왜냐하면 데이터가 row가 엄청 늘어나고 부정확하게 조회될 수 있다.
        return em.createQuery(
                "select distinct o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d " +
                        " join fetch o.orderItems oi " +
                        " join fetch oi.item i", Order.class
        ).getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                "select distinct o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d " +
                        " join fetch o.orderItems oi " +
                        " join fetch oi.item i", Order.class
        ).setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }
}
