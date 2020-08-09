package zda.task.tasktracker.model.issue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import zda.task.tasktracker.model.backlog.ProjectBacklog;
import zda.task.tasktracker.model.backlog.ProjectSprint;
import zda.task.tasktracker.model.user.User;
import zda.task.tasktracker.model.workflow.WorkFlow;
import zda.task.tasktracker.model.workflow.WorkFlowStatus;

import javax.persistence.*;
import java.time.LocalDate;

@Component
@Scope("prototype")
@Entity
@Table(name = "issues")
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @Column
    @Enumerated(EnumType.STRING)
    @Value("TASK")
    private IssueType issueType;

    @Column
    @Value("new task")
    private String title;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_issue_id")
    private Issue parentIssue;

    @Column
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "workflow_id")
    private WorkFlow workflow;

    @Column
    @Enumerated(EnumType.STRING)
    @Value("MEDIUM")
    private IssuePriority priority;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Value("#{T(java.time.LocalDateTime).now()}")
    private LocalDate creationDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "executor_id")
    private User executor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reporter_id")
    private User reporter;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "backlog_id")
    private ProjectBacklog backlog;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sprint_id")
    private ProjectSprint sprint;

    @Column
    private WorkFlowStatus workFlowCurrentStatus;

    public Issue() {
    }

    public Issue(IssueType issueType, String title, Issue parentIssue, String description, WorkFlow workflow, IssuePriority priority, LocalDate creationDate, User executor, User reporter, ProjectBacklog backlog, ProjectSprint sprint, WorkFlowStatus workFlowCurrentStatus) {
        this.issueType = issueType;
        this.title = title;
        this.parentIssue = parentIssue;
        this.description = description;
        this.workflow = workflow;
        this.priority = priority;
        this.creationDate = creationDate;
        this.executor = executor;
        this.reporter = reporter;
        this.backlog = backlog;
        this.sprint = sprint;
        this.workFlowCurrentStatus = workFlowCurrentStatus;
    }

    public IssueType getIssueType() {
        return issueType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IssuePriority getPriority() {
        return priority;
    }

    public void setPriority(IssuePriority priority) {
        this.priority = priority;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public User getExecutor() {
        return executor;
    }

    public void setExecutor(User executor) {
        this.executor = executor;
    }

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
    }

    public Issue getParentIssue() {
        return parentIssue;
    }

    public void setParentIssue(Issue parentIssue) {
        this.parentIssue = parentIssue;
    }

    public WorkFlow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(WorkFlow workflow) {
        this.workflow = workflow;
    }

    public WorkFlowStatus getWorkFlowCurrentStatus() {
        return workFlowCurrentStatus;
    }

    public void setWorkFlowCurrentStatus(WorkFlowStatus workFlowCurrentStatus) {
        this.workFlowCurrentStatus = workFlowCurrentStatus;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setIssueType(IssueType issueType) {
        this.issueType = issueType;
    }

    public ProjectBacklog getBacklog() {
        return backlog;
    }

    public void setBacklog(ProjectBacklog backlog) {
        this.backlog = backlog;
    }

    public ProjectSprint getSprint() {
        return sprint;
    }

    public void setSprint(ProjectSprint sprint) {
        this.sprint = sprint;
    }

    @Override
    public int hashCode() {
        return (int) id ;
    }
}