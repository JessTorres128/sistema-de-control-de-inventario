package com.example.controldeinventario.Datos;

public enum Vistas {
    LOGIN("Login.fxml"),
    ARTICULOS("Articulos.fxml"),
    HERRAMIENTAS("Herramientas.fxml"),
    ROLES("Roles.fxml"),
    EMPLEADOS("Empleados.fxml"),
    PEDIDOS("Pedidos.fxml"),
    TIPOS("TipoArticulo.fxml"),
    GENERAR("Generar.fxml"),
    RESTAURAR("Restaurar.fxml"),
    BORRAR("Borrar.fxml");

    private final String recursoName;

    Vistas(String recursoName) {
        this.recursoName = recursoName;
    }


    public String getRecursoName() {
        return recursoName;
    }
}
