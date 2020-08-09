package zda.task.tasktracker.model.workflow;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Component
@Scope("prototype")
@Entity
@Table(name = "workflows")
public class WorkFlow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String title;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<WorkFlowStatus> workFlowList;

    public WorkFlow() {
        workFlowList = new HashSet<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean add(WorkFlowStatus workFlowStatus) {
        return workFlowList.add(workFlowStatus);
    }

    public boolean remove(WorkFlowStatus workFlowStatus) {
        return workFlowList.remove(workFlowStatus);
    }

    public Set<WorkFlowStatus> getWorkFlowList() {
        return workFlowList;
    }

    public void setWorkFlowList(Set<WorkFlowStatus> workFlowList) {
        this.workFlowList = workFlowList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}