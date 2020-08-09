package zda.task.tasktracker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import zda.task.tasktracker.model.backlog.ProjectBacklog;
import zda.task.tasktracker.model.issue.Issue;
import zda.task.tasktracker.model.project.Project;
import zda.task.tasktracker.repository.BacklogRepository;
import zda.task.tasktracker.repository.IssueRepository;
import zda.task.tasktracker.repository.ProjectRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class IssueService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IssueService.class);

    @Lookup
    Issue createIssue() {
        return null;
    }

    @Autowired
    private BacklogRepository backlogRepository;

    @Autowired
    IssueRepository issueRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectService projectService;

    public Issue createNewIssue(ProjectBacklog backlog) {
        Issue issue = createIssue();

        ProjectBacklog backlogFromDB = backlogRepository.findById(backlog.getId()).get();

        issue.setBacklog(backlogFromDB);
        backlogFromDB.addIssue(issue);
        issueRepository.save(issue);

        return issue;
    }

    @Transactional
    public boolean deleteIssue(long issueId) {
        Issue issue = issueRepository.findIssueById(issueId);

        if (issue == null) {
            LOGGER.info("Can't find Issue id=" + issueId);
            return false;
        }

        ProjectBacklog backlog = issue.getBacklog();
        backlog.removeIssue(issue);
        issue.setBacklog(null);
        issueRepository.save(issue);
        issueRepository.deleteById(issueId);
        LOGGER.info("Delete Issue id=" + issueId);
        return true;
    }

    public Issue getIssueById(long id) {
        return issueRepository.findIssueById(id);
    }

    public boolean updateIssue(Issue issue) {
        Issue issueFromDB = issueRepository.findById(issue.getId()).orElse(null);

        if (issueFromDB == null) {
            LOGGER.info("Can't find issue: " + issue.getId());
            return false;
        }

        issueFromDB.setTitle(issue.getTitle());
        issueFromDB.setIssueType(issue.getIssueType());
        issueFromDB.setExecutor(issue.getExecutor());
        issueFromDB.setReporter(issue.getReporter());
        issueFromDB.setPriority(issue.getPriority());
        issueFromDB.setDescription(issue.getDescription());
        issueFromDB.setCreationDate(issue.getCreationDate());
        issueFromDB.setWorkflow(issue.getWorkflow());
        issueFromDB.setParentIssue(issue.getParentIssue());

        if (issue.getWorkflow() == null || !issue.getWorkflow().getWorkFlowList().contains(issue.getWorkFlowCurrentStatus()))
            issueFromDB.setWorkFlowCurrentStatus(null);
        else
            issueFromDB.setWorkFlowCurrentStatus(issue.getWorkFlowCurrentStatus());

        issueRepository.save(issueFromDB);

        LOGGER.info("Issue ID=" + issueFromDB.getId() + " updated");

        return true;
    }

    public void moveToSprint(long issueId) {
        Issue issue = issueRepository.findById(issueId).orElse(null);

        Project project = projectService.findProjectByIssueId(issueId);
        project.moveIssueToSprint(issue);

        issue.setSprint(project.getCurrentSprint());
        issue.setBacklog(null);

        //     projectRepository.save(project);
        issueRepository.save(issue);

        LOGGER.info("Issue id=" + issueId + " moved to current sprint");
    }

    public void moveToBacklog(long issueId) {
        Issue issue = issueRepository.findById(issueId).orElse(null);

        Project project = projectService.findProjectByIssueId(issueId);
        project.moveIssueToBacklog(issue);

        issue.setSprint(null);
        issue.setBacklog(project.getProjectBacklog());
        //    projectRepository.save(project);
        issueRepository.save(issue);

        LOGGER.info("Issue id=" + issueId + " moved to backlog");
    }

    public List<Issue> findOtherIssueInProject(long exceptIssueId) {

        List<Issue> issueList = new ArrayList<>();
        Project project = projectService.findProjectByIssueId(exceptIssueId);
        issueList.addAll(project.getProjectBacklog().getIssueList());
        project.getSprints().forEach(l -> issueList.addAll(l.getIssueList()));

        for (int i = 0; i < issueList.size(); i++)
            if (issueList.get(i).getId() == exceptIssueId) {
                issueList.remove(issueList.get(i));
                break;
            }

        return issueList;
    }
}