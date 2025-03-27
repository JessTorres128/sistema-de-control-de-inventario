package com.example.controldeinventario.Datos;

public class TipoArticulo {
    public int id;
    public String nombre;
    public String t_material;
    public boolean seleccion;

    public TipoArticulo(int id, String nombre, String t_material) {
        this.id = id;
        this.nombre = nombre;
        this.t_material = t_material;
    }

    public TipoArticulo(int id, String nombre, String t_material, boolean seleccion) {
        this.id = id;
        this.nombre = nombre;
        this.t_material = t_material;
        this.seleccion = seleccion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getT_material() {
        return t_material;
    }

    public void setT_material(String t_material) {
        this.t_material = t_material;
    }

    public Boolean getSeleccion() {return seleccion;}

    public void setSeleccion(Boolean seleccion) {this.seleccion = seleccion;}
}
