package ServidorPrincipal.Service;

import Cliente.User;
import ServidorPrincipal.DAO.ExpenseDAO;
import ServidorPrincipal.DAO.GroupDAO;
import ServidorPrincipal.DAO.UserDAO;
import ServidorPrincipal.Group;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class GroupService {
    private GroupDAO groupDAO;
    private UserDAO userDAO;
    private ExpenseDAO expenseDAO;

    public GroupService(GroupDAO groupDAO, UserDAO userDAO, ExpenseDAO expenseDAO) {
        this.groupDAO = groupDAO;
        this.userDAO = userDAO;
        this.expenseDAO = expenseDAO;
    }

    public String createGroup(String groupName, String creator) {
        try {
            if (groupDAO.groupExists(groupName)) {
                return "[ERRO] O grupo já existe.";
            }

            // Criar o grupo
            boolean groupCreated = groupDAO.createGroup(groupName, creator);

            if (groupCreated) {
//                boolean userAdded = userDAO.addUserToGroup(creator.getName(), groupName);
//                if (userAdded) {
                return "Grupo criado com sucesso.";
            } else {
                return "[ERRO] Ocorreu um erro ao criar o grupo.";
            }
        } catch (SQLException e) {
            System.err.println("Erro ao criar grupo: " + e.getMessage());
            return "[ERRO] Ocorreu um erro ao criar o grupo.";
        }

    }

    public String inviteToGroup(String groupName, User userAuth, String emailInvite) {
        Group group = groupDAO.getGroupByName(groupName);
        if (group == null) {
            return "[ERRO] O grupo não existe.";
        }

        if (!groupDAO.isUserInGroup(groupName, userAuth.getName())) {
            return "[ERRO] O utilizador não pertence ao grupo.";
        }

        User convidado = userDAO.getUserByEmail(emailInvite);
        if (convidado == null) {
            return "[ERRO] O convidado não existe.";
        }

        boolean inviteSent = groupDAO.inviteToGroup(groupName, emailInvite);
        if (inviteSent) {
            return "Convite enviado com sucesso.";
        } else {
            return "[ERRO] Ocorreu um erro ao enviar o convite.";
        }

    }

    public List<String> listGroups(String userName) {
        return groupDAO.listGroups(userName);
    }

    public List<String> getInvitations(String userEmail) {
        return groupDAO.getInvitations(userEmail);
    }

    public String respondToInvitations(String groupName, String userName, String userEmail, boolean accept) {
        if (accept) {
            boolean added = groupDAO.addUserToGroup(groupName, userName);
            if (added) {
                groupDAO.updateInviteStatus(groupName, userEmail, "accepted");
                return "Convite aceito e você foi adicionado ao grupo.";
            }
            return "[ERRO] Não foi possível adicionar você ao grupo.";
        } else {
            groupDAO.updateInviteStatus(groupName, userEmail, "rejected");
            return "Convite recusado.";
        }
    }

    public String renameGroup(String oldName, String newName, String name) {
        try {
            if (!groupDAO.groupExists(oldName)) {
                return "[ERRO] O grupo não existe.";
            }


            if (!groupDAO.isUserInGroup(oldName, name)) {
                return "[ERRO] Você não pertence ao grupo.";
            }

            boolean renamed = groupDAO.renameGroup(oldName, newName);
            if (renamed) {
                return "Grupo renomeado com sucesso.";
            } else {
                return "[ERRO] Ocorreu um erro ao renomear o grupo.";
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String deleteGroup(String groupName, String name) {
        try {
            if (!groupDAO.groupExists(groupName)) {
                return "[ERRO] O grupo não existe.";
            }

            if (!groupDAO.isUserInGroup(groupName, name)) {
                return "[ERRO] Você não pertence ao grupo.";
            }
            if (expenseDAO.checkExpenseStatus(groupName)) {
                return "[ERRO] O grupo possui despesas pendentes.";
            }

            boolean deleted = groupDAO.deleteGroup(groupName);
            if (deleted) {
                return "Grupo deletado com sucesso.";
            } else {
                return "[ERRO] Ocorreu um erro ao deletar o grupo.";
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String leaveGroup(String groupName, String name) {
        try {
            if (!groupDAO.groupExists(groupName)) {
                return "[ERRO] O grupo não existe.";
            }

            if (!groupDAO.isUserInGroup(groupName, name)) {
                return "[ERRO] Você não pertence ao grupo.";
            }

            if (expenseDAO.checkExpenseStatus(groupName, name)) {
                return "[ERRO] Você possui despesas pendentes.";
            }

            boolean left = groupDAO.leaveGroup(groupName, name);
            if (left) {
                return "Você saiu do grupo com sucesso.";
            } else {
                return "[ERRO] Ocorreu um erro ao sair do grupo.";
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}


