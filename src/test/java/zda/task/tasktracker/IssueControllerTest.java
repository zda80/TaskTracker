package zda.task.tasktracker;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import zda.task.tasktracker.controller.IssueController;
import zda.task.tasktracker.model.backlog.ProjectBacklog;
import zda.task.tasktracker.model.backlog.ProjectSprint;
import zda.task.tasktracker.model.issue.Issue;
import zda.task.tasktracker.model.issue.IssuePriority;
import zda.task.tasktracker.model.issue.IssueType;
import zda.task.tasktracker.model.project.FilterProject;
import zda.task.tasktracker.model.project.Project;
import zda.task.tasktracker.model.project.RequestDTO;
import zda.task.tasktracker.model.user.User;
import zda.task.tasktracker.model.workflow.WorkFlow;
import zda.task.tasktracker.service.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(value = IssueController.class)
public class IssueControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    UserService userService;

    @MockBean
    DecorateService decorateService;

    @MockBean
    ProjectService projectService;

    @MockBean
    IssueService issueService;

    @MockBean
    WorkflowService workflowService;

    @Mock
    private static FilterProject filterProject;

    private static Issue issue1, issue2;
    private static List<Issue> issueList = new ArrayList<>();
    private static List<WorkFlow> workFlowList = new ArrayList<>();
    private static List<User> userList = new ArrayList<>();
    private static Project project;
    private static RequestDTO requestDTO;

    @BeforeClass
    public static void init() {
        issue1 = new Issue(IssueType.EPIC, "Main task", null, null, null, IssuePriority.HI, LocalDate.of(2010, 11, 1), null, null, null, null, null);
        issue1.setId(1l);
        issue2 = new Issue(IssueType.TASK, "First task", null, null, null, IssuePriority.LOW, LocalDate.of(2011, 11, 1), null, null, null, null, null);
        issueList.add(issue2);

        workFlowList.add(new WorkFlow());

        User user1 = new User("TestUser1", "password1", "password1", "Ivan", "Ivanov", null);
        User user2 = new User("TestUser2", "password2", "password2", "Max", "Petrov", null);
        userList.add(user1);
        userList.add(user2);

        project = new Project("Project №1", "Description of the Project", "Department №1", user1, user2);
        project.setId(1l);
        ProjectBacklog projectBacklog = new ProjectBacklog();
        projectBacklog.setId(1l);
        ProjectBacklog projectBacklog2 = new ProjectBacklog();
        project.setProjectBacklog(projectBacklog);
        ProjectSprint projectSprint = new ProjectSprint();
        project.setCurrentSprint(projectSprint);

        filterProject = new FilterProject();
        filterProject.setProjectBacklog(projectBacklog2);
        filterProject.setWrapped(project);
        filterProject.setRequestDTO(new RequestDTO());

        requestDTO = new RequestDTO();
    }

    @Test
    @WithMockUser
    public void givenIssueController_whenGetEditIssue_thenAddAttributes() throws Exception {
        given(issueService.findOtherIssueInProject(anyLong())).willReturn(issueList);
        given(workflowService.findAllWorkflow()).willReturn(workFlowList);
        given(userService.findAllUsers()).willReturn(userList);
        given(issueService.getIssueById(anyLong())).willReturn(issue1);

        mvc.perform(get("/issues/editIssue/1"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("issue", equalTo(issue1)))
                .andExpect(model().attribute("issueList", equalTo(issueList)))
                .andExpect(model().attribute("issueTypes", equalTo(IssueType.values())))
                .andExpect(model().attribute("priorityList", equalTo(IssuePriority.values())))
                .andExpect(model().attribute("workflowList", equalTo(workFlowList)))
                .andExpect(model().attribute("userList", equalTo(userList)))
                .andExpect(view().name("issues/editIssue"));
    }

    @Test
    @WithMockUser
    public void givenIssueController_whenGetEditProject_thenAddAttributes() throws Exception {
        given(filterProject.getProjectBacklog()).willReturn(project.getProjectBacklog());
        given(userService.findAllUsers()).willReturn(userList);
        given(decorateService.findProjectByIdAndSetFilter(anyLong())).willReturn(filterProject);
        given(decorateService.getFilterRequestDTO()).willReturn(requestDTO);

        mvc.perform(get("/issues/editProject/1/0/not"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(model().attribute("info", equalTo(IssueController.ISSUE_NOT_SELECTED_MESSAGE)))
                .andExpect(model().attribute("requestDTO", equalTo(requestDTO)))
                .andExpect(model().attribute("userList", equalTo(userList)))
                .andExpect(model().attribute("currentProject", equalTo(filterProject)))
                .andExpect(view().name("projects/editProject"));
    }

    @Test
    @WithMockUser
    public void givenIssueController_whenPostNewIssue_thenEditNewIssue() throws Exception {
        given(issueService.createNewIssue(any())).willReturn(issue1);
        given(issueService.findOtherIssueInProject(anyLong())).willReturn(issueList);
        given(workflowService.findAllWorkflow()).willReturn(workFlowList);
        given(userService.findAllUsers()).willReturn(userList);

        mvc.perform(post("/issues/newIssue"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:editIssue/1"));
    }

    @Test
    @WithMockUser
    public void givenIssueController_whenPostNewChildIssueAndParentIsNull_thenReturnProjectPage() throws Exception {
        given(filterProject.getProjectBacklog()).willReturn(project.getProjectBacklog());
        given(userService.findAllUsers()).willReturn(userList);
        given(decorateService.findProjectByIdAndSetFilter(anyLong())).willReturn(filterProject);
        given(decorateService.getFilterRequestDTO()).willReturn(requestDTO);

        mvc.perform(post("/issues/newChildIssue")
                .param("backlogIssue", "")
                .param("currentSprintIssue", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:editProject/0/0/not"));
    }

    @Test
    @WithMockUser
    public void givenIssueController_whenPostNewChildIssueAndParentIsNotNull_thenEditChildIssue() throws Exception {
        given(issueService.createNewIssue(any())).willReturn(issue1);
        given(issueService.findOtherIssueInProject(issue1.getId())).willReturn(issueList);
        given(workflowService.findAllWorkflow()).willReturn(workFlowList);
        given(userService.findAllUsers()).willReturn(userList);

        mvc.perform(post("/issues/newChildIssue")
                .param("backlogIssue", "1")
                .param("currentSprintIssue", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:editIssue/1"));
    }

    @Test
    @WithMockUser
    public void givenIssueController_whenPostDeleteIssueAndIssueIsNull_thenReturnProjectPage() throws Exception {
        given(filterProject.getProjectBacklog()).willReturn(project.getProjectBacklog());
        given(userService.findAllUsers()).willReturn(userList);
        given(decorateService.findProjectByIdAndSetFilter(anyLong())).willReturn(filterProject);
        given(decorateService.getFilterRequestDTO()).willReturn(requestDTO);
        given(issueService.deleteIssue(anyLong())).willReturn(false);

        mvc.perform(post("/issues/deleteIssue")
                .param("backlogIssue", "")
                .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:editProject/1/0/not"));
    }

    @Test
    @WithMockUser
    public void givenIssueController_whenPostDeleteIssueAndIssueIsNotNull_thenDeleteIssueAndReturnProjectPage() throws Exception {
        given(filterProject.getProjectBacklog()).willReturn(project.getProjectBacklog());
        given(userService.findAllUsers()).willReturn(userList);
        given(decorateService.findProjectByIdAndSetFilter(anyLong())).willReturn(filterProject);
        given(decorateService.getFilterRequestDTO()).willReturn(requestDTO);
        given(issueService.deleteIssue(anyLong())).willReturn(true);

        mvc.perform(post("/issues/deleteIssue")
                .param("backlogIssue", "1")
                .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:editProject/1/1/delete"));
    }

    @Test
    @WithMockUser
    public void givenIssueController_whenPostEditIssueAndIssueIsNull_thenReturnProjectPage() throws Exception {
        given(filterProject.getProjectBacklog()).willReturn(project.getProjectBacklog());
        given(userService.findAllUsers()).willReturn(userList);
        given(decorateService.findProjectByIdAndSetFilter(anyLong())).willReturn(filterProject);
        given(decorateService.getFilterRequestDTO()).willReturn(requestDTO);

        mvc.perform(post("/issues/editIssue")
                .param("backlogIssue", "")
                .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:editProject/1/0/not"));
    }

    @Test
    @WithMockUser
    public void givenIssueController_whenPostEditIssueAndIssueIsNotNull_thenEditIssue() throws Exception {
        given(issueService.getIssueById(anyLong())).willReturn(issue1);
        given(issueService.findOtherIssueInProject(anyLong())).willReturn(issueList);
        given(workflowService.findAllWorkflow()).willReturn(workFlowList);
        given(userService.findAllUsers()).willReturn(userList);

        mvc.perform(post("/issues/editIssue")
                .param("backlogIssue", "1")
                .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:editIssue/1"));
    }

    @Test
    @WithMockUser
    public void givenIssueController_whenPostSaveEditIssue_thenSaveIssueInDAO() throws Exception {
        given(filterProject.getProjectBacklog()).willReturn(project.getProjectBacklog());
        given(userService.findAllUsers()).willReturn(userList);
        given(decorateService.findProjectByIdAndSetFilter(anyLong())).willReturn(filterProject);
        given(decorateService.getFilterRequestDTO()).willReturn(requestDTO);
        given(projectService.findProjectByIssueId(anyLong())).willReturn(project);
        given(issueService.updateIssue(any())).willReturn(true);

        mvc.perform(post("/issues/saveEditIssue")
                .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:editProject/1/1/update"));
    }

    @Test
    @WithMockUser
    public void givenIssueController_whenPostMoveToSprint_thenMoveToSprint() throws Exception {
        given(filterProject.getProjectBacklog()).willReturn(project.getProjectBacklog());
        given(decorateService.findProjectByIdAndSetFilter(anyLong())).willReturn(filterProject);
        given(decorateService.getFilterRequestDTO()).willReturn(requestDTO);
        given(projectService.findProjectById(anyLong())).willReturn(project);

        mvc.perform(post("/issues/moveToSprint")
                .param("backlogIssue", "1")
                .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:editProject/1/1/sprint"));
    }

    @Test
    @WithMockUser
    public void givenIssueController_whenPostMoveToBacklog_thenMoveToBacklog() throws Exception {
        given(filterProject.getProjectBacklog()).willReturn(project.getProjectBacklog());
        given(decorateService.findProjectByIdAndSetFilter(anyLong())).willReturn(filterProject);
        given(decorateService.getFilterRequestDTO()).willReturn(requestDTO);
        given(projectService.findProjectById(anyLong())).willReturn(project);

        mvc.perform(post("/issues/moveToBacklog")
                .param("currentSprintIssue", "1")
                .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:editProject/1/1/backlog"));
    }

    @Test
    @WithMockUser
    public void givenIssueController_whenPostEditIssueInSprintAndIssueIsNull_thenReturnProjectPage() throws Exception {
        given(filterProject.getProjectBacklog()).willReturn(project.getProjectBacklog());
        given(decorateService.findProjectByIdAndSetFilter(anyLong())).willReturn(filterProject);

        mvc.perform(post("/issues/editIssueInSprint")
                .param("currentSprintIssue", "")
                .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:editProject/1/0/not"));
    }

    @Test
    @WithMockUser
    public void givenIssueController_whenPostEditIssueInSprintAndIssueIsNotNull_thenEditIssueInSprint() throws Exception {
        given(issueService.getIssueById(anyLong())).willReturn(issue1);

        mvc.perform(post("/issues/editIssueInSprint")
                .param("currentSprintIssue", "1")
                .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:editIssue/1"));
    }

    @Test
    @WithMockUser
    public void givenIssueController_whenPostReloadIssue_thenReloadIssueAttributes() throws Exception {
        given(issueService.findOtherIssueInProject(anyLong())).willReturn(issueList);
        given(workflowService.findAllWorkflow()).willReturn(workFlowList);
        given(userService.findAllUsers()).willReturn(userList);

        mvc.perform(post("/issues/reload"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("issueList", equalTo(issueList)))
                .andExpect(model().attribute("issueTypes", equalTo(IssueType.values())))
                .andExpect(model().attribute("priorityList", equalTo(IssuePriority.values())))
                .andExpect(model().attribute("workflowList", equalTo(workFlowList)))
                .andExpect(model().attribute("userList", equalTo(userList)))
                .andExpect(view().name("issues/editIssue"));
    }
}