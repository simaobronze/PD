package ServidorPrincipal.DAO;

import ServidorPrincipal.ServidorPrincipal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentDAO {
    public List<String> listUserPayments(String name) {
        List<String> payments = new ArrayList<>();
        String query = "SELECT * FROM payments WHERE payer_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String payment = "ID: " + rs.getInt("payment_id") + " | Payer: " + rs.getString("payer_name") + " | Receiver: " + rs.getString("receiver_name") + " | Amount: " + rs.getDouble("amount");
                payments.add(payment);
            }
        } catch (SQLException e) {
            System.err.println("[Erro] Não foi possível listar pagamentos: " + e.getMessage());
        }
        return payments;
    }

    public List<String> listGroupPayments(String groupName) {
        List<String> payments = new ArrayList<>();
        String query = "SELECT * FROM payments WHERE group_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, groupName);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String payment = "ID: " + rs.getInt("payment_id") + " | Payer: " + rs.getString("payer_name") + " | Receiver: " + rs.getString("receiver_name") + " | Amount: " + rs.getDouble("amount");
                payments.add(payment);
            }
        } catch (SQLException e) {
            System.err.println("[Erro] Não foi possível listar pagamentos: " + e.getMessage());
        }
        return payments;
    }

    public void insertPayment(String groupName, String payerName, String receiverName, double amount) {
        String query = "INSERT INTO payments (group_name, payer_name, receiver_name, amount) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, groupName);
            stmt.setString(2, payerName);
            stmt.setString(3, receiverName);
            stmt.setDouble(4, amount);

            stmt.executeUpdate();

            // Incrementa a versão da base de dados
            ServidorPrincipal.updateDatabase();
            ServidorPrincipal.incrementDatabaseVersion();
           // System.out.println("Despesa atualizada. Versão da base de dados incrementada.");
        } catch (SQLException e) {
            System.err.println("[Erro] Não foi possível inserir pagamento: " + e.getMessage());
        }
    }

    public double totalGroup(String groupName) {
        double total = 0;
        String query = "SELECT SUM(amount) FROM payments WHERE group_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, groupName);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                total = rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("[Erro] Não foi possível calcular total do grupo: " + e.getMessage());
        }
        return total;
    }

    public boolean paymentIdExists(String groupName, int id) {
        String query = "SELECT * FROM payments WHERE group_name = ? AND payment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, groupName);
            stmt.setInt(2, id);

            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("[Erro] Não foi possível verificar se o pagamento existe: " + e.getMessage());
        }
        return false;
    }

    public void deletePayment(String groupName, int id) {
        String query = "DELETE FROM payments WHERE group_name = ? AND payment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, groupName);
            stmt.setInt(2, id);

            stmt.executeUpdate();

            // Incrementa a versão da base de dados
            ServidorPrincipal.updateDatabase();
            ServidorPrincipal.incrementDatabaseVersion();

        } catch (SQLException e) {
            System.err.println("[Erro] Não foi possível deletar pagamento: " + e.getMessage());
        }
    }

    public Map<String, Double> calculaGastosPorMembro(String groupName) {
        Map<String, Double> gastos = new HashMap<>();
        String query = "SELECT payer_name, SUM(amount) FROM payments WHERE group_name = ? GROUP BY payer_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, groupName);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String payerName = rs.getString("payer_name");
                double amount = rs.getDouble("SUM(amount)");
                gastos.put(payerName, amount);
            }

        } catch (SQLException e) {
            System.err.println("[Erro] Não foi possível calcular gastos por membro: " + e.getMessage());
        }
        return gastos;
    }
}
