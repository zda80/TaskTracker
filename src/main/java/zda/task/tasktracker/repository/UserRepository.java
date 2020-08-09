package zda.task.tasktracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zda.task.tasktracker.model.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}