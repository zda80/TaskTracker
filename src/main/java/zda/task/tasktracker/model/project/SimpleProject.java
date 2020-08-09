package zda.task.tasktracker.model.project;

import zda.task.tasktracker.model.backlog.ProjectBacklog;
import zda.task.tasktracker.model.backlog.ProjectSprint;
import zda.task.tasktracker.model.user.User;

import java.util.Set;

public interface SimpleProject {

    long getId();

    void setId(long id);

    ProjectBacklog getProjectBacklog();

    void setProjectBacklog(ProjectBacklog projectBacklog);

    Set<ProjectSprint> getSprints();

    void setSprints(Set<ProjectSprint> sprints);

    ProjectSprint getCurrentSprint();

    void setCurrentSprint(ProjectSprint currentSprint);

    String getTitle();

    void setTitle(String title);

    String getDescription();

    void setDescription(String description);

    String getDepartmentName();

    void setDepartmentName(String departmentName);

    User getSupervisor();

    void setSupervisor(User supervisor);

    User getAdmin();

    void setAdmin(User admin);
}