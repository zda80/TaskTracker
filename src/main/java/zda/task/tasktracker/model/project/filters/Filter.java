package zda.task.tasktracker.model.project.filters;

import zda.task.tasktracker.model.issue.Issue;

import java.util.Set;

public abstract class Filter {

    protected Set<Issue> pool;

    private Filter filterChain;

    public Filter(Filter filterChain) {
        this.filterChain = filterChain;
    }

    public Set<Issue> nextFilter(Request request, Set<Issue> issueList) {
        pool = issueList;
        if (filterChain != null)
            pool = filterChain.nextFilter(request, issueList);

        return pool;
    }
}