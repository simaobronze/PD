package ServidorPrincipal.Networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import ServidorPrincipal.DAO.UserDAO;
import ServidorPrincipal.Service.ExpenseService;
import ServidorPrincipal.Service.GroupService;
import ServidorPrincipal.Service.PaymentService;
import ServidorPrincipal.Service.UserService;
import Cliente.User;
import ServidorPrincipal.Session.SessionManager;

public class ServerRequestHandler extends Thread {
    private Socket cliente;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private UserService userService;
    private GroupService groupService;
    private ExpenseService expenseService;
    private PaymentService paymentService;
    private User userAuth;
    private UserDAO userDAO;

    public ServerRequestHandler(Socket cliente, UserService userService, GroupService groupService, ExpenseService expenseService, PaymentService paymentService) {
        this.cliente = cliente;
        this.userService = userService;
        this.groupService = groupService;
        this.expenseService = expenseService;
        this.paymentService = paymentService;
    }

    @Override
    public void run() {
        System.out.println("Cliente conectado com a thread (" + this.getId() + "): " +
                cliente.getInetAddress());

        try {
            output = new ObjectOutputStream(cliente.getOutputStream());
            input = new ObjectInputStream(cliente.getInputStream());

            boolean isConnected = true;

            while (isConnected) {
                // Ler o tipo de comando
                String option = (String) input.readObject();
                System.out.println("Recebido comando: " + option);

                switch (option) {
                    case "login":
                        handleLogin();
                        break;
                    case "register":
                        handleRegister();
                        break;
                    case "updateUser":
                        handleEditUser();
                        break;
                    case "createGroup":
                        handleCreateGroup();
                        break;
                    case "inviteToGroup":
                        handleInviteToGroup();
                        break;
                    case "listGroups":
                        handleListGroups();
                        break;
                    case "logout":  // Um comando "logout" ou "encerrar" encerra o loop
                        isConnected = false;
                        output.writeObject("Desconectando...");
                        output.flush();
                        break;
                    case "getInvitations":
                        handleGetInvitations();
                        break;
                    case "respondToInvitation":
                        handleRespondToInvitations();
                        break;
                    case "renameGroup":
                        handleRenameGroup();
                        break;
                    case "deleteGroup":
                        handleDeleteGroup();
                        break;

                    case "insertExpense":
                        handleInsertExpense();
                        break;

                    case "leaveGroup":
                        handleLeaveGroup();
                        break;
                    case "payDebt":
                        handlePayDebt();
                        break;

                    case "editExpense":
                        handleEditExpense();
                        break;
                    case "deleteExpense":
                        handleDeleteExpense();
                        break;
                    case "exportToCSV":
                        handleExportToCSV();
                        break;

                    case "listUserPayments" :
                        handleUserListPayments();
                        break;
                    case "listGroupPayments" :
                        handleGroupListPayments();
                        break;
                    case "totalGroup":
                        handleGastoTotalGrupo();
                        break;
                    case "deletePayment":
                        handleDeletePayment();
                        break;
                    case "groupMembersBalances":
                        handleGroupMembersBalances();
                        break;
                    case "totalDeve":
                        handleGroupDeve();
                        break;
                    case "deveMembro" :
                        handleDeveMembro();
                        break;
                    case "totalRecebe" :
                        handleGroupRecebe();
                        break;
                    case "recebeMembro" :
                        handleRecebeMembro();
                        break;

                    default:
                        output.writeObject("Comando inválido");
                        output.flush();
                }
            }

        } catch (Exception e) {
            System.err.println("Erro ao processar a solicitação do cliente: " + e.getMessage());
        } finally {
            try {
                System.out.println("Fechando o socket do cliente...");
                cliente.close();
                System.out.println("Socket do cliente fechado.");
            } catch (Exception e) {
                System.err.println("Erro ao fechar o socket do cliente: " + e.getMessage());
            }
        }
    }

