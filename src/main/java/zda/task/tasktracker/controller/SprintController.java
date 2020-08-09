package zda.task.tasktracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import zda.task.tasktracker.model.backlog.ProjectSprint;
import zda.task.tasktracker.model.project.Project;
import zda.task.tasktracker.service.DecorateService;
import zda.task.tasktracker.service.ProjectService;
import zda.task.tasktracker.service.SprintService;
import zda.task.tasktracker.service.UserService;

import javax.validation.Valid;

@Controller
@RequestMapping("/sprints")
public class SprintController {

    public static final String SPRINT_CREATED_MESSAGE = "New Sprint created: ID = ";
    public static final String SPRINT_NOT_SELECTED_MESSAGE = "Select Sprint first";
    public static final String SPRINT_UPDATED_MESSAGE = "Sprint updated: ID = ";
    public static final String SPRINT_DELETE_MESSAGE = "Sprint deleted: ID = ";
    public static final String CURRENT_SPRINT_MESSAGE = "Current Sprint ID = ";

    @Autowired
    private ProjectService projectService;

    @Autowired
    private DecorateService decorateService;

    @Autowired
    private SprintService sprintService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/createSprint", method = RequestMethod.POST)
    public String newSprint(@Valid Long id, Model model) {
        ProjectSprint sprint = sprintService.createNewSprint(id);

        model.addAttribute("currentSprint", sprint);
        model.addAttribute("info", SPRINT_CREATED_MESSAGE + id);

        return "sprints/editSprint";
    }

    @RequestMapping(value = "/editSelectedSprint", method = RequestMethod.POST)
    public String editSprint(@Valid Long selectedSprint, @Valid Long id, Model model) {
        if (selectedSprint == null) {
            model.addAttribute("info", SPRINT_NOT_SELECTED_MESSAGE);
            model.addAttribute("currentProject", decorateService.findProjectByIdAndSetFilter(id));
            model.addAttribute("requestDTO", decorateService.getFilterRequestDTO());
            model.addAttribute("userList", userService.findAllUsers());
            return "projects/editProject";
        }

        model.addAttribute("currentSprint", sprintService.getSprintById(selectedSprint));

        return "sprints/editSprint";
    }

    @RequestMapping(value = "/saveEditSprint", method = RequestMethod.POST)
    public String saveIssue(@ModelAttribute("sprintForm") @Valid ProjectSprint sprint, Model model) {
        if (sprintService.updateSprint(sprint))
            model.addAttribute("info", SPRINT_UPDATED_MESSAGE + sprint.getId());

        Project project = projectService.findProjectBySprintId(sprint.getId());
        model.addAttribute("currentProject", decorateService.findProjectByIdAndSetFilter(project.getId()));
        model.addAttribute("requestDTO", decorateService.getFilterRequestDTO());
        model.addAttribute("userList", userService.findAllUsers());

        return "projects/editProject";
    }

    @RequestMapping(value = "/deleteSprint", method = RequestMethod.POST)
    public String deleteSprint(@Valid Long selectedSprint, @Valid Long id, Model model) {
        if (selectedSprint == null)
            model.addAttribute("info", SPRINT_NOT_SELECTED_MESSAGE);
        else {
            sprintService.deleteSprint(selectedSprint);
            model.addAttribute("info", SPRINT_DELETE_MESSAGE + selectedSprint);
        }

        model.addAttribute("currentProject", decorateService.findProjectByIdAndSetFilter(id));
        model.addAttribute("requestDTO", decorateService.getFilterRequestDTO());
        model.addAttribute("userList", userService.findAllUsers());

        return "projects/editProject";
    }

    @RequestMapping(value = "/changeSprint", method = RequestMethod.POST)
    public String changeSprint(@Valid Long selectedSprint, @Valid Long id, Model model) {
        if (selectedSprint == null)
            model.addAttribute("info", SPRINT_NOT_SELECTED_MESSAGE);
        else {
            sprintService.setCurrentSprint(selectedSprint);
            model.addAttribute("info", CURRENT_SPRINT_MESSAGE + selectedSprint);
        }

        model.addAttribute("currentProject", decorateService.findProjectByIdAndSetFilter(id));
        model.addAttribute("requestDTO", decorateService.getFilterRequestDTO());
        model.addAttribute("userList", userService.findAllUsers());

        return "projects/editProject";
    }
}