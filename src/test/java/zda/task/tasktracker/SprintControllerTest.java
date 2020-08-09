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
import zda.task.tasktracker.controller.SprintController;
import zda.task.tasktracker.model.backlog.ProjectBacklog;
import zda.task.tasktracker.model.backlog.ProjectSprint;
import zda.task.tasktracker.model.project.FilterProject;
import zda.task.tasktracker.model.project.Project;
import zda.task.tasktracker.model.project.RequestDTO;
import zda.task.tasktracker.model.user.User;
import zda.task.tasktracker.service.DecorateService;
import zda.task.tasktracker.service.ProjectService;
import zda.task.tasktracker.service.SprintService;
import zda.task.tasktracker.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(value = SprintController.class)
public class SprintControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    UserService userService;

    @MockBean
    DecorateService decorateService;

    @MockBean
    ProjectService projectService;

    @MockBean
    private SprintService sprintService;

    @Mock
    private static FilterProject filterProject;
    private static List<User> userList = new ArrayList<>();
    private static List<Project> projectList = new ArrayList<>();
    private static Project project;
    private static RequestDTO requestDTO;
    private static ProjectSprint projectSprint;

    @BeforeClass
    public static void init() {
        User user1 = new User("TestUser1", "password1", "password1", "Ivan", "Ivanov", null);
        User user2 = new User("TestUser2", "password2", "password2", "Max", "Petrov", null);
        userList.add(user1);
        userList.add(user2);

        project = new Project("Project №1", "Description of the Project", "Department №1", user1, user2);
        project.setId(1l);
        ProjectBacklog projectBacklog = new ProjectBacklog();
        projectBacklog.setId(1l);
        project.setProjectBacklog(projectBacklog);
        projectList.add(project);

        filterProject = new FilterProject();
        ProjectBacklog projectBacklog2 = new ProjectBacklog();
        filterProject.setProjectBacklog(projectBacklog2);
        filterProject.setWrapped(project);
        filterProject.setRequestDTO(new RequestDTO());

        requestDTO = new RequestDTO();

        projectSprint = new ProjectSprint();
        projectSprint.setId(1l);
    }

    @Test
    @WithMockUser
    public void givenSprintController_whenPostCreateSprint_thenCreateSprint() throws Exception {
        given(userService.findAllUsers()).willReturn(userList);
        given(sprintService.createNewSprint(anyLong())).willReturn(projectSprint);
        given(projectService.findProjectById(anyLong())).willReturn(project);

        mvc.perform(post("/sprints/createSprint")
                .param("id", "1"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("currentSprint", equalTo(projectSprint)))
                .andExpect(model().attribute("info", equalTo(SprintController.SPRINT_CREATED_MESSAGE + projectSprint.getId())))
                .andExpect(view().name("sprints/editSprint"));
    }

    @Test
    @WithMockUser
    public void givenSprintController_whenPostEditSprintAndSprintIsNull_thenReturnProjectPage() throws Exception {
        given(filterProject.getProjectBacklog()).willReturn(project.getProjectBacklog());
        given(userService.findAllUsers()).willReturn(userList);
        given(decorateService.findProjectByIdAndSetFilter(anyLong())).willReturn(filterProject);
        given(decorateService.getFilterRequestDTO()).willReturn(requestDTO);

        mvc.perform(post("/sprints/editSelectedSprint")
                .param("selectedSprint", "")
                .param("id", "1"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("info", equalTo(SprintController.SPRINT_NOT_SELECTED_MESSAGE)))
                .andExpect(model().attribute("currentProject", equalTo(filterProject)))
                .andExpect(model().attribute("userList", equalTo(userList)))
                .andExpect(model().attribute("requestDTO", equalTo(requestDTO)))
                .andExpect(view().name("projects/editProject"));
    }

    @Test
    @WithMockUser
    public void givenISprintController_whenPostEditSprintAndSprintIsNotNull_thenEditSprunt() throws Exception {
        given(userService.findAllUsers()).willReturn(userList);
        given(sprintService.getSprintById(anyLong())).willReturn(projectSprint);
        given(projectService.findProjectById(anyLong())).willReturn(project);

        mvc.perform(post("/sprints/editSelectedSprint")
                .param("selectedSprint", "1")
                .param("id", "1"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("currentSprint", equalTo(projectSprint)))
                .andExpect(view().name("sprints/editSprint"));
    }

    @Test
    @WithMockUser
    public void givenSprintController_whenPostSaveEdiSprint_thenSaveSprintInDAO() throws Exception {
        given(filterProject.getProjectBacklog()).willReturn(project.getProjectBacklog());
        given(userService.findAllUsers()).willReturn(userList);
        given(decorateService.findProjectByIdAndSetFilter(anyLong())).willReturn(filterProject);
        given(decorateService.getFilterRequestDTO()).willReturn(requestDTO);
        given(projectService.findProjectBySprintId(anyLong())).willReturn(project);
        given(sprintService.updateSprint(any())).willReturn(true);

        mvc.perform(post("/sprints/saveEditSprint")
                .param("id", "1"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("info", containsString(SprintController.SPRINT_UPDATED_MESSAGE)))
                .andExpect(model().attribute("currentProject", equalTo(filterProject)))
                .andExpect(model().attribute("userList", equalTo(userList)))
                .andExpect(model().attribute("requestDTO", equalTo(requestDTO)))
                .andExpect(view().name("projects/editProject"));
    }


    @Test
    @WithMockUser
    public void givenIssueController_whenPostDeleteSprintAndSprintIsNull_thenReturnProjectPage() throws Exception {
        given(filterProject.getProjectBacklog()).willReturn(project.getProjectBacklog());
        given(userService.findAllUsers()).willReturn(userList);
        given(decorateService.findProjectByIdAndSetFilter(anyLong())).willReturn(filterProject);
        given(decorateService.getFilterRequestDTO()).willReturn(requestDTO);
        given(sprintService.deleteSprint(anyLong())).willReturn(false);

        mvc.perform(post("/sprints/deleteSprint")
                .param("selectedSprint", "")
                .param("id", "1"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("info", equalTo(SprintController.SPRINT_NOT_SELECTED_MESSAGE)))
                .andExpect(model().attribute("currentProject", equalTo(filterProject)))
                .andExpect(model().attribute("userList", equalTo(userList)))
                .andExpect(model().attribute("requestDTO", equalTo(requestDTO)))
                .andExpect(view().name("projects/editProject"));
    }

    @Test
    @WithMockUser
    public void givenSprintController_whenPostDeleteSprintAndSprintIsNotNull_thenDeleteSprintAndReturnProjectPage() throws Exception {
        given(filterProject.getProjectBacklog()).willReturn(project.getProjectBacklog());
        given(userService.findAllUsers()).willReturn(userList);
        given(decorateService.findProjectByIdAndSetFilter(anyLong())).willReturn(filterProject);
        given(decorateService.getFilterRequestDTO()).willReturn(requestDTO);
        given(sprintService.deleteSprint(anyLong())).willReturn(true);

        mvc.perform(post("/sprints/deleteSprint")
                .param("selectedSprint", "1")
                .param("id", "1"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("info", equalTo(SprintController.SPRINT_DELETE_MESSAGE + projectSprint.getId())))
                .andExpect(model().attribute("currentProject", equalTo(filterProject)))
                .andExpect(model().attribute("userList", equalTo(userList)))
                .andExpect(model().attribute("requestDTO", equalTo(requestDTO)))
                .andExpect(view().name("projects/editProject"));
    }

    @Test
    @WithMockUser
    public void givenSprintController_whenPostChangeSprintAndSprintIsNull_thenReturnProjectPage() throws Exception {
        given(filterProject.getProjectBacklog()).willReturn(project.getProjectBacklog());
        given(userService.findAllUsers()).willReturn(userList);
        given(decorateService.findProjectByIdAndSetFilter(anyLong())).willReturn(filterProject);
        given(decorateService.getFilterRequestDTO()).willReturn(requestDTO);


        mvc.perform(post("/sprints/changeSprint")
                .param("selectedSprint", "")
                .param("id", "1"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("info", equalTo(SprintController.SPRINT_NOT_SELECTED_MESSAGE)))
                .andExpect(model().attribute("currentProject", equalTo(filterProject)))
                .andExpect(model().attribute("userList", equalTo(userList)))
                .andExpect(model().attribute("requestDTO", equalTo(requestDTO)))
                .andExpect(view().name("projects/editProject"));
    }

    @Test
    @WithMockUser
    public void givenSprintController_whenPostChangeSprintAndSprintIsNotNull_thenSetNewCurrentSprint() throws Exception {
        given(filterProject.getProjectBacklog()).willReturn(project.getProjectBacklog());
        given(userService.findAllUsers()).willReturn(userList);
        given(decorateService.findProjectByIdAndSetFilter(anyLong())).willReturn(filterProject);
        given(decorateService.getFilterRequestDTO()).willReturn(requestDTO);

        mvc.perform(post("/sprints/changeSprint")
                .param("selectedSprint", "1")
                .param("id", "1"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("info", equalTo(SprintController.CURRENT_SPRINT_MESSAGE + projectSprint.getId())))
                .andExpect(model().attribute("currentProject", equalTo(filterProject)))
                .andExpect(model().attribute("userList", equalTo(userList)))
                .andExpect(model().attribute("requestDTO", equalTo(requestDTO)))
                .andExpect(view().name("projects/editProject"));
    }
}