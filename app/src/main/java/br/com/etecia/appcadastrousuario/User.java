package br.com.etecia.appcadastrousuario;

public class User {
    private int id;
    private String nome, email;

    public User() {
    }

    public User(int id, String name, String email) {
        this.id = id;
        this.nome = name;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }
}