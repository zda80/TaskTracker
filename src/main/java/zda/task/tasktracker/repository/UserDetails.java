package zda.task.tasktracker.repository;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserDetails {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}