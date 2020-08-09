package zda.task.tasktracker.model.project.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import zda.task.tasktracker.model.issue.Issue;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component("creationAfter")
@DependsOn("creationBefore")
public class CreationAfterFilter extends Filter {

    @Autowired
    public CreationAfterFilter(@Qualifier("creationBefore") Filter filterChain) {
        super(filterChain);
    }

    @Override
    public Set<Issue> nextFilter(Request request, Set<Issue> issueList) {
        pool = issueList;
        if (request.get(FilterType.CREATION_AFTER) != null) {
            pool = new HashSet<>();
            LocalDate finalDate = Arrays.stream((LocalDate[]) request.get(FilterType.CREATION_AFTER))
                    .max(Comparator.naturalOrder())
                    .get();

            pool.addAll(issueList
                    .stream()
                    .filter(l -> Optional.ofNullable(l.getCreationDate()).orElse(LocalDate.of(1970, 1, 1)).compareTo((finalDate)) >= 0)
                    .collect(Collectors.toList()));
        }
        super.nextFilter(request, pool);

        return pool;
    }
}