package ServidorPrincipal.DAO;

import Cliente.User;
import ServidorPrincipal.Group;
import ServidorPrincipal.ServidorPrincipal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupDAO {

    // Cria um novo grupo
    public boolean createGroup(String groupName, String creator) throws SQLException {
        String sql = "INSERT INTO groups (name, creator) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, groupName);
            pstmt.setString(2, creator);
            System.out.println("o grupo" + groupName + "foi criado por " + creator + "com sucesso!");
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                ServidorPrincipal.updateDatabase();
                ServidorPrincipal.incrementDatabaseVersion();
                System.out.println("Grupo criado. Versão da base de dados incrementada.");
            }
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao criar o grupo: " + e.getMessage());
            return false;
        }
    }

    // Verifica se um grupo já existe
    public boolean groupExists(String groupName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM groups WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, groupName);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Retorna true se o grupo já existir
                }
            }
        }
        return false; // O grupo não existe
    }

    public Group getGroupByName(String groupName) {
        String query = "SELECT * FROM groups WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, groupName);  // Substitui o parâmetro de nome do grupo
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Recuperando os dados do grupo
                String creator = rs.getString("creator");
                String membersString = rs.getString("members");  // Suponha que os membros sejam armazenados como uma string
                List<String> members = new ArrayList<>();

                if (membersString != null && !membersString.isEmpty()) {
                    // Convertendo a lista de membros de string para lista
                    String[] membersArray = membersString.split(",");
                    for (String member : membersArray) {
                        members.add(member.trim());
                    }
                }

                // Retornando o objeto Group com os dados recuperados
                return new Group(groupName, creator, members);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao obter o grupo: " + e.getMessage());
        }
        return null; // Retorna null se o grupo não for encontrado
    }

    public boolean isUserInGroup(String groupName, String userName) {
        String query = "SELECT creator, members FROM groups WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, groupName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Verifica se o e-mail do usuário é o criador do grupo
                String creator = rs.getString("creator");
                if (creator != null && creator.equalsIgnoreCase(userName)) {
                    return true;  // Usuário é o criador
                }

                // Verifica se o e-mail do usuário está na lista de membros
                String membersString = rs.getString("members");
                if (membersString != null && !membersString.isEmpty()) {
                    String[] membersArray = membersString.split(",");
                    for (String member : membersArray) {
                        if (member.trim().equalsIgnoreCase(userName)) {
                            return true;  // Usuário é membro do grupo
                        }
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao verificar se o usuário está no grupo: " + e.getMessage());
        }
        return false; // Retorna false se o usuário não for encontrado no grupo
    }

    public boolean inviteToGroup(String groupName, String emailInvite) {
        try {
            String query = "INSERT INTO invitations (group_name, invitee_email) VALUES (?, ?)";
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, groupName);
            stmt.setString(2, emailInvite);
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                ServidorPrincipal.updateDatabase();
                ServidorPrincipal.incrementDatabaseVersion();
                System.out.println("Grupo criado. Versão da base de dados incrementada.");
            }
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar convite: " + e.getMessage());
            return false;
        }
    }

    public List<String> listGroups(String userName) {
        List<String> groupNames = new ArrayList<>();
        String query = "SELECT name FROM groups WHERE creator = ? OR members LIKE ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userName); // Verifica se o usuário é o criador
            stmt.setString(2, "%" + userName + "%"); // Verifica se o usuário está na lista de membros
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                groupNames.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível recuperar os grupos: " + e.getMessage());
        }
        return groupNames;
    }

    public List<String> getInvitations(String userEmail) {
        List<String> invitations = new ArrayList<>();
        String query = "SELECT group_name FROM invitations WHERE invitee_email = ? AND status = 'pending'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userEmail);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                invitations.add(rs.getString("group_name"));
            }
        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível buscar os convites: " + e.getMessage());
        }
        return invitations;
    }

    public boolean addUserToGroup(String groupName, String userName) {
        System.out.println("Adicionando usuário " + userName + " ao grupo " + groupName);

        String query = "UPDATE groups SET members = CASE " +
                "WHEN members IS NULL THEN ? " + // Se estiver NULL, inicializa
                "WHEN members = '' THEN ? " +   // Se estiver vazia, adiciona diretamente
                "ELSE members || ? END " +      // Caso contrário, concatena com vírgula
                "WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userName);               // Inicializa com o usuário
            stmt.setString(2, userName);               // Para o caso de string vazia
            stmt.setString(3, "," + userName);         // Para o caso de concatenação
            stmt.setString(4, groupName);             // Nome do grupo
            int rowsUpdated = stmt.executeUpdate();
            System.out.println("Linhas atualizadas: " + rowsUpdated);
            return rowsUpdated > 0;


        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível adicionar o usuário ao grupo: " + e.getMessage());
            return false;
        }
    }

    public void updateInviteStatus(String groupName, String userEmail, String status) {
        String query = "UPDATE invitations SET status = ? WHERE group_name = ? AND invitee_email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setString(2, groupName);
            stmt.setString(3, userEmail);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível atualizar o status do convite: " + e.getMessage());
        }
    }

    public boolean renameGroup(String oldName, String newName) {
        String query = "UPDATE groups SET name = ? WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newName);
            stmt.setString(2, oldName);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível renomear o grupo: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteGroup(String groupName) {
        String query = "DELETE FROM groups WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, groupName);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível deletar o grupo: " + e.getMessage());
            return false;
        }
    }

    public boolean leaveGroup(String groupName, String name) {
        String query = "UPDATE groups SET members = REPLACE(members, ?, '') WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, groupName);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("[ERRO] Não foi possível sair do grupo: " + e.getMessage());
            return false;
        }
    }


}
