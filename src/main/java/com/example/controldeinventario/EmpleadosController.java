package com.example.controldeinventario;

import com.example.controldeinventario.Datos.Herramienta;
import com.example.controldeinventario.Datos.Usuario;
import com.password4j.Hash;
import com.password4j.Password;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.text.RandomStringGenerator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

public class EmpleadosController {
    Conexion conexion;
    ToggleGroup toggleGroupBusqueda = new ToggleGroup();
    ToggleGroup toggleGroupSexo = new ToggleGroup();
    @FXML
    TabPane tabPaneVentana;
    @FXML
    Tab tabNew, tabSearch;
    @FXML
    Button btnNew, btnSave, btnEdit, btnDelete, btnCancel, btnExit;
    @FXML
    RadioButton rbID, rbNombre, rbRol;
    @FXML TextField txtBusqueda;
    @FXML TableView<Usuario> tableViewUsuarios;
    @FXML Label lblContador;
    @FXML TextField txtID, txtNombre, txtUsername;
    @FXML PasswordField txtPass, txtConfirmarPass;
    @FXML ComboBox<String> cbRoles;
    @FXML RadioButton rbMasculino, rbFemenino;

    TableColumn<Usuario,Integer> tableColumnID = new TableColumn<>("ID");
    TableColumn<Usuario,String> tableColumnNombre = new TableColumn<>("Nombre");
    TableColumn<Usuario,String> tableColumnSexo = new TableColumn<>("Sexo");
    TableColumn<Usuario,String> tableColumnUsername = new TableColumn<>("Nombre de usuario");
    TableColumn<Usuario,String> tableColumnRol = new TableColumn<>("Rol");

    @FXML protected void initialize() throws SQLException {
        ActivateBtn(false,true,false,true,false,false);
        Platform.runLater(() -> {
            txtBusqueda.requestFocus();
            txtBusqueda.selectEnd();
        });
        conexion = new Conexion();
        rbID.setToggleGroup(toggleGroupBusqueda);
        rbNombre.setToggleGroup(toggleGroupBusqueda);
        rbRol.setToggleGroup(toggleGroupBusqueda);
        rbMasculino.setToggleGroup(toggleGroupSexo);
        rbFemenino.setToggleGroup(toggleGroupSexo);

        tableColumnID.setCellValueFactory(new PropertyValueFactory<>("id_user"));
        tableColumnNombre.setCellValueFactory(new PropertyValueFactory<>("nombre_completo"));
        tableColumnSexo.setCellValueFactory(new PropertyValueFactory<>("sexo"));
        tableColumnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        tableColumnRol.setCellValueFactory(new PropertyValueFactory<>("nombre_rol"));

        tableViewUsuarios.getColumns().addAll(tableColumnID,tableColumnNombre,tableColumnSexo,tableColumnUsername,tableColumnRol);

        cbRoles.getItems().clear();
        ResultSet resultSetRoles = conexion.consultar("SELECT `nombre_rol`FROM `tipo_usuario`");
        while (resultSetRoles.next()){
            cbRoles.getItems().add(resultSetRoles.getString("nombre_rol"));
        }
        ActualizarTabla(conexion.consultar("SELECT * FROM `usuario`"));
    }

    private void ActualizarTabla(ResultSet rsUsuarios) throws SQLException {
        int cont=0;
        tableViewUsuarios.getItems().clear();
        while (rsUsuarios.next()){
            Usuario usuario = new Usuario(rsUsuarios.getInt("id_user"), rsUsuarios.getString("nombre_completo")
            ,rsUsuarios.getString("sexo"), rsUsuarios.getString("username"), rsUsuarios.getString("nombre_rol"));
            tableViewUsuarios.getItems().add(usuario);
            cont++;
        }
        lblContador.setText("Se cargaron "+cont+" empleados");

    }
    @FXML private void Busqueda() throws SQLException {
        String busqueda = txtBusqueda.getText();
        String criterio = "";
        if (rbID.isSelected() && !busqueda.equals("")){
            criterio="id_user";
        } else if (rbNombre.isSelected() && !busqueda.equals("")) {
            criterio="nombre_completo";
        } else if (rbRol.isSelected() && !busqueda.equals("")) {
            criterio="nombre_rol";
        }
        if (!busqueda.equals("")){
            ActualizarTabla(conexion.consultar("SELECT * FROM `usuario` WHERE `"+criterio+"` LIKE '%"+busqueda+"%'"));
        }else {
            ActualizarTabla(conexion.consultar("SELECT * FROM `usuario`"));
        }
    }

    @FXML private void NewEmpleado() throws SQLException {
        ActivateBtn(false,false,true,false,false,true);

        tabPaneVentana.getSelectionModel().select(tabNew);
        tabNew.setDisable(false);
        tabSearch.setDisable(true);
        CleanTextFields();
    }

