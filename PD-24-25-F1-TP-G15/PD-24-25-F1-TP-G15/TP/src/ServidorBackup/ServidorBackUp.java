package ServidorBackup;

import java.io.File;
import java.io.IOException;

public class ServidorBackUp {
    private static int id = 0;

    public static void main(String[] args) {
        // Diretório padrão ou fornecido pelo utilizador
        String backupDir = args.length > 0 ? args[0] : "backupDB";

        File dir = new File(backupDir);

        // Verificar se o diretório existe e está vazio
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("Diretório criado com sucesso: " + backupDir);
            } else {
                System.err.println("Falha ao criar diretório: " + backupDir);
                return;
            }
        } else if (dir.list().length > 0) {
            System.err.println("Erro: A diretoria fornecida não está vazia. Encerrando aplicação.");
            return;
        }

        // Iniciar o servidor de backup
        try {
            BackupServerCore server = new BackupServerCore(backupDir, id++);
            server.start();
        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }
}
