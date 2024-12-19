package ServidorPrincipal;

import ServidorPrincipal.DAO.*;
import ServidorPrincipal.Networking.HeartbeatSender;
import ServidorPrincipal.Networking.ServerRequestHandler;
import ServidorPrincipal.Service.ExpenseService;
import ServidorPrincipal.Service.GroupService;
import ServidorPrincipal.Service.PaymentService;
import ServidorPrincipal.Service.UserService;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServidorPrincipal {

    public static int databaseVersion = 1;  // Versão inicial do banco de dados
    public static final int SYNC_PORT = 8000; // Porta de sincronização
    public static final int CLIENT_PORT = 7000; // Porta para clientes
    public static final Object databaseLock = new Object();
    public static boolean queryUpdate = false;  // Flag para saber se há uma atualização

    // Método para marcar a atualização e aumentar a versão
    public static synchronized void updateDatabase() {
        queryUpdate = true; // Marca a atualização como verdadeira
    }

    // Método para incrementar a versão do banco
    public static synchronized void incrementDatabaseVersion() {
        databaseVersion++;
        System.out.println("Versão do banco de dados incrementada: " + databaseVersion);
    }

    public static void main(String[] args) {
        // Inicializa os DAOs e serviços
        UserDAO userDAO = new UserDAO(DatabaseConnection.getConnection());
        GroupDAO groupDAO = new GroupDAO();
        ExpenseDAO expenseDAO = new ExpenseDAO();
        PaymentDAO paymentDAO = new PaymentDAO();
        UserService userService = new UserService(userDAO);
        GroupService groupService = new GroupService(groupDAO, userDAO, expenseDAO);
        ExpenseService expenseService = new ExpenseService(groupDAO, expenseDAO ,userDAO, paymentDAO);
        PaymentService paymentService = new PaymentService(groupDAO, userDAO, expenseDAO, paymentDAO);

        // Inicia o HeartbeatSender em uma nova thread
        HeartbeatSender heartbeatSender = new HeartbeatSender(SYNC_PORT); // Usando a SYNC_PORT (porta 8000)
        Thread heartbeatThread = new Thread(heartbeatSender);
        heartbeatThread.start();

        // Cria um pool de threads para gerenciar as conexões simultâneas
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        // Inicia a escuta nas duas portas
        try {
            // Cria o ServerSocket para a porta 7000 (clientes)
            ServerSocket clientServerSocket = new ServerSocket(CLIENT_PORT);
            System.out.println("Servidor de clientes iniciado na porta: " + CLIENT_PORT);

            // Cria o ServerSocket para a porta 8000 (sincronização)
            ServerSocket syncServerSocket = new ServerSocket(SYNC_PORT);
            System.out.println("Servidor de sincronização iniciado na porta: " + SYNC_PORT);

            // Thread para escutar a porta 7000 (clientes)
            Thread clientListener = new Thread(() -> {
                try {
                    while (true) {
                        Socket clientSocket = clientServerSocket.accept();
                        System.out.println("Nova conexão de cliente recebida na porta " + CLIENT_PORT);
                        ServerRequestHandler requestHandler = new ServerRequestHandler(clientSocket, userService, groupService, expenseService, paymentService);
                        executorService.submit(requestHandler);
                    }
                } catch (IOException e) {
                    System.out.println("Erro ao aceitar conexão na porta " + CLIENT_PORT + ": " + e.getMessage());
                }
            });

            // Thread para escutar a porta 8000 (sincronização)
            Thread syncListener = new Thread(() -> {
                try {
                    while (true) {
                        Socket syncSocket = syncServerSocket.accept();
                        System.out.println("Nova conexão de sincronização recebida na porta " + SYNC_PORT);
                        handleBackupRequest(syncSocket);  // Aqui o banco de dados será enviado
                    }
                } catch (IOException e) {
                    System.out.println("Erro ao aceitar conexão na porta " + SYNC_PORT + ": " + e.getMessage());
                }
            });

            // Inicia as threads
            clientListener.start();
            syncListener.start();

            // Espera as threads terminarem
            clientListener.join();
            syncListener.join();

        } catch (IOException e) {
            System.out.println("Erro ao iniciar o servidor: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Erro ao esperar pelas threads: " + e.getMessage());
        }
    }

    // Método para lidar com a requisição de backup do servidor de backup
    private static void handleBackupRequest(Socket syncSocket) throws IOException {
        // Envia a base de dados para o servidor de backup
       // try (InputStream dbInputStream = new FileInputStream("TP/src/Database/DBPrincipal.db");  // Caminho do seu arquivo de banco de dados NO IDE
        try (InputStream dbInputStream = new FileInputStream("Database/DBPrincipal.db"); // NO JAR
             OutputStream out = syncSocket.getOutputStream()) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = dbInputStream.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            System.out.println("Base de dados enviada para o servidor de backup.");
        } catch (IOException e) {
            System.err.println("Erro ao enviar base de dados: " + e.getMessage());
        }
    }
}
