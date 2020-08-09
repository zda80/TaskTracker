package zda.task.tasktracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zda.task.tasktracker.model.workflow.WorkFlow;

public interface WorkflowRepository extends JpaRepository<WorkFlow, Long> {
    WorkFlow findWorkflowById(long id);
}