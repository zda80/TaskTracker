package zda.task.tasktracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import zda.task.tasktracker.controller.UserController;
import zda.task.tasktracker.model.user.User;
import zda.task.tasktracker.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@WebMvcTest(value = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    private static User user1, user2;
    private static List<User> userList = new ArrayList<>();

    @BeforeClass
    public static void init() {
        user1 = new User("TestUser1", "password1", "password1", "Ivan", "Ivanov", null);
        user2 = new User("TestUser2", "password2", "password2", "Max", "Petrov", null);
        userList.add(user1);
        userList.add(user2);
    }

    @Test
    @WithMockUser
    public void givenUserController_whenGetShowUsers_thenGetUserList() throws Exception {
        given(userService.findAllUsers()).willReturn(userList);

        mvc.perform(get("/users/showusers"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("userList", equalTo(userList)))
                .andExpect(view().name("users/showusers"));
    }

    @Test
    @WithMockUser
    public void givenUserController_whenGetDeleteUsers_thenGetUserList() throws Exception {
        given(userService.findAllUsers()).willReturn(userList);

        mvc.perform(get("/users/deleteuser").with(user("user").password("password").roles("ADMIN")))
                .andExpect(status().is(200))
                .andExpect(model().attribute("userList", equalTo(userList)))
                .andExpect(view().name("users/deleteuser"));
    }

    @Test
    @WithMockUser
    public void givenUserController_whenPostDeleteUsersAndUserIsNull_thenShowMessage() throws Exception {
        given(userService.findAllUsers()).willReturn(userList);
        given(userService.deleteUser(any())).willReturn(false);

        mvc.perform(post("/users/deleteuser").with(user("user").password("password").roles("ADMIN"))
                .param("username", ""))
                .andExpect(status().is(200))
                .andExpect(model().attribute("userList", equalTo(userList)))
                .andExpect(model().attribute("info", equalTo(UserController.USER_NOT_SELECTED_MESSAGE)))
                .andExpect(view().name("users/deleteuser"));
    }

    @Test
    @WithMockUser
    public void givenUserController_whenPostDeleteUsersAndUserPresent_thenDeleteUser() throws Exception {
        given(userService.findAllUsers()).willReturn(userList);
        given(userService.deleteUser(any())).willReturn(true);

        mvc.perform(post("/users/deleteuser").with(user("user").password("password").roles("ADMIN"))
                .param("username", user1.getUsername()))
                .andExpect(status().is(200))
                .andExpect(model().attribute("userList", equalTo(userList)))
                .andExpect(model().attribute("info", equalTo(UserController.USER_DELETED_MESSAGE + user1.getUsername())))
                .andExpect(view().name("users/deleteuser"));
    }

    @Test
    @WithMockUser
    public void givenUserController_whenGetEditUsers_thenGetUserList() throws Exception {
        given(userService.findAllUsers()).willReturn(userList);

        mvc.perform(get("/users/edituser"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("userList", equalTo(userList)))
                .andExpect(view().name("users/edituser"));
    }

    @Test
    @WithMockUser
    public void givenUserController_whenPostEditUser_thenGetCurrentUser() throws Exception {
        given(userService.findAllUsers()).willReturn(userList);
        given(userService.findUserByName(any())).willReturn(user1);

        mvc.perform(post("/users/edituser")
                .param("username", user1.getUsername()))
                .andExpect(status().is(200))
                .andExpect(model().attribute("userList", equalTo(userList)))
                .andExpect(model().attribute("currentUser", equalTo(user1)))
                .andExpect(view().name("users/edituser"));
    }

    @Test
    @WithMockUser
    public void givenUserController_whenPostSaveChanges_thenUpdateFirstNameLastNamePatronymic() throws Exception {
        given(userService.findAllUsers()).willReturn(userList);
        given(userService.updateFirstNameLastNamePatronymic(any())).willReturn(true);

        mvc.perform(post("/users/saveChanges")
                .param("id", "1")
                .param("username", "User1"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("userList", equalTo(userList)))
                .andExpect(model().attribute("info", containsString(UserController.USER_UPDATED_MESSAGE)))
                .andExpect(view().name("users/edituser"));
    }
}