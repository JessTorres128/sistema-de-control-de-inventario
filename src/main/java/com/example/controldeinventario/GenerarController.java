package com.example.controldeinventario;

import com.example.controldeinventario.Datos.*;
import com.itextpdf.text.*;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

public class GenerarController {
    ObservableList<String> listaCbs = FXCollections.observableArrayList();
    public ObservableList<TipoArticulo> registros = FXCollections.observableArrayList();
    Conexion conexion;
    @FXML
    ComboBox<String> cbFormato;




    CheckBoxTreeItem<String> rootItem = new  CheckBoxTreeItem<>("Todo");
    CheckBoxTreeItem<String> rootItem1 = new CheckBoxTreeItem<>("Materiales");
    CheckBoxTreeItem<String> rootItem2 = new CheckBoxTreeItem<>("Herramientas");
    @FXML TreeView<String> treeViewCBs = new TreeView<>();
    @FXML
    CheckBox checkBoxMaterial, checkBoxHerramienta, checkBoxPedidos, checkBoxTiposMaterial;
    @FXML
    HBox pdf;
    @FXML
    VBox excel, imagencb;
    TableColumn<TipoArticulo, Integer> tableColumnNo = new TableColumn<>("No");
    TableColumn<TipoArticulo, String> tableColumnName = new TableColumn<>("Tipo de material");
    TableColumn<TipoArticulo, String> tableColumnCat = new TableColumn<>("Categoría");
    TableColumn<TipoArticulo, String> tableColumnCheck = new TableColumn<>("Seleccion");
    @FXML
    TableView<TipoArticulo> tableViewCategoria;