    private void handleRecebeMembro() {
        if (userAuth == null) {
            try {
                output.writeObject("[ERRO] Não está autenticado.");
                return;
            } catch (IOException e) {
                System.err.println("Erro ao enviar resposta ao cliente: " + e.getMessage());
            }
        }

        try {
            String groupName = (String) input.readObject();
            Map <String, Map<String,Double>> totalRecebeMembro = expenseService.totalRecebeMembro(userAuth.getName(), groupName);

            if (totalRecebeMembro.isEmpty()) {
                output.writeObject("Não existem créditos para si.");
                return;
            } else {
                output.writeObject(totalRecebeMembro);
            }
        } catch (Exception e) {
            System.err.println("Erro listar os créditos do membro: " + e.getMessage());
            try {
                output.writeObject("[ERRO] Não foi possível calcular os créditos do membro.");
            } catch (IOException ex) {
                System.err.println("Erro ao enviar resposta ao cliente: " + ex.getMessage());
            }
        }
    }

    private void handleGroupRecebe() {
        if (userAuth == null) {
            try {
                output.writeObject("[ERRO] Não está autenticado.");
                return;
            } catch (IOException e) {
                System.err.println("Erro ao enviar resposta ao cliente: " + e.getMessage());
            }
        }
        try {
            String groupName = (String) input.readObject();
            Map<String, Double> recebe = expenseService.totalRecebe(groupName);

            if (recebe.isEmpty()) {
                output.writeObject("Não existem créditos no grupo.");
                return;
            } else {
                output.writeObject(recebe);
            }
        } catch (Exception e) {
            System.err.println("Erro listar os créditos do grupo: " + e.getMessage());
            try {
                output.writeObject("[ERRO] Não foi possível calcular os créditos do grupo.");
            } catch (IOException ex) {
                System.err.println("Erro ao enviar resposta ao cliente: " + ex.getMessage());
            }
        }
    }

    private void handleDeveMembro() {
        if (userAuth == null) {
            try {
                output.writeObject("[ERRO] Não está autenticado.");
                return;
            } catch (IOException e) {
                System.err.println("Erro ao enviar resposta ao cliente: " + e.getMessage());
            }
        }

        try {
            String groupName = (String) input.readObject();
            Map <String, Map<String,Double>> totalDeveMembro = expenseService.totalDeveMembro(userAuth.getName(),groupName);
            if (totalDeveMembro.isEmpty()){
                output.writeObject("Ninguém tem dívidas para consigo!");
                return;
            }else {
                output.writeObject(totalDeveMembro);
            }
        } catch (Exception e) {
            System.err.println("Erro ao calcular o valor que o membro deve: " + e.getMessage());
            try {
                output.writeObject("[ERRO] Não foi possível calcular o valor que o membro deve.");
            } catch (IOException ex) {
                System.err.println("Erro ao enviar resposta ao cliente: " + ex.getMessage());
            }
        }
    }

    private void handleGroupDeve() {
        if (userAuth == null) {
            try {
                output.writeObject("[ERRO] Não está autenticado.");
                return;
            } catch (IOException e) {
                System.err.println("Erro ao enviar resposta ao cliente: " + e.getMessage());
            }
        }

        try {
            String groupName = (String) input.readObject();
            Map<String, Double> deve = expenseService.totalDeve(groupName);

            if (deve.isEmpty()) {
                output.writeObject("Não existem dívidas no grupo.");
                return;
            } else {
                output.writeObject(deve);
            }
        } catch (Exception e) {
            System.err.println("Erro listar as dívidas do grupo: " + e.getMessage());
            try {
                output.writeObject("[ERRO] Não foi possível calcular as dívidas do grupo.");
            } catch (IOException ex) {
                System.err.println("Erro ao enviar resposta ao cliente: " + ex.getMessage());
            }
        }
    }

    private void handleGroupMembersBalances() {
        if (userAuth == null) {
            try {
                output.writeObject("[ERRO] Não está autenticado.");
                return;
            } catch (IOException e) {
                System.err.println("Erro ao enviar resposta ao cliente: " + e.getMessage());
            }
        }

        try {
            String groupName = (String) input.readObject();
            Map<String, Double> gastos = paymentService.gastosPorMembro(groupName);

            if (gastos.isEmpty()) {
                output.writeObject("Não existem gastos no grupo.");
                return;
            } else {
                output.writeObject(gastos);
            }
        } catch (Exception e) {
            System.err.println("Erro ao calcular os gastos do grupo: " + e.getMessage());
            try {
                output.writeObject("[ERRO] Não foi possível calcular os gastos do grupo.");
            } catch (IOException ex) {
                System.err.println("Erro ao enviar resposta ao cliente: " + ex.getMessage());
            }
        }
    }

