package zda.task.tasktracker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import zda.task.tasktracker.model.user.Role;
import zda.task.tasktracker.model.user.User;
import zda.task.tasktracker.repository.UserRepository;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    @Test
    public void givenUsers_whenFindByUsername_thenReturnUser() {
        Role role = new Role(1L, "ROLE_USER");
        entityManager.persist(role);

        User user = new User("TestUser1", "password", "password", "Max", "Ivanov", "Petrovich");
        user.setRoles(Collections.singleton(role));

        entityManager.persist(user);
        entityManager.flush();

        User found = userRepository.findByUsername(user.getUsername());

        assertTrue(found.getId() == user.getId());
        assertEquals(found.getUsername(), "TestUser1");
        assertEquals(found.getPassword(), "password");
        assertEquals(found.getPasswordConfirm(), "password");
        assertEquals(found.getFirstName(), "Max");
        assertEquals(found.getLastName(), "Ivanov");
        assertEquals(found.getPatronymic(), "Petrovich");
        assertTrue(found.getRoles().contains(role));
    }
}