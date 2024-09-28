package mate.academy.spring.boot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;


@Entity
@Getter
@Setter
@ToString(exclude = "orderItems")
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    @Column(nullable = false)
    private BigDecimal total;
    @Column(nullable = false)
    private LocalDateTime orderTime;
    @Column(nullable = false)
    private String shippingAddress;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<OrderItem> orderItems;
}
