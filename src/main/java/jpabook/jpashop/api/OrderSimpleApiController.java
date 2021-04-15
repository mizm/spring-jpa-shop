package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.simplequery.SimpleOrderQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * xToOne
 * order
 * order -> member
 * order -> delivery
 * 추가
 * xToMany
 * order -> orderItems
 * orderItems -> Item
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        // 이것만 반환하면 무한 루프에 빠진다.(객체 객체 관계에서 객체에서 @JsonIgnore 해줘야한다
        // 지연로딩에서 가져오지 않았기 때문에 오류가 발생한다.
        // hibernate5module을 설정해줘야 한다.
        // 성능상의 문제가 생긴다. 지연로딩 관련된 모든 쿼리가 나감.
        List<Order> all = orderRepository.findAll(new OrderSearch());
        return all;
    }

    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() {
        // 데이터베이스 쿼리가 너무 많이나감 N(2개) +1 문제
        // 회원 N + 배송 N + 1(처음 조회)
        // order 조회 sql 1번 결과 로우수 2번
        List<Order> orders = orderRepository.findAll(new OrderSearch());

        // 루프 돌면서 로우에 따른 멤버 쿼리, 딜리버리 쿼리 조회
        // 4번 쿼리 나감
        List<SimpleOrderDto> collect = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return collect;
    }

    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        // query 한번에 조회한다.
        // fetch join 활용
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> collect = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());
        return collect;
    }

    /**
     *  v3는 order를 가져오기 때문에 재사용이 가능하다. / entity를 조회했기 떄문에 비즈니스 로직을 사용할 수 있다.
     *  v4는 재사용이 적고 화면단에 필요한것만 가져온다. / 성능이 조금 더 최적 / jpql 쿼리가 복잡하다.
     *  성능 테스트가 필요하다. select field를 고르는건 크게 차이가 안날 수도 있다.
     *  그렇지만 트래픽이 크고 필드 수가 많거나 데이터가 크면 고민해야한다.
     */
    @GetMapping("/api/v4/simple-orders")
    public List<SimpleOrderQueryDto> ordersV4() {
        // dto로 조회하기
        // repository는 순수한 entity를 가져오는데 쓰기 때문에 화면상 쿼리를 가져와야되는 것을 따로 만들자.
        return orderSimpleQueryRepository.findOrderDto();
    }
    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            this.orderId = order.getId();
            this.name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
        }
    }
}
