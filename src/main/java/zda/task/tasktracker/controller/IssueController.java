package zda.task.tasktracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;
import zda.task.tasktracker.model.issue.Issue;
import zda.task.tasktracker.model.issue.IssuePriority;
import zda.task.tasktracker.model.issue.IssueType;
import zda.task.tasktracker.model.project.Project;
import zda.task.tasktracker.service.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/issues")
public class IssueController {

    public static final String ISSUE_NOT_SELECTED_MESSAGE = "Select Issue first";
    public static final String ISSUE_NOT_SELECTED_OR_SPRINT_NOT_PRESENT_MESSAGE = "Select Issue first or Create current sprint";
    public static final String ISSUE_DELETED_MESSAGE = "Issue deleted: ID = ";
    public static final String ISSUE_UPDATED_MESSAGE = "Issue updated: ID = ";
    public static final String ISSUE_MOVED_TO_SPRINT_MESSAGE = "Issue moved to current sprint: ID = ";
    public static final String ISSUE_MOVED_TO_BACKLOG_MESSAGE = "Issue moved to backlog: ID = ";
    public static final String CANT_DELETE_ISSUE_MESSAGE = "This Issue has dependency, remove it first. Issue ID = ";

    @Autowired
    private DecorateService decorateService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private IssueService issueService;

    @Autowired
    private UserService userService;

    @Autowired
    private WorkflowService workflowService;

    @GetMapping("/editIssue/{id}")
    public String editIssue(@PathVariable("id") long id, Model model) {
        model.addAttribute("issue", issueService.getIssueById(id));
        model.addAttribute("issueList", issueService.findOtherIssueInProject(id));
        model.addAttribute("issueTypes", IssueType.values());
        model.addAttribute("priorityList", IssuePriority.values());
        model.addAttribute("workflowList", workflowService.findAllWorkflow());
        model.addAttribute("userList", userService.findAllUsers());

        return "issues/editIssue";
    }

    @GetMapping("/editProject/{id}/{issueId}/{message}")
    public String editProject(@PathVariable("id") long id, @PathVariable("issueId") long issueId, @PathVariable("message") String message, Model model) {
        model.addAttribute("requestDTO", decorateService.getFilterRequestDTO());
        model.addAttribute("userList", userService.findAllUsers());
        model.addAttribute("currentProject", decorateService.findProjectByIdAndSetFilter(id));
        switch (message) {
            case ("not"):
                model.addAttribute("info", ISSUE_NOT_SELECTED_MESSAGE);
                break;
            case ("notsprint"):
                model.addAttribute("info", ISSUE_NOT_SELECTED_OR_SPRINT_NOT_PRESENT_MESSAGE);
                break;
            case ("update"):
                model.addAttribute("info", ISSUE_UPDATED_MESSAGE + issueId);
                break;
            case ("delete"):
                model.addAttribute("info", ISSUE_DELETED_MESSAGE + issueId);
                break;
            case ("notdelete"):
                model.addAttribute("info", CANT_DELETE_ISSUE_MESSAGE + issueId);
                break;
            case ("sprint"):
                model.addAttribute("info", ISSUE_MOVED_TO_SPRINT_MESSAGE + issueId);
                break;
            case ("backlog"):
                model.addAttribute("info", ISSUE_MOVED_TO_BACKLOG_MESSAGE + issueId);
                break;
        }
        return "projects/editProject";
    }

    @RequestMapping(value = "/newIssue", method = RequestMethod.POST)
    public String newIssue(@ModelAttribute("editForm") Project editForm, Model model) {
        Issue issue = issueService.createNewIssue(editForm.getProjectBacklog());

        return "redirect:editIssue/" + issue.getId();
    }

    @RequestMapping(value = "/newChildIssue", method = RequestMethod.POST)
    public String newChildIssue(@ModelAttribute("editForm") Project editForm, @Valid Long backlogIssue, @Valid Long currentSprintIssue, Model model) {
        Long parentIssueId = backlogIssue == null ? currentSprintIssue : backlogIssue;
        if (parentIssueId == null)
            return "redirect:editProject/" + editForm.getId() + "/0/not";

        Issue issue = issueService.createNewIssue(editForm.getProjectBacklog());
        issue.setParentIssue(issueService.getIssueById(parentIssueId));
        issueService.updateIssue(issue);

        return "redirect:editIssue/" + issue.getId();
    }

    @RequestMapping(value = "/deleteIssue", method = RequestMethod.POST)
    public String deleteIssue(@Valid Long backlogIssue, @Valid Long id, Model model) {
        String message = "delete";
        try {
            if (backlogIssue == null)
                return "redirect:editProject/" + id + "/0/not";
            else
                issueService.deleteIssue(backlogIssue);
        } catch (Exception e) {
            message = "notdelete";
        }

        return "redirect:editProject/" + id + "/" + backlogIssue + "/" + message;
    }

    @RequestMapping(value = "/editIssue", method = RequestMethod.POST)
    public String editIssue(@Valid Long backlogIssue, @Valid Long id, Model model) {
        if (backlogIssue == null)
            return "redirect:editProject/" + id + "/0/not";

        return "redirect:editIssue/" + backlogIssue;
    }

    @RequestMapping(value = "/saveEditIssue", method = RequestMethod.POST)
    public String saveIssue(@ModelAttribute("issueForm") @Valid Issue issue, Model model) {
        issueService.updateIssue(issue);
        Project project = projectService.findProjectByIssueId(issue.getId());
        model.addAttribute("currentProject", decorateService.findProjectByIdAndSetFilter(project.getId()));

        return "redirect:editProject/" + project.getId() + "/" + issue.getId() + "/update";
    }

    @RequestMapping(value = "/moveToSprint", method = RequestMethod.POST)
    public String moveToSprint(@Valid Long backlogIssue, @Valid Long id, Model model) {
        if (backlogIssue == null || projectService.findProjectById(id).getCurrentSprint() == null)
            return "redirect:editProject/" + id + "/0/notsprint";
        else
            issueService.moveToSprint(backlogIssue);

        return "redirect:editProject/" + id + "/" + backlogIssue + "/sprint";
    }

    @RequestMapping(value = "/moveToBacklog", method = RequestMethod.POST)
    public String moveToBacklog(@Valid Long currentSprintIssue, @Valid Long id, Model model) {
        String message = "backlog";
        if (currentSprintIssue == null)
            return "redirect:editProject/" + id + "/0/not";
        else
            issueService.moveToBacklog(currentSprintIssue);

        return "redirect:editProject/" + id + "/" + +currentSprintIssue + "/" + message;
    }

    @RequestMapping(value = "/editIssueInSprint", method = RequestMethod.POST)
    public String editIssueInSprint(@Valid Long currentSprintIssue, @Valid Long id, Model model) {
        if (currentSprintIssue == null)
            return "redirect:editProject/" + id + "/0/not";

        return "redirect:editIssue/" + currentSprintIssue;
    }

    @RequestMapping(value = "/reload", method = RequestMethod.POST)
    public String reload(@ModelAttribute("issueForm") @Valid Issue issue, Model model) {
        model.addAttribute("issue", issue);
        model.addAttribute("issueList", issueService.findOtherIssueInProject(issue.getId()));
        model.addAttribute("issueTypes", IssueType.values());
        model.addAttribute("priorityList", IssuePriority.values());
        model.addAttribute("workflowList", workflowService.findAllWorkflow());
        model.addAttribute("userList", userService.findAllUsers());

        return "issues/editIssue";
    }
}