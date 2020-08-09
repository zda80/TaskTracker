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
import zda.task.tasktracker.controller.ProjectController;
import zda.task.tasktracker.model.backlog.ProjectBacklog;
import zda.task.tasktracker.model.project.FilterProject;
import zda.task.tasktracker.model.project.Project;
import zda.task.tasktracker.model.project.RequestDTO;
import zda.task.tasktracker.model.user.User;
import zda.task.tasktracker.service.*;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ProjectController.class)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    UserService userService;

    @MockBean
    DecorateService decorateService;

    @MockBean
    ProjectService projectService;

    @Mock
    private static FilterProject filterProject;
    private static List<User> userList = new ArrayList<>();
    private static List<Project> projectList = new ArrayList<>();
    private static Project project;
    private static RequestDTO requestDTO;

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
    }

    @Test
    @WithMockUser
    public void givenProjectController_whenGetCreateProject_thenAddUsersToAttributes() throws Exception {
        given(userService.findAllUsers()).willReturn(userList);

        mvc.perform(get("/projects/createProject"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("userList", equalTo(userList)))
                .andExpect(view().name("projects/createProject"));
    }

    @Test
    @WithMockUser
    public void givenProjectController_whenPostCreateProject_thenCreateProject() throws Exception {
        given(userService.findAllUsers()).willReturn(userList);
        given(projectService.createNewProject(any())).willReturn(project);

        mvc.perform(post("/projects/createProject"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("userList", equalTo(userList)))
                .andExpect(model().attribute("info", equalTo(ProjectController.PROJECT_CREATED_MESSAGE + project.getId())))
                .andExpect(view().name("projects/createProject"));
    }

    @Test
    @WithMockUser
    public void givenProjectController_whenGetShowProject_thenGetProjectList() throws Exception {
        given(projectService.findAllProjects()).willReturn(projectList);

        mvc.perform(get("/projects/showProjects"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("projectList", equalTo(projectList)))
                .andExpect(view().name("projects/showProjects"));
    }

    @Test
    @WithMockUser
    public void givenProjectController_whenGetDeleteProject_thenGetProjectList() throws Exception {
        given(projectService.findAllProjects()).willReturn(projectList);

        mvc.perform(get("/projects/deleteProject"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("projectList", equalTo(projectList)))
                .andExpect(view().name("projects/deleteProject"));
    }

    @Test
    @WithMockUser
    public void givenProjectController_whenPostDeleteProject_thenDeleteProject() throws Exception {
        given(projectService.deleteProject(anyLong())).willReturn(true);
        given(projectService.findAllProjects()).willReturn(projectList);
        given(userService.findAllUsers()).willReturn(userList);

        mvc.perform(post("/projects/deleteProject")
                .param("projectId", "1"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("projectList", equalTo(projectList)))
                .andExpect(model().attribute("userList", equalTo(userList)))
                .andExpect(model().attribute("info", equalTo(ProjectController.PROJECT_DELETED_MESSAGE + project.getId())))
                .andExpect(view().name("projects/deleteProject"));
    }

    @Test
    @WithMockUser
    public void givenProjectController_whenGetSelectProject_thenGetProjectList() throws Exception {
        given(projectService.findAllProjects()).willReturn(projectList);

        mvc.perform(get("/projects/selectProject"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("projectList", equalTo(projectList)))
                .andExpect(view().name("projects/selectProject"));
    }

    @Test
    @WithMockUser
    public void givenProjectController_whenPostSelectProjectAndProjectNotNull_thenEditProject() throws Exception {
        given(filterProject.getProjectBacklog()).willReturn(project.getProjectBacklog());
        given(userService.findAllUsers()).willReturn(userList);
        given(decorateService.findProjectByIdAndSetFilter(anyLong())).willReturn(filterProject);
        given(decorateService.getFilterRequestDTO()).willReturn(requestDTO);

        mvc.perform(post("/projects/selectProject")
                .param("projectId", "1"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("currentProject", equalTo(filterProject)))
                .andExpect(model().attribute("userList", equalTo(userList)))
                .andExpect(model().attribute("requestDTO", equalTo(requestDTO)))
                .andExpect(view().name("projects/editProject"));
    }

    @Test
    @WithMockUser
    public void givenProjectController_whenPostSelectProjectAndProjectIsNull_thenSelectProject() throws Exception {
        given(projectService.findAllProjects()).willReturn(projectList);

        mvc.perform(post("/projects/selectProject")
                .param("projectId", ""))
                .andExpect(status().is(200))
                .andExpect(model().attribute("projectList", equalTo(projectList)))
                .andExpect(model().attribute("info", equalTo(ProjectController.PROJECT_NOT_SELECTED_MESSAGE)))
                .andExpect(view().name("projects/selectProject"));
    }

    @Test
    @WithMockUser
    public void givenProjectController_whenPostEditProject_thenUpdateProject() throws Exception {
        given(filterProject.getProjectBacklog()).willReturn(project.getProjectBacklog());
        given(userService.findAllUsers()).willReturn(userList);
        given(decorateService.findProjectByIdAndSetFilter(anyLong())).willReturn(filterProject);
        given(decorateService.getFilterRequestDTO()).willReturn(requestDTO);
        given(projectService.updateProjectDescription(any())).willReturn(true);

        mvc.perform(post("/projects/editProject")
                .param("projectId", "1"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("currentProject", equalTo(filterProject)))
                .andExpect(model().attribute("userList", equalTo(userList)))
                .andExpect(model().attribute("requestDTO", equalTo(requestDTO)))
                .andExpect(view().name("projects/editProject"));
    }

    @Test
    @WithMockUser
    public void givenProjectController_whenPostReloadProject_thenReloadProjectAttributes() throws Exception {
        given(filterProject.getProjectBacklog()).willReturn(project.getProjectBacklog());
        given(userService.findAllUsers()).willReturn(userList);
        given(decorateService.findProjectByIdAndSetFilter(anyLong())).willReturn(filterProject);
        given(decorateService.getFilterRequestDTO()).willReturn(requestDTO);

        mvc.perform(post("/projects/reloadProject")
                .param("projectId", "1"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("currentProject", equalTo(filterProject)))
                .andExpect(model().attribute("userList", equalTo(userList)))
                .andExpect(model().attribute("requestDTO", equalTo(requestDTO)))
                .andExpect(model().attribute("info", equalTo(ProjectController.FILTER_CHANGED_MESSAGE)))
                .andExpect(view().name("projects/editProject"));
    }
}