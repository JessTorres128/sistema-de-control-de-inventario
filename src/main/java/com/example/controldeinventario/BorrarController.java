package com.example.controldeinventario;

import com.password4j.Hash;
import com.password4j.Password;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.text.RandomStringGenerator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

public class BorrarController {
    @FXML CheckBox checkBoxHorarios, checkBoxUsuarios, checkBoxAlumnos,checkBoxRoles, checkBoxPedidos,checkBoxHerramientas, checkBoxMateriales,checkBoxTipos_Material;
    Conexion conexion;

    @FXML protected void initialize(){
        conexion = new Conexion();
    }

    @FXML private void Borrar() throws SQLException {
        if(CheckConfirmar()){
            if (ConfirmarBorrar("¿Esta seguro de realizar esta operación?", "Si", "No")){

                if (checkBoxMateriales.isSelected() || checkBoxHerramientas.isSelected() || checkBoxPedidos.isSelected() || checkBoxTipos_Material.isSelected()){
                    if (ConfirmarBorrar("¿Desea hacer una copia de seguridad de estos registros?", "Si", "No")){
                        GenerarController generarController = new GenerarController();
                        generarController.GenerarExcel(checkBoxMateriales.isSelected(), checkBoxHerramientas.isSelected(),checkBoxPedidos.isSelected(), checkBoxTipos_Material.isSelected());
                    }else if (ConfirmarBorrar("¿Esta seguro?", "No, realizar copia de seguridad", "Si, continuar sin realizar copia de seguridad")){
                        GenerarController generarController = new GenerarController();
                        generarController.GenerarExcel(checkBoxMateriales.isSelected(), checkBoxHerramientas.isSelected(),checkBoxPedidos.isSelected(), checkBoxTipos_Material.isSelected());
                    }
                }
                if (checkBoxUsuarios.isSelected()){
                    conexion.insmodelim("DELETE FROM `usuario` WHERE 1");
                    String sal= GenerarSal();
                    conexion.insmodelim("INSERT INTO `usuario`(`id_user`, `nombre_completo`, `sexo`, `username`, `password`, `sal`, `nombre_rol`) VALUES ('1','Admin','M','admin','"+Encriptar("admin", sal)+"', '"+sal+"', 'Administrador')");
                    sal = GenerarSal();
                    conexion.insmodelim("INSERT INTO `usuario`(`id_user`, `nombre_completo`, `sexo`, `username`, `password`, `sal`, `nombre_rol`) VALUES ('2','Invitado','M','invitado','"+Encriptar("invitado", sal)+"', '"+sal+"', 'Invitado')");
                    if (checkBoxRoles.isSelected()){
                        conexion.insmodelim("DELETE FROM `tipo_usuario` WHERE 1");
                        conexion.insmodelim("INSERT INTO `tipo_usuario`(`id_rol`, `nombre_rol`, `create_material`, `update_material`, `delete_material`, `create_herramienta`, `update_herramienta`, `delete_herramienta`, `crud_pedido`, `create_t_articulo`, `update_t_articulo`, `delete_t_articulo`, `crud_roles`, `crud_empleados`, `generar_bd`, `respaldar_bd`, `eliminar_bd`) VALUES ('1','Administrador','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1')");
                        conexion.insmodelim("INSERT INTO `tipo_usuario`(`id_rol`, `nombre_rol`, `create_material`, `update_material`, `delete_material`, `create_herramienta`, `update_herramienta`, `delete_herramienta`, `crud_pedido`, `create_t_articulo`, `update_t_articulo`, `delete_t_articulo`, `crud_roles`, `crud_empleados`, `generar_bd`, `respaldar_bd`, `eliminar_bd`) VALUES ('2','Invitado','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0')");
                        sal= GenerarSal();
                        conexion.insmodelim("INSERT INTO `usuario`(`id_user`, `nombre_completo`, `sexo`, `username`, `password`, `sal`, `nombre_rol`) VALUES ('1','Admin','M','admin','"+Encriptar("admin", sal)+"', '"+sal+"', 'Administrador')");
                        sal = GenerarSal();
                        conexion.insmodelim("INSERT INTO `usuario`(`id_user`, `nombre_completo`, `sexo`, `username`, `password`, `sal`, `nombre_rol`) VALUES ('2','Invitado','M','invitado','"+Encriptar("invitado", sal)+"', '"+sal+"', 'Invitado')");
                    }
                }
                if (checkBoxHorarios.isSelected()){
                    conexion.insmodelim("DELETE FROM `materia` WHERE 1");
                    if (checkBoxAlumnos.isSelected()){
                        conexion.insmodelim("DELETE FROM `alumnos` WHERE 1");
                    }
                }
                if (checkBoxMateriales.isSelected()){
                    conexion.insmodelim("DELETE FROM `material` WHERE 1");
                    if (checkBoxTipos_Material.isSelected()){
                        conexion.insmodelim("DELETE FROM `tipo_material` WHERE `tipo_material`='Material Consumible' or `tipo_material`='Material Fijo'");
                    }
                }

                if (checkBoxHerramientas.isSelected()){
                    conexion.insmodelim("DELETE FROM `herramienta` WHERE 1");
                    if (checkBoxTipos_Material.isSelected()){
                        conexion.insmodelim("DELETE FROM `tipo_material` WHERE `tipo_material`='Herramienta'");
                    }
                }
                if (checkBoxPedidos.isSelected()){
                    ResultSet rsPendientes = conexion.consultar("SELECT * FROM `pedido_material` WHERE `estado`='Pendiente'");
                    while (rsPendientes.next()){
                        // System.out.println("cantidad pendiente: "+rsPendientes.getInt("cantidad"));
                        ResultSet rsMat = conexion.consultar("SELECT * FROM `material` WHERE `cb_material`= ?",rsPendientes.getString("cb_material"));
                        ResultSet rsHerramienta = conexion.consultar("SELECT * FROM `herramienta` WHERE `cb_herramienta`= ?", rsPendientes.getString("cb_material"));
                        if (rsMat.next()){
                            // System.out.println("cantidad material: "+rsMat.getInt("cantidad"));
                            conexion.insmodelim("UPDATE `material` SET `cantidad`= ? WHERE `cb_material`= ?", String.valueOf((rsPendientes.getInt("cantidad")+rsMat.getInt("cantidad"))), rsMat.getString("cb_material"));
                        } else if (rsHerramienta.next()) {
                            // System.out.println("cantidad herramienta: "+rsHerramienta.getInt("cantidad"));
                            conexion.insmodelim("UPDATE `herramienta` SET `cantidad`= ? WHERE `cb_herramienta`= ? ", String.valueOf((rsHerramienta.getInt("cantidad")+rsPendientes.getInt("cantidad"))), rsHerramienta.getString("cb_herramienta"));
                        }
                    }
                    conexion.insmodelim("DELETE FROM `pedido` WHERE 1");
                    conexion.insmodelim("DELETE FROM `pedido_material` WHERE 1");
                }
                Exito("Se han eliminado los recursos seleccionados");
            }else{
                Error("Se ha cancelado la operación");
            }
        }else {
            Error("No se ha seleccionado ningun campo");
        }

    }
    public String Encriptar(String password, String sal){
        Hash hash = Password.hash(password).addSalt(sal).withPBKDF2();
        return hash.getResult();
    }
    public String GenerarSal(){
        RandomStringGenerator stringGenerator = new RandomStringGenerator.Builder().withinRange('a','z').build();
        return stringGenerator.generate(32);
    }

