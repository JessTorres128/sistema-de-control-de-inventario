package com.example.controldeinventario;

import com.example.controldeinventario.Datos.Articulo;
import com.example.controldeinventario.Datos.Herramienta;
import com.example.controldeinventario.Datos.Registro;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ConsultarMaterialController {
    PedidosController pedidosController;
    TableColumn<Articulo,Long> colCB=new TableColumn<>("Codigo de barras");
    TableColumn <Articulo, String> colTArmario=new TableColumn<>("Tipo de armario");
    TableColumn <Articulo, String> colGaveta=new TableColumn<>("Gaveta");
    TableColumn <Articulo, String> colSubCom=new TableColumn<>("Sub-compartimento");
    TableColumn <Articulo,String> colMaterial=new TableColumn<>("Material");
    TableColumn <Articulo,String> colTipo=new TableColumn<>("Tipo");
    TableColumn <Articulo, String> colNumParte=new TableColumn<>("Número de parte");
    TableColumn <Articulo, String> colValor=new TableColumn<>("Valor");
    TableColumn <Articulo,String> colUMedida=new TableColumn<>("Unidad de medida");
    TableColumn <Articulo, String> colCaracteristicas=new TableColumn<>("Caracteristicas");
    TableColumn <Articulo, String> colFUso=new TableColumn<>("Frecuencia de uso");
    TableColumn <Articulo, Integer> colCantidad=new TableColumn<>("Cantidad");
    TableColumn <Articulo,Integer> colCantidadMin=new TableColumn<>("Cantidad minima");

    Conexion conexion;
    @FXML Button btnSalir;
    ToggleGroup toggleGroupMostrar = new ToggleGroup();
    ToggleGroup toggleGroupBuscar = new ToggleGroup();
    @FXML TableView tableViewMats;
    @FXML TextField txtBusqueda;
    @FXML RadioButton rbMaterial, rbHerramienta;
    @FXML RadioButton rbCodigo_Barras, rbNombre, rbTipo;

    TableColumn<Herramienta,Long> colID = new TableColumn<>("CB Herramienta");
    TableColumn<Herramienta,String> colHerramienta= new TableColumn<>("Herramienta");
    TableColumn<Herramienta,String> colTipoHerramienta= new TableColumn<>("Tipo");
    TableColumn<Herramienta,String> colCaracteristicasHerramienta = new TableColumn<>("Caracteristicas");
    TableColumn<Herramienta,String> colFUsoHerramienta=new TableColumn<>("Frecuencia de uso");
    TableColumn<Herramienta,Integer> colCantidadHerramienta=new TableColumn<>("Cantidad");
    TableColumn<Herramienta,Integer> colCantidadMinHerramienta=new TableColumn<>("Cantidad minima");


    @FXML protected void initialize() throws SQLException {
        pedidosController = new PedidosController();
        //Material
        colCB.setCellValueFactory(new PropertyValueFactory<>("codigo_barras"));
        colTArmario.setCellValueFactory(new PropertyValueFactory<>("tipo_de_armario"));
        colGaveta.setCellValueFactory(new PropertyValueFactory<>("gaveta"));
        colSubCom.setCellValueFactory(new PropertyValueFactory<>("sub_compartimento"));
        colMaterial.setCellValueFactory(new PropertyValueFactory<>("material"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colNumParte.setCellValueFactory(new PropertyValueFactory<>("numero_parte"));
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));
        colUMedida.setCellValueFactory(new PropertyValueFactory<>("unidad_medida"));
        colCaracteristicas.setCellValueFactory(new PropertyValueFactory<>("caracteristicas"));
        colFUso.setCellValueFactory(new PropertyValueFactory<>("f_uso"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCantidadMin.setCellValueFactory(new PropertyValueFactory<>("cantidad_min"));

        conexion = new Conexion();

        //Herramienta
        colID.setCellValueFactory(new PropertyValueFactory<>("cb_herramienta"));
        colHerramienta.setCellValueFactory(new PropertyValueFactory<>("herramienta"));
        colTipoHerramienta.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colCaracteristicasHerramienta.setCellValueFactory(new PropertyValueFactory<>("caracteristicas"));
        colFUsoHerramienta.setCellValueFactory(new PropertyValueFactory<>("frecuencia_de_uso"));
        colCantidadHerramienta.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCantidadMinHerramienta.setCellValueFactory(new PropertyValueFactory<>("cantidad_min"));
        rbMaterial.setToggleGroup(toggleGroupMostrar);
        rbHerramienta.setToggleGroup(toggleGroupMostrar);
        rbCodigo_Barras.setToggleGroup(toggleGroupBuscar);
        rbNombre.setToggleGroup(toggleGroupBuscar);
        rbTipo.setToggleGroup(toggleGroupBuscar);
        tableViewMats.getColumns().addAll(colCB,colTArmario,colGaveta,colSubCom,colMaterial,colTipo,colNumParte,colValor,colUMedida,colCaracteristicas,colFUso,colCantidad,colCantidadMin);
        ActualizarTabla(conexion.consultar("SELECT * FROM `material` INNER JOIN tipo_material ON material.id_material = tipo_material.id_material;"));
    }
    @FXML private void SalirConsultaMat() throws SQLException {
        Registro registro;
        if (tableViewMats.getSelectionModel().getSelectedItem() != null){
            cerrarVentana();
            if (rbMaterial.isSelected()){
                Articulo articulo= (Articulo) tableViewMats.getSelectionModel().getSelectedItem();
                registro = new Registro(articulo.getCodigo_barras(),articulo.getMaterial(),articulo.getTipo(),articulo.getValor(), articulo.getUnidad_medida(),1,false);
                if (articulo.getCantidad()!=0){
                    pedidosController.AgregarMaterial(registro);
                }else {
                    Error("No hay cantidad de este material");
                }
            }else {
                Herramienta herramienta = (Herramienta) tableViewMats.getSelectionModel().getSelectedItem();
                registro = new Registro(herramienta.getCb_herramienta(),herramienta.getHerramienta(), herramienta.getTipo(), 1,false);
                if (herramienta.getCantidad()!=0){
                    pedidosController.AgregarMaterial(registro);
                }else {
                    Error("No hay cantidad de este material");
                }
            }



        }

    }

    @FXML private void Busqueda() throws SQLException {
        String busqueda= txtBusqueda.getText();
        String criterio="";

        if (rbMaterial.isSelected()){
            if (rbCodigo_Barras.isSelected() && !busqueda.equals("")){
                criterio="cb_material";
            } else if (rbNombre.isSelected() && !busqueda.equals("")) {
                criterio="material";
            } else if (rbTipo.isSelected() && !busqueda.equals("")) {
                criterio="tipo";
            }
            if (!busqueda.equals("")){
                ActualizarTabla(conexion.consultar("SELECT * FROM `material` INNER JOIN tipo_material ON material.id_material = tipo_material.id_material WHERE `"+criterio+"` LIKE '%"+busqueda+"%'"));
            }else {
                ActualizarTabla(conexion.consultar("SELECT * FROM `material` INNER JOIN tipo_material ON material.id_material = tipo_material.id_material;"));
            }
        }else if (rbHerramienta.isSelected()){
            if (rbCodigo_Barras.isSelected() && !busqueda.equals("")){
                criterio="cb_herramienta";
            } else if (rbNombre.isSelected() && !busqueda.equals("")) {
                criterio="material";
            } else if (rbTipo.isSelected() && !busqueda.equals("")) {
                criterio="tipo";
            }
            if (!busqueda.equals("")){
                ActualizarTabla(conexion.consultar("SELECT * FROM `herramienta` INNER JOIN tipo_material ON herramienta.id_herramienta = tipo_material.id_material WHERE `"+criterio+"` LIKE '%"+busqueda+"%'"));
            }else {
                ActualizarTabla(conexion.consultar("SELECT * FROM `herramienta` INNER JOIN tipo_material ON herramienta.id_herramienta = tipo_material.id_material;"));
            }
        }

    }

    private void ActualizarTabla(ResultSet rsMats) throws SQLException {
        tableViewMats.getItems().clear();
        if (rbMaterial.isSelected()){
            while (rsMats.next()){
                Articulo material=new Articulo(rsMats.getLong("cb_material"), rsMats.getString("tipo_de_armario"), rsMats.getString("gaveta"), rsMats.getString("sub_compartimento"), rsMats.getString("material"),
                        rsMats.getString("tipo"), rsMats.getString("numero_parte"), rsMats.getString("valor"), rsMats.getString("unidad_de_medida"), rsMats.getString("caracteristicas"), rsMats.getString("frecuencia_de_uso"),
                        rsMats.getInt("cantidad"), rsMats.getInt("cantidad_min"));
                tableViewMats.getItems().add(material);

            }
        }else {
            while (rsMats.next()){
                Herramienta h= new Herramienta(rsMats.getLong("cb_herramienta"),
                        rsMats.getString("material"),
                        rsMats.getString("tipo"),
                        rsMats.getString("caracteristicas"),
                        rsMats.getString("frecuencia_de_uso"),
                        rsMats.getInt("cantidad"),
                        rsMats.getInt("cantidad_min"));
                tableViewMats.getItems().add(h);
            }
        }


    }
    @FXML private void CambiarColumnas() throws SQLException {
        tableViewMats.getColumns().clear();
        if (rbMaterial.isSelected()){
            tableViewMats.getColumns().addAll(colCB,colTArmario,colGaveta,colSubCom,colMaterial,colTipo,colNumParte,colValor,colUMedida,colCaracteristicas,colFUso,colCantidad,colCantidadMin);
            Busqueda();
         } else if (rbHerramienta.isSelected()) {
            tableViewMats.getColumns().addAll(colID,colHerramienta,colTipoHerramienta,colCaracteristicasHerramienta,colFUsoHerramienta,colCantidadHerramienta,colCantidadMinHerramienta);
            Busqueda();
        }

    }
    public void cerrarVentana() {
        Stage stage = (Stage) btnSalir.getScene().getWindow();
        stage.close();
    }
    private void Error(String mensaje){
        Alert alert= new Alert(Alert.AlertType.ERROR);
        alert.setContentText(mensaje);
        alert.setTitle("Error");
        alert.show();
    }
}


