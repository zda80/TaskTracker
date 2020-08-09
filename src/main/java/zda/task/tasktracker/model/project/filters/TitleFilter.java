package zda.task.tasktracker.model.project.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import zda.task.tasktracker.model.issue.Issue;


import java.util.*;
import java.util.stream.Collectors;

@Component("title")
@DependsOn("reporter")
public class TitleFilter extends Filter {

    @Autowired
    public TitleFilter(@Qualifier("reporter") Filter filterChain) {
        super(filterChain);
    }

    @Override
    public Set<Issue> nextFilter(Request request, Set<Issue> issueList) {
        pool = issueList;
        if (request.get(FilterType.TITLE) != null) {
            Set<String> set = new HashSet<>(Arrays.asList((String[]) request.get(FilterType.TITLE)));
            String[] result = set.toArray(new String[set.size()]);
            request.add(FilterType.TITLE, result);
            pool = new HashSet<>();

            Arrays.asList(request.get(FilterType.TITLE)).forEach(m ->
                    pool.addAll(issueList
                            .stream()
                            .filter(l -> Optional.ofNullable(l.getTitle()).orElse("").equals(m))
                            .collect(Collectors.toList())));
        }
        super.nextFilter(request, pool);

        return pool;
    }
}