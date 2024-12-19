package Cliente.Networking;

import Cliente.User;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class ClienteRequestHandler implements Closeable {
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public ClienteRequestHandler(String host, int port) {
        try {
            this.socket = new Socket(host, port);
            this.output = new ObjectOutputStream(socket.getOutputStream());
            this.input = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            System.err.println("Erro ao conectar ao servidor: " + e.getMessage());
        }

    }

    public User login(String email, String password) {
        try {
            output.writeObject("login");
            output.writeObject(email);
            output.writeObject(password);

            // Primeiro, lê a resposta do servidor (sucesso ou erro)
            String response = (String) input.readObject();
            System.out.println("Resposta do servidor: " + response);

            if (response.equals("Login bem-sucedido")) {
                // Depois do sucesso, recebe o objeto User
                return (User) input.readObject(); // Retorna o usuário autenticado
            }
        } catch (Exception e) {
            System.err.println("Erro ao fazer login: " + e.getMessage());
        }
        return null; // Retorna null em caso de falha
    }

    public String register(String email, String name, String phone, String password, double saldo) {
        if (!isConnect()) {
            return "[ERRO] Não foi possível conectar ao servidor.";
        }
        try {
            output.writeObject("register");
            output.writeObject(email);
            output.writeObject(name);
            output.writeObject(phone);
            output.writeObject(password);
            output.writeObject(saldo);

            return (String) input.readObject();
        } catch (Exception e) {
            System.err.println("Erro ao registar utilizador: " + e.getMessage());
            return "[ERRO]: " + e.getMessage();
        }
    }

    public String updateUser(String name, String phone, String password, double saldo) throws IOException, ClassNotFoundException {
        output.writeObject("updateUser"); // Ação a ser realizada

        // Enviando os dados de forma adequada:
        output.writeObject(name);  // Envia o nome
        output.writeObject(phone); // Envia o telefone
        output.writeObject(password); // Envia a senha
        output.writeObject(saldo); // Envia o saldo

        // Agora, aguardamos a resposta do servidor
        return (String) input.readObject();
    }

    public String createGroup(String groupName, String creator) {
        try {
            output.writeObject("createGroup");
            output.writeObject(groupName);
            output.writeObject(creator);

            // Aguarda a resposta do servidor
            return (String) input.readObject();
        } catch (Exception e) {
            System.err.println("Erro ao criar grupo: " + e.getMessage());
            return "Erro ao criar grupo";
        }
    }

    @Override
    public void close() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (socket != null) socket.close();
        } catch (Exception e) {
            System.err.println("Erro ao fechar o socket: " + e.getMessage());
        }
    }

    public boolean isConnect() {
        return socket.isConnected(); // Verifica se o socket está conectado
        // 1. Se o socket está conectado, retorna true
        // 2. Se o socket não está conectado, retorna false
    }

    public String inviteToGroup(String groupName, String inviteeEmail) {
        try {
            output.writeObject("inviteToGroup"); // Ação no servidor
            output.writeObject(groupName);
            output.writeObject(inviteeEmail);

            // Lê a resposta do servidor
            return (String) input.readObject();
        } catch (Exception e) {
            System.err.println("Erro ao enviar convite: " + e.getMessage());
            return "Erro ao enviar convite";
        }
    }

    public String listGroups(String name) {
        try {
            output.writeObject("listGroups");

            return (String) input.readObject();
        } catch (Exception e) {
            System.err.println("Erro ao listar grupos: " + e.getMessage());
            return "Erro ao listar grupos";
        }
    }

    public List<String> getInvitation() {
        try {
            output.writeObject("getInvitations"); // Ação no servidor

            Object response = input.readObject(); // Resposta do servidor
            if (response instanceof List) {
                return (List<String>) response; // Lista de convites recebida do servidor
            } else {
                System.err.println("[ERRO] Resposta inesperada do servidor: " + response);
                return null;
            }

        } catch (Exception e) {
            System.err.println("[ERRO] Não foi possível carregar os convites: " + e.getMessage());
            return null;
        }
    }

    public String respondToInvitation(String groupName, boolean accept) {
        try {
            output.writeObject("respondToInvitation"); // Ação no servidor
            output.writeObject(groupName);
            output.writeObject(accept); // Envia true para aceitar, false para recusar

            return (String) input.readObject(); // Resposta do servidor
        } catch (Exception e) {
            System.err.println("[ERRO] Não foi possível responder ao convite: " + e.getMessage());
            return "[ERRO] Falha ao responder ao convite.";
        }
    }

    public String renameGroup(String groupNameEdit, String newGroupName) {
        try {
            output.writeObject("renameGroup");
            output.writeObject(groupNameEdit);
            output.writeObject(newGroupName);

            return (String) input.readObject();
        } catch (Exception e) {
            System.err.println("Erro ao editar grupo: " + e.getMessage());
            return "Erro ao editar grupo";
        }
    }

    public String insertExpense(String groupNameExpense, String receiverName, String payerName, double value, String description) {
        try {
            output.writeObject("insertExpense");
            output.writeObject(groupNameExpense);
            output.writeObject(receiverName);
            output.writeObject(payerName);
            output.writeObject(value);
            output.writeObject(description);

            return (String) input.readObject();
        } catch (Exception e) {
            System.err.println("Erro ao inserir despesa: " + e.getMessage());
            return "Erro ao inserir despesa";
        }
    }

    public String deleteGroup(String groupNameDelete, String name) {
        try {
            output.writeObject("deleteGroup");
            output.writeObject(groupNameDelete);
            output.writeObject(name);

            return (String) input.readObject();
        } catch (Exception e) {
            System.err.println("Erro ao deletar grupo: " + e.getMessage());
            return "Erro ao deletar grupo";
        }
    }

    public String leaveGroup(String groupNameLeave, String name) {
        try {
            output.writeObject("leaveGroup");
            output.writeObject(groupNameLeave);
            output.writeObject(name);

            return (String) input.readObject();
        } catch (Exception e) {
            System.err.println("Erro ao sair do grupo: " + e.getMessage());
            return "Erro ao sair do grupo";
        }
    }

    public String payDebt(String groupNamePay, String name, String receiverNamePay, double value) {
        try {
            output.writeObject("payDebt");
            output.writeObject(groupNamePay);
            output.writeObject(receiverNamePay);
            output.writeObject(name);
            output.writeObject(value);

            return (String) input.readObject();
        } catch (Exception e) {
            System.err.println("Erro ao pagar dívida: " + e.getMessage());
            return "Erro ao pagar dívida";
        }
    }

    public String editExpense(String groupNameEditExpense, int id, String newReceiver, String newPayer, double value, String description) {
        try {
            output.writeObject("editExpense");
            output.writeObject(groupNameEditExpense);
            output.writeObject(id);
            output.writeObject(newReceiver);
            output.writeObject(newPayer);
            output.writeObject(value);
            output.writeObject(description);

            return (String) input.readObject();
        } catch (Exception e) {
            System.err.println("Erro ao editar despesa: " + e.getMessage());
            return "Erro ao editar despesa";
        }
    }

    public String deleteExpense(String groupNameDeleteExpense, int expenseIdDelete) {
        try {
            output.writeObject("deleteExpense");
            output.writeObject(groupNameDeleteExpense);
            output.writeObject(expenseIdDelete);

            return (String) input.readObject();
        } catch (Exception e) {
            System.err.println("Erro ao apagar despesa: " + e.getMessage());
            return "Erro ao apagar despesa";
        }
    }

    public String exportToCSV(String name) {
        try {
            output.writeObject("exportToCSV");
            output.writeObject(name);

            return (String) input.readObject();
        } catch (Exception e) {
            System.err.println("Erro ao exportar para CSV: " + e.getMessage());
            return "Erro ao exportar para CSV";
        }
    }

    public String listPayments(String name) {
        try {
            output.writeObject("listUserPayments");
            output.writeObject(name);

            return (String) input.readObject();
        } catch (Exception e) {
            System.err.println("Erro ao listar pagamentos: " + e.getMessage());
            return "Erro ao listar pagamentos";
        }
    }

    public String listPaymentsGroup(String groupNamePayments) {
        try {
            output.writeObject("listGroupPayments");
            output.writeObject(groupNamePayments);

            return (String) input.readObject();
        } catch (Exception e) {
            System.err.println("Erro ao listar pagamentos do grupo: " + e.getMessage());
            return "Erro ao listar pagamentos do grupo";
        }
    }

    public double totalGroup(String groupNameTotal) {
        try {
            output.writeObject("totalGroup");
            output.writeObject(groupNameTotal);

            return (double) input.readObject();
        } catch (Exception e) {
            System.err.println("Erro ao calcular total do grupo: " + e.getMessage());
            return -1;
        }
    }

    public String deletePayment(String groupNameDeletePayment, int paymentId) {
        try {
            output.writeObject("deletePayment");
            output.writeObject(groupNameDeletePayment);
            output.writeObject(paymentId);

            return (String) input.readObject();
        } catch (Exception e) {
            System.err.println("Erro ao apagar pagamento: " + e.getMessage());
            return "Erro ao apagar pagamento";
        }
    }

    public String groupMembersBalances(String groupName) {
        try {
            output.writeObject("groupMembersBalances");
            output.writeObject(groupName);
            Object response = input.readObject();

            if (response instanceof Map) {
                Map<String, Double> gastosPorElemento = (Map<String, Double>) response;
                System.out.println("Dividas de cada elemento: ");
                for (Map.Entry<String, Double> entry : gastosPorElemento.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }
            } else if (response instanceof String) {
                System.out.println((String) response);
            } else {
                System.err.println("[ERRO] Resposta inesperada do servidor: " + response);
            }
        } catch (Exception e) {
            System.err.println("Erro ao calcular gasto total por cada elemento do grupo: " + e.getMessage());
            return "Erro ao calcular gasto total de cada elemento do grupo";
        }
        return groupName;
    }


    public String totalDeve(String groupNameBalances) {
        try {
            output.writeObject("totalDeve");
            output.writeObject(groupNameBalances);
            Object response = input.readObject();

            if (response instanceof Map) {
                Map<String, Double> totalDeve = (Map<String, Double>) response;
                System.out.println("Total cada elemento deve: ");
                for (Map.Entry<String, Double> entry : totalDeve.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }
            } else if (response instanceof String) {
                System.out.println((String) response);
            } else {
                System.err.println("[ERRO] Resposta inesperada do servidor: " + response);
            }

        } catch (Exception e) {
            System.err.println("Erro ao calcular total devido: " + e.getMessage());
            return "Erro ao calcular total devido";
        }
        return groupNameBalances;
    }

    public String deveMembro(String groupNameBalances) {
        try {
            output.writeObject("deveMembro");
            output.writeObject(groupNameBalances);
            Object response = input.readObject();

            if (response instanceof Map) {
                Map<String, Map<String, Double>> deveMembro = (Map<String, Map<String, Double>>) response;
                System.out.println("Quanto deve a: ");
                for (Map.Entry<String, Map<String, Double>> entry : deveMembro.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }
            } else if (response instanceof String) {
                System.out.println((String) response);
            } else {
                System.err.println("[ERRO] Resposta inesperada do servidor: " + response);
            }
        } catch (Exception e) {
            System.err.println("Erro ao calcular quem deve a quem: " + e.getMessage());
            return "Erro ao calcular quem deve a quem";
        }
        return groupNameBalances;
    }

    public String totalRecebe(String groupNameBalances) {
        try {
            output.writeObject("totalRecebe");
            output.writeObject(groupNameBalances);
            Object response = input.readObject();

            if (response instanceof Map) {
                Map<String, Double> totalRecebe = (Map<String, Double>) response;
                System.out.println("Total cada elemento tem a receber: ");
                for (Map.Entry<String, Double> entry : totalRecebe.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }
            } else if (response instanceof String) {
                System.out.println((String) response);
            } else {
                System.err.println("[ERRO] Resposta inesperada do servidor: " + response);
            }

        } catch (Exception e) {
            System.err.println("Erro ao calcular total a receber: " + e.getMessage());
            return "Erro ao calcular total a receber";
        }
        return groupNameBalances;

    }

    public String recebeMembro(String groupNameBalances) {
        try {
            output.writeObject("recebeMembro");
            output.writeObject(groupNameBalances);
            Object response = input.readObject();

            if (response instanceof Map) {
                Map<String, Map<String, Double>> recebeMembro = (Map<String, Map<String, Double>>) response;
                System.out.println("Quanto tem a receber de: ");
                for (Map.Entry<String, Map<String, Double>> entry : recebeMembro.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }
            } else if (response instanceof String) {
                System.out.println((String) response);
            } else {
                System.err.println("[ERRO] Resposta inesperada do servidor: " + response);
            }
        } catch (Exception e) {
            System.err.println("Erro ao calcular quem tem a receber de quem: " + e.getMessage());
            return "Erro ao calcular quem tem a receber de quem";
        }
        return groupNameBalances;
    }
}
