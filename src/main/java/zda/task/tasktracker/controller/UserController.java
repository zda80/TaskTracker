package zda.task.tasktracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import zda.task.tasktracker.model.user.User;
import zda.task.tasktracker.service.UserService;

import javax.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {

    public static final String USER_NOT_SELECTED_MESSAGE = "Select User first";
    public static final String CANT_DELETE_USER_MESSAGE = "This User has dependency, remove it first. User ID = ";
    public static final String USER_DELETED_MESSAGE = "User deleted: ID = ";
    public static final String USER_UPDATED_MESSAGE = "User updated: ID = ";

    @Autowired
    private UserService userService;

    @GetMapping("/showusers")
    public String getUsers(Model model) {
        model.addAttribute("userList", userService.findAllUsers());

        return "users/showusers";
    }

    @GetMapping("/deleteuser")
    public String deleteUser(Model model) {
        model.addAttribute("userList", userService.findAllUsers());

        return "users/deleteuser";
    }

    @RequestMapping(value = "/deleteuser", method = RequestMethod.POST)
    public String deleteUser(@Valid String username, Model model) {
        try {
            if (userService.deleteUser(username)) model.addAttribute("info", USER_DELETED_MESSAGE + username);
            else model.addAttribute("info", USER_NOT_SELECTED_MESSAGE);
        } catch (Exception e) {
            model.addAttribute("info", CANT_DELETE_USER_MESSAGE + username);
        }
        model.addAttribute("userList", userService.findAllUsers());

        return "users/deleteuser";
    }

    @GetMapping("/edituser")
    public String takeUserForEdit(Model model) {
        model.addAttribute("userList", userService.findAllUsers());

        return "users/edituser";
    }

    @RequestMapping(value = "/edituser", method = RequestMethod.POST)
    public String editUser(@Valid String username, Model model) {
        User user = userService.findUserByName(username);
        model.addAttribute("userList", userService.findAllUsers());
        model.addAttribute("currentUser", user);

        return "users/edituser";
    }

    @RequestMapping(value = "/saveChanges", method = RequestMethod.POST)
    public String editUser(@ModelAttribute("editForm") User editForm, Model model) {
        if (!editForm.getUsername().equals("") && userService.updateFirstNameLastNamePatronymic(editForm))
            model.addAttribute("info", USER_UPDATED_MESSAGE + editForm.getUsername());
        else
            model.addAttribute("info", USER_NOT_SELECTED_MESSAGE);
        model.addAttribute("userList", userService.findAllUsers());

        return "users/edituser";
    }
}