    private void handleDeletePayment() {
        try {
            String groupName = (String) input.readObject();
            int id = (int) input.readObject();

            String response = paymentService.deletePayment(groupName, id);
            output.writeObject(response); // Envia a resposta ao cliente
        } catch (Exception e) {
            System.err.println("Erro ao apagar o pagamento: " + e.getMessage());
            try {
                output.writeObject("[ERRO] Não foi possível apagar o pagamento.");
            } catch (IOException ex) {
                System.err.println("Erro ao enviar resposta ao cliente: " + ex.getMessage());
            }
        }
    }

    private void handleGastoTotalGrupo() {
        if (userAuth == null) {
            try {
                output.writeObject("[ERRO] Não está autenticado.");
                return;
            } catch (IOException e) {
                System.err.println("Erro ao enviar resposta ao cliente: " + e.getMessage());
            }
        }

        try {
            String groupName = (String) input.readObject();
            double total = paymentService.totalGroup(groupName);

            output.writeObject(total);
        } catch (Exception e) {
            System.err.println("Erro ao calcular o gasto total do grupo: " + e.getMessage());
            try {
                output.writeObject("[ERRO] Não foi possível calcular o gasto total do grupo.");
            } catch (IOException ex) {
                System.err.println("Erro ao enviar resposta ao cliente: " + ex.getMessage());
            }
        }
    }

    private void handleGroupListPayments() {
        if (userAuth == null) {
            try {
                output.writeObject("[ERRO] Não está autenticado.");
                return;
            } catch (IOException e) {
                System.err.println("Erro ao enviar resposta ao cliente: " + e.getMessage());
            }
        }

        try {
            String groupName = (String) input.readObject();
            List<String> payments = paymentService.listGroupPayments(groupName);

            if(payments.isEmpty()){
                output.writeObject("Não existem pagamentos feitos!");
                return;
            } else {
                String paymentList = String.join(", ", payments);
                output.writeObject(paymentList);}

        } catch (Exception e) {
            System.err.println("Erro ao listar pagamentos: " + e.getMessage());
            try {
                output.writeObject("[ERRO] Não foi possível listar os pagamentos.");
            } catch (IOException ex) {
                System.err.println("Erro ao enviar resposta ao cliente: " + ex.getMessage());
            }
        }
    }

    private void handleUserListPayments() {
        if (userAuth == null) {
            try {
                output.writeObject("[ERRO] Não está autenticado.");
                return;
            } catch (IOException e) {
                System.err.println("Erro ao enviar resposta ao cliente: " + e.getMessage());
            }
        }

        try {
            input.readObject();
            List<String> payments = paymentService.listUserPayments(userAuth.getName());

            if(payments.isEmpty()){
                output.writeObject("Não existem pagamentos feitos!");
                return;
            } else {
                String paymentList = String.join(", ", payments);
                output.writeObject(paymentList);}

        } catch (Exception e) {
            System.err.println("Erro ao listar pagamentos: " + e.getMessage());
            try {
                output.writeObject("[ERRO] Não foi possível listar os pagamentos.");
            } catch (IOException ex) {
                System.err.println("Erro ao enviar resposta ao cliente: " + ex.getMessage());
            }
        }
    }

    private void handleExportToCSV() {
        try {
            if (userAuth == null) {
                output.writeObject("[ERRO] Não está autenticado.");
                return;
            }

        String userName = userAuth.getName();

        String filePath = expenseService.exportToCSV(userName);

        output.writeObject(filePath);
        } catch (IOException e) {
            System.err.println("Erro ao enviar resposta ao cliente: " + e.getMessage());
        }
    }

    private void handleLeaveGroup() {
        if (userAuth == null) {
            try {
                output.writeObject("[ERRO] Não está autenticado.");
                return;
            } catch (IOException e) {
                System.err.println("Erro ao enviar resposta ao cliente: " + e.getMessage());
            }
        }

        try {
            String groupName = (String) input.readObject();

            String response = groupService.leaveGroup(groupName, userAuth.getName());
            output.writeObject(response); // Envia a resposta ao cliente
        } catch (Exception e) {
            System.err.println("Erro ao sair do grupo: " + e.getMessage());
            try {
                output.writeObject("[ERRO] Não foi possível sair do grupo.");
            } catch (IOException ex) {
                System.err.println("Erro ao enviar resposta ao cliente: " + ex.getMessage());
            }
        }
    }

