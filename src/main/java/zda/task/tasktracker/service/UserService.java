package zda.task.tasktracker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import zda.task.tasktracker.repository.RoleRepository;
import zda.task.tasktracker.repository.UserRepository;
import zda.task.tasktracker.model.user.Role;
import zda.task.tasktracker.model.user.User;

import java.util.Collections;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService() {
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            LOGGER.error("User not found");
            throw new UsernameNotFoundException("User not found");
        }
        return user;
    }

    public boolean saveUser(User user) {
        User userFromDB = userRepository.findByUsername(user.getUsername());

        if (userFromDB != null) {
            LOGGER.info("Username already present in DB");
            return false;
        }
        user.setRoles(Collections.singleton(new Role(1L, "ROLE_USER")));
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }

    public boolean updateFirstNameLastNamePatronymic(User user) {
        User userFromDB = userRepository.findByUsername(user.getUsername());
        if (userFromDB == null) {
            LOGGER.info("Can't find user: " + user.getUsername());
            return false;
        }

        userFromDB.setLastName(user.getLastName());
        userFromDB.setFirstName(user.getFirstName());
        userFromDB.setPatronymic(user.getPatronymic());

        userRepository.save(userFromDB);
        LOGGER.info("User updated: " + user.getUsername());

        return true;
    }

    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    public User findUserByName(String username) {
        return userRepository.findByUsername(username);
    }

    public User findUserById(Long id) {
        return userRepository.findById(id).get();
    }

    public boolean deleteUser(String username) {
        User user = findUserByName(username);
        if (user == null) return false;

        userRepository.delete(user);
        LOGGER.info("User " + username + " deleted");
        return true;
    }

    public boolean isRoleExists(String role) {
        return roleRepository.findAll().stream().anyMatch(l -> l.getName().equals(role));
    }
}