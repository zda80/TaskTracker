package zda.task.tasktracker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import zda.task.tasktracker.model.workflow.WorkFlow;
import zda.task.tasktracker.model.workflow.WorkFlowStatus;
import zda.task.tasktracker.repository.WorkflowRepository;
import zda.task.tasktracker.service.WorkflowService;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class WorkflowServiceTest {

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private WorkflowRepository workflowRepository;

    @Before
    public void setUp(){
        workflowRepository.deleteAll();
    }

    @Test
    public void givenWorkflow_whenCreateNewWorkflow_thenCreateNewWorkflowInDAO() {
        WorkFlow workFlow = workflowService.createNewWorkflow();
        WorkFlow foundWorkflow = workflowService.getWorkflowById(workFlow.getId());

        assertThat(workFlow.getId(), equalTo(foundWorkflow.getId()));
    }

    @Test
    public void givenWorkflow_whenUpdateTitle_thenUpdateTitleInDAO() {
        WorkFlow workFlow = workflowService.createNewWorkflow();
        WorkFlow workFlowDTO = new WorkFlow();
        workFlowDTO.setId(workFlow.getId());
        workFlowDTO.setTitle("new title");
        workflowService.updateTitle(workFlowDTO);
        WorkFlow workFlowFromDAO = workflowService.getWorkflowById(workFlow.getId());

        assertThat(workFlowFromDAO.getTitle(), equalTo(workFlowDTO.getTitle()));
    }

    @Test
    public void givenWorkflow_whenAddNewStatus_thenAddNewStatusInDAO() {
        WorkFlow workFlow = workflowService.createNewWorkflow();
        workflowService.addStatusToWorkflow(workFlow.getId(), WorkFlowStatus.OPEN_ISSUE);
        workflowService.addStatusToWorkflow(workFlow.getId(), WorkFlowStatus.CLOSE_ISSUE);
        WorkFlow workFlowFromDAO = workflowService.getWorkflowById(workFlow.getId());

        assertThat(workFlowFromDAO.getWorkFlowList(), containsInAnyOrder(WorkFlowStatus.OPEN_ISSUE, WorkFlowStatus.CLOSE_ISSUE));
    }

    @Test
    public void givenWorkflow_whenRemoveStatus_thenRemoveStatusInDAO() {
        WorkFlow workFlow = workflowService.createNewWorkflow();
        workflowService.addStatusToWorkflow(workFlow.getId(), WorkFlowStatus.OPEN_ISSUE);
        workflowService.addStatusToWorkflow(workFlow.getId(), WorkFlowStatus.CLOSE_ISSUE);
        workflowService.addStatusToWorkflow(workFlow.getId(), WorkFlowStatus.INPROGRESS_ISSUE);
        workflowService.removeStatusFromWorkflow(workFlow.getId(), WorkFlowStatus.CLOSE_ISSUE);
        WorkFlow workFlowFromDAO = workflowService.getWorkflowById(workFlow.getId());

        assertThat(workFlowFromDAO.getWorkFlowList(), containsInAnyOrder(WorkFlowStatus.OPEN_ISSUE, WorkFlowStatus.INPROGRESS_ISSUE));
    }

    @Test
    public void givenWorkflow_whenGetWorkflowById_thenGetWorkflowFromDAO() {
        WorkFlow workFlow = workflowService.createNewWorkflow();
        WorkFlow workFlowFromDAO = workflowService.getWorkflowById(workFlow.getId());

        assertThat(workFlowFromDAO.getId(), equalTo(workFlow.getId()));
    }

    @Test
    public void givenWorkflow_whenGetAllWorkflow_thenGetAllWorkflowFromDAO() {
        WorkFlow workFlow1 = workflowService.createNewWorkflow();
        WorkFlow workFlow2 = workflowService.createNewWorkflow();
        List<WorkFlow> workFlowList = workflowService.findAllWorkflow();

        assertThat(workFlowList, hasSize(2));
        assertThat(workFlowList.stream().map(WorkFlow::getId).collect(Collectors.toList()), containsInAnyOrder(workFlow1.getId(),workFlow2.getId()));
    }

    @Test
    public void givenWorkflow_whenDeleteWorkflow_thenDeleteWorkflowFromDAO() {
        WorkFlow workFlow = workflowService.createNewWorkflow();
        boolean deleteWorkflow = workflowService.deleteWorkflow(workFlow.getId());

        assertTrue(deleteWorkflow);
        assertTrue(workflowService.getWorkflowById(workFlow.getId())==null);
    }
}