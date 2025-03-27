package com.example.controldeinventario.Datos;

import java.util.Date;

public class Pedido {
    private int id_pedido;
    private String nombre_persona;
    private String num_control;
    private String estado;
    private Date fecha;
    private String profesor;
    private String materia;

    public Pedido(int id_pedido, String nombre_persona, String num_control, String estado, Date fecha, String profesor, String materia) {
        this.id_pedido = id_pedido;
        this.nombre_persona = nombre_persona;
        this.num_control = num_control;
        this.estado = estado;
        this.fecha = fecha;
        this.profesor = profesor;
        this.materia = materia;
    }

    public int getId_pedido() {
        return id_pedido;
    }

    public void setId_pedido(int id_pedido) {
        this.id_pedido = id_pedido;
    }

    public String getNombre_persona() {
        return nombre_persona;
    }

    public void setNombre_persona(String nombre_persona) {
        this.nombre_persona = nombre_persona;
    }

    public String getNum_control() {
        return num_control;
    }

    public void setNum_control(String num_control) {
        this.num_control = num_control;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getProfesor() {
        return profesor;
    }

    public void setProfesor(String profesor) {
        this.profesor = profesor;
    }

    public String getMateria() {
        return materia;
    }

    public void setMateria(String materia) {
        this.materia = materia;
    }
}
