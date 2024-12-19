package ServidorPrincipal.Service;

import ServidorPrincipal.DAO.ExpenseDAO;
import ServidorPrincipal.DAO.GroupDAO;
import ServidorPrincipal.DAO.PaymentDAO;
import ServidorPrincipal.DAO.UserDAO;
import ServidorPrincipal.Expense;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class ExpenseService {
    private GroupDAO groupDAO;
    private ExpenseDAO expenseDAO;
    private UserDAO userDAO;
    private PaymentDAO paymentDAO;

    public ExpenseService(GroupDAO groupDAO, ExpenseDAO expenseDAO, UserDAO userDAO, PaymentDAO paymentDAO) {
        this.groupDAO = groupDAO;
        this.expenseDAO = expenseDAO;
        this.userDAO = userDAO;
        this.paymentDAO = paymentDAO;
    }

    public String insertExpense(String groupName, String receiverName, String payerName, double amount, String description) {
        try {
            if (!groupDAO.isUserInGroup(groupName, payerName)) {
                return "[ERRO] O pagador  não pertence ao grupo.";
            }

            if (!groupDAO.isUserInGroup(groupName, receiverName)) {
                return "[ERRO] Você não pertence ao grupo.";
            }

            if (!groupDAO.groupExists(groupName)) {
                return "[ERRO] O grupo não existe.";
            }

            boolean expenseInserted = expenseDAO.insertExpense(groupName, receiverName, payerName, amount, description);

            if (expenseInserted) {
                return "Despesa inserida com sucesso.";
            } else {
                return "[ERRO] Ocorreu um erro ao inserir a despesa.";
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String payDebt(String groupName, String receiverName, String payerName, double amount) {
        try {
            if (!groupDAO.groupExists(groupName)) {
                return "[ERRO] O grupo não existe.";
            } else if (!groupDAO.isUserInGroup(groupName, payerName)) {
                return "[ERRO] Você não pertence ao grupo.";
            } else if (!groupDAO.isUserInGroup(groupName, receiverName)) {
                return "[ERRO] O recebedor não pertence ao grupo.";
            } else if (!expenseDAO.checkExpenseStatus(groupName, payerName)) {
                return "[ERRO] Você não tem dívidas pendentes neste grupo.";
            } else if (!expenseDAO.checkExpenseStatus(groupName, payerName, receiverName)) {
                return "[ERRO] Não existe dívida entre vocês.";
            } else if (userDAO.checkSaldo(payerName) < amount) {
                return "[ERRO] Saldo insuficiente.";
            } else if (!expenseDAO.checkExpenseValue(groupName, payerName, receiverName, amount)) {
                return "[ERRO] O valor a pagar está incorreto.";
            } else {
                userDAO.RemoveSaldo(payerName, amount);
                userDAO.addSaldo(receiverName, amount);
                expenseDAO.updateExpenseStatus(groupName, receiverName, payerName, "paid");
                paymentDAO.insertPayment(groupName,payerName, receiverName,amount);
                return "Dívida paga com sucesso.";
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String editExpenseReceiver(String groupName, int id, String receiverName) {
        try {
            if (!groupDAO.groupExists(groupName)) {
                return "[ERRO] O grupo não existe.";
            }

            if (!expenseDAO.checkExpenseId(groupName, id)) {
                return "[ERRO] A despesa não existe.";
            }

            if (!groupDAO.isUserInGroup(groupName, receiverName)) {
                return "[ERRO] O recebedor não pertence ao grupo.";
            }

            boolean edited = expenseDAO.editExpenseReceiver(groupName, id, receiverName);
            if (edited) {
                return "Despesa editada com sucesso.";
            } else {
                return "[ERRO] Ocorreu um erro ao editar a despesa.";
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String editExpensePayer(String groupName, int id, String payerName) {
        try {
            if (!groupDAO.groupExists(groupName)) {
                return "[ERRO] O grupo não existe.";
            }

            if (!expenseDAO.checkExpenseId(groupName, id)) {
                return "[ERRO] A despesa não existe.";
            }

            if (!groupDAO.isUserInGroup(groupName, payerName)) {
                return "[ERRO] O pagador não pertence ao grupo.";
            }

            boolean edited = expenseDAO.editExpensePayer(groupName, id, payerName);
            if (edited) {
                return "Despesa editada com sucesso.";
            } else {
                return "[ERRO] Ocorreu um erro ao editar a despesa.";
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String editExpenseAmount(String groupName, int id, double amount) {
        try {
            if (!groupDAO.groupExists(groupName)) {
                return "[ERRO] O grupo não existe.";
            }

            if (!expenseDAO.checkExpenseId(groupName, id)) {
                return "[ERRO] A despesa não existe.";
            }

            boolean edited = expenseDAO.editExpenseAmount(groupName, id, amount);
            if (edited) {
                return "Despesa editada com sucesso.";
            } else {
                return "[ERRO] Ocorreu um erro ao editar a despesa.";
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String editExpenseDescription(String groupName, int id, String description) {
        try {
            if (!groupDAO.groupExists(groupName)) {
                return "[ERRO] O grupo não existe.";
            }

            if (!expenseDAO.checkExpenseId(groupName, id)) {
                return "[ERRO] A despesa não existe.";
            }

            boolean edited = expenseDAO.editExpenseDescription(groupName, id, description);
            if (edited) {
                return "Despesa editada com sucesso.";
            } else {
                return "[ERRO] Ocorreu um erro ao editar a despesa.";
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String deleteExpense(String groupName, int id) {
        try {
            if (!groupDAO.groupExists(groupName)) {
                return "[ERRO] O grupo não existe.";
            }

            if (!expenseDAO.checkExpenseId(groupName, id)) {
                return "[ERRO] A despesa não existe.";
            }

            boolean deleted = expenseDAO.deleteExpense(groupName, id);
            if (deleted) {
                return "Despesa apagada com sucesso.";
            } else {
                return "[ERRO] Ocorreu um erro ao apagar a despesa.";
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String exportToCSV(String userName) {
        List<Expense> expenses = expenseDAO.getExpenses(userName);

        if (expenses.isEmpty()) {
            return "[ERRO] Não há despesas para exportar.";
        }

        String filename = "expenses_" + userName + ".csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("Grupo ,Recebedor  ,Pagador    ,Valor  ,Descrição  ,Status \n");
            for (Expense expense : expenses) {
                writer.write(expense.getGroupName() + ",    "
                        + expense.getReceiverName() + ",    "
                        + expense.getPayerName() + ",   "
                        + expense.getValue() + ",   "
                        + expense.getDescription() + ", "
                        + expense.getStatus() + "\n");
            }
            return "Despesas exportadas com sucesso. Arquivo: " + filename;

        }
        catch (Exception e) {
            return "[ERRO] Ocorreu um erro ao exportar as despesas.";
        }
    }

    public Map<String, Double> totalDeve(String groupName) {
        try {
            if (!groupDAO.groupExists(groupName)) {
                System.err.println("[ERRO] O grupo não existe.");
                return null;
            }
            return expenseDAO.totalDeve(groupName);
        } catch (SQLException e) {
            throw new RuntimeException("[ERRO] Falha ao calcular dividas do grupo: " + e.getMessage(), e);
        }
    }

    public Map<String, Map<String, Double>> totalDeveMembro(String name, String groupName) {
        try {
            if (!groupDAO.groupExists(groupName)) {
                System.err.println("[ERRO] O grupo não existe.");
                return null;
            }
            if(!groupDAO.isUserInGroup(groupName, name)){
                System.err.println("[ERRO] O membro não pertence ao grupo.");
                return null;
            }

            return expenseDAO.totalDeveMembro(name, groupName);
        } catch (SQLException e) {
            throw new RuntimeException("[ERRO] Falha ao calcular dividas do membro: " + e.getMessage(), e);
        }
    }

    public Map<String, Double> totalRecebe(String groupName) {
        try {
            if (!groupDAO.groupExists(groupName)) {
                System.err.println("[ERRO] O grupo não existe.");
                return null;
            }
            return expenseDAO.totalRecebe(groupName);
        } catch (SQLException e) {
            throw new RuntimeException("[ERRO] Falha ao calcular os créditos do grupo: " + e.getMessage(), e);
        }
    }

    public Map <String, Map<String,Double>> totalRecebeMembro(String name, String groupName) {
        try {
            if(!groupDAO.groupExists(groupName)){
                System.err.println("[ERRO] O grupo não existe.");
                return null;
            }else
            if(!groupDAO.isUserInGroup(groupName, name)){
                System.err.println("[ERRO] O membro não pertence ao grupo.");
                return null;
            }
            return expenseDAO.totalRecebeMembro(name, groupName);
        }
        catch (SQLException e) {
            throw new RuntimeException("[ERRO] Falha ao calcular os créditos do membro: " + e.getMessage(), e);
        }
    }
}
