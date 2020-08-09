package zda.task.tasktracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zda.task.tasktracker.model.user.Role;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
    List<Role> findAll();
}