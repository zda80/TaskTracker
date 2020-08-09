package zda.task.tasktracker.model.backlog;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import zda.task.tasktracker.model.issue.Issue;
import zda.task.tasktracker.model.project.Project;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Component
@Scope("prototype")
@Entity
@Table(name = "sprints")
public class ProjectSprint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String title;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "sprint", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<Issue> issueList = new HashSet<>();

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Value("#{T(java.time.LocalDateTime).now()}")
    private LocalDate startDate;

    @Column
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate finishDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project_id")
    private Project project;

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(LocalDate finishDate) {
        this.finishDate = finishDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    @Override
    public int hashCode() {
        return (int) id;
    }
}