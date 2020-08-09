package zda.task.tasktracker.model.project.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import zda.task.tasktracker.model.issue.Issue;

import java.util.Set;

@Component("chain")
@DependsOn("priority")
public class ChainOfFilters {

    @Autowired
    @Qualifier("priority")
    private Filter filterChain;

    public Set<Issue> startFilter(Request request, Set<Issue> issueList) {
        return filterChain.nextFilter(request, issueList);
    }
}