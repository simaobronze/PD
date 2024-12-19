package com.meta2tp.services;

import com.meta2tp.models.Expense;
import com.meta2tp.models.Group;

import java.util.List;

public class GroupService {
    public List<Group> getAllGroups() {
        // Lógica para listar todos os grupos
        return List.of(new Group()); // Exemplo de retorno
    }

    public List<Expense> getExpensesByGroup(Long groupId) {
        // Lógica para listar despesas de um grupo
        return List.of(new Expense());
    }
}
