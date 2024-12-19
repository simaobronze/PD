import Cliente.Cliente;
import ServidorBackup.ServidorBackUp;
import ServidorPrincipal.ServidorPrincipal;
import Tests.TestAddUserToGroup;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Escolha o programa para executar:");
        System.out.println("1 - Servidor");
        System.out.println("2 - Cliente");
        System.out.println("3 - Backup");
        System.out.println("4 - Teste");

        int choice = scanner.nextInt();
        scanner.nextLine();  // Consome a nova linha deixada pelo nextInt()

        switch (choice) {
            case 1:
                ServidorPrincipal.main(args);  // Chama o programa 1
                break;
            case 2:
                Cliente.main(args);  // Chama o programa 2
                break;
            case 3:
                // Solicita um argumento adicional se a opção for 3
                System.out.println("Digite um argumento para o Backup:");
                String argumento = scanner.nextLine();  // Lê o argumento
                // Chama o programa 3, passando o argumento
                ServidorBackUp.main(new String[]{argumento});
                break;
            case 4:
                TestAddUserToGroup.main(args);  // Chama o programa 4
                break;
            default:
                System.out.println("Escolha inválida.");
                break;
        }
    }
}
