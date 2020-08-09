package zda.task.tasktracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import zda.task.tasktracker.model.user.User;
import zda.task.tasktracker.service.UserService;

import javax.validation.Valid;

@Controller
@RequestMapping("/users")
public class RegistrationController {

    public static final String BAD_USERNAME = "Login and password must contain at least 2 characters";
    public static final String BAD_PASSWORD = "Passwords do not match";
    public static final String USER_TAKEN = "This user name is already taken";
    public static final String USER_REGISTERED = "New user registered: ";

    @Autowired
    private UserService userService;

    @GetMapping("/registration")
    public String newUser() {
        return "users/registration";
    }

    @RequestMapping(path = "/registration", method = RequestMethod.POST)
    public String getNewUser(@ModelAttribute("regForm") @Valid User regForm, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("info", BAD_USERNAME);
            return "users/registration";
        }

        if (!regForm.getPassword().equals(regForm.getPasswordConfirm())) {
            model.addAttribute("info", BAD_PASSWORD);
            return "users/registration";
        }

        if (!userService.saveUser(regForm)) {
            model.addAttribute("info", USER_TAKEN);
            return "users/registration";
        }

        model.addAttribute("info", USER_REGISTERED + regForm.getUsername());

        return "users/registration";
    }
}