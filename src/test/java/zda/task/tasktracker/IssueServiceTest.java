package zda.task.tasktracker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import zda.task.tasktracker.model.issue.Issue;
import zda.task.tasktracker.model.issue.IssuePriority;
import zda.task.tasktracker.model.issue.IssueType;
import zda.task.tasktracker.model.project.Project;
import zda.task.tasktracker.model.user.User;
import zda.task.tasktracker.model.workflow.WorkFlow;
import zda.task.tasktracker.service.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class IssueServiceTest {

    @Autowired
    IssueService issueService;

    @Autowired
    WorkflowService workflowService;

    @Autowired
    UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SprintService sprintService;

    private Project project;

    private User user;

    @Before
    public void setUp() {
        project = projectService.createNewProject(new Project("Project №1", "Description of the Project", "Department №1", null, null));
        sprintService.createNewSprint(project.getId());
        user = new User("User", "password", "password", "Max", "Petrov", "Ivanovich");
        userService.saveUser(user);
    }

    @After
    public void tearDown() {
        projectService.deleteProject(project.getId());
        userService.deleteUser("User");
    }

    @Test
    public void givenProject_whenCreateNewIssueInBacklog_thenCreateNewIssueInDAO() {
        Issue newIssue = issueService.createNewIssue(project.getProjectBacklog());
        Issue found = issueService.getIssueById(newIssue.getId());

        assertThat(newIssue.getId(), equalTo(found.getId()));
        assertThat(newIssue.getTitle(), equalTo(found.getTitle()));
        assertThat(newIssue.getBacklog().getId(), equalToObject(found.getBacklog().getId()));
        assertThat(newIssue.getCreationDate(), equalTo(found.getCreationDate()));
    }

    @Test
    public void givenIssue_whenDeleteIssue_thenDeleteIssueInDAO() {
        Issue newIssue = issueService.createNewIssue(project.getProjectBacklog());
        boolean deleteIssue = issueService.deleteIssue(newIssue.getId());

        assertTrue(deleteIssue);
        assertTrue(issueService.getIssueById(newIssue.getId()) == null);
    }

    @Test
    public void givenIssues_whenFindIssueById_thenGetIssueFromDAO() {
        Issue newIssue = issueService.createNewIssue(project.getProjectBacklog());
        Issue found = issueService.getIssueById(newIssue.getId());

        assertThat(newIssue.getId(), equalTo(found.getId()));
    }

    @Test
    public void givenIssue_whenUpdateIssue_thenChangeAttributesInDAO() {
        Issue issue = issueService.createNewIssue(project.getProjectBacklog());
        WorkFlow workFlow = workflowService.createNewWorkflow();
        Issue newDTOIssue = new Issue(IssueType.EPIC, "Main task", issue, "description of the issue", workFlow, IssuePriority.HI, LocalDate.of(2010, 11, 1), user, user, project.getProjectBacklog(), null, null);
        newDTOIssue.setId(issue.getId());
        issueService.updateIssue(newDTOIssue);
        Issue issueFromDAO = issueService.getIssueById(issue.getId());

        assertThat(issueFromDAO.getId(), equalTo(newDTOIssue.getId()));
        assertThat(issueFromDAO.getTitle(), equalTo(newDTOIssue.getTitle()));
        assertThat(issueFromDAO.getIssueType(), equalTo(newDTOIssue.getIssueType()));
        assertThat(issueFromDAO.getExecutor().getId(), equalTo(newDTOIssue.getExecutor().getId()));
        assertThat(issueFromDAO.getReporter().getId(), equalTo(newDTOIssue.getReporter().getId()));
        assertThat(issueFromDAO.getPriority(), equalTo(newDTOIssue.getPriority()));
        assertThat(issueFromDAO.getDescription(), equalTo(newDTOIssue.getDescription()));
        assertThat(issueFromDAO.getCreationDate(), equalTo(newDTOIssue.getCreationDate()));
        assertThat(issueFromDAO.getWorkflow().getId(), equalTo(newDTOIssue.getWorkflow().getId()));
        assertThat(issueFromDAO.getParentIssue().getId(), equalTo(newDTOIssue.getParentIssue().getId()));
        assertThat(issueFromDAO.getWorkFlowCurrentStatus(), equalTo(newDTOIssue.getWorkFlowCurrentStatus()));
    }

    @Test
    public void givenIssues_whenFindOtherIssue_thenGetIssueExceptThis() {
        Issue newIssue1 = issueService.createNewIssue(project.getProjectBacklog());
        Issue newIssue2 = issueService.createNewIssue(project.getProjectBacklog());
        Issue newIssue3 = issueService.createNewIssue(project.getProjectBacklog());
        List<Long> foundListId = issueService.findOtherIssueInProject(newIssue2.getId()).stream().map(Issue::getId).collect(Collectors.toList());

        assertThat(foundListId, hasSize(2));
        assertThat(foundListId, containsInAnyOrder(newIssue1.getId(), newIssue3.getId()));
    }

    @Test
    public void givenProject_whenMoveIssueToSprint_thenIssueInSprintAndNotInBacklog() {
        Issue newIssue = issueService.createNewIssue(project.getProjectBacklog());
        issueService.moveToSprint(newIssue.getId());
        project = projectService.findProjectById(project.getId());

        assertThat(project.getCurrentSprint().getIssueList(), hasSize(1));
        assertThat(project.getProjectBacklog().getIssueList(), hasSize(0));
    }

    @Test
    public void givenProject_whenMoveIssueToBacklog_thenIssueInBacklogAndNotInSprint() {
        Issue newIssue = issueService.createNewIssue(project.getProjectBacklog());
        issueService.moveToSprint(newIssue.getId());
        issueService.moveToBacklog(newIssue.getId());
        project = projectService.findProjectById(project.getId());

        assertThat(project.getProjectBacklog().getIssueList(), hasSize(1));
        assertThat(project.getCurrentSprint().getIssueList(), hasSize(0));
    }
}