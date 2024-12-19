package ServidorBackup;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BackupServerCore {
    private final String directoryPath;
    private final int id;
    private final ExecutorService executor;
    private static final String MULTICAST_ADDRESS = "230.44.44.44";
    private static final int MULTICAST_PORT = 4444;
    private static final int TIMEOUT_SECONDS = 30;
    private boolean running = true;
    private long lastHeartbeatTime;
    private int databaseVersion = 1;

    public BackupServerCore(String directoryPath, int id) {
        this.directoryPath = directoryPath;
        this.id = id;
        this.executor = Executors.newFixedThreadPool(2);
        this.lastHeartbeatTime = System.currentTimeMillis();
    }

    public void start() throws IOException {
        // Sincronização inicial da base de dados
        System.out.println("Sincronizando base de dados inicial...");
        synchronizeDatabaseInitial();

        // Iniciar threads para escutar heartbeats e verificar timeout
        executor.execute(this::listenForHeartbeats);
        executor.execute(this::checkHeartbeatTimeout);

        System.out.println("Servidor de backup iniciado...");
    }

    private void listenForHeartbeats() {
        try (MulticastSocket socket = new MulticastSocket(MULTICAST_PORT)) {
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);

            byte[] buffer = new byte[1024];
            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                handleHeartbeat(message);
            }
            socket.leaveGroup(group);
        } catch (IOException e) {
            System.err.println("Erro ao escutar heartbeats: " + e.getMessage());
        }
    }

    private void handleHeartbeat(String message) {
        try {
            String[] parts = message.split(";");
            int version = Integer.parseInt(parts[0].split(":")[1]);
            int port = Integer.parseInt(parts[1].split(":")[1]);
            boolean queryUpdate = Boolean.parseBoolean(parts[2].split(":")[1]);

            System.out.println("Heartbeat recebido: versão " + version);

            if (version != databaseVersion) {
                if (!queryUpdate || version != databaseVersion + 1) {
                    System.err.println("Versão incompatível detectada. Encerrando servidor...");
                    stopServer();
                    return;
                }
            }

            lastHeartbeatTime = System.currentTimeMillis();

            if (version != databaseVersion) {
                System.out.println("Sincronizando base de dados com versão " + version);
                synchronizeDatabase(port);
                databaseVersion = version;
            }
        } catch (Exception e) {
            System.err.println("Erro ao processar heartbeat: " + e.getMessage());
        }
    }

    private void synchronizeDatabaseInitial() throws IOException {
        int initialPort = 8000; // Defina a porta inicial adequada
        synchronizeDatabase(initialPort);
    }

    private void synchronizeDatabase(int port) throws IOException {
        try (Socket socket = new Socket("localhost", port);
             InputStream in = socket.getInputStream();
             FileOutputStream out = new FileOutputStream(directoryPath + "/database" + id + ".db")) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            System.out.println("Sincronização da base de dados concluída.");
        } catch (IOException e) {
            System.err.println("Erro ao sincronizar base de dados: " + e.getMessage());
            stopServer();
        }
    }

    private void checkHeartbeatTimeout() {
        while (running) {
            if (System.currentTimeMillis() - lastHeartbeatTime > TIMEOUT_SECONDS * 1000) {
                System.err.println("Timeout de heartbeat detectado. Encerrando servidor...");
                stopServer();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void stopServer() {
        running = false;
        executor.shutdownNow();
        System.out.println("Servidor de backup encerrado.");
        System.exit(0);
    }
}
