package zda.task.tasktracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zda.task.tasktracker.model.backlog.ProjectSprint;

public interface SprintRepository extends JpaRepository<ProjectSprint, Long> {
}