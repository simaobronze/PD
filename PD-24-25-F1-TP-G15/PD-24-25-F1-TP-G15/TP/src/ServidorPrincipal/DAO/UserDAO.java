package ServidorPrincipal.DAO;

import Cliente.User;
import ServidorPrincipal.ServidorPrincipal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    private Connection connection;

    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean createUser(User user) {
        String sql = "INSERT INTO users (name, phone, email, password, saldo) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPhone());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setDouble(5, user.getSaldo());

            stmt.execute();

            // Incrementa a versão da base de dados
            ServidorPrincipal.updateDatabase();
            ServidorPrincipal.incrementDatabaseVersion();
            System.out.println("Utilizador criado. Versão da base de dados incrementada.");




            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao criar utilizador: " + e.getMessage());
            return false;
        }
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getString("email"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        rs.getDouble("saldo")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erro ao encontrar user: " + e.getMessage());
        }
        return null;
    }

    public static User loginUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getString("email"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        rs.getDouble("saldo")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erro ao encontrar user: " + e.getMessage());
        }

        return null;
    }

    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, phone = ?, password = ?, saldo = ? WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getPhone());
            pstmt.setString(3, user.getPassword());
            pstmt.setDouble(4, user.getSaldo());
            pstmt.setString(5, user.getEmail());

            System.out.println("Atualizando usuário com email: " + user.getEmail());
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Linhas afetadas: " + rowsAffected);

            if (rowsAffected > 0) {
                ServidorPrincipal.updateDatabase();
                ServidorPrincipal.incrementDatabaseVersion();
                System.out.println("Utilizador atualizado. Versão da base de dados incrementada.");

            }
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar o usuário no banco de dados: " + e.getMessage());
            throw e;
        }
    }

    public double checkSaldo(String userName) {
        String query = "SELECT saldo FROM users WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("saldo");
            }
        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível verificar o saldo: " + e.getMessage());
        }
        return -1.0;
    }

    public void addSaldo(String userName, double saldo) {
        String query = "UPDATE users SET saldo = saldo + ? WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, saldo);
            stmt.setString(2, userName);
            stmt.execute();
        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível adicionar saldo: " + e.getMessage());
        }
    }

    public void RemoveSaldo(String userName, double saldo) {
        String query = "UPDATE users SET saldo = saldo - ? WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, saldo);
            stmt.setString(2, userName);
            stmt.execute();
        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível remover saldo: " + e.getMessage());
        }
    }
}
