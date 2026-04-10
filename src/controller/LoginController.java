package controller;

import dao.UserDAO;
import model.User;

public class LoginController {

    private UserDAO userDAO;

    public LoginController() {
        userDAO = new UserDAO();
    }

    public boolean register(String name, String email, String password, String role) {

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);

        return userDAO.registerUser(user);
    }

    public String getLastRegisterError() {
        return userDAO.getLastRegisterError();
    }

    public User login(String email, String password) {
        return userDAO.login(email, password);
    }
}
