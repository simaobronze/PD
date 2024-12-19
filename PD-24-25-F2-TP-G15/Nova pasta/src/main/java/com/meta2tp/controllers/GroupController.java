package com.meta2tp.controllers;

import com.meta2tp.models.Expense;
import com.meta2tp.models.Group;
import com.meta2tp.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    // Endpoint para listar todos os grupos
    @GetMapping
    public List<Group> getAllGroups() {
        return groupService.getAllGroups();
    }

    // Endpoint para listar as despesas de um grupo específico
    @GetMapping("/{groupId}/expenses")
    public List<Expense> getExpensesByGroup(@PathVariable Long groupId) {
        return groupService.getExpensesByGroup(groupId);
    }
}