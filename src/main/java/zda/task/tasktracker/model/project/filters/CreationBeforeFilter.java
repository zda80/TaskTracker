package zda.task.tasktracker.model.project.filters;

import org.springframework.stereotype.Component;
import zda.task.tasktracker.model.issue.Issue;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component("creationBefore")
public class CreationBeforeFilter extends Filter {

    public CreationBeforeFilter() {
        super(null);
    }

    @Override
    public Set<Issue> nextFilter(Request request, Set<Issue> issueList) {
        pool = issueList;
        if (request.get(FilterType.CREATION_BEFORE) != null) {
            pool = new HashSet<>();
            LocalDate finalDate = Arrays.stream((LocalDate[]) request.get(FilterType.CREATION_BEFORE))
                    .min(Comparator.naturalOrder())
                    .get();

            pool.addAll(issueList
                    .stream()
                    .filter(l -> Optional.ofNullable(l.getCreationDate()).orElse(LocalDate.of(1970, 1, 1)).compareTo((finalDate)) <= 0)
                    .collect(Collectors.toList()));
        }
        super.nextFilter(request, pool);

        return pool;
    }
}