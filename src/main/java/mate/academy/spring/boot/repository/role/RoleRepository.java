package mate.academy.spring.boot.repository.role;

import mate.academy.spring.boot.model.Role;
import mate.academy.spring.boot.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
}
