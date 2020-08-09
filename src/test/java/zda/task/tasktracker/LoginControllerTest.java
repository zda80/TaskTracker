package zda.task.tasktracker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import zda.task.tasktracker.controller.LoginController;
import zda.task.tasktracker.service.UserService;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(value = LoginController.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mvc;

    @SpyBean
    LoginController loginController;

    @MockBean
    UserService userService;

    @Test
    @WithMockUser
    public void givenLoginController_whenGetMainPaige_thenGetCurrentUser() throws Exception {
        given(loginController.getUserName()).willReturn("username1");

        mvc.perform(get("/main"))
                .andExpect(status().is(200))
                .andExpect(model().attribute("username", equalTo("username1")))
                .andExpect(view().name("main"));
    }

    @Test
    @WithMockUser
    public void givenLoginController_whenGetStartPaige_thenRedirectLoginPage() throws Exception {
        mvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser
    public void givenLoginController_whenLogout_thenRedirectToLoginPage() throws Exception {
        mvc.perform(get("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}