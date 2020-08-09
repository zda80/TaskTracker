package zda.task.tasktracker.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import zda.task.tasktracker.service.UserService;

import javax.annotation.PostConstruct;

@Configuration
public class RoleConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(RoleConfig.class);

    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String CREATE_USER = "INSERT INTO role(id, name) VALUES (1, 'ROLE_USER');";
    public static final String CREATE_ADMIN = "INSERT INTO role(id, name) VALUES (2, 'ROLE_ADMIN');";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private UserService userService;

    @PostConstruct
    public void initDb() {
        if (!userService.isRoleExists(ROLE_USER)) {
            jdbcTemplate.execute(CREATE_USER);
            LOGGER.info("Add ROLE_USER into database");
        }
        if (!userService.isRoleExists(ROLE_ADMIN)) {
            jdbcTemplate.execute(CREATE_ADMIN);
            LOGGER.info("Add ROLE_ADMIN into database");
        }

    }
}