    @FXML private void SaveEmpleado() throws SQLException {
        try {
            if (!VerifyTxt(cbRoles, txtUsername)){
                throw new Exception("Faltan campos por rellenar");
            }
            ResultSet resultSetRol = conexion.consultar("SELECT `nombre_rol` FROM `tipo_usuario` WHERE `nombre_rol`= ? LIMIT 1", cbRoles.getSelectionModel().getSelectedItem());
            if (!resultSetRol.next()){
                throw new Exception("Selecciona el rol");
            }
            if (txtID.getText().isEmpty() && txtID.getText().equals("2")){
                throw new Exception("No se puede editar al invitado");
            }
            if (!txtPass.getText().equals(txtConfirmarPass.getText())){
                throw new Exception("Las contraseñas deben coincidir");
            }
            if (!txtID.getText().isEmpty()){ //Editar
                ResultSet rsUsuario = conexion.consultar("SELECT `username` FROM `usuario` WHERE `id_user`= ?",txtID.getText());
                if (!rsUsuario.next()){
                    throw new Exception("Error desconocido, operación cancelada");
                }
                ResultSet rsUser= conexion.consultar("SELECT * FROM `usuario` WHERE `username`= ? AND `username` <> ?;", txtUsername.getText(),rsUsuario.getString("username"));
                if (rsUser.next()){
                    Error("Ya existe un usuario con este username");
                }
                if (!txtPass.getText().isEmpty()){
                    String sal = GenerarSal();
                    String passEncriptada = Encriptar(txtPass.getText(), sal);
                    if (txtID.getText().equals("1")){//Saber si es admin
                        conexion.insmodelim("UPDATE `usuario` SET `nombre_completo`= ?,`sexo`= ?,`username`= ?,`password`= ?,`sal`= ? WHERE `id_user`= ?", txtNombre.getText(), ((RadioButton) toggleGroupSexo.getSelectedToggle()).getText(), txtUsername.getText(), passEncriptada, sal, txtID.getText());
                        Exito("No se puede cambiar los roles del administrador/invitado, pero los otros datos fueron actualizados con exito");
                    }else {
                        conexion.insmodelim("UPDATE `usuario` SET `nombre_completo`= ?,`sexo`= ?,`username`= ?,`password`= ?,`sal`= ?,`nombre_rol`= ? WHERE `id_user`= ?",txtNombre.getText(), ((RadioButton) toggleGroupSexo.getSelectedToggle()).getText(), txtUsername.getText(), passEncriptada, sal, cbRoles.getSelectionModel().getSelectedItem(), txtID.getText());
                        Exito("Actualizado con exito");
                    }
                }else{
                    if (txtID.getText().equals("1")){//Saber si es admin
                        conexion.insmodelim("UPDATE `usuario` SET `nombre_completo`= ?,`sexo`= ?,`username`= ? WHERE `id_user`= ?", txtNombre.getText(), ((RadioButton) toggleGroupSexo.getSelectedToggle()).getText(), txtUsername.getText(), txtID.getText());
                        Exito("No se puede cambiar los roles del administrador/invitado, pero los otros datos fueron actualizados con exito");
                    }else {
                        conexion.insmodelim("UPDATE `usuario` SET `nombre_completo`= ?,`sexo`= ?,`username`= ?,`nombre_rol`= ? WHERE `id_user`= ?",txtNombre.getText(), ((RadioButton) toggleGroupSexo.getSelectedToggle()).getText(), txtUsername.getText(),  cbRoles.getSelectionModel().getSelectedItem(), txtID.getText());
                        Exito("Actualizado con exito");
                    }
                }
            }else {//Insertar
                String sal = GenerarSal();
                String passEncriptada = Encriptar(txtPass.getText(), sal);
                ResultSet rsUser = conexion.consultar("SELECT * FROM `usuario` WHERE `username`= ?;",txtUsername.getText());
                if (rsUser.next()){
                    throw new Exception("Ya existe un usuario con ese username");
                }
                conexion.insmodelim("INSERT INTO `usuario`(`nombre_completo`, `sexo`, `username`, `password`, `sal`, `nombre_rol`) VALUES (?, ?, ?, ?, ?, ?)",txtNombre.getText(),((RadioButton) toggleGroupSexo.getSelectedToggle()).getText(),txtUsername.getText(),passEncriptada,sal,cbRoles.getSelectionModel().getSelectedItem());
                Exito(txtNombre.getText()+" agregado");
            }
            CleanTextFields();
            tabPaneVentana.getSelectionModel().select(tabSearch);
            tabSearch.setDisable(false);
            tabNew.setDisable(true);
            ActivateBtn(false,true,false,true,false,false);
            ActualizarTabla(conexion.consultar("SELECT * FROM `usuario`"));


        }catch (Exception e){
            Error(e.getMessage());
        }
    }

