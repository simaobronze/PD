package Cliente;

import Cliente.Networking.ClienteRequestHandler;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 7000;

        try (ClienteRequestHandler clienteRequestHandler = new ClienteRequestHandler(host, port)) {

            if (!clienteRequestHandler.isConnect()) {
                return;
            }

            User loggedUser = null;

            Scanner scanner = new Scanner(System.in);
            boolean running = true;
            boolean loggedIn = false;
            String email = "";
            String name = "";
            String phone = "";
            String password = "";

            System.out.println("Bem-vindo, escolha uma opção:");

            while (running) {
                if (!loggedIn) {
                    System.out.println("1 - Login");
                    System.out.println("2 - Register");
                    System.out.println("0 - Sair");

                    int option = scanner.nextInt();
                    scanner.nextLine();

                    switch (option) {
                        case 1: // Login
                            System.out.print("Email: ");
                            email = scanner.nextLine();
                            System.out.print("Senha: ");
                            password = scanner.nextLine();

                            // Faz o login e recebe o usuário autenticado
                            loggedUser = clienteRequestHandler.login(email, password);

                            if (loggedUser != null) {
                                loggedIn = true;
                                System.out.println("Bem-vindo, " + loggedUser.getName() + "!");
                            } else {
                                System.out.println("Falha no login. Verifique suas credenciais.");
                            }
                            break;

                        case 2: // Register
                            System.out.print("Email: ");
                            email = scanner.nextLine();
                            System.out.print("Nome: ");
                            name = scanner.nextLine();
                            System.out.print("Telefone: ");
                            phone = scanner.nextLine();
                            System.out.print("Senha: ");
                            password = scanner.nextLine();
                            System.out.println("Saldo inicial: ");
                            double saldo = scanner.nextDouble();
                            scanner.nextLine();

                            String registerResponse = clienteRequestHandler.register(email, name, phone, password, saldo);
                            System.out.println("Resposta do server: " + registerResponse);
                            break;

                        case 0: // Sair
                            System.out.println("A sair...");
                            running = false;
                            break;
                    }
                } else {
                    // Menu após login
                    System.out.println("Bem-vindo! Escolha uma opção:");
                    System.out.println("1 - Editar perfil");
                    System.out.println("2 - Criar novo grupo");
                    System.out.println("3 - Listar grupos");
                    System.out.println("4 - Convidar para grupo");
                    System.out.println("5 - Ver convites recebidos");
                    System.out.println("6 - Editar nome do grupo");
                    System.out.println("7 - Eliminar grupo");
                    System.out.println("8 - Inserir despesa");
                    System.out.println("9 - Sair do grupo");
                    System.out.println("10- Colmatar despesa");
                    System.out.println("11- Ver saldo");
                    System.out.println("12- Editar despesas");
                    System.out.println("13- Eliminar despesa");
                    System.out.println("14- Exportar para CSV lista despesas");
                    System.out.println("15- Listar pagamentos do User");
                    System.out.println("16- Listar pagamentos do Grupo");
                    System.out.println("17- Visualizar gasto total do grupo");
                    System.out.println("18- Apagar pagamento do grupo");

                    System.out.println("0 - Logout");

                    int option = scanner.nextInt();
                    scanner.nextLine();

                    switch (option) {
                        case 1: // Editar perfil
                            System.out.println("O que pretende editar? ");
                            System.out.println("1 - Nome");
                            System.out.println("2 - Telefone");
                            System.out.println("3 - Senha");
                            System.out.println("4 - Saldo");
                            System.out.println("0 - Voltar atrás");

                            int editOption = scanner.nextInt();
                            scanner.nextLine();

                            switch (editOption) {
                                case 1: // Editar nome
                                    System.out.print("Novo nome: ");
                                    String newName = scanner.nextLine();
                                    System.out.println(clienteRequestHandler.updateUser(newName, null, null, 0));
                                    break;
                                case 2: // Editar telefone
                                    System.out.print("Novo telefone: ");
                                    String newPhone = scanner.nextLine();
                                    System.out.println(clienteRequestHandler.updateUser(null, newPhone, null, 0));
                                    break;
                                case 3: // Editar senha
                                    System.out.print("Nova senha: ");
                                    String newPassword = scanner.nextLine();
                                    System.out.println(clienteRequestHandler.updateUser(null, null, newPassword, 0));
                                    break;
                                case 4: // Editar saldo
                                    System.out.print("Novo saldo: ");
                                    double newSaldo = scanner.nextDouble();
                                    scanner.nextLine();
                                    System.out.println(clienteRequestHandler.updateUser(null, null, null, newSaldo));
                                    break;
                                case 0: // Voltar atrás
                                    break;
                            }

                            break;
                        case 2: // Criar novo grupo
                            System.out.print("Nome do grupo: ");
                            String groupName = scanner.nextLine();
                            System.out.println("Nome do grupo: " + groupName);
                            String creator = loggedUser.getName();
                            System.out.println("Criador: " + creator);
                            String registerResponse = clienteRequestHandler.createGroup(groupName, creator);
                            System.out.println("Resposta do server: " + registerResponse);
                            break;
                        case 3: // Listar grupos
                            System.out.println("Grupos do utilizador " + loggedUser.getName() + ":");
                            String groupListResponse = clienteRequestHandler.listGroups(loggedUser.getName());

                            if (groupListResponse == null) {
                                System.out.println("Não foi possível listar os grupos.");
                            } else {
                                System.out.println(groupListResponse);
                            }

                            break;
                        case 4: // Convidar para grupo

                            System.out.print("Nome do grupo: ");
                            String groupNameInvite = scanner.nextLine();
                            System.out.print("Email do convidado: ");
                            String emailInvite = scanner.nextLine();

                            String inviteResponse = clienteRequestHandler.inviteToGroup(groupNameInvite, emailInvite);
                            System.out.println("Resposta do server: " + inviteResponse);
                            break;

                        case 5: // Ver convites recebidos

                            // Solicitar a lista de convites ao servidor
                            System.out.println("A carregar os seus convites...");
                            List<String> invitations = clienteRequestHandler.getInvitation();

                            if (invitations == null || invitations.isEmpty()) {
                                System.out.println("Não existem convites pendentes.");
                            } else {
                                System.out.println("Convites recebidos:");
                                for (int i = 0; i < invitations.size(); i++) {
                                    System.out.println((i + 1) + " - " + invitations.get(i));
                                }

                                System.out.println("Escolha uma opção:");
                                System.out.println("1 - Aceitar um convite");
                                System.out.println("2 - Recusar um convite");
                                System.out.println("0 - Voltar");

                                int invitationOption = scanner.nextInt();
                                scanner.nextLine();

                                switch (invitationOption) {
                                    case 1: // Aceitar um convite
                                        System.out.print("Digite o número do convite a aceitar: ");
                                        int invitationToAccept = scanner.nextInt();
                                        scanner.nextLine();
                                        if (invitationToAccept > 0 && invitationToAccept <= invitations.size()) {
                                            String acceptResponse = clienteRequestHandler.respondToInvitation(invitations.get(invitationToAccept - 1), true);
                                            System.out.println(acceptResponse);
                                        } else {
                                            System.out.println("[ERRO] Opção inválida.");
                                        }
                                        break;

                                    case 2: // Recusar um convite
                                        System.out.print("Digite o número do convite a recusar: ");
                                        int invitationToReject = scanner.nextInt();
                                        scanner.nextLine();
                                        if (invitationToReject > 0 && invitationToReject <= invitations.size()) {
                                            String rejectResponse = clienteRequestHandler.respondToInvitation(invitations.get(invitationToReject - 1), false);
                                            System.out.println(rejectResponse);
                                        } else {
                                            System.out.println("[ERRO] Opção inválida.");
                                        }
                                        break;

                                    case 0: // Voltar
                                        break;
                                }
                            }
                            break;
                        case 6: // Editar nome do grupo
                            System.out.println("Vai editar o nome do grupo");
                            System.out.println("Nome do grupo: ");
                            String groupNameEdit = scanner.nextLine();
                            System.out.println("Novo nome do grupo: ");
                            String newGroupName = scanner.nextLine();

                            String editGroupResponse = clienteRequestHandler.renameGroup(groupNameEdit, newGroupName);
                            System.out.println(editGroupResponse);
                            break;

                        case 7: // Eliminar grupo
                            System.out.println("Vai eliminar o grupo");
                            System.out.println("Nome do grupo: ");
                            String groupNameDelete = scanner.nextLine();

                            String deleteGroupResponse = clienteRequestHandler.deleteGroup(groupNameDelete, loggedUser.getName());
                            System.out.println(deleteGroupResponse);
                            break;

                        case 8: // Inserir despesa
                            System.out.println("Vai inserir uma despesa");
                            System.out.println("Nome do grupo: ");
                            String groupNameExpense = scanner.nextLine();
                            String receiverName = loggedUser.getName();
                            System.out.println("Nome do pagador: ");
                            String payerName = scanner.nextLine();
                            System.out.println("Valor da despesa: ");
                            double amount = scanner.nextDouble();
                            scanner.nextLine();
                            System.out.println("Descrição da despesa: ");
                            String desc = scanner.nextLine();

                            String expenseResponse = clienteRequestHandler.insertExpense(groupNameExpense, receiverName, payerName, amount, desc);
                            System.out.println(expenseResponse);
                            break;

                        case 9: // Sair do grupo
                            System.out.println("Vai sair do grupo");
                            System.out.println("Nome do grupo: ");
                            String groupNameLeave = scanner.nextLine();

                            String leaveGroupResponse = clienteRequestHandler.leaveGroup(groupNameLeave, loggedUser.getName());
                            System.out.println(leaveGroupResponse);
                            break;

                        case 10: // Colmatar despesa
                            System.out.println("Em que grupo deseja fazer o pagamento? ");
                            String groupNamePay = scanner.nextLine();
                            System.out.println("A quem deseja pagar? ");
                            String receiverNamePay = scanner.nextLine();
                            System.out.println("Quanto deseja pagar? ");
                            double value = scanner.nextDouble();
                            scanner.nextLine();

                            String payResponse = clienteRequestHandler.payDebt(groupNamePay, loggedUser.getName(), receiverNamePay, value);
                            System.out.println(payResponse);
                            break;

                        case 11:
                            System.out.println("Vai ver os saldos do grupo: ");
                            System.out.println("Nome do grupo: ");
                            String groupNameBalances = scanner.nextLine();
                            System.out.println("Total que os membros do grupo devem");
                            String balancesResponse = clienteRequestHandler.groupMembersBalances(groupNameBalances);

                            System.out.println("Total que você deve");
                            String totalDeve = clienteRequestHandler.totalDeve(groupNameBalances);

                            System.out.println("Total que deve a cada membro");
                            String deveMembro = clienteRequestHandler.deveMembro(groupNameBalances);

                            System.out.println("Total que tem a receber");
                            String totalRecebe = clienteRequestHandler.totalRecebe(groupNameBalances);

                            System.out.println("Total que tem a receber de cada membro");
                            String recebeMembro = clienteRequestHandler.recebeMembro(groupNameBalances);

                            break;

                        case 12:
                            System.out.println("Vai editar despesas");
                            System.out.println("Nome do grupo: ");
                            String groupNameEditExpense = scanner.nextLine();
                            System.out.println("ID da despesa: ");
                            int expenseId = scanner.nextInt();
                            scanner.nextLine();

                            System.out.println("O que pretende editar? ");
                            System.out.println("1- Receiver");
                            System.out.println("2- Payer");
                            System.out.println("3- Valor");
                            System.out.println("4- Descrição");
                            System.out.println("0- Voltar atrás");
                            int editExpenseOption = scanner.nextInt();
                            scanner.nextLine();
                            switch (editExpenseOption) {
                                case 1: // Editar receiver
                                    System.out.print("Novo receiver: ");
                                    String newReceiver = scanner.nextLine();
                                    String editReceiverResponse = clienteRequestHandler.editExpense(groupNameEditExpense, expenseId, newReceiver, null, 0, null);
                                    System.out.println(editReceiverResponse);
                                    break;

                                case 2: // Editar payer
                                    System.out.print("Novo payer: ");
                                    String newPayer = scanner.nextLine();
                                    String editPayerResponse = clienteRequestHandler.editExpense(groupNameEditExpense, expenseId, null, newPayer, 0, null);
                                    System.out.println(editPayerResponse);
                                    break;

                                case 3: // Editar valor
                                    System.out.print("Novo valor: ");
                                    double newValue = scanner.nextDouble();
                                    scanner.nextLine();
                                    String editValueResponse = clienteRequestHandler.editExpense(groupNameEditExpense, expenseId, null, null, newValue, null);
                                    System.out.println(editValueResponse);
                                    break;

                                case 4: // Editar descrição
                                    System.out.print("Nova descrição: ");
                                    String newDesc = scanner.nextLine();
                                    String editDescResponse = clienteRequestHandler.editExpense(groupNameEditExpense, expenseId, null, null, 0, newDesc);
                                    System.out.println(editDescResponse);
                                    break;

                                case 0: // Voltar atrás
                                    break;
                            }
                            break;
                        case 13: //eliminar despesa
                            System.out.println("Vai eliminar despesa");
                            System.out.println("Nome do grupo: ");
                            String groupNameDeleteExpense = scanner.nextLine();
                            System.out.println("ID da despesa: ");
                            int expenseIdDelete = scanner.nextInt();
                            scanner.nextLine();

                            String deleteExpenseResponse = clienteRequestHandler.deleteExpense(groupNameDeleteExpense, expenseIdDelete);
                            System.out.println(deleteExpenseResponse);

                            break;
                        case 14: //exportar para CSV
                            System.out.println("Vai exportar para CSV");
                            String exportResponse = clienteRequestHandler.exportToCSV(loggedUser.getName());
                            System.out.println(exportResponse);
                            break;

                        case 15: //listar pagamentos do user
                            System.out.println("A listar pagamentos de" + loggedUser.getName() +"..." );

                            String paymentListResponse = clienteRequestHandler.listPayments(loggedUser.getName());
                            System.out.println(paymentListResponse);
                            break;

                        case 16: //listar pagamentos do grupo
                            System.out.println("A listar pagamentos do grupo..." );
                            System.out.println("Nome do grupo: ");
                            String groupNamePayments = scanner.nextLine();
                            String paymentListResponseGroup = clienteRequestHandler.listPaymentsGroup(groupNamePayments);
                            System.out.println(paymentListResponseGroup);
                            break;
                        case 17: //visualizar gasto total do grupo
                            System.out.println("A visualizar gasto total do grupo...");
                            System.out.println("Nome do grupo: ");
                            String groupNameTotal = scanner.nextLine();
                            double totalGroupResponse = clienteRequestHandler.totalGroup(groupNameTotal);
                            System.out.println(totalGroupResponse);
                            break;
                        case 18: //apagar pagamento do grupo
                            System.out.println("A apagar pagamento do grupo...");
                            System.out.println("Nome do grupo: ");
                            String groupNameDeletePayment = scanner.nextLine();
                            System.out.println("ID do pagamento: ");
                            int paymentId = scanner.nextInt();
                            scanner.nextLine();
                            String deletePaymentResponse = clienteRequestHandler.deletePayment(groupNameDeletePayment, paymentId);
                            System.out.println(deletePaymentResponse);
                            break;
                        case 0: // Sair
                            System.out.println("A sair...");
                            loggedIn = false;
                            loggedUser = null;
                            running = false;
                            break;

                    }

                }
            }

        } catch (Exception e) {
            System.err.println(("Erro ao ligar ao servidor: " + e.getMessage()));
        }
    }
}
