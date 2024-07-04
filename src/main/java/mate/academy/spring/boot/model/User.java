package mate.academy.spring.boot.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Data
@SQLDelete(sql = "UPDATE users SET is_deleted = true WHERE id=?")
@SQLRestriction("is_deleted = false")
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    private String shippingAddress;
    @Column(nullable = false)
    boolean isDeleted = false;
}
