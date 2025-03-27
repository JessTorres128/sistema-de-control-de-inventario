package com.example.controldeinventario;

import com.example.controldeinventario.Datos.TipoArticulo;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

public class TipoArticuloController {
    Conexion conexion;
    ToggleGroup toggleGroupBusqueda= new ToggleGroup();
    ToggleGroup toggleGroupTMaterial= new ToggleGroup();

    @FXML TabPane tabPaneVentana;
    @FXML Tab tabNew, tabSearch;

    @FXML CheckBox checkBoxHerramienta, checkBoxMaterial;
    @FXML Button btnNew, btnSave, btnEdit, btnDelete, btnCancel, btnExit;
    @FXML RadioButton radioButtonID, radioButtonNombre;
    @FXML TextField txtBusqueda;
    @FXML
    TableView<TipoArticulo> tableViewTMateriales = new TableView<>();
    @FXML Label lblContador;

    @FXML RadioButton rbMaterial, rbHerramienta,rbEquipo;
    @FXML TextField txtID, txtNombre;

    TableColumn<TipoArticulo, Integer> tableColumnID = new TableColumn<>("No");
    TableColumn<TipoArticulo, String> tableColumnNombre = new TableColumn<>("Nombre");
    TableColumn<TipoArticulo, String> tableColumnTipo = new TableColumn<>("Tipo de material");

    @FXML protected void initialize() throws SQLException {
        ActivateBtn(false,true,false,true,false,false);
        Platform.runLater(() -> {
            txtBusqueda.requestFocus();
            txtBusqueda.selectEnd();
        });
        tableColumnID.setCellValueFactory(new PropertyValueFactory<>("id"));
        tableColumnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        tableColumnTipo.setCellValueFactory(new PropertyValueFactory<>("t_material"));

        tableViewTMateriales.getColumns().addAll(tableColumnID,tableColumnNombre,tableColumnTipo);

        radioButtonID.setToggleGroup(toggleGroupBusqueda);
        radioButtonNombre.setToggleGroup(toggleGroupBusqueda);
        rbMaterial.setToggleGroup(toggleGroupTMaterial);
        rbHerramienta.setToggleGroup(toggleGroupTMaterial);
        rbEquipo.setToggleGroup(toggleGroupTMaterial);
        conexion = new Conexion();
        ActualizarTabla(conexion.consultar("SELECT * FROM `tipo_material`"));
    }

    private void ActualizarTabla(ResultSet rsTipos) throws SQLException {
        int cont=0;
        tableViewTMateriales.getItems().clear();
        while (rsTipos.next()){
            TipoArticulo tipoArticulo = new TipoArticulo(rsTipos.getInt("id_material"), rsTipos.getString("material"),rsTipos.getString("tipo_material"));
            tableViewTMateriales.getItems().add(tipoArticulo);
            cont++;
        }
        lblContador.setText("Se cargaron "+cont+" tipos de articulos");
    }

    @FXML private void NewTipoArticulo() throws SQLException {
        ActivateBtn(false,false,true,false,false,true);
        tabPaneVentana.getSelectionModel().select(tabNew);
        tabNew.setDisable(false);
        tabSearch.setDisable(true);
        CleanTextFields();
    }

    @FXML private void SaveTipoArticulo() throws SQLException {
        if (!txtNombre.getText().isEmpty()){
            ResultSet resultSetUpdate = conexion.consultar("SELECT * FROM `tipo_material` WHERE `id_material`= ? LIMIT 1", txtID.getText());
            if (resultSetUpdate.next()){
                conexion.insmodelim("UPDATE `tipo_material` SET `material`= ?,`tipo_material`= ? WHERE `id_material`= ?", txtNombre.getText(), ((RadioButton) toggleGroupTMaterial.getSelectedToggle()).getText(), txtID.getText());

            }else {
                conexion.insmodelim("INSERT INTO `tipo_material`(`material`, `tipo_material`) VALUES (?, ?)",txtNombre.getText(),((RadioButton) toggleGroupTMaterial.getSelectedToggle()).getText());
            }
            tabPaneVentana.getSelectionModel().select(tabSearch);
            tabSearch.setDisable(false);
            tabNew.setDisable(true);
            ActivateBtn(false,true,false,true,false,false);
            Busqueda();
        }else{
            Error("Introduce el nombre del tipo de material");
        }
    }

    @FXML private void EditTipoArticulo() {
        try {
            if (tableViewTMateriales.getSelectionModel().getSelectedItem() == null){
                throw new Exception("Selecciona un registro");
            }
            TipoArticulo tipoArticulo= tableViewTMateriales.getSelectionModel().getSelectedItem();
            tabPaneVentana.getSelectionModel().select(tabNew);
            tabSearch.setDisable(true);
            tabNew.setDisable(false);
            txtID.setText(String.valueOf(tipoArticulo.getId()));
            txtNombre.setText(tipoArticulo.getNombre());
            switch (tipoArticulo.getT_material()) {
                case "Material Consumible" -> toggleGroupTMaterial.selectToggle(rbMaterial);
                case "Herramienta" -> toggleGroupTMaterial.selectToggle(rbHerramienta);
                case "Material Fijo" -> toggleGroupTMaterial.selectToggle(rbEquipo);
            }
            ActivateBtn(true,false,true,false,false,true);
        } catch (Exception e) {
            Error(e.getMessage());
        }
    }

