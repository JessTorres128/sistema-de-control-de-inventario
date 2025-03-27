package com.example.controldeinventario;

import com.password4j.Hash;
import com.password4j.PBKDF2Function;
import com.password4j.Password;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.text.RandomStringGenerator;


import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class LoginController {
@FXML TextField txtuser;
@FXML TextField txtpassword;
    static ResultSet resultado;
Conexion conexion;

@FXML protected void initialize() throws SQLException, IOException {
    conexion=new Conexion();
    RevisarSiHayUsuariosCreados();

}
    private void Logear(String user, String pass){
        ResultSet usuario = conexion.consultar("SELECT `username`,`password`,`sal` FROM `usuario` where username = ? LIMIT 1", user);
        try {
            if (!usuario.next()){
                throw new Exception("No se encontro un usuario con ese nombre de usuario, revisa la sintaxis");
            }
            //System.out.println("el usuario es "+user+" la contraseña de la bd "+usuario.getString("password")+" la sal es "+usuario.getString("sal"));
            //System.out.println("el usuario es "+user+" la contraseña encriptada "+passEncriptada+" la sal es "+RecuperarSal(user));
            if (!CheckPassword(usuario.getString("password"), usuario.getString("sal"), pass)){
                throw new Exception("Datos incorrectos, compruebe la contraseña");
            }
            resultado = conexion.consultar("SELECT tipo_usuario.*, usuario.username FROM `usuario` INNER JOIN tipo_usuario ON usuario.nombre_rol = tipo_usuario.nombre_rol WHERE username= ? LIMIT 1", user);
            if (!resultado.next()){
                throw new Exception("No se pudo encontrar al usuario, intente de nuevo");
            }
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Principal.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
            HelloApplication.primarystage.setX(100);
            HelloApplication.primarystage.setY(50);
            HelloApplication.primarystage.setTitle("-Instituto Tecnológico Superior de Nuevo Casas Grandes-");
            HelloApplication.primarystage.setScene(scene);
            HelloApplication.primarystage.setResizable(false);
        }catch (Exception e){
            Error(e.getMessage());
        }
    }
    @FXML private void IngresarLogin() {
        String user = txtuser.getText();
        String pass = txtpassword.getText();
        Logear(user, pass);
    }

    @FXML private void IngresarLoginInvitado() {
        String user = "invitado";
        String pass = "invitado";
        Logear(user, pass);
    }
    public void RevisarSiHayUsuariosCreados() throws SQLException{
        ResultSet usuarios = conexion.consultar("SELECT COUNT(*) AS usuarios FROM `usuario`");
        usuarios.next();
        if(usuarios.getInt(1) == 0){
            conexion.insmodelim("INSERT INTO `tipo_usuario`(`id_rol`, `nombre_rol`, `create_material`, `update_material`, `delete_material`, `create_herramienta`, `update_herramienta`, `delete_herramienta`, `crud_pedido`, `create_t_articulo`, `update_t_articulo`, `delete_t_articulo`, `crud_roles`, `crud_empleados`, `generar_bd`, `respaldar_bd`, `eliminar_bd`) VALUES ('1','Administrador','1','1','1','1','1','1','1','1','1','1','1','1','1','1','1')");
            conexion.insmodelim("INSERT INTO `tipo_usuario`(`id_rol`, `nombre_rol`, `create_material`, `update_material`, `delete_material`, `create_herramienta`, `update_herramienta`, `delete_herramienta`, `crud_pedido`, `create_t_articulo`, `update_t_articulo`, `delete_t_articulo`, `crud_roles`, `crud_empleados`, `generar_bd`, `respaldar_bd`, `eliminar_bd`) VALUES ('2','Invitado','0','0','0','0','0','0','0','0','0','0','0','0','0','0','0')");
            String sal = GenerarSal();
            conexion.insmodelim("INSERT INTO `usuario`(`id_user`, `nombre_completo`, `sexo`, `username`, `password`, `sal`, `nombre_rol`) VALUES ('1','Admin','M','admin', '"+ Encriptar("admin", sal) +"', '"+sal+"','Administrador')");
            sal = GenerarSal();
            conexion.insmodelim("INSERT INTO `usuario`(`id_user`, `nombre_completo`, `sexo`, `username`, `password`, `sal`, `nombre_rol`) VALUES ('2','Invitado','M','invitado','"+ Encriptar("invitado", sal) +"', '"+sal+"','Invitado')");
            sal = "";
            Exito("Se crearon los usuarios de admin y invitado, ahora se puede acceder con ambos, es recomendable cambiar la contraseña del administrador (admin, admin)");
        }
    }
    public boolean CheckPassword(String passFromDB, String salt, String passByUser){
        return Password.check(passByUser, passFromDB).addSalt(salt).withPBKDF2();
    }
    public String Encriptar(String password, String sal){
        Hash hash = Password.hash(password).addSalt(sal).withPBKDF2();
        return hash.getResult();
    }
    public String RecuperarSal(String username) throws SQLException {
        ResultSet sal = conexion.consultar("SELECT `password`, `sal` FROM `usuario` WHERE username = ? LIMIT 1", username);
        if (sal.next()){
            return sal.getString("sal");
        }else{
            return "null";
        }
    }
    public String GenerarSal(){
        RandomStringGenerator stringGenerator = new RandomStringGenerator.Builder().withinRange('a','z').build();
        return stringGenerator.generate(32);
    }

    private void Exito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeight(300);
        alert.setContentText(mensaje);
        alert.setTitle("Exito");
        alert.show();
    }
    private void Error(String mensaje){
        Alert alert= new Alert(Alert.AlertType.ERROR);
        alert.setContentText(mensaje);
        alert.setTitle("Error");
        alert.show();
    }


}