package zda.task.tasktracker.model.project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import zda.task.tasktracker.model.backlog.ProjectBacklog;
import zda.task.tasktracker.model.backlog.ProjectSprint;
import zda.task.tasktracker.model.issue.IssuePriority;
import zda.task.tasktracker.model.project.filters.ChainOfFilters;
import zda.task.tasktracker.model.project.filters.FilterType;
import zda.task.tasktracker.model.project.filters.Request;
import zda.task.tasktracker.model.user.User;
import zda.task.tasktracker.service.UserService;

import java.time.LocalDate;
import java.util.Set;

@Component
public class FilterProject implements ProjectDecorator {

    @Autowired
    private Project project;

    @Autowired
    private Request request;

    @Autowired
    private RequestDTO requestDTO;

    @Autowired
    private ProjectBacklog filteredProjectBacklog;

    @Autowired
    private ChainOfFilters chain;

    @Autowired
    private UserService userService;

    public FilterProject() {
    }

    @Override
    public void setWrapped(Project project) {
        this.project = project;
        filteredProjectBacklog.setId(project.getProjectBacklog().getId());
        filteredProjectBacklog.setTitle(project.getProjectBacklog().getTitle());
    }

    @Override
    public RequestDTO getRequestDTO() {
        return requestDTO;
    }

    @Override
    public void setRequestDTO(RequestDTO requestDTO) {
        this.requestDTO = requestDTO;
    }

    @Override
    public ProjectBacklog getProjectBacklog() {

        if (requestDTO.getExecutorFilterId()!=null)
            request.add(FilterType.EXECUTOR, new User[]{userService.findUserById(requestDTO.getExecutorFilterId()) });
        else request.remove(FilterType.EXECUTOR);

        if (requestDTO.getReporterFilterId()!=null)
            request.add(FilterType.REPORTER, new User[]{userService.findUserById(requestDTO.getReporterFilterId()) });
        else request.remove(FilterType.REPORTER);

        if (requestDTO.getIssuePriorityFilter()!=null)
            request.add(FilterType.PRIORITY, new IssuePriority[]{requestDTO.getIssuePriorityFilter()});
        else request.remove(FilterType.PRIORITY);

        if (requestDTO.getCreationBefore()!=null)
            request.add(FilterType.CREATION_BEFORE, new LocalDate[]{requestDTO.getCreationBefore()});
        else request.remove(FilterType.CREATION_BEFORE);

        if (requestDTO.getCreationAfter()!=null)
            request.add(FilterType.CREATION_AFTER, new LocalDate[]{requestDTO.getCreationAfter()});
        else request.remove(FilterType.CREATION_AFTER);


        if (requestDTO.getTitleFilter()!=null && !requestDTO.getTitleFilter().equals(""))
            request.add(FilterType.TITLE, new String[]{requestDTO.getTitleFilter()});
        else request.remove(FilterType.TITLE);


        filteredProjectBacklog.setIssueList(chain.startFilter(request, project.getProjectBacklog().getIssueList()));


        return filteredProjectBacklog;
    }

    @Override
    public long getId() {
        return project.getId();
    }

    @Override
    public void setId(long id) {
        project.setId(id);
    }

    @Override
    public void setProjectBacklog(ProjectBacklog projectBacklog) {
        filteredProjectBacklog=projectBacklog;
    }

    @Override
    public Set<ProjectSprint> getSprints() {
        return project.getSprints();
    }

    @Override
    public void setSprints(Set<ProjectSprint> sprints) {
        project.setSprints(sprints);
    }

    @Override
    public ProjectSprint getCurrentSprint() {
        return project.getCurrentSprint();
    }

    @Override
    public void setCurrentSprint(ProjectSprint currentSprint) {
        project.setCurrentSprint(currentSprint);
    }

    @Override
    public String getTitle() {
        return project.getTitle();
    }

    @Override
    public void setTitle(String title) {
        project.setTitle(title);
    }

    @Override
    public String getDescription() {
        return project.getDescription();
    }

    @Override
    public void setDescription(String description) {
        project.setDescription(description);
    }

    @Override
    public String getDepartmentName() {
        return project.getDepartmentName();
    }

    @Override
    public void setDepartmentName(String departmentName) {
        project.setDepartmentName(departmentName);
    }

    @Override
    public User getSupervisor() {
        return project.getSupervisor();
    }

    @Override
    public void setSupervisor(User supervisor) {
        project.setSupervisor(supervisor);
    }

    @Override
    public User getAdmin() {
        return project.getAdmin();
    }

    @Override
    public void setAdmin(User admin) {
        project.setAdmin(admin);
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
}