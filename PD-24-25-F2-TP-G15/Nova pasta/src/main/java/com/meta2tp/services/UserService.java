package com.meta2tp.services;

import com.meta2tp.models.User;

public class UserService {
    // Simulação de um repositório de usuários
    public User register(User user) {
        // Lógica para registrar o usuário
        return user;
    }

    public String authenticate(User user) {
        // Lógica de autenticação
        return "Authenticated";
    }
}
