package zda.task.tasktracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zda.task.tasktracker.model.backlog.ProjectBacklog;

public interface BacklogRepository extends JpaRepository<ProjectBacklog, Long> {
}