package zda.task.tasktracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import zda.task.tasktracker.model.workflow.WorkFlow;
import zda.task.tasktracker.model.workflow.WorkFlowStatus;
import zda.task.tasktracker.service.WorkflowService;

import javax.validation.Valid;

@Controller
@RequestMapping("/workflow")
public class WorkflowController {

    public static final String WORKFLOW_NOT_SELECTED_MESSAGE = "Select Workflow first";
    public static final String STATUS_NOT_SELECTED_MESSAGE = "Select Status first";
    public static final String WORKFLOW_DELETE_MESSAGE = "Workflow deleted: ID = ";
    public static final String WORKFLOW_SAVE_MESSAGE = "Workflow saved: ID = ";
    public static final String CANT_DELETE_WORKFLOW_MESSAGE = "This Workflow has dependency, remove it first. Workflow ID = ";
    public static final String STATUS_REMOVED = "Status Removed: ";
    public static final String STATUS_ADDED = "Status added: ";

    @Autowired
    WorkflowService workflowService;

    @GetMapping("/createWorkflow")
    public String newWorkflow(Model model) {

        model.addAttribute("statuses", WorkFlowStatus.values());
        model.addAttribute("workflow", workflowService.createNewWorkflow());

        return "workflow/editWorkflow";
    }

    @RequestMapping(value = "/saveWorkflow", method = RequestMethod.POST)
    public String saveWorkflow(@ModelAttribute("workflow") @Valid WorkFlow workFlow, Model model) {

        model.addAttribute("info", WORKFLOW_SAVE_MESSAGE + workFlow.getId());
        model.addAttribute("workflow", workflowService.updateTitle(workFlow));
        model.addAttribute("statuses", WorkFlowStatus.values());

        return "workflow/editWorkflow";
    }

    @RequestMapping(value = "/addStatus", method = RequestMethod.POST)
    public String addStatus(@ModelAttribute("workflow") @Valid WorkFlow workFlow, @Valid WorkFlowStatus statusList, Model model) {
        if (statusList == null)
            model.addAttribute("info", STATUS_NOT_SELECTED_MESSAGE);
        else if (workflowService.addStatusToWorkflow(workFlow.getId(), statusList))
            model.addAttribute("info", STATUS_ADDED + statusList);

        model.addAttribute("workflow", workflowService.getWorkflowById(workFlow.getId()));
        model.addAttribute("statuses", WorkFlowStatus.values());

        return "workflow/editWorkflow";
    }

    @RequestMapping(value = "/removeStatus", method = RequestMethod.POST)
    public String removeStatus(@ModelAttribute("workflow") @Valid WorkFlow workFlow, @Valid WorkFlowStatus currentStatus, Model model) {
        if (currentStatus == null)
            model.addAttribute("info", STATUS_NOT_SELECTED_MESSAGE);
        else if (workflowService.removeStatusFromWorkflow(workFlow.getId(), currentStatus))
            model.addAttribute("info", STATUS_REMOVED + currentStatus);
        model.addAttribute("workflow", workflowService.getWorkflowById(workFlow.getId()));
        model.addAttribute("statuses", WorkFlowStatus.values());

        return "workflow/editWorkflow";
    }

    @GetMapping("/selectWorkflow")
    public String takeProjectForEdit(Model model) {
        model.addAttribute("workflowList", workflowService.findAllWorkflow());

        return "workflow/selectWorkflow";
    }

    @RequestMapping(value = "/selectWorkflow", method = RequestMethod.POST)
    public String editProject(@Valid Long workflowId, Model model) {
        if (workflowId == null) {
            model.addAttribute("info", WORKFLOW_NOT_SELECTED_MESSAGE);
            model.addAttribute("workflowList", workflowService.findAllWorkflow());
            return "workflow/selectWorkflow";
        }

        model.addAttribute("statuses", WorkFlowStatus.values());
        model.addAttribute("workflow", workflowService.getWorkflowById(workflowId));

        return "workflow/editWorkflow";
    }

    @GetMapping("/showWorkflow")
    public String showProjects(Model model) {
        model.addAttribute("workflowList", workflowService.findAllWorkflow());

        return "workflow/showWorkflow";
    }

    @GetMapping("/deleteWorkflow")
    public String deleteWorkflow(Model model) {
        model.addAttribute("workflowList", workflowService.findAllWorkflow());

        return "workflow/deleteWorkflow";
    }

    @RequestMapping(value = "/deleteWorkflow", method = RequestMethod.POST)
    public String deleteWorkflow(@Valid Long workflowId, Model model) {
        try {
            if (workflowId != null && workflowService.deleteWorkflow(workflowId))
                model.addAttribute("info", WORKFLOW_DELETE_MESSAGE + workflowId);
            else model.addAttribute("info", WORKFLOW_NOT_SELECTED_MESSAGE);
        } catch (Exception e) {
            model.addAttribute("info", CANT_DELETE_WORKFLOW_MESSAGE + workflowId);
        }
        model.addAttribute("workflowList", workflowService.findAllWorkflow());

        return "workflow/deleteWorkflow";
    }
}