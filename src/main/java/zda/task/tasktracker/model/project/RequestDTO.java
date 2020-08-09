package zda.task.tasktracker.model.project;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import zda.task.tasktracker.model.issue.IssuePriority;

import java.time.LocalDate;

@Component
public class RequestDTO {
    private Long executorFilterId;
    private Long reporterFilterId;
    private IssuePriority issuePriorityFilter;
    private String titleFilter;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate creationBefore;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate creationAfter;

    private IssuePriority[] issuePriorityList = IssuePriority.values();

    public RequestDTO() {
    }

    public Long getExecutorFilterId() {
        return executorFilterId;
    }

    public void setExecutorFilterId(Long executorFilterId) {
        this.executorFilterId = executorFilterId;
    }

    public Long getReporterFilterId() {
        return reporterFilterId;
    }

    public void setReporterFilterId(Long reporterFilterId) {
        this.reporterFilterId = reporterFilterId;
    }

    public IssuePriority getIssuePriorityFilter() {
        return issuePriorityFilter;
    }

    public void setIssuePriorityFilter(IssuePriority issuePriorityFilter) {
        this.issuePriorityFilter = issuePriorityFilter;
    }

    public IssuePriority[] getIssuePriorityList() {
        return issuePriorityList;
    }

    public void setIssuePriorityList(IssuePriority[] issuePriorityList) {
        this.issuePriorityList = issuePriorityList;
    }

    public LocalDate getCreationBefore() {
        return creationBefore;
    }

    public void setCreationBefore(LocalDate creationBefore) {
        this.creationBefore = creationBefore;
    }

    public LocalDate getCreationAfter() {
        return creationAfter;
    }

    public void setCreationAfter(LocalDate creationAfter) {
        this.creationAfter = creationAfter;
    }

    public String getTitleFilter() {
        return titleFilter;
    }

    public void setTitleFilter(String titleFilter) {
        this.titleFilter = titleFilter;
    }
}