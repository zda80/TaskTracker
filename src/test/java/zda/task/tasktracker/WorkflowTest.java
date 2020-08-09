package zda.task.tasktracker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Example;
import org.springframework.test.context.junit4.SpringRunner;
import zda.task.tasktracker.model.workflow.WorkFlow;
import zda.task.tasktracker.model.workflow.WorkFlowStatus;
import zda.task.tasktracker.repository.WorkflowRepository;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class WorkflowTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private WorkflowRepository workflowRepository;

    @Test
    public void givenWorkflows_whenFindWorkflow_thenReturnWorkflow() {
        WorkFlow workFlow = new WorkFlow();
        workFlow.add(WorkFlowStatus.OPEN_ISSUE);
        workFlow.add(WorkFlowStatus.CLOSE_ISSUE);
        entityManager.persist(workFlow);

        WorkFlow found = workflowRepository.findWorkflowById(workFlow.getId());

        assertEquals(found, workFlow);
    }
}