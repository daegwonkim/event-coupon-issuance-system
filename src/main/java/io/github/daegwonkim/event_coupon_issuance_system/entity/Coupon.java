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

    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;

    @Column(name = "issued_quantity", nullable = false)
    private Integer issuedQuantity;

    public static Coupon create(Event event, Integer totalQuantity, Integer issuedQuantity) {
        Coupon coupon = new Coupon();
        coupon.event = event;
        coupon.totalQuantity = totalQuantity;
        coupon.issuedQuantity = issuedQuantity;
        return coupon;
    }

    public void decreaseStock() {
        if (totalQuantity <= issuedQuantity) {
            throw new IllegalArgumentException("쿠폰 재고가 모두 소진되었습니다.");
        }

        issuedQuantity++;
    }
}
