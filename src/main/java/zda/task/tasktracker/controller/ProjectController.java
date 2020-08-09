package zda.task.tasktracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import zda.task.tasktracker.model.project.Project;
import zda.task.tasktracker.model.project.RequestDTO;
import zda.task.tasktracker.service.DecorateService;
import zda.task.tasktracker.service.ProjectService;
import zda.task.tasktracker.service.UserService;

import javax.validation.Valid;

@Controller
@RequestMapping("/projects")
public class ProjectController {

    public static final String PROJECT_NOT_SELECTED_MESSAGE = "Select Project first";
    public static final String FILTER_CHANGED_MESSAGE = "Filter changed";
    public static final String PROJECT_CREATED_MESSAGE = "New project created: ID = ";
    public static final String PROJECT_DELETED_MESSAGE = "Project deleted: ID = ";
    public static final String PROJECT_UPDATED_MESSAGE = "Project updated: ID = ";

    @Autowired
    private ProjectService projectService;

    @Autowired
    private DecorateService decorateService;

    @Autowired
    private UserService userService;

    @GetMapping("/createProject")
    public String newProject(Model model) {
        model.addAttribute("userList", userService.findAllUsers());

        return "projects/createProject";
    }

    @RequestMapping(path = "/createProject", method = RequestMethod.POST)
    public String getNewProject(@ModelAttribute("createProjectForm") @Valid Project projectForm, Model model) {
        Project newProject = projectService.createNewProject(projectForm);

        model.addAttribute("userList", userService.findAllUsers());
        model.addAttribute("info", PROJECT_CREATED_MESSAGE + newProject.getId());

        return "projects/createProject";
    }

    @GetMapping("/showProjects")
    public String showProjects(Model model) {
        model.addAttribute("projectList", projectService.findAllProjects());

        return "projects/showProjects";
    }

    @GetMapping("/deleteProject")
    public String deleteProject(Model model) {
        model.addAttribute("projectList", projectService.findAllProjects());

        return "projects/deleteProject";
    }

    @RequestMapping(value = "/deleteProject", method = RequestMethod.POST)
    public String deleteProject(@Valid Long projectId, Model model) {
        if (projectId != null && projectService.deleteProject(projectId))
            model.addAttribute("info", PROJECT_DELETED_MESSAGE + projectId);
        else model.addAttribute("info", PROJECT_NOT_SELECTED_MESSAGE);

        model.addAttribute("projectList", projectService.findAllProjects());
        model.addAttribute("userList", userService.findAllUsers());

        return "projects/deleteProject";
    }

    @GetMapping("/selectProject")
    public String takeProjectForEdit(Model model) {
        model.addAttribute("projectList", projectService.findAllProjects());

        return "projects/selectProject";
    }

    @RequestMapping(value = "/selectProject", method = RequestMethod.POST)
    public String editProject(@Valid Long projectId, Model model) {

        if (projectId == null) {
            model.addAttribute("info", PROJECT_NOT_SELECTED_MESSAGE);
            model.addAttribute("projectList", projectService.findAllProjects());
            return "projects/selectProject";
        }

        model.addAttribute("info", "Now u can edit project");
        model.addAttribute("currentProject", decorateService.findProjectByIdAndSetFilter(projectId));
        model.addAttribute("userList", userService.findAllUsers());
        model.addAttribute("requestDTO", decorateService.getFilterRequestDTO());

        return "projects/editProject";
    }

    @RequestMapping(value = "/editProject", method = RequestMethod.POST)
    public String updateProject(@ModelAttribute("editForm") @Valid Project projectDTO, Model model) {

        if (projectService.updateProjectDescription(projectDTO))
            model.addAttribute("info", PROJECT_UPDATED_MESSAGE + projectDTO.getId());

        model.addAttribute("userList", userService.findAllUsers());
        model.addAttribute("currentProject", decorateService.findProjectByIdAndSetFilter(projectDTO.getId()));
        model.addAttribute("requestDTO", decorateService.getFilterRequestDTO());

        return "projects/editProject";
    }

    @RequestMapping(value = "/reloadProject", method = RequestMethod.POST)
    public String reloadProject(@ModelAttribute("editForm") @Valid Project projectDTO,
                                @ModelAttribute("RequestDTO") @Valid RequestDTO requestDTO, Model model) {
        decorateService.setFilterRequestDTO(requestDTO);

        model.addAttribute("info", FILTER_CHANGED_MESSAGE);
        model.addAttribute("userList", userService.findAllUsers());
        model.addAttribute("currentProject", decorateService.findProjectByIdAndSetFilter(projectDTO.getId()));
        model.addAttribute("requestDTO", decorateService.getFilterRequestDTO());

        return "projects/editProject";
    }
}