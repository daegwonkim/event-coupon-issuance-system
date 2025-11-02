package io.github.daegwonkim.event_coupon_issuance_system.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "coupons")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "stock", nullable = false)
    private Integer stock;

//    @Version
    private Long version;

    public static Coupon create(Event event, Integer stock) {
        Coupon coupon = new Coupon();
        coupon.event = event;
        coupon.stock = stock;
        return coupon;
    }

    public void decreaseStock() {
        if (stock <= 0) {
            throw new IllegalArgumentException("쿠폰 재고가 모두 소진되었습니다.");
        }

        stock--;
    }
}
