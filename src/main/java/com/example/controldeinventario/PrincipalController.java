package com.example.controldeinventario;

import com.example.controldeinventario.Datos.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Material;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PrincipalController {
    Stage ventanaSecundaria;
    @FXML
    MenuItem menuItemIniciarSesion, menuItemCerrarSesion, menuItemCerrarPrograma;
    @FXML
    MenuItem menuItemMateriales, menuItemHerramientas;
    @FXML
    MenuItem menuItemPedidos;
    @FXML
    MenuItem menuItemTMateriales;
    @FXML
    MenuItem menuItemRoles, menuItemEmpleados;
    @FXML
    MenuItem menuItemGenerarBD, menuItemEliminarBD, menuItemRestaurarBD;

    Conexion conexion;

    @FXML
    protected void initialize() throws SQLException, IOException {
        conexion = new Conexion();
        HabilitarMenus(LoginController.resultado);
        // Image imgSearch= new Image("",25,25,false,true);menuItemB.setGraphic(new ImageView(imgSearch));



    }

    private void CambiarVista(Vistas vista) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(vista.getRecursoName()));
            Parent root = fxmlLoader.load();
            AbrirVentana(root);
        }catch (Exception e){
            Error(e.getMessage());
        }

    }


    //Controles del menú
    @FXML
    private void IngresarArticulos() {
        CambiarVista(Vistas.ARTICULOS);


    }
                      //   E      X     C      E     L     L
    @FXML
    private void ExportarBD() throws IOException, SQLException {

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Materiales");
        Sheet sheet1 = workbook.createSheet("Herramientas");
        Sheet sheet2 = workbook.createSheet("Pedidos");

        Font headerFont = workbook.createFont();
        headerFont.setFontName("Arial");
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        CellStyle dataStyle = workbook.createCellStyle();
        CellStyle dataStyleColor = workbook.createCellStyle();
        dataStyleColor.setFillForegroundColor(IndexedColors.RED.getIndex()); // Cambia "RED" al color deseado
        dataStyleColor.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        dataStyle.setAlignment(HorizontalAlignment.CENTER);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        // Crear una fila para el título
        Row titleRow = sheet.createRow(0);
        Row titleRow1 = sheet1.createRow(0);
        Row titleRow2 = sheet2.createRow(0);


        // Crear una celda para el título
        Cell titleCell = titleRow.createCell(0);
        Cell titleCell1 = titleRow1.createCell(0);
        Cell titleCell2 = titleRow2.createCell(0);
        titleCell.setCellValue("Inventario de materiales");
        titleCell1.setCellValue("Inventario de herramientas");
        titleCell2.setCellValue("Registro de pedidos");

        // Combinar las celdas para crear una celda de título grande
        CellRangeAddress titleRange = new CellRangeAddress(0, 0, 0, 11);
        sheet.addMergedRegion(titleRange);
        sheet1.addMergedRegion(titleRange);
        sheet2.addMergedRegion(titleRange);

        // Establecer el estilo de la celda de título
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();

        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);
//        titleStyle.setAlignment((short) 2);
        titleCell.setCellStyle(titleStyle);
        titleCell1.setCellStyle(titleStyle);
        titleCell2.setCellStyle(titleStyle);


        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);


        sheet.autoSizeColumn(0); // ajustar automáticamente el ancho de la columna 0
        sheet1.autoSizeColumn(0); // ajustar automáticamente el ancho de la columna 0
        sheet2.autoSizeColumn(0); // ajustar automáticamente el ancho de la columna 0











            // Crear el archivo de selección


    }
    @FXML private void IngresarHerramientas() {
        CambiarVista(Vistas.HERRAMIENTAS);



    }
    @FXML private void IngresarLogin() {
        try {
            LoginController.resultado = null;
            HelloApplication.primarystage.close();
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(Vistas.LOGIN.getRecursoName()));
            Scene scene = new Scene(fxmlLoader.load(), 700, 500);
            HelloApplication.primarystage.setTitle("Inventario");
            HelloApplication.primarystage.setScene(scene);
            HelloApplication.primarystage.show();
            HelloApplication.primarystage.setResizable(false);
        }catch (Exception e){
            Error(e.getMessage());
        }

    }
    @FXML private void IngresarRoles() {
        CambiarVista(Vistas.ROLES);

    }
    @FXML private void IngresarEmpleados() {
        CambiarVista(Vistas.EMPLEADOS);

    }
    @FXML private void IngresarPedidos() {
        CambiarVista(Vistas.PEDIDOS);

    }
    @FXML private void IngresarTipos() {
        CambiarVista(Vistas.TIPOS);
    }
    @FXML private void IngresarGenerar() {
        CambiarVista(Vistas.GENERAR);
    }
    @FXML private void IngresarRestaurar() {
        CambiarVista(Vistas.RESTAURAR);
    }
    @FXML private void IngresarBorrar() {
        CambiarVista(Vistas.BORRAR);
    }
    @FXML public void CerrarVentana(){
        Platform.exit();
        System.exit(0);
    }
    private void AbrirVentana(Parent root){
        ventanaSecundaria = new Stage();
        ventanaSecundaria.initModality(Modality.APPLICATION_MODAL);
        ventanaSecundaria.initOwner(HelloApplication.primarystage);
        ventanaSecundaria.setScene(new Scene(root));
        ventanaSecundaria.setResizable(false);
        ventanaSecundaria.show();

    }
    private void HabilitarMenus(ResultSet resultSetUsuario) {
        try {
            menuItemIniciarSesion.setDisable(!"invitado".equals(resultSetUsuario.getString("username")));
            menuItemCerrarSesion.setDisable("invitado".equals(resultSetUsuario.getString("username")));
            menuItemGenerarBD.setDisable(resultSetUsuario.getInt("generar_bd") != 1);
            menuItemRestaurarBD.setDisable(resultSetUsuario.getInt("respaldar_bd") != 1);
            menuItemEliminarBD.setDisable(resultSetUsuario.getInt("eliminar_bd") != 1);
        } catch (Exception e) {
            Error(e.getMessage());
        }

    }


    private void Error(String mensaje){
        Alert alert= new Alert(Alert.AlertType.ERROR);
        alert.setContentText(mensaje);
        alert.setTitle("Error");
        alert.show();
    }
}
