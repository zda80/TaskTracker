package zda.task.tasktracker.model.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import zda.task.tasktracker.model.backlog.ProjectBacklog;
import zda.task.tasktracker.model.backlog.ProjectSprint;
import zda.task.tasktracker.model.issue.Issue;
import zda.task.tasktracker.model.user.User;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Component
@Scope("prototype")
@Entity
@Table(name = "projects")
public class Project implements SimpleProject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String title;

    @Column
    private String description;

    @Column
    private String departmentName;

    @ManyToOne(fetch = FetchType.EAGER )
    @JoinColumn(name = "super_id")
    private User supervisor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "admin_id" )
    private User admin;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "project" , cascade = CascadeType.ALL)
    @Autowired
    private ProjectBacklog projectBacklog;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "project", cascade=CascadeType.ALL)
    private Set<ProjectSprint> sprints  = new HashSet<>();;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "current_sprint_id")
    private ProjectSprint currentSprint;

    public Project() {
    }

    public Project(String title, String description, String departmentName, User supervisor, User admin) {
        this.title = title;
        this.description = description;
        this.departmentName = departmentName;
        this.supervisor = supervisor;
        this.admin = admin;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ProjectBacklog getProjectBacklog() {
        return projectBacklog;
    }

    public void setProjectBacklog(ProjectBacklog projectBacklog) {
        this.projectBacklog = projectBacklog;
    }

    public Set<ProjectSprint> getSprints() {
        return sprints;
    }

    public void setSprints(Set<ProjectSprint> sprints) {
        this.sprints = sprints;
    }

    public boolean addSprint(ProjectSprint projectSprint) {

        return sprints.add(projectSprint);
    }

    public boolean removeSprint(ProjectSprint projectSprint) {

        return sprints.remove(projectSprint);
    }

    public boolean moveIssueToSprint(Issue issue) {

        return projectBacklog.removeIssue(issue) & currentSprint.addIssue(issue);
    }

    public boolean moveIssueToBacklog(Issue projectIssue) {

        return currentSprint.removeIssue(projectIssue) & projectBacklog.addIssue(projectIssue);
    }

    public ProjectSprint getCurrentSprint() {
        return currentSprint;
    }

    public void setCurrentSprint(ProjectSprint currentSprint) {
        this.currentSprint = currentSprint;
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

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public User getSupervisor() {
        return supervisor;
    }

    public void setSupervisor(User supervisor) {
        this.supervisor = supervisor;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }
}