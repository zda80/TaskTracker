package zda.task.tasktracker.model.project.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import zda.task.tasktracker.model.issue.Issue;
import zda.task.tasktracker.model.issue.IssuePriority;

import java.util.*;
import java.util.stream.Collectors;

@Component("priority")
@DependsOn("executor")
public class PriorityFilter extends Filter {

    @Autowired
    public PriorityFilter(@Qualifier("executor") Filter filterChain) {
        super(filterChain);
    }

    @Override
    public Set<Issue> nextFilter(Request request, Set<Issue> issueList) {
        pool = issueList;
        if (request.get(FilterType.PRIORITY)!=null) {
            pool = new HashSet<>();

            Arrays.asList((IssuePriority[]) request.get(FilterType.PRIORITY)).forEach(m ->
                    pool.addAll(issueList
                            .stream()
                            .filter(l -> l.getPriority() ==  m)
                            .collect(Collectors.toList())));
        }
        super.nextFilter(request, pool);

        return pool;
    }
}