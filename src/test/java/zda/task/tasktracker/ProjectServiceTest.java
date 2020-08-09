package zda.task.tasktracker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import zda.task.tasktracker.model.backlog.ProjectSprint;
import zda.task.tasktracker.model.issue.Issue;
import zda.task.tasktracker.model.project.Project;
import zda.task.tasktracker.model.user.User;
import zda.task.tasktracker.repository.ProjectRepository;
import zda.task.tasktracker.service.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ProjectServiceTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    UserService userService;

    @Autowired
    IssueService issueService;

    @Autowired
    SprintService sprintService;

    private User user1, user2;

    @Before
    public void setUp() {
        user1 = new User("User1", "password1", "password1", "Ivan", "Ivanov", "Stepanovich");
        user2 = new User("User2", "password2", "password2", "Max", "Petrov", "Ivanovich");
        userService.saveUser(user1);
        userService.saveUser(user2);
    }

    @After
    public void tearDown() {
        projectRepository.deleteAll();
        userService.deleteUser("User1");
        userService.deleteUser("User2");
    }

    @Test
    public void givenProject_whenCreateNewProject_thenCreateNewProjectInDAO() {
        Project project = projectService.createNewProject(new Project("Project №1", "Description of the Project", "Department №1", user1, user2));
        Project found = projectService.findProjectById(project.getId());

        assertThat(project.getId(), equalTo(found.getId()));
        assertThat(project.getTitle(), equalTo(found.getTitle()));
        assertThat(project.getDescription(), equalTo(found.getDescription()));
        assertThat(project.getDepartmentName(), equalTo(found.getDepartmentName()));
        assertThat(project.getAdmin().getId(), equalTo(found.getAdmin().getId()));
        assertThat(project.getSupervisor().getId(), equalTo(found.getSupervisor().getId()));
    }

    @Test
    public void givenProjects_wheFindAllProjects_thenGetAllProjectsFromDAO() {
        Project project1 = projectService.createNewProject(new Project("Project №1", "Description of the Project1", "Department №1", user1, user2));
        Project project2 = projectService.createNewProject(new Project("Project №2", "Description of the Project2", "Department №1", user2, user1));
        List<Project> projectList = projectService.findAllProjects();

        assertThat(projectList, hasSize(2));
        assertThat(projectList.stream().map(Project::getId).collect(Collectors.toList()), containsInAnyOrder(project1.getId(), project2.getId()));
    }

    @Test
    public void givenProjects_wheFindProjectById_thenGetProjectFromDAO() {
        Project project1 = projectService.createNewProject(new Project("Project №1", "Description of the Project1", "Department №1", user1, user2));
        Project project2 = projectService.createNewProject(new Project("Project №2", "Description of the Project2", "Department №1", user1, user2));
        Project foundProject = projectService.findProjectById(project2.getId());

        assertThat(foundProject.getId(), equalTo(project2.getId()));
    }

    @Test
    public void givenProjects_wheDeleteProject_thenDeleteProjectInDAO() {
        Project project = projectService.createNewProject(new Project("Project №1", "Description of the Project1", "Department №1", user1, user2));
        boolean deleteProject = projectService.deleteProject(project.getId());

        assertTrue(deleteProject);
        assertTrue(projectService.findProjectById(project.getId()) == null);
    }

    @Test
    public void givenProject_whenUpdateProject_thenChangeAttributesInDAO() {
        Project project = projectService.createNewProject(new Project("Project №1", "Description of the Project1", "Department №1", user2, user2));
        Project newDTOProject = projectService.createNewProject(new Project("Project №2", "Description of the Project2", "Department №1", user1, user1));
        newDTOProject.setId(project.getId());
        newDTOProject.getProjectBacklog().setTitle("new backlog title");
        projectService.updateProjectDescription(newDTOProject);

        Project projectFromDAO = projectService.findProjectById(project.getId());

        assertThat(projectFromDAO.getId(), equalTo(newDTOProject.getId()));
        assertThat(projectFromDAO.getTitle(), equalTo(newDTOProject.getTitle()));
        assertThat(projectFromDAO.getDescription(), equalTo(newDTOProject.getDescription()));
        assertThat(projectFromDAO.getDepartmentName(), equalTo(newDTOProject.getDepartmentName()));
        assertThat(projectFromDAO.getAdmin().getId(), equalTo(newDTOProject.getAdmin().getId()));
        assertThat(projectFromDAO.getSupervisor().getId(), equalTo(newDTOProject.getSupervisor().getId()));
        assertThat(projectFromDAO.getProjectBacklog().getTitle(), equalTo(newDTOProject.getProjectBacklog().getTitle()));
    }

    @Test
    public void givenProject_whenFindProjectByIssueId_thenGetProjectFromDAO() {
        Project project = projectService.createNewProject(new Project("Project №1", "Description of the Project1", "Department №1", user2, user2));
        Issue newIssue = issueService.createNewIssue(project.getProjectBacklog());
        Project foundProject = projectService.findProjectByIssueId(newIssue.getId());

        assertThat(foundProject.getId(), equalTo(project.getId()));
    }

    @Test
    public void givenProject_whenFindProjectBySprintId_thenGetProjectFromDAO() {
        Project project = projectService.createNewProject(new Project("Project №1", "Description of the Project1", "Department №1", user2, user2));
        ProjectSprint sprint = sprintService.createNewSprint(project.getId());
        Project foundProject = projectService.findProjectBySprintId(sprint.getId());

        assertThat(foundProject.getId(), equalTo(project.getId()));
    }
}