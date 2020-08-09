package zda.task.tasktracker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import zda.task.tasktracker.model.backlog.ProjectSprint;
import zda.task.tasktracker.model.project.Project;
import zda.task.tasktracker.service.ProjectService;
import zda.task.tasktracker.service.SprintService;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class SprintServiceTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    SprintService sprintService;

    private Project project;

    @Before
    public void setUp() {
        project = projectService.createNewProject(new Project("Project №1", "Description of the Project", "Department №1", null, null));
    }

    @After
    public void tearDown() {
        projectService.deleteProject(project.getId());
    }

    @Test
    public void givenProject_whenCreateNewSprint_thenCreateNewSprintInDAO() {
        ProjectSprint sprint = sprintService.createNewSprint(project.getId());
        ProjectSprint sprintFromDAO = sprintService.getSprintById(sprint.getId());

        assertThat(sprint.getId(), equalTo(sprintFromDAO.getId()));
    }

    @Test
    public void givenSprint_whenUpdateSprint_thenChangeAttributesInDAO() {
        ProjectSprint sprint = sprintService.createNewSprint(project.getId());
        ProjectSprint sprintDTO = new ProjectSprint();
        sprintDTO.setId(sprint.getId());
        sprintDTO.setTitle("New title");
        sprintDTO.setStartDate(LocalDate.of(2020, 11, 1));
        sprintDTO.setFinishDate(LocalDate.of(2021, 10, 1));
        sprintService.updateSprint(sprintDTO);
        ProjectSprint updatedSprint = sprintService.getSprintById(sprint.getId());

        assertThat(updatedSprint.getId(), equalTo(sprintDTO.getId()));
        assertThat(updatedSprint.getTitle(), equalTo(sprintDTO.getTitle()));
        assertThat(updatedSprint.getStartDate(), equalTo(sprintDTO.getStartDate()));
        assertThat(updatedSprint.getFinishDate(), equalTo(sprintDTO.getFinishDate()));
    }

    @Test
    public void givenSprints_wheFindSprintById_thenGetSprintFromDAO() {
        ProjectSprint sprint = sprintService.createNewSprint(project.getId());
        ProjectSprint foundSprint = sprintService.getSprintById(sprint.getId());

        assertThat(foundSprint.getId(), equalTo(sprint.getId()));
        assertThat(foundSprint.getTitle(), equalTo(sprint.getTitle()));
    }

    @Test
    public void givenSprint_wheDeleteSprint_thenDeleteSprintInDAO() {
        ProjectSprint sprint = sprintService.createNewSprint(project.getId());
        boolean deleteSprint = sprintService.deleteSprint(sprint.getId());

        assertTrue(deleteSprint);
        assertTrue(sprintService.getSprintById(sprint.getId()) == null);
    }

    @Test
    public void givenProject_wheSetCurrentSprint_thenUpdateCurrentSprint() {
        ProjectSprint sprint1 = sprintService.createNewSprint(project.getId());
        ProjectSprint sprint2 = sprintService.createNewSprint(project.getId());
        project.setCurrentSprint(sprint1);

        assertThat(project.getCurrentSprint(), equalTo(sprint1));
    }
}