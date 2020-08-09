package zda.task.tasktracker.model.project.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import zda.task.tasktracker.model.issue.Issue;
import zda.task.tasktracker.model.user.User;

import java.util.*;
import java.util.stream.Collectors;

@Component("reporter")
@DependsOn("creationAfter")
public class ReporterFilter extends Filter {

    @Autowired
    public ReporterFilter(@Qualifier("creationAfter") Filter filterChain) {
        super(filterChain);
    }

    @Override
    public Set<Issue> nextFilter(Request request, Set<Issue> issueList) {
        pool = issueList;
        if (request.get(FilterType.REPORTER) != null) {
            Set<User> set = new HashSet<>(Arrays.asList((User[]) request.get(FilterType.REPORTER)));
            User[] result = set.toArray(new User[set.size()]);
            request.add(FilterType.REPORTER, result);
            pool = new HashSet<>();

            Arrays.asList(request.get(FilterType.REPORTER)).forEach(m ->
                    pool.addAll(issueList
                            .stream()
                            .filter(l -> m.equals(l.getReporter()))
                            .collect(Collectors.toList())));
        }
        super.nextFilter(request, pool);

        return pool;
    }
}