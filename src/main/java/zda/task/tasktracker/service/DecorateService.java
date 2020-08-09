package zda.task.tasktracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import zda.task.tasktracker.model.project.FilterProject;
import zda.task.tasktracker.model.project.RequestDTO;
import zda.task.tasktracker.repository.ProjectRepository;

@Service
public class DecorateService {

    @Autowired
    private FilterProject filterProject;

    @Autowired
    private ProjectRepository projectRepository;

    public FilterProject findProjectByIdAndSetFilter(long id) {
        filterProject.setWrapped(projectRepository.findProjectById(id));

        return filterProject;
    }
    public RequestDTO getFilterRequestDTO(){
        return filterProject.getRequestDTO();
    }

    public void setFilterRequestDTO(RequestDTO requestDTO){
        filterProject.setRequestDTO(requestDTO);
    }
}