    private void handleDeleteGroup() {
        if (userAuth == null) {
            try {
                output.writeObject("[ERRO] Não está autenticado.");
                return;
            } catch (IOException e) {
                System.err.println("Erro ao enviar resposta ao cliente: " + e.getMessage());
            }
        }

        try {
            String groupName = (String) input.readObject();

            String response = groupService.deleteGroup(groupName, userAuth.getName());
            output.writeObject(response); // Envia a resposta ao cliente
        } catch (Exception e) {
            System.err.println("Erro ao deletar o grupo: " + e.getMessage());
            try {
                output.writeObject("[ERRO] Não foi possível deletar o grupo.");
            } catch (IOException ex) {
                System.err.println("Erro ao enviar resposta ao cliente: " + ex.getMessage());
            }
        }
    }

    private void handleRenameGroup() {
        if (userAuth == null) {
            try {
                output.writeObject("[ERRO] Não está autenticado.");
                return;
            } catch (IOException e) {
                System.err.println("Erro ao enviar resposta ao cliente: " + e.getMessage());
            }
        }

        try {
            String oldName = (String) input.readObject();
            String newName = (String) input.readObject();

            String response = groupService.renameGroup(oldName, newName, userAuth.getName());
            output.writeObject(response); // Envia a resposta ao cliente
        } catch (Exception e) {
            System.err.println("Erro ao renomear o grupo: " + e.getMessage());
            try {
                output.writeObject("[ERRO] Não foi possível renomear o grupo.");
            } catch (IOException ex) {
                System.err.println("Erro ao enviar resposta ao cliente: " + ex.getMessage());
            }
        }
    }

    private void handleGetInvitations() {
        if (userAuth == null) {
            try {
                output.writeObject("[ERRO] Não está autenticado.");
                return;
            } catch (IOException e) {
                System.err.println("Erro ao enviar resposta ao cliente: " + e.getMessage());
            }
        }

        try {
            List<String> invitations = groupService.getInvitations(userAuth.getEmail());
            output.writeObject(invitations); // Envia a lista de convites ao cliente
        } catch (Exception e) {
            System.err.println("Erro ao carregar convites: " + e.getMessage());
            try {
                output.writeObject("[ERRO] Não foi possível carregar os convites.");
            } catch (IOException ex) {
                System.err.println("Erro ao enviar resposta ao cliente: " + ex.getMessage());
            }
        }
    }

    private void handleRespondToInvitations() {
        if (userAuth == null) {
            try {
                output.writeObject("[ERRO] Não está autenticado.");
                return;
            } catch (IOException e) {
                System.err.println("Erro ao enviar resposta ao cliente: " + e.getMessage());
            }
        }

        try {
            String groupName = (String) input.readObject();
            boolean accept = (boolean) input.readObject();

            String response = groupService.respondToInvitations(groupName, userAuth.getName(), userAuth.getEmail(), accept);
            output.writeObject(response); // Envia a resposta ao cliente
        } catch (Exception e) {
            System.err.println("Erro ao responder ao convite: " + e.getMessage());
            try {
                output.writeObject("[ERRO] Não foi possível processar sua resposta.");
            } catch (IOException ex) {
                System.err.println("Erro ao enviar resposta ao cliente: " + ex.getMessage());
            }
        }
    }

    private void handleListGroups() {
        if (userAuth == null) {
            try {
                output.writeObject("[ERRO] Não está autenticado");
                return;
            } catch (IOException e) {
                System.err.println("Erro ao enviar a resposta ao cliente: " + e.getMessage());
            }
        }

        try {
            List<String> groupNames = groupService.listGroups(userAuth.getName());

            if (groupNames.isEmpty()) {
                output.writeObject("Não existem grupos");
                return;
            } else {
                String groupList = String.join(", ", groupNames);
                output.writeObject(groupList);
            }
        } catch (Exception e) {
            System.err.println("Erro ao listar os grupos: " + e.getMessage());
        }
    }

