package zda.task.tasktracker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import zda.task.tasktracker.model.project.Project;
import zda.task.tasktracker.model.project.ProjectDecorator;
import zda.task.tasktracker.model.project.RequestDTO;
import zda.task.tasktracker.service.DecorateService;
import zda.task.tasktracker.service.ProjectService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class DecorateServiceTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private DecorateService decorateService;

    @Test
    public void givenProject_whenFindProjectByIdAndSetFilter_thenGetDecoratedProject() {
        Project project = projectService.createNewProject(new Project("Project №1", "Description of the Project1", "Department №1", null, null));
        ProjectDecorator decoratedProject = decorateService.findProjectByIdAndSetFilter(project.getId());

        assertThat(decoratedProject.getId(), equalTo(project.getId()));
    }

    @Test
    public void givenDecoratedProject_whenAsqFilterRequestDTO_thenGetFilterRequestDTO() {
        Project project = projectService.createNewProject(new Project("Project №1", "Description of the Project1", "Department №1", null, null));
        ProjectDecorator decoratedProject = decorateService.findProjectByIdAndSetFilter(project.getId());
        RequestDTO requestDTO = decorateService.getFilterRequestDTO();

        assertThat(requestDTO, equalTo(decoratedProject.getRequestDTO()));
    }

    @Test
    public void givenDecoratedProject_whenSetFilterRequestDTO_thenUpdateFilterRequestDTO() {
        Project project = projectService.createNewProject(new Project("Project №1", "Description of the Project1", "Department №1", null, null));
        ProjectDecorator decoratedProject = decorateService.findProjectByIdAndSetFilter(project.getId());
        RequestDTO requestDTO = new RequestDTO();
        decoratedProject.setRequestDTO(requestDTO);

        assertThat(requestDTO, equalTo(decoratedProject.getRequestDTO()));
    }
}