    public boolean ConfirmarBorrar(String mensaje, String txtConfirmar, String txtCancelar) {

        AtomicBoolean confirmar = new AtomicBoolean(false);
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Confirmar acción");

        Label lblmsg = new Label(mensaje);
        Button btnConfirmar = new Button(txtConfirmar);
        Button btnCancelar = new Button(txtCancelar);

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

        Scene dialogScene = new Scene(vbox, 350, 120);
        dialog.setScene(dialogScene);
        dialog.showAndWait();

        return confirmar.get();
    }
    @FXML private void CheckBoxChange(){
        if(checkBoxRoles.isSelected()){
            checkBoxUsuarios.setSelected(true);
        }
        if (checkBoxTipos_Material.isSelected()){
            checkBoxMateriales.setSelected(true);
            checkBoxHerramientas.setSelected(true);
        }
        if (checkBoxAlumnos.isSelected()){
            checkBoxHorarios.setSelected(true);
        }
    }
    private boolean CheckConfirmar(){
        return checkBoxAlumnos.isSelected() || checkBoxHerramientas.isSelected() || checkBoxHorarios.isSelected() || checkBoxMateriales.isSelected() || checkBoxPedidos.isSelected() || checkBoxRoles.isSelected() || checkBoxTipos_Material.isSelected() || checkBoxUsuarios.isSelected();
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