    private void handleLogin() {
        try {
            System.out.println("Aguardando login...");
            String email = (String) input.readObject();
            String password = (String) input.readObject();

            System.out.println("Email: " + email);
            System.out.println("Password: " + password);

            User user = userService.login(email, password);
            if (user != null) {
                userAuth = user;

                String sessionId = cliente.getInetAddress().toString();

                SessionManager.addToSession(sessionId, user);

                System.out.println("Login bem-sucedido para " + userAuth.getEmail());
                output.writeObject("Login bem-sucedido");
                output.writeObject(user);

            } else {
                output.writeObject("Credenciais inválidas");

            }

        } catch (Exception e) {
            System.err.println("Erro ao realizar o login: " + e.getMessage());

            try {
                output.writeObject("Erro ao processar o login");

            } catch (IOException ex) {
                System.err.println("Erro ao enviar a resposta ao cliente: " + ex.getMessage());
            }
        }
    }

    private void handleRegister() {
        try {
            String name = (String) input.readObject();
            String phone = (String) input.readObject();
            String email = (String) input.readObject();
            String password = (String) input.readObject();
            double saldo = (double) input.readObject();

            User user = new User(name, phone, email, password, saldo);

            boolean sucesso = userService.register(user);

            if (sucesso) {
                output.writeObject("Registro bem-sucedido");
            } else {
                output.writeObject("Erro ao processar o registro");

            }
        } catch (Exception e) {
            System.err.println("Erro ao realizar o registro: " + e.getMessage());

            try {
                output.writeObject("Erro ao processar o registro");

            } catch (IOException ex) {
                System.err.println("Erro ao enviar a resposta ao cliente: " + ex.getMessage());

            }
        }

    }

    private void handleEditUser() {

        String sessionId = cliente.getInetAddress().toString();

        if (!SessionManager.isUserAuthenticated(sessionId)) {
            try {
                output.writeObject("Não está autenticado");
                return;
            } catch (IOException e) {
                System.err.println("Erro ao enviar a resposta ao cliente: " + e.getMessage());
            }
        }

        try {

            String newName = (String) input.readObject();
            String newPhone = (String) input.readObject();
            String newPassword = (String) input.readObject();
            double newSaldo = (double) input.readObject();

            System.out.println("Nome recebido: " + newName);
            System.out.println("Telefone recebido: " + newPhone);
            System.out.println("Senha recebida: " + newPassword);
            System.out.println("Saldo recebido: " + newSaldo);

            User userAuth = SessionManager.getFromSession(sessionId);

            if (newName != null) userAuth.setName(newName);
            if (newPhone != null) userAuth.setPhone(newPhone);
            if (newPassword != null) userAuth.setPassword(newPassword);
            if (newSaldo > 0) {
                userAuth.setSaldo(newSaldo);
            }

            boolean updateResponse = userService.updateUser(userAuth);

            if (updateResponse) {
                output.writeObject("Utilizador atualizado com sucesso");
            } else {
                output.writeObject("Erro ao atualizar o utilizador");
            }

        } catch (Exception e) {
            System.err.println("Erro ao editar o utilizador: " + e.getMessage());
        }
    }

    private void handleCreateGroup() {
        if (userAuth == null) {
            try {
                output.writeObject("Não está autenticado");
                return;
            } catch (IOException e) {
                System.err.println("Erro ao enviar a resposta ao cliente: " + e.getMessage());
            }
        }

        try {
            String groupName = (String) input.readObject();
            System.out.println("Nome do grupo recebido: " + groupName);
            String creator = (String) input.readObject();
            System.out.println("Criador do grupo: " + creator);
            if (groupName == null || groupName.isEmpty()) {
                output.writeObject("Nome do grupo inválido");
                return;
            }

            String createGroupResponse = groupService.createGroup(groupName, creator);
            System.out.println("Resposta do serviço: " + createGroupResponse);

            output.writeObject(createGroupResponse);

        } catch (Exception e) {
            System.err.println("Erro ao criar o grupo: " + e.getMessage());
        }
    }

