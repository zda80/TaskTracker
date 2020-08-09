package zda.task.tasktracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zda.task.tasktracker.model.project.Project;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findAll();
    Project findProjectById(long id);
}