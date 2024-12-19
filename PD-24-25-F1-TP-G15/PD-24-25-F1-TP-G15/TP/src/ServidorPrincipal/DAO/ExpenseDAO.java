package ServidorPrincipal.DAO;

import ServidorPrincipal.Expense;
import ServidorPrincipal.ServidorPrincipal;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseDAO {
    public boolean insertExpense(String groupName, String receiverName, String payerName, double amount, String description) {
        String query = "INSERT INTO expenses (group_name, receiver, payer, amount, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, groupName);
            stmt.setString(2, receiverName);
            stmt.setString(3, payerName);
            stmt.setDouble(4, amount);
            stmt.setString(5, description);
            int rowsInserted = stmt.executeUpdate();


            // Incrementa a versão da base de dados
            ServidorPrincipal.updateDatabase();
            ServidorPrincipal.incrementDatabaseVersion();
            //System.out.println("Despesa criado. Versão da base de dados incrementada.");
            return rowsInserted > 0;

        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível inserir a despesa: " + e.getMessage());
            return false;
        }
    }

    public void updateExpenseStatus(String groupName, String receiverName, String payerName, String status) {
        String query = "UPDATE expenses SET status = ? WHERE group_name = ? AND receiver = ? AND payer = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setString(2, groupName);
            stmt.setString(3, receiverName);
            stmt.setString(4, payerName);
            stmt.executeUpdate();

            // Incrementa a versão da base de dados
            ServidorPrincipal.updateDatabase();
            ServidorPrincipal.incrementDatabaseVersion();
            //System.out.println("Despesa atualizada. Versão da base de dados incrementada.");

        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível atualizar o status da despesa: " + e.getMessage());
        }
    }

    public boolean checkExpenseStatus(String groupName) {
        String query = "SELECT status FROM expenses WHERE group_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, groupName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                if (rs.getString("status").equals("pending")) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível verificar o status da despesa: " + e.getMessage());
        }
        return false;
    }

    public boolean checkExpenseStatus(String groupName, String payerName) {
        String query = "SELECT status FROM expenses WHERE group_name = ? AND payer = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, groupName);
            stmt.setString(2, payerName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                if (rs.getString("status").equals("pending")) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível verificar o status da despesa: " + e.getMessage());
        }
        return false;
    }

    public boolean checkExpenseStatus(String groupName, String payerName, String receiverName) {
        String query = "SELECT status FROM expenses WHERE group_name = ? AND payer = ? AND receiver = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, groupName);
            stmt.setString(2, payerName);
            stmt.setString(3, receiverName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                if (rs.getString("status").equals("pending")) {
                    return true;
                }
            }

        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível verificar o status da despesa: " + e.getMessage());
        }
        return false;
    }

    public boolean checkExpenseValue(String groupName, String payerName, String receiverName, double value) {
        String query = "SELECT amount FROM expenses WHERE group_name = ? AND payer = ? AND receiver = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, groupName);
            stmt.setString(2, payerName);
            stmt.setString(3, receiverName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                if (rs.getDouble("amount") == value) {
                    return true;
                }
            }

        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível verificar o valor da despesa: " + e.getMessage());
        }
        return false;
    }

    public boolean editExpenseReceiver(String groupName, int id, String receiverName) {
        String query = "UPDATE expenses SET receiver = ? WHERE group_name = ? AND id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, receiverName);
            stmt.setString(2, groupName);
            stmt.setInt(3, id);
            int rowsUpdated = stmt.executeUpdate();

            // Incrementa a versão da base de dados
            ServidorPrincipal.updateDatabase();
            ServidorPrincipal.incrementDatabaseVersion();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível editar o recebedor da despesa: " + e.getMessage());
            return false;
        }
    }

    public boolean editExpensePayer(String groupName, int id, String payerName) {
        String query = "UPDATE expenses SET payer = ? WHERE group_name = ? AND id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, payerName);
            stmt.setString(2, groupName);
            stmt.setInt(3, id);
            int rowsUpdated = stmt.executeUpdate();

            // Incrementa a versão da base de dados
            ServidorPrincipal.updateDatabase();
            ServidorPrincipal.incrementDatabaseVersion();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível editar o pagador da despesa: " + e.getMessage());
            return false;
        }
    }

    public boolean checkExpenseId(String groupName, int id) {
        String query = "SELECT id FROM expenses WHERE group_name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, groupName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                if (rs.getInt("id") == id) {
                    return true;
                }
            }

        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível verificar o id da despesa: " + e.getMessage());
        }
        return false;
    }

    public boolean editExpenseAmount(String groupName, int id, double amount) {
        String query = "UPDATE expenses SET amount = ? WHERE group_name = ? AND id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, amount);
            stmt.setString(2, groupName);
            stmt.setInt(3, id);
            int rowsUpdated = stmt.executeUpdate();

            // Incrementa a versão da base de dados
            ServidorPrincipal.updateDatabase();
            ServidorPrincipal.incrementDatabaseVersion();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível editar o valor da despesa: " + e.getMessage());
            return false;
        }
    }

    public boolean editExpenseDescription(String groupName, int id, String description) {
        String query = "UPDATE expenses SET description = ? WHERE group_name = ? AND id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, description);
            stmt.setString(2, groupName);
            stmt.setInt(3, id);
            int rowsUpdated = stmt.executeUpdate();

            // Incrementa a versão da base de dados
            ServidorPrincipal.updateDatabase();
            ServidorPrincipal.incrementDatabaseVersion();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível editar a descrição da despesa: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteExpense(String groupName, int id) {
        String query = "DELETE FROM expenses WHERE group_name = ? AND id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, groupName);
            stmt.setInt(2, id);
            int rowsDeleted = stmt.executeUpdate();

            // Incrementa a versão da base de dados
            ServidorPrincipal.updateDatabase();
            ServidorPrincipal.incrementDatabaseVersion();

            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível apagar a despesa: " + e.getMessage());
            return false;
        }
    }

    public List<Expense> getExpenses(String userName) {
        String query = "SELECT * FROM expenses WHERE receiver = ? OR payer = ?";
        List<Expense> expenses = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userName);
            stmt.setString(2, userName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                expenses.add(new Expense(
                        rs.getString("group_name"),
                        rs.getString("receiver"),
                        rs.getString("payer"),
                        rs.getDouble("amount"),
                        rs.getString("description"),
                        rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível obter as despesas: " + e.getMessage());
        }
        return expenses;
    }

    public Map<String, Double> totalDeve(String groupName) {
        Map<String, Double> totalDeve = new HashMap<>();
        String query = "SELECT payer, SUM(amount) FROM expenses WHERE group_name = ? AND status = 'pending' GROUP BY payer";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, groupName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String payer = rs.getString("payer");
                double amount = rs.getDouble("SUM(amount)");
                totalDeve.put(payer, amount);
            }

        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível calcular o total devido: " + e.getMessage());
        }
        return totalDeve;
    }

    public Map<String, Map<String, Double>> totalDeveMembro(String name, String groupName) {
        Map<String, Map<String, Double>> totalDeveMembro = new HashMap<>();
        String query = "SELECT payer, receiver, SUM(amount) FROM expenses WHERE group_name = ? AND payer = ? AND status = 'pending' GROUP BY payer, receiver";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, groupName);
            stmt.setString(2, name);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String payer = rs.getString("payer");
                String receiver = rs.getString("receiver");
                double amount = rs.getDouble("SUM(amount)");
                Map<String, Double> totalDeve = new HashMap<>();
                totalDeve.put(payer, amount);
                totalDeveMembro.put(receiver, totalDeve);
            }

        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível calcular o total devido por membro: " + e.getMessage());
        }
        return totalDeveMembro;
    }

    public Map<String, Double> totalRecebe(String groupName) {
        Map<String, Double> totalRecebe = new HashMap<>();
        String query = "SELECT receiver, SUM(amount) FROM expenses WHERE group_name = ? AND status = 'pending' GROUP BY receiver";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, groupName);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String receiver = rs.getString("receiver");
                double amount = rs.getDouble("SUM(amount)");
                totalRecebe.put(receiver, amount);
            }

        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível calcular o total recebido: " + e.getMessage());
        }
        return totalRecebe;
    }

    public Map<String, Map<String, Double>> totalRecebeMembro(String name, String groupName) {
        Map<String, Map<String, Double>> totalRecebeMembro = new HashMap<>();
        String query = "SELECT receiver, payer, SUM(amount) FROM expenses WHERE group_name = ? AND receiver = ? AND status = 'pending' GROUP BY receiver, payer";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, groupName);
            stmt.setString(2, name);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String receiver = rs.getString("receiver");
                String payer = rs.getString("payer");
                double amount = rs.getDouble("SUM(amount)");
                Map<String, Double> totalRecebe = new HashMap<>();
                totalRecebe.put(payer, amount);
                totalRecebeMembro.put(receiver, totalRecebe);
            }
        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível calcular o total devido por membro: " + e.getMessage());
        }
        return totalRecebeMembro;
    }
}
