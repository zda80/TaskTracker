package zda.task.tasktracker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import zda.task.tasktracker.model.backlog.ProjectBacklog;
import zda.task.tasktracker.model.backlog.ProjectSprint;
import zda.task.tasktracker.model.issue.Issue;
import zda.task.tasktracker.model.issue.IssuePriority;
import zda.task.tasktracker.model.issue.IssueType;
import zda.task.tasktracker.model.project.Project;
import zda.task.tasktracker.model.user.User;
import zda.task.tasktracker.model.workflow.WorkFlow;
import zda.task.tasktracker.model.workflow.WorkFlowStatus;
import zda.task.tasktracker.repository.ProjectRepository;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ProjectTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    public void givenRoles_whenFindProject_thenReturnProject() {

        User user1 = new User("User1", "password1", "password1", "Ivan", "Ivanov", "Stepanovich");
        User user2 = new User("User2", "password2", "password2", "Max", "Petrov", "Ivanovich");
        entityManager.persist(user1);
        entityManager.persist(user2);

        WorkFlow workFlow = new WorkFlow();
        workFlow.add(WorkFlowStatus.OPEN_ISSUE);
        entityManager.persist(workFlow);

        Project project = new Project("Project №1", "Description of the Project", "Department №1", user1, user2);
        ProjectBacklog backlog = new ProjectBacklog();
        project.setProjectBacklog(backlog);

        ProjectSprint sprint = new ProjectSprint();
        project.addSprint(sprint);
        project.setCurrentSprint(sprint);

        Issue issue = new Issue(IssueType.EPIC, "Main task", null, "description of the issue", workFlow, IssuePriority.HI, LocalDate.of(2010, 11, 1), user1, user2, project.getProjectBacklog(), null, null);
        project.getProjectBacklog().addIssue(issue);

        entityManager.persist(project);
        entityManager.flush();

        Project foundProject = projectRepository.findProjectById(project.getId());
        ProjectBacklog foundBacklog = foundProject.getProjectBacklog();
        ProjectSprint foundSprint = foundProject.getCurrentSprint();

        assertEquals(foundProject, project);
        assertEquals(foundBacklog, backlog);
        assertEquals(foundSprint, sprint);
        assertThat(foundBacklog.getIssueList(), contains(issue));
    }
}