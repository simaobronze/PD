package Cliente;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String phone;
    private String email;
    private String password;
    private double saldo;

    //Construtores
    public User(String email,String name, String phone, String password, double saldo) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.saldo = saldo;
    }

    public User() {
        // Construtor vazio
    }

    //Getters e Setters

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }
}



