package ServidorPrincipal.Service;

import ServidorPrincipal.DAO.ExpenseDAO;
import ServidorPrincipal.DAO.GroupDAO;
import ServidorPrincipal.DAO.PaymentDAO;
import ServidorPrincipal.DAO.UserDAO;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PaymentService {
    private GroupDAO groupDAO;
    private UserDAO userDAO;
    private ExpenseDAO expenseDAO;
    private PaymentDAO paymentDAO;

    public PaymentService(GroupDAO groupDAO, UserDAO userDAO, ExpenseDAO expenseDAO, PaymentDAO paymentDAO) {
        this.groupDAO = groupDAO;
        this.userDAO = userDAO;
        this.expenseDAO = expenseDAO;
        this.paymentDAO = paymentDAO;
    }

    public List<String> listUserPayments(String name) {
        return paymentDAO.listUserPayments(name);
    }

    public List<String> listGroupPayments(String groupName) {
        try {
            if (!groupDAO.groupExists(groupName)) {
                System.err.println("[ERRO] O grupo não existe.");
                return null;
            }

            return paymentDAO.listGroupPayments(groupName);
        } catch (SQLException e) {
            throw new RuntimeException("[ERRO] Falha ao listar pagamentos do grupo: " + e.getMessage(), e);
        }
    }

    public double totalGroup(String groupName) {
        try {
            if (!groupDAO.groupExists(groupName)) {
                System.err.println("[ERRO] O grupo não existe.");
                return -1;
            }
            return paymentDAO.totalGroup(groupName);
        } catch (SQLException e) {
            throw new RuntimeException("[ERRO] Falha ao calcular total do grupo: " + e.getMessage(), e);
        }
    }

    public String deletePayment(String groupName, int id) {
        try {
            if (!groupDAO.groupExists(groupName)) {
                System.err.println("[ERRO] O grupo não existe.");
                return "O grupo não existe.";
            }

            if (!paymentDAO.paymentIdExists(groupName, id)) {
                System.err.println("[ERRO] O pagamento não existe.");
                return "O pagamento não existe.";
            }

            paymentDAO.deletePayment(groupName, id);
            return "Pagamento apagado com sucesso.";
        } catch (SQLException e) {
            throw new RuntimeException("[ERRO] Falha ao apagar o pagamento: " + e.getMessage(), e);
        }
    }

    public Map<String, Double> gastosPorMembro(String groupName) {
        try {
            if (!groupDAO.groupExists(groupName)) {
                System.err.println("[ERRO] O grupo não existe.");
                return Collections.emptyMap();
            }

            return paymentDAO.calculaGastosPorMembro(groupName);
        } catch (SQLException e) {
            throw new RuntimeException("[ERRO] Falha ao calcular gastos do grupo: " + e.getMessage(), e);
        }
    }
}