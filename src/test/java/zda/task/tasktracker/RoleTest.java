package zda.task.tasktracker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Example;
import org.springframework.test.context.junit4.SpringRunner;
import zda.task.tasktracker.model.user.Role;
import zda.task.tasktracker.repository.RoleRepository;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RoleTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void givenRoles_whenFindOneRole_thenReturnOneRole() {
        Role role = new Role(1L, "ROLE_USER");
        entityManager.persist(role);
        entityManager.flush();

        Role found = roleRepository.findOne(Example.of(role)).orElse(null);

        assertEquals(found, role);
    }
}