    private void handleInviteToGroup() {
        if (userAuth == null) {
            try {
                output.writeObject("[ERRO] Não está autenticado");
                return;
            } catch (IOException e) {
                System.err.println("Erro ao enviar a resposta ao cliente: " + e.getMessage());
            }
        }

        try {
            String groupName = (String) input.readObject();
            System.out.println("Nome do grupo recebido: " + groupName);
            String emailInvite = (String) input.readObject();
            System.out.println("Email do convidado: " + emailInvite);

            if (groupName == null || groupName.isEmpty() || emailInvite == null || emailInvite.isEmpty()) {
                output.writeObject("Dados inválidos");
                return;
            }

            String inviteResponse = groupService.inviteToGroup(groupName, userAuth, emailInvite);
            output.writeObject(inviteResponse);

        } catch (Exception e) {
            System.err.println("Erro ao convidar para o grupo: " + e.getMessage());
        }
    }

    private void handleEditExpense() {
        if (userAuth == null) {
            try {
                output.writeObject("[ERRO] Não está autenticado.");
                return;
            } catch (IOException e) {
                System.err.println("Erro ao enviar resposta ao cliente: " + e.getMessage());
            }
        }

        try {
            String groupName = (String) input.readObject();
            int id = (int) input.readObject();
            String receiverName = (String) input.readObject();
            String payerName = (String) input.readObject();
            double amount = (double) input.readObject();
            String description = (String) input.readObject();
            String response = null;

            if (receiverName != null) {
                response = expenseService.editExpenseReceiver(groupName, id, receiverName);
            } else if (payerName != null) {
                response = expenseService.editExpensePayer(groupName, id, payerName);
            } else if (amount != 0) {
                response = expenseService.editExpenseAmount(groupName, id, amount);
            } else if (description != null) {
                response = expenseService.editExpenseDescription(groupName, id, description);
            }

            output.writeObject(response); // Envia a resposta ao cliente
        } catch (Exception e) {
            System.err.println("Erro ao editar despesa: " + e.getMessage());
            try {
                output.writeObject("[ERRO] Não foi possível editar a despesa.");
            } catch (IOException ex) {
                System.err.println("Erro ao enviar resposta ao cliente: " + ex.getMessage());
            }
        }
    }

    private void handlePayDebt() {
        if (userAuth == null) {
            try {
                output.writeObject("[ERRO] Não está autenticado.");
                return;
            } catch (IOException e) {
                System.err.println("Erro ao enviar resposta ao cliente: " + e.getMessage());
            }
        }

        try {
            String groupName = (String) input.readObject();
            String receiverName = (String) input.readObject();
            String payerName = (String) input.readObject();
            double amount = (double) input.readObject();

            String response = expenseService.payDebt(groupName, receiverName, payerName, amount);
            output.writeObject(response); // Envia a resposta ao cliente
        } catch (Exception e) {
            System.err.println("Erro ao pagar dívida: " + e.getMessage());
            try {
                output.writeObject("[ERRO] Não foi possível pagar a dívida.");
            } catch (IOException ex) {
                System.err.println("Erro ao enviar resposta ao cliente: " + ex.getMessage());
            }
        }
    }

    private void handleInsertExpense() {
        if (userAuth == null) {
            try {
                output.writeObject("[ERRO] Não está autenticado.");
                return;
            } catch (IOException e) {
                System.err.println("Erro ao enviar resposta ao cliente: " + e.getMessage());
            }
        }

        try {
            String groupName = (String) input.readObject();
            String receiverName = (String) input.readObject();
            String payerName = (String) input.readObject();
            double amount = (double) input.readObject();
            String description = (String) input.readObject();

            String response = expenseService.insertExpense(groupName, receiverName, payerName, amount, description);
            output.writeObject(response); // Envia a resposta ao cliente
        } catch (Exception e) {
            System.err.println("Erro ao inserir despesa: " + e.getMessage());
            try {
                output.writeObject("[ERRO] Não foi possível inserir a despesa.");
            } catch (IOException ex) {
                System.err.println("Erro ao enviar resposta ao cliente: " + ex.getMessage());
            }
        }
    }

    private void handleDeleteExpense() {
        try {
            String groupName = (String) input.readObject();
            int id = (int) input.readObject();

            String response = expenseService.deleteExpense(groupName, id);
            output.writeObject(response); // Envia a resposta ao cliente
        } catch (Exception e) {
            System.err.println("Erro ao deletar despesa: " + e.getMessage());
            try {
                output.writeObject("[ERRO] Não foi possível deletar a despesa.");
            } catch (IOException ex) {
                System.err.println("Erro ao enviar resposta ao cliente: " + ex.getMessage());
            }
        }
    }

}
