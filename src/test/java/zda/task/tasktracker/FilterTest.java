package zda.task.tasktracker;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import org.junit.Before;
import org.junit.Test;
import zda.task.tasktracker.model.backlog.ProjectBacklog;
import zda.task.tasktracker.model.issue.Issue;
import zda.task.tasktracker.model.issue.IssuePriority;
import zda.task.tasktracker.model.issue.IssueType;
import zda.task.tasktracker.model.project.Project;
import zda.task.tasktracker.model.project.filters.ChainOfFilters;
import zda.task.tasktracker.model.project.filters.FilterType;
import zda.task.tasktracker.model.project.filters.Request;
import zda.task.tasktracker.model.user.User;

import java.time.LocalDate;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class FilterTest {

    private Request request;
    private Set<Issue> pool;
    private ChainOfFilters chain;
    private Project project;
    private User user1, user2, user3;
    private Issue issue1, issue2, issue3, issue4, issue5;

    @Before
    public void setUp() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.scan("zda.task.tasktracker");
        ctx.refresh();
        chain = ctx.getBean(ChainOfFilters.class);
        request = new Request();

        user1 = new User(null, null, null, null, "Ivanov", null);
        user2 = new User(null, null, null, null, "Petrov", null);
        user3 = new User(null, null, null, null, "Sidorov", null);

        project = new Project("Project №1", "Description of the Project", "Department №1", user1, user2);
        project.setProjectBacklog(new ProjectBacklog());

        issue1 = new Issue(IssueType.EPIC, "Main task", null, null, null, IssuePriority.HI, LocalDate.of(2010, 11, 1), user1, user1, project.getProjectBacklog(), null, null);
        issue2 = new Issue(IssueType.BUG, "Task 1", null, null, null, IssuePriority.HI, null, user1, user2, project.getProjectBacklog(), null, null);
        issue3 = new Issue(IssueType.BUG, "Task 2", null, null, null, IssuePriority.LOW, LocalDate.of(2012, 11, 1), user2, user3, project.getProjectBacklog(), null, null);
        issue4 = new Issue(IssueType.TASK, "Task 3", null, null, null, IssuePriority.LOW, null, user1, user3, project.getProjectBacklog(), null, null);
        issue5 = new Issue(IssueType.STORY, "Task 4", null, null, null, IssuePriority.MEDIUM, null, user1, null, project.getProjectBacklog(), null, null);
        project.getProjectBacklog().addIssue(issue1);
        project.getProjectBacklog().addIssue(issue2);
        project.getProjectBacklog().addIssue(issue3);
        project.getProjectBacklog().addIssue(issue4);
        project.getProjectBacklog().addIssue(issue5);
    }

    @Test
    public void givenPriorityFilter_whenPriorityIsHiAndMedium_thenBacklogIsHiAndMedium() {
        request.add(FilterType.PRIORITY, new IssuePriority[]{IssuePriority.HI, IssuePriority.MEDIUM});
        pool = chain.startFilter(request, project.getProjectBacklog().getIssueList());

        assertThat(pool, containsInAnyOrder(issue1, issue2, issue5));
    }

    @Test
    public void givenTitleFilter_whenTitleFilterSet_thenBacklogContainsThisTitle() {
        request.add(FilterType.TITLE, new String[]{"Main task", "Task 3", "Task 4"});
        pool = chain.startFilter(request, project.getProjectBacklog().getIssueList());

        assertThat(pool, containsInAnyOrder(issue1, issue4, issue5));
    }

    @Test
    public void givenExecutorFilter_whenExecutorIsUser1AndUser2_thenBacklogContainsUser1AndUser2() {
        request.add(FilterType.EXECUTOR, new User[]{user1, user3});
        pool = chain.startFilter(request, project.getProjectBacklog().getIssueList());

        assertThat(pool, containsInAnyOrder(issue1, issue2, issue4, issue5));
    }

    @Test
    public void givenReporterFilter_whenReporterIsUser1AndUser3_thenBacklogContainsUser1AndUser3() {
        request.add(FilterType.REPORTER, new User[]{user1, user3});
        pool = chain.startFilter(request, project.getProjectBacklog().getIssueList());

        assertThat(pool, containsInAnyOrder(issue1, issue3, issue4));
    }

    @Test
    public void givenDifferentFilters_whenDifferentFiltersSet_thenBacklogContainsThisFilters() {
        request.add(FilterType.PRIORITY, new IssuePriority[]{IssuePriority.HI, IssuePriority.LOW});
        request.add(FilterType.CREATION_AFTER, new LocalDate[]{LocalDate.of(2008, 11, 1), LocalDate.of(2005, 11, 1)});
        request.add(FilterType.CREATION_BEFORE, new LocalDate[]{LocalDate.of(2015, 11, 1)});
        request.add(FilterType.EXECUTOR, new User[]{user1, user2});
        pool = chain.startFilter(request, project.getProjectBacklog().getIssueList());

        assertThat(pool, containsInAnyOrder(issue1, issue3));
    }
}