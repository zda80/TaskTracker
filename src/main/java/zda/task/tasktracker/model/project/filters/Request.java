package zda.task.tasktracker.model.project.filters;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class  Request<T> {

    private Map<FilterType, T[] > filters = new HashMap<>();

    public Request() {
    }

    public void add(FilterType filterType, T[] list){
        filters.put(filterType, list);
    }

    public void remove(FilterType filterType){
        filters.remove(filterType);
    }

    public T[] get(FilterType filterType){
        return filters.get(filterType);
    }
}