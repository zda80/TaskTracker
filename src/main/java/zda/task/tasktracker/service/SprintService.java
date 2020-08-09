package zda.task.tasktracker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;
import zda.task.tasktracker.model.backlog.ProjectSprint;
import zda.task.tasktracker.model.project.Project;
import zda.task.tasktracker.repository.ProjectRepository;
import zda.task.tasktracker.repository.SprintRepository;

@Service
public class SprintService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SprintService.class);

    @Lookup
    ProjectSprint createSprint() {
        return null;
    }

    @Autowired
    SprintRepository sprintRepository;

    @Autowired
    private ProjectRepository projectRepository;


    public ProjectSprint createNewSprint(long projectId) {
        ProjectSprint sprint = createSprint();

        Project projectFromDB = projectRepository.findById(projectId).get();
        projectFromDB.setCurrentSprint(sprint);

        sprint.setProject(projectFromDB);
        projectFromDB.addSprint(sprint);
        sprintRepository.save(sprint);
        projectRepository.save(projectFromDB);

        return sprint;
    }

    public boolean updateSprint(ProjectSprint sprint) {
        ProjectSprint sprintFromDB = sprintRepository.findById(sprint.getId()).orElse(null);
        if (sprintFromDB == null) {
            LOGGER.info("Can't find sprint: " + sprint.getId());
            return false;
        }

        sprintFromDB.setTitle(sprint.getTitle());
        sprintFromDB.setStartDate(sprint.getStartDate());
        sprintFromDB.setFinishDate(sprint.getFinishDate());
        sprintRepository.save(sprintFromDB);

        LOGGER.info("Update sprint: " + sprintFromDB.getId());

        return true;
    }

    public ProjectSprint getSprintById(long id) {
        return sprintRepository.findById(id).orElse(null);
    }

    public boolean deleteSprint(long sprintId) {

        ProjectSprint sprint = sprintRepository.findById(sprintId).orElse(null);
        if (sprint == null) {
            LOGGER.info("Can't find Sprint id=" + sprintId);
            return false;
        }

        Project project = sprint.getProject();
        if (project.getCurrentSprint()!=null && project.getCurrentSprint().getId()==sprint.getId()) project.setCurrentSprint(null);
        project.removeSprint(sprint);
        sprint.setProject(null);
        projectRepository.save(project);
        sprintRepository.save(sprint);
        sprintRepository.deleteById(sprintId);

        LOGGER.info("Delete Sprint id=" + sprintId);

        return true;
    }

    public void setCurrentSprint(Long selectedSprint) {
        ProjectSprint sprint = sprintRepository.findById(selectedSprint).orElse(null);
        sprint.getProject().setCurrentSprint(sprint);
        projectRepository.save(sprint.getProject());
    }
}