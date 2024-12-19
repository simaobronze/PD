package ServidorPrincipal.Service;

import Cliente.User;
import ServidorPrincipal.DAO.UserDAO;

public class UserService {
    private UserDAO userDAO;

    public UserService(UserDAO userDAO){
        this.userDAO = userDAO;
    }

    public boolean register(User user){
        return userDAO.createUser(user);
    }

    public User login(String email, String password){
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Email e senha são obrigatórios.");
        }
        User user = userDAO.loginUser(email, password);

        return user;
    }

    public boolean updateUser(User user) {
        try {
            userDAO.updateUser(user);  // atualiza o usuário no banco
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao atualizar utilizador: " + e.getMessage());
            return false;
        }
    }

}
