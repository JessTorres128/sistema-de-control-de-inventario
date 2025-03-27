package com.example.controldeinventario.Datos;

public class Registro {
    private Long cb;
    private int id_registro;
    private String nombre;
    private String tipo;
    private String valor;
    private String unidad_medida;
    private int cantidad;
    private boolean entregado;

    public Registro(Long cb, int id_registro, int cantidad, boolean entregado) {
        this.cb = cb;
        this.id_registro = id_registro;
        this.cantidad = cantidad;
        this.entregado = entregado;
    }

    public Registro(Long cb, int id_registro, String nombre, String tipo, String valor, String unidad_medida, int cantidad, boolean entregado) {
        this.cb = cb;
        this.id_registro = id_registro;
        this.nombre = nombre;
        this.tipo = tipo;
        this.valor = valor;
        this.unidad_medida = unidad_medida;
        this.cantidad = cantidad;
        this.entregado = entregado;
    }

    public Registro(Long cb, int id_registro, String nombre, String tipo, int cantidad, boolean entregado) {
        this.cb = cb;
        this.id_registro = id_registro;
        this.nombre = nombre;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.entregado = entregado;
    }

    public Registro(Long cb, String nombre, String tipo, String valor, String unidad_medida, int cantidad, boolean entregado) {
        this.cb = cb;
        this.nombre = nombre;
        this.tipo = tipo;
        this.valor = valor;
        this.unidad_medida = unidad_medida;
        this.cantidad = cantidad;
        this.entregado=entregado;
    }

    public Registro(Long cb, String nombre, String tipo, int cantidad, boolean entregado) {
        this.cb = cb;
        this.nombre = nombre;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.entregado=entregado;
    }

    public Long getCb() {
        return cb;
    }

    public void setCb(Long cb) {
        this.cb = cb;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getUnidad_medida() {
        return unidad_medida;
    }

    public void setUnidad_medida(String unidad_medida) {
        this.unidad_medida = unidad_medida;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public boolean isEntregado() {
        return entregado;
    }

    public void setEntregado(boolean entregado) {
        this.entregado = entregado;
    }

    public int getId_registro() {
        return id_registro;
    }

    public void setId_registro(int id_registro) {
        this.id_registro = id_registro;
    }
}
