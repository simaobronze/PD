package Tests;

import ServidorPrincipal.DAO.DatabaseConnection;
import ServidorPrincipal.DAO.GroupDAO;

public class TestAddUserToGroup {

    public static void main(String[] args) {
        GroupDAO groupDAO = new GroupDAO();


        // Teste 1: Adicionar um usuário a um grupo existente
        System.out.println("Teste 1: Adicionar usuário 'user3@example.com' ao grupo 'Fig-Foz'");
        boolean result1 = groupDAO.addUserToGroup("Fig-Foz", "user3@example.com");
        System.out.println("Resultado: " + (result1 ? "Sucesso" : "Falha"));

        // Teste 2: Adicionar um usuário a um grupo vazio
        System.out.println("Teste 2: Adicionar usuário 'user4@example.com' ao grupo 'Coimbra'");
        boolean result2 = groupDAO.addUserToGroup("Coimbra", "user4@example.com");
        System.out.println("Resultado: " + (result2 ? "Sucesso" : "Falha"));

        // Teste 3: Adicionar um usuário a um grupo inexistente
        System.out.println("Teste 3: Adicionar usuário 'user5@example.com' ao grupo 'Porto'");
        boolean result3 = groupDAO.addUserToGroup("Porto", "user5@example.com");
        System.out.println("Resultado: " + (result3 ? "Sucesso" : "Falha"));

        // Teste 4: Adicionar um usuário que já está no grupo
        System.out.println("Teste 4: Adicionar usuário 'user3@example.com' novamente ao grupo 'Fig-Foz'");
        boolean result4 = groupDAO.addUserToGroup("Fig-Foz", "user3@example.com");
        System.out.println("Resultado: " + (result4 ? "Sucesso" : "Falha"));
    }
}

