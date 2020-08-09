package zda.task.tasktracker.model.project;

public interface ProjectDecorator extends SimpleProject {

    void setWrapped(Project project);

    RequestDTO getRequestDTO();

    void setRequestDTO(RequestDTO requestDTO);
}