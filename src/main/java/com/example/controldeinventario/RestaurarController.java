package com.example.controldeinventario;

import com.example.controldeinventario.Datos.Articulo;
import com.example.controldeinventario.Datos.Herramienta;
import com.example.controldeinventario.Datos.TipoArticulo;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import org.apache.poi.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.awt.*;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RestaurarController {
    Conexion conexion;
    String ruta="";
    @FXML HBox hboxMat,hboxHerra;
    @FXML Label lblArchivo;
    @FXML CheckBox cbMat, cbHerramienta;
    @FXML TextField txtNHojaMat,txtNHojaHerra;
    @FXML TextField txtNColMat,txtNColHerra;
    @FXML Button btnCargar;


    @FXML protected void initialize(){
        conexion= new Conexion();
        ruta="";
        hboxMat.setDisable(true);
        hboxHerra.setDisable(true);
        btnCargar.setDisable(true);
        cbMat.setDisable(true);
        cbHerramienta.setDisable(true);
    }

    @FXML private void ElegirArchivo() throws IOException {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Elegir archivo Excel");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Todos los archivos", "*.*"));
        File selectedDirectory = fileChooser.showOpenDialog(null);


        if (selectedDirectory != null) {
            ruta=selectedDirectory.getAbsolutePath();
            if (selectedDirectory.getName().endsWith(".xlsx") || selectedDirectory.getName().endsWith(".xls") || selectedDirectory.getName().endsWith(".xlsx") ){ // EXTENSIONES DE EXCEL <-------------------------------------------------------------

                lblArchivo.setText("Archivo cargado: "+selectedDirectory.getName());
                cbMat.setDisable(false);
                cbHerramienta.setDisable(false);
                btnCargar.setDisable(false);
                Exito("Archivo cargado");




            }else {
                Error("No se ha podido cargar el archivo");
            }


        } else {
            Error("El usuario canceló la selección de carpeta.");
        }
    }
    private Workbook DetectarArchivo(String ruta) throws IOException {
        if (ruta.endsWith(".xls")){
            return new HSSFWorkbook(new FileInputStream(ruta));
        } else if (ruta.endsWith(".xlsx")) {
            return new XSSFWorkbook(new FileInputStream(ruta));
        }else {
            Error("El archivo no es valido");
            throw new IllegalArgumentException("El archivo no es valido");
        }
    }

    @FXML private void Cargar() throws IOException, SQLException {
        Workbook workbook = DetectarArchivo(ruta);
        if (VerificarTxts(true,txtNColHerra,txtNHojaHerra,txtNHojaMat,txtNColMat)){
            if (cbMat.isSelected()){
                if (VerificarTxts(false,txtNHojaMat,txtNColMat)){
                    int colInicial=-1;
                    Sheet sheet = workbook.getSheetAt(Integer.parseInt(txtNHojaMat.getText())-1);
                    for (Row row : sheet){
                        if (row.getRowNum()+1 == Integer.parseInt(txtNColMat.getText())){
                            for (Cell cell : row){
                                if (!cell.getStringCellValue().isEmpty()){
                                    colInicial = cell.getColumnIndex();
                                    break;
                                }
                            }

                        }else if (colInicial !=-1){
                            Cell cb = row.getCell(colInicial);
                            Cell tipodeArmario = row.getCell(colInicial+1);
                            Cell gaveta = row.getCell(colInicial+2);
                            Cell sub = row.getCell(colInicial+3);
                            Cell material = row.getCell(colInicial+4);
                            Cell tipo = row.getCell(colInicial+5);
                            Cell nparte = row.getCell(colInicial+6);
                            Cell valor = row.getCell(colInicial+7);
                            Cell u_medida = row.getCell(colInicial+8);
                            Cell caracteristicas = row.getCell(colInicial+9);
                            Cell f_uso = row.getCell(colInicial+10);
                            Cell cantidad = row.getCell(colInicial+11);
                            Cell cantidad_min = row.getCell(colInicial+12);
                            Cell tmat = row.getCell(colInicial+13);
                            if (cb.getCellType() == CellType.NUMERIC && !String.valueOf(cb.getNumericCellValue()).isEmpty()) {
                                Articulo articulo = new Articulo((cb.getCellType() == CellType.NUMERIC) ? (long) cb.getNumericCellValue() : Long.valueOf(cb.getStringCellValue())
                                        , (tipodeArmario.getCellType() == CellType.NUMERIC) ? String.valueOf(tipodeArmario.getNumericCellValue()) : tipodeArmario.getStringCellValue()
                                        , (gaveta.getCellType() == CellType.NUMERIC) ? String.valueOf(gaveta.getNumericCellValue()) : gaveta.getStringCellValue()
                                        , (sub.getCellType() == CellType.NUMERIC) ? String.valueOf(sub.getNumericCellValue()) : sub.getStringCellValue()
                                        , (material.getCellType() == CellType.NUMERIC) ? String.valueOf(material.getNumericCellValue()) : material.getStringCellValue()
                                        , (tipo.getCellType() == CellType.NUMERIC) ? String.valueOf(tipo.getNumericCellValue()) : tipo.getStringCellValue()
                                        , (nparte.getCellType() == CellType.NUMERIC) ? String.valueOf(nparte.getNumericCellValue()) : nparte.getStringCellValue()
                                        , (valor.getCellType() == CellType.NUMERIC) ? String.valueOf(valor.getNumericCellValue()) : valor.getStringCellValue()
                                        , (u_medida.getCellType() == CellType.NUMERIC) ? String.valueOf(u_medida.getNumericCellValue()) : u_medida.getStringCellValue()
                                        , (caracteristicas.getCellType() == CellType.NUMERIC) ? String.valueOf(caracteristicas.getNumericCellValue()) : caracteristicas.getStringCellValue()
                                        , (f_uso.getCellType() == CellType.NUMERIC) ? String.valueOf(f_uso.getNumericCellValue()) : f_uso.getStringCellValue()
                                        , (cantidad.getCellType() == CellType.NUMERIC) ? (int) cantidad.getNumericCellValue() : Integer.parseInt(cantidad.getStringCellValue())
                                        , (cantidad_min.getCellType() == CellType.NUMERIC) ? (int) cantidad_min.getNumericCellValue() : Integer.parseInt(cantidad_min.getStringCellValue())
                                );
                                ResultSet resultSet = conexion.consultar("SELECT * FROM `tipo_material` WHERE `material`= ? LIMIT 1", articulo.getMaterial());
                                if (resultSet.next()) {
                                    conexion.insmodelim("INSERT INTO `material`(`cb_material`, `tipo_de_armario`, `gaveta`, `sub_compartimento`, `id_material`, `tipo`, `numero_parte`, `valor`, `unidad_de_medida`, `caracteristicas`, `frecuencia_de_uso`, `cantidad`, `cantidad_min`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", String.valueOf(articulo.getCodigo_barras()), articulo.getTipo_de_armario(), articulo.getGaveta(), articulo.getSub_compartimento(), String.valueOf(resultSet.getInt("id_material")), articulo.getTipo(), articulo.getNumero_parte(), articulo.getValor(), articulo.getUnidad_medida(), articulo.getCaracteristicas(), articulo.getF_uso(), String.valueOf(articulo.getCantidad()), String.valueOf(articulo.getCantidad_min()));

                                } else {
                                    conexion.insmodelim("INSERT INTO `tipo_material`(`material`, `tipo_material`) VALUES (?,'Material Consumible')", articulo.getMaterial());
                                    ResultSet resultSet1 = conexion.consultar("SELECT `id_material` FROM `tipo_material` ORDER BY `id_material` DESC LIMIT 1;");
                                    if (resultSet1.next()) {
                                        conexion.insmodelim("INSERT INTO `material`(`cb_material`, `tipo_de_armario`, `gaveta`, `sub_compartimento`, `id_material`, `tipo`, `numero_parte`, `valor`, `unidad_de_medida`, `caracteristicas`, `frecuencia_de_uso`, `cantidad`, `cantidad_min`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", String.valueOf(articulo.getCodigo_barras()), articulo.getTipo_de_armario(), articulo.getGaveta(), articulo.getSub_compartimento(), String.valueOf(resultSet1.getInt("id_material")), articulo.getTipo(), articulo.getNumero_parte(), articulo.getValor(), articulo.getUnidad_medida(), articulo.getCaracteristicas(), articulo.getF_uso(), String.valueOf(articulo.getCantidad()), String.valueOf(articulo.getCantidad_min()));

                                    }
                                }

                            }
                        }
                    }
                }else {
                    Error("No se ha cargado la información necesaria");
                }
            }
            if (cbHerramienta.isSelected()){
                if (VerificarTxts(false,txtNHojaHerra,txtNColHerra)){
                    int colInicial=-1;
                    Sheet sheet = workbook.getSheetAt(Integer.parseInt(txtNHojaHerra.getText())-1);
                    for (Row row : sheet){
                        if (row.getRowNum()+1 == Integer.parseInt(txtNColHerra.getText())){
                            for (Cell cell : row){
                                if (!cell.getStringCellValue().isEmpty()){
                                    colInicial = cell.getColumnIndex();
                                    break;
                                }
                            }

                        }else if (colInicial !=-1){
                            Cell cb = row.getCell(colInicial);
                            Cell therramienta = row.getCell(colInicial+1);
                            Cell tipo = row.getCell(colInicial+2);
                            Cell caracteristicas = row.getCell(colInicial+3);
                            Cell f_uso = row.getCell(colInicial+4);
                            Cell cantidad = row.getCell(colInicial+5);
                            Cell cantidad_min = row.getCell(colInicial+6);
                            if (cb.getCellType() == CellType.NUMERIC && !String.valueOf(cb.getNumericCellValue()).isEmpty()) {
                                Herramienta herramienta = new Herramienta((cb.getCellType() == CellType.NUMERIC) ? (long) cb.getNumericCellValue() : Long.valueOf(cb.getStringCellValue())
                                        ,(therramienta.getCellType() == CellType.NUMERIC) ? String.valueOf(therramienta.getNumericCellValue()) : therramienta.getStringCellValue()
                                        , (tipo.getCellType() == CellType.NUMERIC) ? String.valueOf(tipo.getNumericCellValue()) : tipo.getStringCellValue()
                                        , (caracteristicas.getCellType() == CellType.NUMERIC) ? String.valueOf(caracteristicas.getNumericCellValue()) : caracteristicas.getStringCellValue()
                                        , (f_uso.getCellType() == CellType.NUMERIC) ? String.valueOf(f_uso.getNumericCellValue()) : f_uso.getStringCellValue()
                                        , (cantidad.getCellType() == CellType.NUMERIC) ? (int) cantidad.getNumericCellValue() : Integer.parseInt(cantidad.getStringCellValue())
                                        , (cantidad_min.getCellType() == CellType.NUMERIC) ? (int) cantidad_min.getNumericCellValue() : Integer.parseInt(cantidad_min.getStringCellValue())
                                );

                                ResultSet resultSet3= conexion.consultar("SELECT * FROM `tipo_material` WHERE `material`= ? LIMIT 1",herramienta.getHerramienta());
                                if (resultSet3.next()){
                                    conexion.insmodelim("INSERT INTO `herramienta`(`cb_herramienta`, `id_herramienta`, `tipo`, `caracteristicas`, `frecuencia_de_uso`, `cantidad`, `cantidad_min`) VALUES (?, ?, ?, ?, ?, ?, '0')",String.valueOf(herramienta.getCb_herramienta()), String.valueOf(resultSet3.getInt("id_material")), herramienta.getTipo(), herramienta.getCaracteristicas(), herramienta.getFrecuencia_de_uso(), String.valueOf(herramienta.getCantidad()));

                                }else {
                                    conexion.insmodelim("INSERT INTO `tipo_material`(`material`, `tipo_material`) VALUES ( ?,'Herramienta')", herramienta.getHerramienta());
                                    ResultSet resultSet4 = conexion.consultar("SELECT `id_material` FROM `tipo_material` ORDER BY `id_material` DESC LIMIT 1;");
                                    if(resultSet4.next()){
                                        conexion.insmodelim("INSERT INTO `herramienta`(`cb_herramienta`, `id_herramienta`, `tipo`, `caracteristicas`, `frecuencia_de_uso`, `cantidad`, `cantidad_min`) VALUES (?, ?, ?, ?, ?, ?,'0')", String.valueOf(herramienta.getCb_herramienta()), String.valueOf(resultSet4.getInt("id_material")), herramienta.getTipo(), herramienta.getCaracteristicas(), herramienta.getFrecuencia_de_uso(), String.valueOf(herramienta.getCantidad()));

                                    }
                                }

                            }
                        }
                    }
                }else {
                    Error("No se ha cargado la información necesaria");
                }
            }
            Exito("Se ha terminado de cargar la informacion con exito");
        }else {
            Error("No se ha rellenado ningun campo");
        }

    }
    @FXML private void GenerarPlantilla() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Font headerFont = workbook.createFont();
        headerFont.setFontName("Arial");
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        CellStyle dataStyle = workbook.createCellStyle();
        CellStyle dataStyleColor = workbook.createCellStyle();
        dataStyle.setAlignment(HorizontalAlignment.CENTER);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();

        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        Sheet material = workbook.createSheet("Materiales");
        Row titleRow = material.createRow(0);
        Cell titleCell = titleRow.createCell(3);
        titleCell.setCellValue("Inventario de Materiales");
        CellRangeAddress rango = new CellRangeAddress(0, 0, 3, 15);
        material.addMergedRegion(rango);

        titleCell.setCellStyle(titleStyle);
        material.autoSizeColumn(0);

        //SETUPPPPP------------------------------------------------------------------------------------------------------------------------------------------------
        Row headerRow = material.createRow(2);

        headerRow.createCell(3).setCellValue("Codigo");
        headerRow.createCell(4).setCellValue("Armario");
        headerRow.createCell(5).setCellValue("Gaveta");
        headerRow.createCell(6).setCellValue("Sub_compartimiento");
        headerRow.createCell(7).setCellValue("Material");
        headerRow.createCell(8).setCellValue("Tipo");
        headerRow.createCell(9).setCellValue("Numero de parte");
        headerRow.createCell(10).setCellValue("Valor");
        headerRow.createCell(11).setCellValue("Unidad de medida");
        headerRow.createCell(12).setCellValue("Caracteristicas");
        headerRow.createCell(13).setCellValue("Frecuencia de uso");
        headerRow.createCell(14).setCellValue("Cantidad");
        headerRow.createCell(15).setCellValue("Cantidad minima");
        headerRow.createCell(16).setCellValue("Tipo de material");


        for (int i = 3; i < 17; i++) {
            headerRow.getCell(i).setCellStyle(headerCellStyle);
        }
        for (int i = 3; i <= 16; i++) {
            material.autoSizeColumn(i);
        }



        Sheet herramienta = workbook.createSheet("Herramientas");
        Row titleRow1 = herramienta.createRow(0);
        Cell titleCell1 = titleRow1.createCell(0);
        titleCell1.setCellValue("Inventario de herramientas");
        CellRangeAddress titleRange = new CellRangeAddress(0, 0, 0, 11);
        herramienta.addMergedRegion(titleRange);
        titleCell1.setCellStyle(titleStyle);
        herramienta.autoSizeColumn(0);
        //SETUP---------------------------------------------------------------------------------------------------------------------------------------------

        Row headerRow1 = herramienta.createRow(2);

        headerRow1.createCell(3).setCellValue("CB Herramienta");
        headerRow1.createCell(4).setCellValue("Herramienta");
        headerRow1.createCell(5).setCellValue("Tipo");
        headerRow1.createCell(6).setCellValue("Caracteristicas");
        headerRow1.createCell(7).setCellValue("Frecuencia de uso");
        headerRow1.createCell(8).setCellValue("Cantidad");
        headerRow1.createCell(9).setCellValue("Cantidad minima");

        for (int i = 3; i < 10; i++) {
            headerRow1.getCell(i).setCellStyle(headerCellStyle);
        }

        for (int i = 3; i <= 9; i++) {
            herramienta.autoSizeColumn(i);
        }
        File file = new File(System.getProperty("user.home") + "/Desktop/Plantilla.xlsx");
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            workbook.write(outputStream);
            Exito("El excel se ha creado con exito en el escritorio");
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
    private boolean VerificarTxts(boolean todos, TextField... txts){
        if (!todos){
            for (TextField txt : txts){
                if (txt.getText().isEmpty() || !txt.getText().matches("^[0-9]*$")){
                    return false;
                }
            }
        }else {
            for (TextField txt : txts){
                if (!txt.getText().isEmpty() && txt.getText().matches("^[0-9]*$")){
                    break;
                }
            }
            return false;
        }

        return true;
    }

    @FXML private void CheckBoxChange(){
        hboxMat.setDisable(!cbMat.isSelected());
        hboxHerra.setDisable(!cbHerramienta.isSelected());

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

