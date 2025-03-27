package com.example.controldeinventario.Datos;

public class Usuario {
    public int id_user;
    public String nombre_completo;
    public String sexo;
    public String username;
    public String password;
    public String sal;
    public String nombre_rol;

    public Usuario(int id_user, String nombre_completo, String sexo, String username, String password, String sal, String nombre_rol) {
        this.id_user = id_user;
        this.nombre_completo = nombre_completo;
        this.sexo = sexo;
        this.username = username;
        this.password = password;
        this.sal = sal;
        this.nombre_rol = nombre_rol;
    }

    public Usuario(int id_user, String nombre_completo, String sexo, String username, String nombre_rol) {
        this.id_user = id_user;
        this.nombre_completo = nombre_completo;
        this.sexo = sexo;
        this.username = username;
        this.nombre_rol = nombre_rol;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    public String getNombre_completo() {
        return nombre_completo;
    }

    public void setNombre_completo(String nombre_completo) {
        this.nombre_completo = nombre_completo;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSal() {
        return sal;
    }

    public void setSal(String sal) {
        this.sal = sal;
    }

    public String getNombre_rol() {
        return nombre_rol;
    }

    public void setNombre_rol(String nombre_rol) {
        this.nombre_rol = nombre_rol;
    }
}
