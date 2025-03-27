package com.example.controldeinventario.Datos;

public class Articulo {
    public Long codigo_barras;
    public String tipo_de_armario;
    public String gaveta;
    public String sub_compartimento;
    public String material;
    public String tipo;
    public String numero_parte;
    public String valor;
    public String unidad_medida;
    public String caracteristicas;
    public String f_uso;
    public int cantidad;
    public int cantidad_min;

    public Articulo(Long codigo_barras, String tipo_de_armario, String gaveta, String sub_compartimento, String material, String tipo, String numero_parte, String valor, String unidad_medida, String caracteristicas, String f_uso, int cantidad, int cantidad_min) {
        this.codigo_barras = codigo_barras;
        this.tipo_de_armario = tipo_de_armario;
        this.gaveta = gaveta;
        this.sub_compartimento = sub_compartimento;
        this.material = material;
        this.tipo = tipo;
        this.numero_parte = numero_parte;
        this.valor = valor;
        this.unidad_medida = unidad_medida;
        this.caracteristicas = caracteristicas;
        this.f_uso = f_uso;
        this.cantidad = cantidad;
        this.cantidad_min = cantidad_min;
    }



    public String getF_uso() {
        return f_uso;
    }

    public void setF_uso(String f_uso) {
        this.f_uso = f_uso;
    }

    public long getCodigo_barras() {return codigo_barras;}
    public void setCodigo_barras(long codigo_barras) {this.codigo_barras = codigo_barras;}
    public String getTipo_de_armario() {return tipo_de_armario;}
    public void setTipo_de_armario(String tipo_de_armario) {this.tipo_de_armario = tipo_de_armario;}
    public String getGaveta() {return gaveta;}
    public void setGaveta(String gaveta) {this.gaveta = gaveta;}
    public String getSub_compartimento() {return sub_compartimento;}
    public void setSub_compartimento(String sub_compartimento) {this.sub_compartimento = sub_compartimento;}
    public String getMaterial() {return material;}
    public void setMaterial(String material) {this.material = material;}
    public String getTipo() {return tipo;}
    public void setTipo(String tipo) {this.tipo = tipo;}
    public String getNumero_parte() {return numero_parte;}
    public void setNumero_parte(String numero_parte) {this.numero_parte = numero_parte;}

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getUnidad_medida() {return unidad_medida;}
    public void setUnidad_medida(String unidad_medida) {this.unidad_medida = unidad_medida;}
    public String getCaracteristicas() {return caracteristicas;}
    public void setCaracteristicas(String caracteristicas) {this.caracteristicas = caracteristicas;}
    public int getCantidad() {return cantidad;}
    public void setCantidad(int cantidad) {this.cantidad = cantidad;}
    public int getCantidad_min() {return cantidad_min;}
    public void setCantidad_min(int cantidad_min) {this.cantidad_min = cantidad_min;}
}
