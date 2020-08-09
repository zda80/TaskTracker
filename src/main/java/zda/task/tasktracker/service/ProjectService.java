package zda.task.tasktracker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;
import zda.task.tasktracker.model.issue.Issue;
import zda.task.tasktracker.model.project.FilterProject;
import zda.task.tasktracker.model.project.Project;
import zda.task.tasktracker.repository.*;

import java.util.List;

@Service
public class ProjectService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectService.class);

    @Lookup
    Project createProject() {
        return null;
    }

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    IssueRepository issueRepository;

    @Autowired
    SprintRepository sprintRepository;

    public Project createNewProject(Project projectDTO) {

        Project newProject = createProject();

        newProject.setTitle(projectDTO.getTitle());
        newProject.setDescription(projectDTO.getDescription());
        newProject.setSupervisor(projectDTO.getSupervisor());
        newProject.setAdmin(projectDTO.getAdmin());
        newProject.getProjectBacklog().setTitle("backlog");
        newProject.getProjectBacklog().setProject(newProject);

        projectRepository.save(newProject);
        LOGGER.info("New project created: " + projectDTO.getTitle());

        return newProject;
    }

    public List<Project> findAllProjects() {
        return projectRepository.findAll();
    }

    public Project findProjectById(long id) {
        return projectRepository.findProjectById(id);
    }

    public boolean deleteProject(long id) {
        Project project = projectRepository.findProjectById(id);
        if (project == null) return false;

        projectRepository.deleteById(id);

        LOGGER.info("Project id " + id + " deleted");
        return true;
    }

    public boolean updateProjectDescription(Project project) {
        Project projectFromDB = projectRepository.findProjectById(project.getId());

        if (projectFromDB == null) {
            LOGGER.info("Can't find project: " + project.getId());
            return false;
        }

        projectFromDB.setTitle(project.getTitle());
        projectFromDB.setDescription(project.getDescription());
        projectFromDB.setDepartmentName(project.getDepartmentName());
        projectFromDB.setAdmin(project.getAdmin());
        projectFromDB.setSupervisor(project.getSupervisor());
        projectFromDB.getProjectBacklog().setTitle(project.getProjectBacklog().getTitle());
        projectRepository.save(projectFromDB);

        LOGGER.info("User updated: " + project.getId());

        return true;
    }

    public Project findProjectByIssueId(long issueId) {
        Issue issue = issueRepository.findById(issueId).get();

        return issue.getBacklog() == null ? issue.getSprint().getProject() : issue.getBacklog().getProject();
    }

    public Project findProjectBySprintId(Long id) {
        return sprintRepository.findById(id).get().getProject();
    }
}