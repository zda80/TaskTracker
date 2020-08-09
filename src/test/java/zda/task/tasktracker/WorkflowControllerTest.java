package zda.task.tasktracker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import zda.task.tasktracker.controller.WorkflowController;
import zda.task.tasktracker.model.workflow.WorkFlow;
import zda.task.tasktracker.model.workflow.WorkFlowStatus;
import zda.task.tasktracker.service.*;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(value = WorkflowController.class)
public class WorkflowControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    WorkflowService workflowService;

    @MockBean
    UserService userService;

    private WorkFlow workFlow = new WorkFlow();
    private List<WorkFlow> workFlowList = new ArrayList<>();

    @Test
    @WithMockUser
    public void givenWorkflowController_whenGetCreateWorkflow_thenCreateNewWorkflow() throws Exception {
        given(workflowService.createNewWorkflow()).willReturn(workFlow);

        mvc.perform(get("/workflow/createWorkflow"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("workflow", equalTo(workFlow)))
                .andExpect(model().attribute("statuses", equalTo(WorkFlowStatus.values())))
                .andExpect(view().name("workflow/editWorkflow"));
    }

    @Test
    @WithMockUser
    public void givenWorkflowController_whenPostSaveWorkflow_thenUpdateTitle() throws Exception {
        given(workflowService.updateTitle(any())).willReturn(workFlow);

        mvc.perform(post("/workflow/saveWorkflow"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("workflow", equalTo(workFlow)))
                .andExpect(model().attribute("statuses", equalTo(WorkFlowStatus.values())))
                .andExpect(model().attribute("info", containsString(WorkflowController.WORKFLOW_SAVE_MESSAGE)))
                .andExpect(view().name("workflow/editWorkflow"));
    }

    @Test
    @WithMockUser
    public void givenWorkflowController_whenAddStatusAndStatusIsNull_thenStatusNotSelectedMessage() throws Exception {
        given(workflowService.getWorkflowById(anyLong())).willReturn(workFlow);

        mvc.perform(post("/workflow/addStatus")
                .param("statusList", ""))
                .andExpect(status().is(200))
                .andExpect(model().attribute("workflow", equalTo(workFlow)))
                .andExpect(model().attribute("statuses", equalTo(WorkFlowStatus.values())))
                .andExpect(model().attribute("info", containsString(WorkflowController.STATUS_NOT_SELECTED_MESSAGE)))
                .andExpect(view().name("workflow/editWorkflow"));
    }

    @Test
    @WithMockUser
    public void givenWorkflowController_whenAddStatusAndStatusIsNotNull_thenAddStatus() throws Exception {
        given(workflowService.getWorkflowById(anyLong())).willReturn(workFlow);

        mvc.perform(post("/workflow/addStatus")
                .param("statusList", String.valueOf(WorkFlowStatus.OPEN_ISSUE)))
                .andExpect(status().is(200))
                .andExpect(model().attribute("workflow", equalTo(workFlow)))
                .andExpect(model().attribute("statuses", equalTo(WorkFlowStatus.values())))
                .andExpect(model().attribute("info", equalTo(null)))
                .andExpect(view().name("workflow/editWorkflow"));
    }

    @Test
    @WithMockUser
    public void givenWorkflowController_whenRemoveStatusAndStatusIsNull_thenStatusNotSelectedMessage() throws Exception {
        given(workflowService.getWorkflowById(anyLong())).willReturn(workFlow);

        mvc.perform(post("/workflow/removeStatus")
                .param("currentStatus", ""))
                .andExpect(status().is(200))
                .andExpect(model().attribute("workflow", equalTo(workFlow)))
                .andExpect(model().attribute("statuses", equalTo(WorkFlowStatus.values())))
                .andExpect(model().attribute("info", containsString(WorkflowController.STATUS_NOT_SELECTED_MESSAGE)))
                .andExpect(view().name("workflow/editWorkflow"));
    }

    @Test
    @WithMockUser
    public void givenWorkflowController_whenRemoveStatusAndStatusIsNotNull_thenAddStatus() throws Exception {
        given(workflowService.getWorkflowById(anyLong())).willReturn(workFlow);

        mvc.perform(post("/workflow/removeStatus")
                .param("currentStatus", String.valueOf(WorkFlowStatus.OPEN_ISSUE)))
                .andExpect(status().is(200))
                .andExpect(model().attribute("workflow", equalTo(workFlow)))
                .andExpect(model().attribute("statuses", equalTo(WorkFlowStatus.values())))
                .andExpect(model().attribute("info", equalTo(null)))
                .andExpect(view().name("workflow/editWorkflow"));
    }

    @Test
    @WithMockUser
    public void givenWorkflowController_whenGetSelectWorkflow_thenGetWorkflowList() throws Exception {
        given(workflowService.findAllWorkflow()).willReturn(workFlowList);

        mvc.perform(get("/workflow/selectWorkflow"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("workflowList", equalTo(workFlowList)))
                .andExpect(view().name("workflow/selectWorkflow"));
    }

    @Test
    @WithMockUser
    public void givenWorkflowController_whenSelectWorkflowAndWorkflowIsNull_thenWorkflowNotSelectedMessage() throws Exception {
        given(workflowService.findAllWorkflow()).willReturn(workFlowList);

        mvc.perform(post("/workflow/selectWorkflow")
                .param("workflowId", ""))
                .andExpect(status().is(200))
                .andExpect(model().attribute("workflowList", equalTo(workFlowList)))
                .andExpect(model().attribute("info", equalTo(WorkflowController.WORKFLOW_NOT_SELECTED_MESSAGE)))
                .andExpect(view().name("workflow/selectWorkflow"));
    }

    @Test
    @WithMockUser
    public void givenWorkflowController_whenSelectWorkflowAndWorkflowIsNotNull_thenEditWorkflow() throws Exception {
        given(workflowService.getWorkflowById(anyLong())).willReturn(workFlow);

        mvc.perform(post("/workflow/selectWorkflow")
                .param("workflowId", "1"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("workflow", equalTo(workFlow)))
                .andExpect(model().attribute("statuses", equalTo(WorkFlowStatus.values())))
                .andExpect(view().name("workflow/editWorkflow"));
    }

    @Test
    @WithMockUser
    public void givenWorkflowController_whenGetShowWorkflow_thenGetWorkflowList() throws Exception {
        given(workflowService.findAllWorkflow()).willReturn(workFlowList);

        mvc.perform(get("/workflow/showWorkflow"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("workflowList", equalTo(workFlowList)))
                .andExpect(view().name("workflow/showWorkflow"));
    }

    @Test
    @WithMockUser
    public void givenWorkflowController_whenDeleteWorkflowAndWorkflowIsNull_thenWorkflowNotSelectedMessage() throws Exception {
        given(workflowService.findAllWorkflow()).willReturn(workFlowList);

        mvc.perform(post("/workflow/deleteWorkflow")
                .param("workflowId", ""))
                .andExpect(status().is(200))
                .andExpect(model().attribute("workflowList", equalTo(workFlowList)))
                .andExpect(model().attribute("info", equalTo(WorkflowController.WORKFLOW_NOT_SELECTED_MESSAGE)))
                .andExpect(view().name("workflow/deleteWorkflow"));
    }

    @Test
    @WithMockUser
    public void givenWorkflowController_whenDeleteWorkflowAndWorkflowIsNotNull_thenDeleteWorkflow() throws Exception {
        given(workflowService.findAllWorkflow()).willReturn(workFlowList);
        given(workflowService.deleteWorkflow(anyLong())).willReturn(true);
        mvc.perform(post("/workflow/deleteWorkflow")
                .param("workflowId", "1"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("workflowList", equalTo(workFlowList)))
                .andExpect(model().attribute("info", containsString(WorkflowController.WORKFLOW_DELETE_MESSAGE)))
                .andExpect(view().name("workflow/deleteWorkflow"));
    }
}