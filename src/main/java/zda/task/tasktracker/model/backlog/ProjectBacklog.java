package zda.task.tasktracker.model.backlog;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import zda.task.tasktracker.model.issue.Issue;
import zda.task.tasktracker.model.project.Project;

import javax.persistence.*;
import java.util.*;

@Component
@Scope("prototype")
@Entity
@Table(name = "backlogs")
public class ProjectBacklog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "backlog", cascade = CascadeType.ALL)
    private Set<Issue> issueList = new HashSet<>();

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public Set<Issue> getIssueList() {
        return issueList;
    }

    public void setIssueList(Set<Issue> issueList) {
        this.issueList = issueList;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public boolean addIssue(Issue issue) {
        return issueList.add(issue);
    }

    public boolean removeIssue(Issue issue) {
        return issueList.remove(issue);
    }
}