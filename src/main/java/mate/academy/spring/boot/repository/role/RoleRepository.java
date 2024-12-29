package mate.academy.spring.boot.repository.role;

import java.util.Optional;
import mate.academy.spring.boot.model.Role;
import mate.academy.spring.boot.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
