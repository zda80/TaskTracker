package zda.task.tasktracker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;
import zda.task.tasktracker.model.backlog.ProjectBacklog;
import zda.task.tasktracker.model.issue.Issue;
import zda.task.tasktracker.model.project.Project;
import zda.task.tasktracker.model.workflow.WorkFlow;
import zda.task.tasktracker.model.workflow.WorkFlowStatus;
import zda.task.tasktracker.repository.BacklogRepository;
import zda.task.tasktracker.repository.IssueRepository;
import zda.task.tasktracker.repository.ProjectRepository;
import zda.task.tasktracker.repository.WorkflowRepository;

import java.util.List;

@Service
public class WorkflowService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowService.class);

    @Autowired
    WorkflowRepository workflowRepository;

    @Lookup
    WorkFlow createWorkflow() {
        return null;
    }

    public WorkFlow createNewWorkflow() {
        WorkFlow workFlow =createWorkflow();

        workflowRepository.save(workFlow);

        return workFlow;
    }

    public WorkFlow updateTitle(WorkFlow workFlowDTO){

        WorkFlow workFlowFromDB = workflowRepository.findWorkflowById(workFlowDTO.getId());
        if (workFlowFromDB==null) return null;
        workFlowFromDB.setTitle(workFlowDTO.getTitle());

        workflowRepository.save(workFlowFromDB);
        return workFlowFromDB;
    }

    public boolean addStatusToWorkflow(long workflowId, WorkFlowStatus status){
        WorkFlow workFlowFromDB = workflowRepository.findById(workflowId).orElse(null);
        if (workFlowFromDB==null) return false;

        workFlowFromDB.add(status);
        workflowRepository.save(workFlowFromDB);

        return true;
    }

    public boolean removeStatusFromWorkflow(long workflowId, WorkFlowStatus status){
        WorkFlow workFlowFromDB = workflowRepository.findWorkflowById(workflowId);
        if (workFlowFromDB==null) return false;

        workFlowFromDB.remove(status);
        workflowRepository.save(workFlowFromDB);

        return true;
    }

    public WorkFlow getWorkflowById(long workflowId){
        return workflowRepository.findWorkflowById(workflowId);
    }

    public List<WorkFlow> findAllWorkflow(){
        return workflowRepository.findAll();
    }

    public boolean deleteWorkflow(long workflowId) {
        WorkFlow workFlowFromDB = workflowRepository.findWorkflowById(workflowId);
        if (workFlowFromDB == null) return false;

        workflowRepository.delete(workFlowFromDB);
        return true;
    }
}