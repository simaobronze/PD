package ServidorPrincipal.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // URL do banco de dados (substitua com o caminho do seu arquivo .db)
    //private static final String DATABASE_URL = "jdbc:sqlite:TP/src/Database/DBPrincipal.db"; NO IDE
    private static final String DATABASE_URL = "jdbc:sqlite:Database/DBPrincipal.db"; //NO JAR
    private static Connection connection = null;

    // Método para obter a conexão com o banco de dados
    public static Connection getConnection() {
            try {
                // Estabelece a conexão
                connection = DriverManager.getConnection(DATABASE_URL);
                System.out.println("Conexão com o banco de dados SQLite estabelecida.");
            } catch (SQLException e) {
                System.err.println("Erro ao conectar com o banco de dados: " + e.getMessage());
            }
        return connection;
    }

    // Método para fechar a conexão com o banco de dados
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Conexão com o banco de dados SQLite fechada.");
            } catch (SQLException e) {
                System.err.println("Erro ao fechar a conexão com o banco de dados: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}