    @FXML private void EditEmpleado() throws SQLException {
        try {
            if (tableViewUsuarios.getSelectionModel().getSelectedItem() == null){
                throw new Exception("Selecciona un registro");
            }
            if (tableViewUsuarios.getSelectionModel().getSelectedItem().getId_user() == 2){
                throw new Exception("No se puede editar al invitado");
            }
            Usuario usuario= tableViewUsuarios.getSelectionModel().getSelectedItem();
            ResultSet rsUsuario = conexion.consultar("SELECT * FROM `usuario` WHERE `id_user`= ?",String.valueOf(usuario.getId_user()));
            if (rsUsuario.next()){
                usuario = new Usuario(rsUsuario.getInt("id_user"), rsUsuario.getString("nombre_completo")
                        ,rsUsuario.getString("sexo"), rsUsuario.getString("username"), rsUsuario.getString("password"), "null", rsUsuario.getString("nombre_rol"));
                tabPaneVentana.getSelectionModel().select(tabNew);
                tabSearch.setDisable(true);
                tabNew.setDisable(false);
                txtID.setText(String.valueOf(usuario.getId_user()));
                txtNombre.setText(String.valueOf(usuario.getNombre_completo()));
                txtUsername.setText(String.valueOf(usuario.getUsername()));
                cbRoles.getSelectionModel().select(usuario.getNombre_rol());
                switch (usuario.getSexo()) {
                    case "Masculino" -> toggleGroupSexo.selectToggle(rbMasculino);
                    case "Femenino" -> toggleGroupSexo.selectToggle(rbFemenino);
                }
                ActivateBtn(true,false,true,false,false,true);
            }
        } catch (Exception e) {
            Error(e.getMessage());
        }
    }

    @FXML private void DeleteEmpleado() throws SQLException {
        if (tableViewUsuarios.getSelectionModel().getSelectedItem() != null){
            Usuario usuario= (Usuario) tableViewUsuarios.getSelectionModel().getSelectedItem();
            if (ConfirmarBorrar("Deseas borrar a "+usuario.getNombre_completo())){
                conexion.insmodelim("DELETE FROM `usuario` WHERE `id_user`= ?",String.valueOf(usuario.getId_user()));
                Exito("Registro borrado exitosamente");
                ActualizarTabla(conexion.consultar("SELECT * FROM `usuario`"));
            }

        }else {
            Error("Selecciona un registro");
        }


    }

    @FXML private void CancelEmpleado() throws SQLException {
        txtID.setText("");
        CleanTextFields();
        ActivateBtn(false,true,false,true,false,false);
        tabPaneVentana.getSelectionModel().select(tabSearch);
        tabSearch.setDisable(false);
        tabNew.setDisable(true);
    }

    @FXML private void ExitEmpleado(){
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }
    private boolean VerifyTxt(ComboBox<String> cbRol, TextField... campos){
        for (TextField campo : campos){
            if (campo.getText().isEmpty()){
                return false;
            }
        }
        return cbRol.getSelectionModel().getSelectedIndex() != -1;
    }
    private void ActivateBtn(boolean New, boolean save, boolean edit, boolean cancel, boolean exit, boolean delete) throws SQLException {
        if (LoginController.resultado.getInt("crud_empleados")==0){
            btnNew.setDisable(true);
            btnEdit.setDisable(true);
            btnDelete.setDisable(true);
        }else {
            btnNew.setDisable(New);
            btnEdit.setDisable(edit);
            btnDelete.setDisable(delete);
        }


        btnSave.setDisable(save);
        btnCancel.setDisable(cancel);
        btnExit.setDisable(exit);
    }

    private void CleanTextFields(){
        txtID.setText("");
        txtNombre.setText("");
        txtUsername.setText("");
        txtPass.setText("");
        txtConfirmarPass.setText("");

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

        Scene dialogScene = new Scene(vbox, 300, 100);
        dialog.setScene(dialogScene);
        dialog.showAndWait();

        return confirmar.get();
    }
    public String Encriptar(String password, String sal){
        Hash hash = Password.hash(password).addSalt(sal).withPBKDF2();
        return hash.getResult();
    }
    public String GenerarSal(){
        RandomStringGenerator stringGenerator = new RandomStringGenerator.Builder().withinRange('a','z').build();
        return stringGenerator.generate(32);
    }
    private void Error(String mensaje){
        Alert alert= new Alert(Alert.AlertType.ERROR);
        alert.setContentText(mensaje);
        alert.setTitle("Error");
        alert.show();
    }
    private void Exito(String mensaje){
        Alert alert= new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText(mensaje);
        alert.setTitle("Exito");
        alert.show();
    }
}