    @FXML private void DeleteTipoArticulo() throws SQLException {
        try {
            if (tableViewTMateriales.getSelectionModel().getSelectedItem() == null){
                throw new Exception("Selecciona un registro");
            }
            TipoArticulo tipoArticulo= tableViewTMateriales.getSelectionModel().getSelectedItem();
            if (ConfirmarBorrar("Deseas borrar "+tipoArticulo.getNombre()+", realizar esta accion \n tambien borrará a los registros que tengan este tipo de articulo")){
                conexion.insmodelim("DELETE FROM `tipo_material` WHERE `id_material`= ?", tipoArticulo.getId());
                Exito("Registro borrado exitosamente");
                Busqueda();

            }else{
                throw new Exception("Operación cancelada");
            }
        } catch (Exception e) {
            Error(e.getMessage());
        }
    }

    @FXML private void CancelTipoArticulo() throws SQLException {
        txtID.setText("");
        CleanTextFields();
        ActivateBtn(false,true,false,true,false,false);
        tabPaneVentana.getSelectionModel().select(tabSearch);
        tabSearch.setDisable(false);
        tabNew.setDisable(true);
    }

    @FXML private void ExitTipoArticulo(){
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    @FXML private void Busqueda() throws SQLException {
        String busqueda= txtBusqueda.getText();
        String criterio="";
        if (radioButtonID.isSelected() && !busqueda.isEmpty()){
            criterio="id_material";
        } else if (radioButtonNombre.isSelected() && !busqueda.isEmpty()) {
            criterio="material";
        }
        if (checkBoxHerramienta.isSelected() && checkBoxMaterial.isSelected() && !busqueda.isEmpty()){
            ActualizarTabla(conexion.consultar("SELECT * FROM `tipo_material` WHERE `"+criterio+"` LIKE '%"+busqueda+"%'"));
        } else if (!checkBoxHerramienta.isSelected() && !checkBoxMaterial.isSelected()) {
            ActualizarTabla(conexion.consultar("SELECT * FROM `tipo_material` WHERE 0"));
        } else if (checkBoxMaterial.isSelected() && !busqueda.isEmpty() && !checkBoxHerramienta.isSelected()) {
            ActualizarTabla(conexion.consultar("SELECT * FROM `tipo_material` WHERE `"+criterio+"` LIKE '%"+busqueda+"%' AND tipo_material LIKE '%Material%'"));
        } else if (checkBoxHerramienta.isSelected() && !checkBoxMaterial.isSelected() && !busqueda.isEmpty()) {
            ActualizarTabla(conexion.consultar("SELECT * FROM `tipo_material` WHERE `"+criterio+"` LIKE '%"+busqueda+"%' AND tipo_material='Herramienta'"));
        } else if (checkBoxMaterial.isSelected() && busqueda.isEmpty() && !checkBoxHerramienta.isSelected()) {
            ActualizarTabla(conexion.consultar("SELECT * FROM `tipo_material` WHERE tipo_material LIKE '%Material%'"));
        } else if (checkBoxHerramienta.isSelected() && !checkBoxMaterial.isSelected() && busqueda.isEmpty()) {
            ActualizarTabla(conexion.consultar("SELECT * FROM `tipo_material` WHERE tipo_material='Herramienta'"));
        }else {
            ActualizarTabla(conexion.consultar("SELECT * FROM `tipo_material` WHERE 1;"));
        }
    }

    public boolean ConfirmarBorrar(String mensaje) {
        AtomicBoolean confirmar = new AtomicBoolean(false);
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Confirmar acción");

        Label lblmsg = new Label(mensaje);
        Button btnConfirmar = new Button("Aceptar");
        Button btnCancelar = new Button("Cancelar");

        btnConfirmar.setOnAction(e -> {
            confirmar.set(true);
            dialog.close();
        });

        btnCancelar.setOnAction(e -> {
            confirmar.set(false);
            dialog.close();
        });

        VBox vbox = new VBox(lblmsg, btnConfirmar, btnCancelar);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);

        Scene dialogScene = new Scene(vbox, 300, 150);
        dialog.setScene(dialogScene);
        dialog.showAndWait();

        return confirmar.get();
    }

    private void ActivateBtn(boolean New, boolean save, boolean edit, boolean cancel, boolean exit, boolean delete) throws SQLException {
        if (LoginController.resultado.getInt("create_t_articulo")==0){
            btnNew.setDisable(true);
        }else {btnNew.setDisable(New);}
        if (LoginController.resultado.getInt("update_t_articulo")==0){
            btnEdit.setDisable(true);
        }else {btnEdit.setDisable(edit);}
        if (LoginController.resultado.getInt("delete_t_articulo")==0){
            btnDelete.setDisable(true);
        }else {btnDelete.setDisable(delete);}

        btnSave.setDisable(save);
        btnCancel.setDisable(cancel);
        btnExit.setDisable(exit);
    }

    private void CleanTextFields(){
        txtID.setText("");
        txtNombre.setText("");
    }

    private void Error(String mensaje){
        Alert alert= new Alert(Alert.AlertType.ERROR);
        alert.setContentText(mensaje);
        alert.setTitle("Error");
        alert.show();
    }
    private void Exito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(mensaje);
        alert.setTitle("Exito");
    }

}
