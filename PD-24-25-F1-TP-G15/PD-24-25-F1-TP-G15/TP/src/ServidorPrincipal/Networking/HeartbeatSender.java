package ServidorPrincipal.Networking;

import ServidorPrincipal.ServidorPrincipal;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class HeartbeatSender extends Thread {
    private final int syncPort;  // Porta para sincronização
    private static final String MULTICAST_ADDRESS = "230.44.44.44";
    private static final int MULTICAST_PORT = 4444;
    private static final String BACKUP_SERVER_ADDRESS = "localhost"; // Assumindo que o servidor de backup está em localhost

    public HeartbeatSender(int syncPort) {
        this.syncPort = syncPort;
    }

    @Override
    public void run() {
        try (DatagramSocket socket = new DatagramSocket()) {
            // Use multicast para comunicar com múltiplos servidores de backup, ou use unicast para um único servidor.
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);

            // Use o socket para enviar os dados para o servidor de backup
            while (true) {
                // Obtém a versão do banco de dados em tempo real
                int currentDatabaseVersion;
                boolean currentQueryUpdate;
                synchronized (ServidorPrincipal.databaseLock) {
                    currentDatabaseVersion = ServidorPrincipal.databaseVersion;
                    currentQueryUpdate = ServidorPrincipal.queryUpdate;  // Verifica se há atualização
                    // Cria a mensagem do heartbeat
                    String heartbeatMessage = String.format("version:%d;port:%d;query:%b",
                            currentDatabaseVersion, syncPort, currentQueryUpdate);
                    byte[] buffer = heartbeatMessage.getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, MULTICAST_PORT);

                    // Envia o heartbeat via multicast
                    socket.send(packet);
                    System.out.println("Heartbeat enviado: " + heartbeatMessage);


                        ServidorPrincipal.queryUpdate = false; // Resetando o flag após o envio

                }

                // Envia um heartbeat a cada 5 segundos
                Thread.sleep(10000);
            }
        } catch (IOException e) {
            System.err.println("Erro ao enviar o heartbeat: " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Heartbeat interrompido: " + e.getMessage());
            Thread.currentThread().interrupt();  // Restaura o estado de interrupção da thread
        }
    }
}
