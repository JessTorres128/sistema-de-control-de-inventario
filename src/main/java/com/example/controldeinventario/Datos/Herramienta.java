package com.example.controldeinventario.Datos;

public class Herramienta {
    public Long cb_herramienta;
    public String herramienta;
    public String tipo;
    public String caracteristicas;
    public String frecuencia_de_uso;
    public int cantidad;
    public int cantidad_min;

    public Herramienta(Long cb_herramienta, String herramienta, String tipo, String caracteristicas, String frecuencia_de_uso, int cantidad, int cantidad_min) {
        this.cb_herramienta = cb_herramienta;
        this.herramienta = herramienta;
        this.tipo = tipo;
        this.caracteristicas = caracteristicas;
        this.frecuencia_de_uso = frecuencia_de_uso;
        this.cantidad = cantidad;
        this.cantidad_min = cantidad_min;
    }

    public Long getCb_herramienta() {
        return cb_herramienta;
    }

    public void setCb_herramienta(Long cb_herramienta) {
        this.cb_herramienta = cb_herramienta;
    }

    public String getHerramienta() {
        return herramienta;
    }

    public void setHerramienta(String herramienta) {
        this.herramienta = herramienta;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(String caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    public String getFrecuencia_de_uso() {
        return frecuencia_de_uso;
    }

    public void setFrecuencia_de_uso(String frecuencia_de_uso) {
        this.frecuencia_de_uso = frecuencia_de_uso;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getCantidad_min() {
        return cantidad_min;
    }

    public void setCantidad_min(int cantidad_min) {
        this.cantidad_min = cantidad_min;
    }
}
