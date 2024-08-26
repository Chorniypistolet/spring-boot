package mate.academy.spring.boot.repository.role;

import mate.academy.spring.boot.model.Role;
import mate.academy.spring.boot.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