    Callback<TableColumn<TipoArticulo, String>, TableCell<TipoArticulo, String>> celdaCheck =
            objectStringTableColumn -> {
                return new TableCell<TipoArticulo, String>() {
                    CheckBox checkBox = new CheckBox("");

                    @Override
                    protected void updateItem(String s, boolean b) {
                        if (b) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            checkBox.setSelected(registros.get(getIndex()).getSeleccion());
                            checkBox.setOnAction(event -> {
                                        ObservableList<TipoArticulo> productosRESP = FXCollections.observableArrayList();
                                        productosRESP.clear();
                                        productosRESP.addAll(registros);
                                registros.clear();
                                registros.addAll(productosRESP);

                                        if (checkBox.isSelected()) {
                                            checkBox.setDisable(registros.get(getIndex()).getSeleccion());
                                            checkBox.setDisable(false);
                                            registros.get(getIndex()).setSeleccion(true);

                                        } else {
                                            registros.get(getIndex()).setSeleccion(false);

                                        }
                                    }
                            );
                            setGraphic(checkBox);
                            setText(null);


                            setGraphic(checkBox);
                            setText(null);
                        }


                    }
                };
            };
    Callback<TableColumn<TipoArticulo, Integer>, TableCell<TipoArticulo, Integer>> celdaNo =
            objectStringTableColumn -> {
                return new TableCell<TipoArticulo, Integer>() {
                    @Override
                    protected void updateItem(Integer s, boolean b) {
                        if (b) {
                            setText(null);
                        } else {
                            int index = getIndex() + 1;
                            setText(String.valueOf(index));
                        }


                    }
                };
            };

    @FXML
    protected void initialize() throws SQLException {
        try {
            conexion = new Conexion();
            cbFormato.getItems().clear();
            cbFormato.getItems().addAll("Excel", "PDF", "JPG");
            cbFormato.getSelectionModel().select(0);

            tableColumnNo.setCellFactory(celdaNo);
            tableColumnName.setCellValueFactory(new PropertyValueFactory<TipoArticulo, String>("nombre"));
            tableColumnCat.setCellValueFactory(new PropertyValueFactory<TipoArticulo, String>("t_material"));
            tableColumnCheck.setCellFactory(celdaCheck);

            rootItem.getChildren().addAll(rootItem1,rootItem2);
            tableViewCategoria.getColumns().addAll(tableColumnNo, tableColumnName, tableColumnCat, tableColumnCheck);
            tableViewCategoria.setItems(registros);
            rootItem.setExpanded(true);
            treeViewCBs.setRoot(rootItem);
            ResultSet rsTMat = conexion.consultar("SELECT * FROM `tipo_material`;");
            while (rsTMat.next()){
                if (rsTMat.getString("tipo_material").equals("Material Fijo")||rsTMat.getString("tipo_material").equals("Material Consumible")){
                    ResultSet rsMaterial = conexion.consultar("SELECT `cb_material`,`tipo`,`numero_parte` FROM `material` WHERE `id_material`="+rsTMat.getInt("id_material"));
                    if (rsMaterial.next()){
                        CheckBoxTreeItem<String> tmat = new CheckBoxTreeItem(rsTMat.getString("material"));
                        rootItem1.getChildren().add(tmat);
                        while (rsMaterial.next()){
                            CheckBoxTreeItem<String> mat = new CheckBoxTreeItem(rsMaterial.getString("cb_material")+" "+rsMaterial.getString("tipo")+" "+rsMaterial.getString("numero_parte"));
                            tmat.getChildren().add(mat);
                        }
                    }
                }else {
                    ResultSet rsHerramienta = conexion.consultar("SELECT `cb_herramienta`,`tipo` FROM `herramienta` WHERE `id_herramienta`="+rsTMat.getInt("id_material"));
                    if (rsHerramienta.next()){
                        CheckBoxTreeItem<String> therra = new CheckBoxTreeItem(rsTMat.getString("material"));
                        rootItem2.getChildren().add(therra);
                        while (rsHerramienta.next()){
                            CheckBoxTreeItem<String> herra = new CheckBoxTreeItem(rsHerramienta.getString("cb_herramienta")+" "+rsHerramienta.getString("tipo"));
                            therra.getChildren().add(herra);
                        }
                    }

                }
            }



            treeViewCBs.setCellFactory(CheckBoxTreeCell.<String>forTreeView());
            ActualizarTabla(conexion.consultar("SELECT * FROM `tipo_material`"));
        } catch (Exception e) {
           Error(e.getMessage());
        }
    }

    private void ActualizarTabla(ResultSet rsCategoria) throws SQLException {
        tableViewCategoria.getItems().clear();
        while (rsCategoria.next()) {
            TipoArticulo tipoArticulo = new TipoArticulo(rsCategoria.getInt("id_material"), rsCategoria.getString("material"), rsCategoria.getString("tipo_material"));
            tableViewCategoria.getItems().add(tipoArticulo);
        }

    }

    @FXML
    private void TurnOff() {
        excel.setVisible(cbFormato.getSelectionModel().getSelectedItem().equals("Excel"));
        pdf.setVisible(cbFormato.getSelectionModel().getSelectedItem().equals("PDF"));
        imagencb.setVisible(cbFormato.getSelectionModel().getSelectedItem().equals("JPG"));
    }

    @FXML
    private void GenerarExcelCBool() throws SQLException {
        boolean bmaterial = false;
        boolean bherramienta = false;
        boolean bpedido = false;
        boolean btmateriales = false;

        if (checkBoxMaterial.isSelected()) {
            bmaterial = true;
        }
        if (checkBoxHerramienta.isSelected()) {
            bherramienta = true;
        }
        if (checkBoxPedidos.isSelected()) {
            bpedido = true;
        }
        if (checkBoxTiposMaterial.isSelected()) {
            btmateriales = true;
        }
        GenerarExcel(bmaterial, bherramienta, bpedido, btmateriales);
    }

    protected void GenerarExcel(boolean bmaterial, boolean bherramienta, boolean bpedido, boolean btmateriales) throws SQLException {
        Conexion conexion1 = new Conexion();
        Workbook excel = new XSSFWorkbook();
        Font headerFont = excel.createFont();
        headerFont.setFontName("Arial");
        headerFont.setFontHeightInPoints((short) 12);

        CellStyle headerCellStyle = excel.createCellStyle();
        headerCellStyle.setFont(headerFont);

        CellStyle dataStyle = excel.createCellStyle();
        CellStyle dataStyleColor = excel.createCellStyle();
        dataStyleColor.setFillForegroundColor(IndexedColors.RED.getIndex());
        dataStyleColor.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        dataStyle.setAlignment(HorizontalAlignment.CENTER);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        CellStyle titleStyle = excel.createCellStyle();
        Font titleFont = excel.createFont();

        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);

        CellStyle headerStyle = excel.createCellStyle();
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        if (bmaterial) {
            Sheet material = excel.createSheet("Material");
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


            int rowIndex = 3;
            ResultSet rsArticulos = conexion1.consultar("SELECT * FROM `material` INNER JOIN tipo_material ON material.id_material = tipo_material.id_material;");

            while (rsArticulos.next()) {
                Articulo producto = new Articulo(rsArticulos.getLong("cb_material"), rsArticulos.getString("tipo_de_armario"), rsArticulos.getString("gaveta"), rsArticulos.getString("sub_compartimento"), rsArticulos.getString("material"),
                        rsArticulos.getString("tipo"), rsArticulos.getString("numero_parte"), rsArticulos.getString("valor"), rsArticulos.getString("unidad_de_medida"), rsArticulos.getString("caracteristicas"), rsArticulos.getString("frecuencia_de_uso"),
                        rsArticulos.getInt("cantidad"), rsArticulos.getInt("cantidad_min"));

                Row dataRow = material.createRow(rowIndex++);
                dataRow.createCell(3).setCellValue(producto.getCodigo_barras());
                dataRow.createCell(4).setCellValue(producto.getTipo_de_armario());
                dataRow.createCell(5).setCellValue(producto.getGaveta());
                dataRow.createCell(6).setCellValue(producto.getSub_compartimento());
                dataRow.createCell(7).setCellValue(producto.getMaterial());
                dataRow.createCell(8).setCellValue(producto.getTipo());
                dataRow.createCell(9).setCellValue(producto.getNumero_parte());
                dataRow.createCell(10).setCellValue(producto.getValor());
                dataRow.createCell(11).setCellValue(producto.getUnidad_medida());
                dataRow.createCell(12).setCellValue(producto.getCaracteristicas());
                dataRow.createCell(13).setCellValue(producto.getF_uso());
                dataRow.createCell(14).setCellValue(producto.getCantidad());
                dataRow.createCell(15).setCellValue(producto.getCantidad_min());
                dataRow.createCell(16).setCellValue(rsArticulos.getString("tipo_material"));

                if (producto.getCantidad() < producto.getCantidad_min()) {
                    dataRow.getCell(3).setCellStyle(dataStyleColor);
                    dataRow.getCell(4).setCellStyle(dataStyleColor);
                    dataRow.getCell(5).setCellStyle(dataStyleColor);
                    dataRow.getCell(6).setCellStyle(dataStyleColor);
                    dataRow.getCell(7).setCellStyle(dataStyleColor);
                    dataRow.getCell(8).setCellStyle(dataStyleColor);
                    dataRow.getCell(9).setCellStyle(dataStyleColor);
                    dataRow.getCell(10).setCellStyle(dataStyleColor);
                    dataRow.getCell(11).setCellStyle(dataStyleColor);
                    dataRow.getCell(12).setCellStyle(dataStyleColor);
                    dataRow.getCell(13).setCellStyle(dataStyleColor);
                    dataRow.getCell(14).setCellStyle(dataStyleColor);
                    dataRow.getCell(15).setCellStyle(dataStyleColor);
                    dataRow.getCell(16).setCellStyle(dataStyleColor);
                }

                for (int i = 3; i < material.getRow(0).getLastCellNum(); i++) {
                    material.autoSizeColumn(i);
                }

                for (int i = 3; i <= 15; i++) {
                    material.autoSizeColumn(i);
                }

                for (int i = 3; i < material.getRow(0).getLastCellNum(); i++) {
                    material.autoSizeColumn(i);
                }

            }
        }

        if (bherramienta) {
            Sheet herramienta = excel.createSheet("Herramientas");
            Row titleRow = herramienta.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Inventario de herramientas");
            CellRangeAddress titleRange = new CellRangeAddress(0, 0, 0, 11);
            herramienta.addMergedRegion(titleRange);
            titleCell.setCellStyle(titleStyle);
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
            int rowIndex = 3;
            ResultSet rsHerramienta = conexion1.consultar("SELECT * FROM `herramienta` INNER JOIN tipo_material ON herramienta.id_herramienta = tipo_material.id_material;");
            while (rsHerramienta.next()) {
                Herramienta herramientas = new Herramienta(rsHerramienta.getLong("cb_herramienta"), rsHerramienta.getString("material"), rsHerramienta.getString("tipo"), rsHerramienta.getString("caracteristicas"), rsHerramienta.getString("frecuencia_de_uso"),
                        rsHerramienta.getInt("cantidad"), rsHerramienta.getInt("cantidad_min"));

                Row dataRow = herramienta.createRow(rowIndex++);
                dataRow.createCell(3).setCellValue(herramientas.getCb_herramienta());
                dataRow.createCell(4).setCellValue(herramientas.getHerramienta());
                dataRow.createCell(5).setCellValue(herramientas.getTipo());
                dataRow.createCell(6).setCellValue(herramientas.getCaracteristicas());
                dataRow.createCell(7).setCellValue(herramientas.getFrecuencia_de_uso());
                dataRow.createCell(8).setCellValue(herramientas.getCantidad());
                dataRow.createCell(9).setCellValue(herramientas.getCantidad_min());


                if (herramientas.getCantidad() < herramientas.getCantidad_min()) {
                    dataRow.getCell(3).setCellStyle(dataStyleColor);
                    dataRow.getCell(4).setCellStyle(dataStyleColor);
                    dataRow.getCell(5).setCellStyle(dataStyleColor);
                    dataRow.getCell(6).setCellStyle(dataStyleColor);
                    dataRow.getCell(7).setCellStyle(dataStyleColor);
                    dataRow.getCell(8).setCellStyle(dataStyleColor);
                    dataRow.getCell(9).setCellStyle(dataStyleColor);
                }
                for (int i = 3; i < herramienta.getRow(0).getLastCellNum(); i++) {
                    herramienta.autoSizeColumn(i);
                }

                for (int i = 3; i <= 14; i++) {
                    herramienta.autoSizeColumn(i);
                }

                for (int i = 3; i < herramienta.getRow(0).getLastCellNum(); i++) {
                    herramienta.autoSizeColumn(i);
                }
            }
        }

        if (bpedido) {
            Sheet pedidos = excel.createSheet("Pedidos");
            Row titleRow = pedidos.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Registro de pedidos");
            CellRangeAddress titleRange = new CellRangeAddress(0, 0, 0, 11);
            pedidos.addMergedRegion(titleRange);
            titleCell.setCellStyle(titleStyle);
            pedidos.autoSizeColumn(0);
            //SE>TUP_-------------------------
            int rowIndex2 = 2;


            ResultSet rsPedidos = conexion1.consultar("SELECT * FROM `pedido`");


            //ResultSet rsHerramientaPedido = conexion.consultar("SELECT tipo_material.material,`tipo`,`cantidad` FROM `herramienta` INNER JOIN tipo_material ON herramienta.id_herramienta = tipo_material.id_material WHERE cb_herramienta='"+rsArticulos.getLong("cb_material")+"'");

            while (rsPedidos.next()) {
                Row headerRow2 = pedidos.createRow(rowIndex2++);

                headerRow2.createCell(3).setCellValue("ID Pedido");
                headerRow2.createCell(4).setCellValue("Nombre");
                headerRow2.createCell(5).setCellValue("Numero de control");
                headerRow2.createCell(6).setCellValue("Estado");
                headerRow2.createCell(7).setCellValue("Fecha");
                headerRow2.createCell(8).setCellValue("Profesor");
                headerRow2.createCell(9).setCellValue("Materia");


                for (int i = 3; i < 10; i++) {
                    headerRow2.getCell(i).setCellStyle(headerCellStyle);
                }

                for (int i = 3; i <= 9; i++) {
                    pedidos.autoSizeColumn(i);
                }

                Pedido pedido = new Pedido(rsPedidos.getInt("id_pedido"), rsPedidos.getString("nombre_persona"), rsPedidos.getString("num_control"), rsPedidos.getString("estado"), rsPedidos.getDate("fecha"),
                        rsPedidos.getString("profesor"), rsPedidos.getString("materia"));
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");


                Row dataRow2 = pedidos.createRow(rowIndex2++);
                dataRow2.createCell(3).setCellValue(pedido.getId_pedido());
                dataRow2.createCell(4).setCellValue(pedido.getNombre_persona());
                dataRow2.createCell(5).setCellValue(pedido.getNum_control());
                dataRow2.createCell(6).setCellValue(pedido.getEstado());
                dataRow2.createCell(7).setCellValue(dateFormat.format(pedido.getFecha()));
                dataRow2.createCell(8).setCellValue(pedido.getProfesor());
                dataRow2.createCell(9).setCellValue(pedido.getMateria());
                ResultSet rsPedido = conexion1.consultar("SELECT * FROM `pedido_material` WHERE `id_pedido`= ?", String.valueOf(pedido.getId_pedido()));
                Row filaInfoP = pedidos.createRow(rowIndex2++);
                filaInfoP.createCell(3).setCellValue("Código de barras");
                filaInfoP.createCell(4).setCellValue("Cantidad");
                filaInfoP.createCell(5).setCellValue("Material");
                filaInfoP.createCell(6).setCellValue("Tipo");
                filaInfoP.createCell(7).setCellValue("Valor");
                filaInfoP.createCell(8).setCellValue("Unidad de medida");
                filaInfoP.createCell(9).setCellValue("Estado");
                while (rsPedido.next()) {
                    Row filaMaterialP = pedidos.createRow(rowIndex2++);
                    ResultSet rsArticulo = conexion1.consultar("SELECT * FROM `material` INNER JOIN tipo_material ON material.id_material = tipo_material.id_material WHERE `cb_material`= ?", String.valueOf(rsPedido.getLong("cb_material")));
                    ResultSet rsHerramientaDN = conexion1.consultar("SELECT * FROM `herramienta` INNER JOIN tipo_material ON herramienta.id_herramienta = tipo_material.id_material WHERE `cb_herramienta`= ?", String.valueOf(rsPedido.getLong("cb_material")));
                    if (rsArticulo.next()) {
                        filaMaterialP.createCell(3).setCellValue(rsPedido.getLong("cb_material"));
                        filaMaterialP.createCell(4).setCellValue(rsPedido.getInt("cantidad"));
                        filaMaterialP.createCell(5).setCellValue(rsArticulo.getString("material"));
                        filaMaterialP.createCell(6).setCellValue(rsArticulo.getString("tipo"));
                        filaMaterialP.createCell(7).setCellValue(rsArticulo.getString("valor"));
                        filaMaterialP.createCell(8).setCellValue(rsArticulo.getString("unidad_de_medida"));
                        filaMaterialP.createCell(9).setCellValue(rsPedido.getString("estado"));
                    } else if (rsHerramientaDN.next()) {
                        filaMaterialP.createCell(3).setCellValue(rsPedido.getLong("cb_material"));
                        filaMaterialP.createCell(4).setCellValue(rsPedido.getLong("cantidad"));
                        filaMaterialP.createCell(5).setCellValue(rsHerramientaDN.getString("material"));
                        filaMaterialP.createCell(6).setCellValue(rsHerramientaDN.getString("tipo"));
                        filaMaterialP.createCell(7).setCellValue("N/A");
                        filaMaterialP.createCell(8).setCellValue("N/A");
                        filaMaterialP.createCell(9).setCellValue(rsPedido.getString("estado"));
                    }


                }
                // dataRow2.createCell(10).setCellValue(pal);


                for (int i = 3; i < pedidos.getRow(0).getLastCellNum(); i++) {
                    pedidos.autoSizeColumn(i);
                }

                for (int i = 3; i <= 14; i++) {
                    pedidos.autoSizeColumn(i);
                }

                for (int i = 3; i < pedidos.getRow(0).getLastCellNum(); i++) {
                    pedidos.autoSizeColumn(i);
                }
                rowIndex2++;
            }
        }


        if (btmateriales) {
            Sheet tiposdeMat = excel.createSheet("Tipos de articulos");
            Row titleRow = tiposdeMat.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Tipos de materiales");
            CellRangeAddress titleRange = new CellRangeAddress(0, 0, 0, 11);
            tiposdeMat.addMergedRegion(titleRange);
            titleCell.setCellStyle(titleStyle);
            tiposdeMat.autoSizeColumn(0);

            int rowIndex = 3;
            Row headerRow1 = tiposdeMat.createRow(2);

            headerRow1.createCell(3).setCellValue("No");
            headerRow1.createCell(4).setCellValue("Material");
            headerRow1.createCell(5).setCellValue("Categoría");

            for (int i = 3; i < 6; i++) {
                headerRow1.getCell(i).setCellStyle(headerCellStyle);
            }

            for (int i = 3; i <= 6; i++) {
                tiposdeMat.autoSizeColumn(i);
            }
            ResultSet rsTMat = conexion.consultar("SELECT * FROM `tipo_material`");
            int cont = 1;
            while (rsTMat.next()) {
                TipoArticulo tipoArticulo = new TipoArticulo(cont, rsTMat.getString("material"), rsTMat.getString("tipo_material"));
                Row dataRow = tiposdeMat.createRow(rowIndex++);
                dataRow.createCell(3).setCellValue(cont);
                dataRow.createCell(4).setCellValue(tipoArticulo.getNombre());
                dataRow.createCell(5).setCellValue(tipoArticulo.getT_material());
                cont++;
                for (int i = 3; i < tiposdeMat.getRow(0).getLastCellNum(); i++) {
                    tiposdeMat.autoSizeColumn(i);
                }

                for (int i = 3; i <= 14; i++) {
                    tiposdeMat.autoSizeColumn(i);
                }

                for (int i = 3; i < tiposdeMat.getRow(0).getLastCellNum(); i++) {
                    tiposdeMat.autoSizeColumn(i);
                }

            }
        }

        if (!bmaterial && !bherramienta && !bpedido && !btmateriales){
            Error("No se ha seleccionado nada");
        }else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar archivo Excel");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Excel", "*.xlsx")
            );
            File file = fileChooser.showSaveDialog(null);
            if (file != null) {

                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    excel.write(outputStream);
                    Exito("El excel se ha creado con exito");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @FXML
    private void GenerarPDF() throws SQLException, DocumentException, IOException {
        if (VerSeleccionPDFS()){
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Selecciona una carpeta para guardar el archivo");
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

            File selectedDirectory = directoryChooser.showDialog(HelloApplication.primarystage);

            if (selectedDirectory != null) {
                for (TipoArticulo articulo : registros) {
                    if (articulo.getSeleccion()) {
                        if (articulo.getT_material().equals("Material Consumible") || articulo.getT_material().equals("Material Fijo")) {
                            GenerarLibro(articulo.getNombre(), "Material", selectedDirectory.getAbsolutePath());
                        } else {
                            GenerarLibro(articulo.getNombre(), "Herramienta", selectedDirectory.getAbsolutePath());
                        }
                    }
                }
                Exito("PDFs creados exitosamente");
            } else {
                Error("El usuario canceló la selección de carpeta");
            }
        }else {
            Error("No se ha seleccionado ningun campo");
        }

    }

    private boolean VerSeleccionPDFS(){
        for (TipoArticulo articulo : registros) {
            if (articulo.getSeleccion()){
                return true;
            }
        }
        return false;
    }







    private void GenerarLibro(String material, String tmat, String ruta) throws SQLException, IOException, DocumentException {
        Code39Bean code39Bean = new Code39Bean();
        final int dpi = 150;
        code39Bean.setModuleWidth(UnitConv.in2mm(1.0f / dpi));
        code39Bean.setWideFactor(3);
        code39Bean.doQuietZone(true);
        ResultSet resultSet;
        PdfPTable pdfPTableCB;
        Document documento = new Document(PageSize.A4.rotate(), 10f, 10f, 0f, 0f);
        boolean esmat;
        if (tmat.equals("Material")){
            resultSet = conexion.consultar("SELECT * FROM `material` INNER JOIN tipo_material ON material.id_material = tipo_material.id_material WHERE tipo_material.material= ?;", material);
            esmat=true;


            if (material.equals("N/A")) {
                PdfWriter pdfWriter = PdfWriter.getInstance(documento, new FileOutputStream(new File(ruta,"Libro_NA_material.pdf")));
            } else {
                PdfWriter pdfWriter = PdfWriter.getInstance(documento, new FileOutputStream(new File(ruta,"Libro_" + material + ".pdf")));
            }
            // pdfWriter.addPageDictEntry(PdfName.ROTATE, PdfPage.LANDSCAPE);
            documento.open();
            pdfPTableCB = new PdfPTable(7);
            pdfPTableCB.setWidthPercentage(100);
            PdfPCell cellCB = new PdfPCell(Phrase.getInstance("Código"));
            cellCB.setPadding(5);
            cellCB.setBorder(Rectangle.ALIGN_CENTER);
            cellCB.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPTableCB.addCell(cellCB);
            PdfPCell cellMat = new PdfPCell(Phrase.getInstance("Material"));
            cellMat.setPadding(5);
            cellMat.setBorder(com.itextpdf.text.Rectangle.ALIGN_CENTER);
            cellMat.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPTableCB.addCell(cellMat);
            PdfPCell cellTipo = new PdfPCell(Phrase.getInstance("Tipo"));
            cellTipo.setPadding(5);
            cellTipo.setBorder(com.itextpdf.text.Rectangle.ALIGN_CENTER);
            cellTipo.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPTableCB.addCell(cellTipo);
            PdfPCell cellNParte = new PdfPCell(Phrase.getInstance("Número de parte"));
            cellNParte.setPadding(5);
            cellNParte.setBorder(com.itextpdf.text.Rectangle.ALIGN_CENTER);
            cellNParte.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPTableCB.addCell(cellNParte);
            PdfPCell cellValor = new PdfPCell(Phrase.getInstance("Valor"));
            cellValor.setPadding(5);
            cellValor.setBorder(com.itextpdf.text.Rectangle.ALIGN_CENTER);
            cellValor.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPTableCB.addCell(cellValor);
            PdfPCell cellUM = new PdfPCell(Phrase.getInstance("Unidad de medida"));
            cellUM.setPadding(5);
            cellUM.setBorder(com.itextpdf.text.Rectangle.ALIGN_CENTER);
            cellUM.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPTableCB.addCell(cellUM);
            PdfPCell cellCar = new PdfPCell(Phrase.getInstance("Caracteristicas"));
            cellCar.setPadding(5);
            cellCar.setBorder(com.itextpdf.text.Rectangle.ALIGN_CENTER);
            cellCar.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPTableCB.addCell(cellCar);

            while (resultSet.next()) {
                String cb = resultSet.getString("cb_material");
                OutputStream out = new FileOutputStream(new File(ruta+File.separator+"imgcb",cb + ".png"));
                BitmapCanvasProvider canvas = new BitmapCanvasProvider(out, "image/png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
                code39Bean.generateBarcode(canvas, cb);
                canvas.finish();
                javafx.scene.image.Image image = null;
                try {
                    image = new javafx.scene.image.Image(new FileInputStream(new File(ruta+File.separator+"imgcb",cb + ".png")));
                    cellCB.setImage(com.itextpdf.text.Image.getInstance(ruta+File.separator+"imgcb"+File.separator+cb+".png"));
                    cellCB.setPaddingTop(5);
                    pdfPTableCB.addCell(cellCB);
                    cellMat.setPhrase(Phrase.getInstance(resultSet.getString("material")));
                    cellMat.setPaddingTop(5);
                    pdfPTableCB.addCell(cellMat);
                    cellTipo.setPhrase(Phrase.getInstance(resultSet.getString("tipo")));
                    cellTipo.setPaddingTop(5);
                    pdfPTableCB.addCell(cellTipo);
                    cellNParte.setPhrase(Phrase.getInstance(resultSet.getString("numero_parte")));
                    cellNParte.setPaddingTop(5);
                    pdfPTableCB.addCell(cellNParte);
                    cellValor.setPhrase(Phrase.getInstance(resultSet.getString("valor")));
                    cellValor.setPaddingTop(5);
                    pdfPTableCB.addCell(cellValor);
                    cellUM.setPhrase(Phrase.getInstance(resultSet.getString("unidad_de_medida")));
                    cellUM.setPaddingTop(5);
                    pdfPTableCB.addCell(cellUM);
                    cellCar.setPhrase(Phrase.getInstance(resultSet.getString("caracteristicas")));
                    cellCar.setPaddingTop(5);
                    pdfPTableCB.addCell(cellCar);
                } catch (FileNotFoundException | BadElementException e) {
                    e.printStackTrace();
                }
            }
        }else {
            resultSet = conexion.consultar("SELECT * FROM `herramienta` INNER JOIN tipo_material ON herramienta.id_herramienta = tipo_material.id_material WHERE tipo_material.material= ?;", material);

            if (material.equals("N/A")) {
                PdfWriter pdfWriter = PdfWriter.getInstance(documento, new FileOutputStream(new File(ruta,"Libro_NA_material.pdf")));
            } else {
                PdfWriter pdfWriter = PdfWriter.getInstance(documento, new FileOutputStream(new File(ruta,"Libro_" + material + ".pdf")));
            }
            documento.open();
            pdfPTableCB = new PdfPTable(4);
            pdfPTableCB.setWidthPercentage(100);
            PdfPCell cellCB = new PdfPCell(Phrase.getInstance("Código"));
            cellCB.setPadding(5);
            cellCB.setBorder(Rectangle.ALIGN_CENTER);
            cellCB.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPTableCB.addCell(cellCB);
            PdfPCell cellMat = new PdfPCell(Phrase.getInstance("Material"));
            cellMat.setPadding(5);
            cellMat.setBorder(com.itextpdf.text.Rectangle.ALIGN_CENTER);
            cellMat.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPTableCB.addCell(cellMat);
            PdfPCell cellTipo = new PdfPCell(Phrase.getInstance("Tipo"));
            cellTipo.setPadding(5);
            cellTipo.setBorder(com.itextpdf.text.Rectangle.ALIGN_CENTER);
            cellTipo.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPTableCB.addCell(cellTipo);
            PdfPCell cellCar = new PdfPCell(Phrase.getInstance("Caracteristicas"));
            cellCar.setPadding(5);
            cellCar.setBorder(com.itextpdf.text.Rectangle.ALIGN_CENTER);
            cellCar.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfPTableCB.addCell(cellCar);

            while (resultSet.next()) {
                String cb =resultSet.getString("cb_herramienta");
                OutputStream out = new FileOutputStream(new File(ruta+File.separator+"imgcb",cb + ".png"));
                BitmapCanvasProvider canvas = new BitmapCanvasProvider(out, "image/png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
                code39Bean.generateBarcode(canvas, cb);
                canvas.finish();
                javafx.scene.image.Image image = null;
                try {
                    image = new javafx.scene.image.Image(new FileInputStream(new File(ruta+File.separator+"imgcb",cb + ".png")));
                    cellCB.setImage(com.itextpdf.text.Image.getInstance(ruta+File.separator+"imgcb"+File.separator+cb+".png"));
                    cellCB.setPaddingTop(5);
                    pdfPTableCB.addCell(cellCB);
                    cellMat.setPhrase(Phrase.getInstance(resultSet.getString("material")));
                    cellMat.setPaddingTop(5);
                    pdfPTableCB.addCell(cellMat);
                    cellTipo.setPhrase(Phrase.getInstance(resultSet.getString("tipo")));
                    cellTipo.setPaddingTop(5);
                    pdfPTableCB.addCell(cellTipo);
                    cellCar.setPhrase(Phrase.getInstance(resultSet.getString("caracteristicas")));
                    cellCar.setPaddingTop(5);
                    pdfPTableCB.addCell(cellCar);
                } catch (FileNotFoundException | BadElementException e) {
                    e.printStackTrace();
                }
            }





        }

            documento.add(pdfPTableCB);
            documento.close();
            if (material.equals("N/A")) {
                Desktop.getDesktop().browse(new File(ruta,"Libro_NA_material.pdf").toURI());
            } else {
                Desktop.getDesktop().browse(new File(ruta,"Libro_" + material + ".pdf").toURI());
            }












    }

    @FXML private void GenerarCbs() throws IOException {
        listaCbs.clear();
        VerArbol(treeViewCBs.getRoot(),listaCbs);
        if (listaCbs.isEmpty()){
            Error("No se ha seleccionado nada");
        }else {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Selecciona una carpeta para guardar el archivo");
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));

            File selectedDirectory = directoryChooser.showDialog(HelloApplication.primarystage);

            if (selectedDirectory != null) {
                File nuevaCarpeta = new File(selectedDirectory.getAbsolutePath() + File.separator + "imgcb");
                if (nuevaCarpeta.mkdir()) {
                } else {
                }
                for (String articulo : listaCbs){
                    String[] articulosep = articulo.split(" ");
                    articulo = articulosep[0];
                    Code39Bean code39Bean = new Code39Bean();
                    final int dpi = 150;
                    code39Bean.setModuleWidth(UnitConv.in2mm(1.0f / dpi));
                    code39Bean.setWideFactor(3);
                    code39Bean.doQuietZone(true);
                    OutputStream out = new FileOutputStream(new File(selectedDirectory+File.separator+"imgcb",articulo + ".png"));
                    BitmapCanvasProvider canvas = new BitmapCanvasProvider(out, "image/png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
                    code39Bean.generateBarcode(canvas, articulo);
                    canvas.finish();
                    javafx.scene.image.Image image = null;
                    try {
                        image = new javafx.scene.image.Image(new FileInputStream("code39.png"));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }




                }
                Exito("Imagenes generadas exitosamente");


            } else {
                Error("El usuario canceló la selección de carpeta.");
            }
        }
    }




    private void VerArbol(TreeItem<String> item, List<String> listacbs){
        if (item instanceof CheckBoxTreeItem) {
            CheckBoxTreeItem<String> checkBoxItem = (CheckBoxTreeItem<String>) item;
            if (checkBoxItem.isSelected() && checkBoxItem.isLeaf()) {
                listacbs.add(checkBoxItem.getValue());
            }
        }

        if (!item.isLeaf()) {
            for (TreeItem<String> child : item.getChildren()) {
                VerArbol(child, listacbs);
            }
        }
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
