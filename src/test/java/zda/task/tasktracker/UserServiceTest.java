package zda.task.tasktracker;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import zda.task.tasktracker.model.user.User;
import zda.task.tasktracker.repository.UserRepository;
import zda.task.tasktracker.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @After
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void givenUser_whenSaveUser_thenSaveUserInDAO() {
        User user = new User("TestUser1", "password", "password", null, "Ivanov", null);
        boolean save = userService.saveUser(user);
        User found = userService.findUserByName("TestUser1");

        assertTrue(save);
        assertThat(user.getUsername(), equalTo(found.getUsername()));
        assertThat(user.getFirstName(), equalTo(found.getFirstName()));
        assertThat(user.getLastName(), equalTo(found.getLastName()));
        assertThat(user.getPatronymic(), equalTo(found.getPatronymic()));
        assertThat(user.getPassword(), equalTo(found.getPassword()));
    }

    @Test
    public void givenUser_whenSaveUserTwice_thenFalse() {
        User user = new User("TestUser2", "password", "password", null, "Ivanov", null);
        boolean saveOne = userService.saveUser(user);
        boolean saveTwo = userService.saveUser(user);

        assertTrue(saveOne);
        assertFalse(saveTwo);
    }

    @Test
    public void givenUsers_whenFindUserByUsername_thenReturnUser() {
        User user1 = new User("TestUser3", "password1", "password1", null, "Ivanov", null);
        User user2 = new User("TestUser4", "password2", "password2", null, "Petrov", null);
        userService.saveUser(user1);
        userService.saveUser(user2);

        User found = userService.findUserByName("TestUser3");
        assertThat(user1.getUsername(), equalTo(found.getUsername()));
        assertThat(user1.getFirstName(), equalTo(found.getFirstName()));
        assertThat(user1.getLastName(), equalTo(found.getLastName()));
        assertThat(user1.getPatronymic(), equalTo(found.getPatronymic()));
        assertThat(user1.getPassword(), equalTo(found.getPassword()));
    }

    @Test
    public void givenUser_whenLoadUserByUsername_thenReturnUser() {
        User user = new User("TestUser5", "password5", "password5", null, "Ivanov", null);
        userService.saveUser(user);
        User found = userService.findUserByName("TestUser5");

        assertThat(user.getUsername(), equalTo(found.getUsername()));
        assertThat(user.getFirstName(), equalTo(found.getFirstName()));
        assertThat(user.getLastName(), equalTo(found.getLastName()));
        assertThat(user.getPatronymic(), equalTo(found.getPatronymic()));
        assertThat(user.getPassword(), equalTo(found.getPassword()));
    }

    @Test
    public void givenUser_whenUpdateFirstNameLastNamePatronymic_thenUpdateFirstNameLastNamePatronymicInDAO() {
        User user1 = new User("TestUser7", "password1", "password1", null, "Ivanov", null);
        User user2 = new User("TestUser7", "password3", "password3", "Petr", "Petrov", "Petrovich");
        userService.saveUser(user1);
        userService.updateFirstNameLastNamePatronymic(user2);
        User found = userService.findUserByName("TestUser7");

        assertEquals(found.getFirstName(), user2.getFirstName());
        assertEquals(found.getLastName(), user2.getLastName());
        assertEquals(found.getPatronymic(), user2.getPatronymic());
    }

    @Test
    public void givenNeedFindRole_whenIsRoleExist_thenTrue() {
        jdbcTemplate.execute("INSERT INTO role(id, name) VALUES (555, 'ROLE_USER2')");
        assertTrue(userService.isRoleExists("ROLE_USER2"));
    }

    @Test
    public void givenUsers_whenFindAllUsers_thenGetAllUsers() {
        User user1 = new User("TestUser8", "password7", "password7", null, "Ivanov", null);
        User user2 = new User("TestUser9", "password8", "password8", "Petr", "Petrov", "Petrovich");
        userService.saveUser(user1);
        userService.saveUser(user2);
        List<User> userList = userService.findAllUsers();

        assertTrue(userList.size() >= 2);
    }

    @Test
    public void givenUsers_whenFindUserByName_thenGetUser() {
        User user = new User("TestUser10", "password7", "password7", null, "Ivanov", null);
        userService.saveUser(user);
        User found = userService.findUserByName("TestUser10");

        assertThat(user.getUsername(), equalTo(found.getUsername()));
        assertThat(user.getFirstName(), equalTo(found.getFirstName()));
        assertThat(user.getLastName(), equalTo(found.getLastName()));
        assertThat(user.getPatronymic(), equalTo(found.getPatronymic()));
        assertThat(user.getPassword(), equalTo(found.getPassword()));
    }

    @Test
    public void givenUsers_whenFindUserById_thenGetUser() {
        User user = new User("TestUser11", "password7", "password7", null, "Ivanov", null);
        userService.saveUser(user);
        User found = userService.findUserById(user.getId());

        assertThat(user.getUsername(), equalTo(found.getUsername()));
        assertThat(user.getFirstName(), equalTo(found.getFirstName()));
        assertThat(user.getLastName(), equalTo(found.getLastName()));
        assertThat(user.getPatronymic(), equalTo(found.getPatronymic()));
        assertThat(user.getPassword(), equalTo(found.getPassword()));
    }

    @Test
    public void givenUsers_whenDeleteUser_thenDeleteUserInDAO() {
        User user = new User("TestUser12", "password7", "password7", null, "Ivanov", null);
        boolean save = userService.saveUser(user);
        boolean delete1 = userService.deleteUser("TestUser12");
        boolean delete2 = userService.deleteUser("TestUser12");

        assertTrue(save);
        assertTrue(delete1);
        assertFalse(delete2);
        assertTrue(userService.findUserByName("TestUser12") == null);
    }
}