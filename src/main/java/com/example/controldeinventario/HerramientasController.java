package com.example.controldeinventario;

import com.example.controldeinventario.Datos.Herramienta;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class HerramientasController {


    @FXML ImageView imgCodeBar = new ImageView();
    Conexion conexion;
    @FXML TabPane tabPaneHerramientas;
    @FXML Tab tabNew, tabSearch;
    @FXML TextField txtBusqueda;
    @FXML RadioButton rbID, rbNombre;
    @FXML RadioButton rbCBPequeño, rbCBMedio, rbCBGrande;
    @FXML TableView<Herramienta> tableViewHerramientas;
    @FXML Label lblRegistros;
    @FXML TextField txtCB, txtTipo, txtStock, txtStockMin;
    @FXML TextArea txtCaracteristicas;
    @FXML RadioButton rbBajo, rbMedio, rbAlto;
    @FXML ComboBox<String> cbHerramienta = new ComboBox<>();
    @FXML Button btnNew,btnSave,btnEdit,btnCancel,btnExit,btnDelete;
    private boolean edit = false;
    private long cbedit = 0L;
    ToggleGroup toggleGroupBusqueda = new ToggleGroup();
    ToggleGroup toogleGroupCBSize = new ToggleGroup();
    ToggleGroup toggleGroupFrecuencia = new ToggleGroup();

    TableColumn<Herramienta,Long> colID = new TableColumn<>("CB Herramienta");
    TableColumn<Herramienta,String> colHerramienta= new TableColumn<>("Herramienta");
    TableColumn<Herramienta,String> colTipo= new TableColumn<>("Tipo");
    TableColumn<Herramienta,String> colCaracteristicas = new TableColumn<>("Caracteristicas");
    TableColumn<Herramienta,String> colFUso=new TableColumn<>("Frecuencia de uso");
    TableColumn<Herramienta,Integer> colCantidad=new TableColumn<>("Cantidad");
    TableColumn<Herramienta,Integer> colCantidadMin=new TableColumn<>("Cantidad minima");




    @FXML protected void initialize() throws SQLException {
        Platform.runLater(() -> {
            txtBusqueda.requestFocus();
            txtBusqueda.selectEnd();
        });
        rbBajo.setToggleGroup(toggleGroupFrecuencia);
        rbMedio.setToggleGroup(toggleGroupFrecuencia);
        rbAlto.setToggleGroup(toggleGroupFrecuencia);

        rbID.setToggleGroup(toggleGroupBusqueda);
        rbNombre.setToggleGroup(toggleGroupBusqueda);

        rbCBPequeño.setToggleGroup(toogleGroupCBSize);
        rbCBMedio.setToggleGroup(toogleGroupCBSize);
        rbCBGrande.setToggleGroup(toogleGroupCBSize);

        colID.setCellValueFactory(new PropertyValueFactory<>("cb_herramienta"));
        colHerramienta.setCellValueFactory(new PropertyValueFactory<>("herramienta"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colCaracteristicas.setCellValueFactory(new PropertyValueFactory<>("caracteristicas"));
        colFUso.setCellValueFactory(new PropertyValueFactory<>("frecuencia_de_uso"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colCantidadMin.setCellValueFactory(new PropertyValueFactory<>("cantidad_min"));

        tableViewHerramientas.getColumns().addAll(colID,colHerramienta,colTipo,colCaracteristicas,colFUso,colCantidad,colCantidadMin);
        conexion=new Conexion();
        cbHerramienta.getItems().clear();
        ResultSet resultSet= conexion.consultar("SELECT * FROM `tipo_material` WHERE `tipo_material`='Herramienta'");
        while (resultSet.next()){
            cbHerramienta.getItems().add((String) resultSet.getObject("material"));
        }
        cbHerramienta.getSelectionModel().select(0);
        ActualizarTabla(conexion.consultar("SELECT * FROM `herramienta` INNER JOIN tipo_material ON herramienta.id_herramienta = tipo_material.id_material;"));
        ActivateBtn(false,true,false,true,false,false);
    }
    @FXML private void NewHerramienta() throws SQLException {
        ActivateBtn(false,false,true,false,false,true);
        Long cb = GenerateNumber();
        tabPaneHerramientas.getSelectionModel().select(tabNew);
        tabNew.setDisable(false);
        tabSearch.setDisable(true);
        CleanTextFields();

    }
    @FXML private void SaveHerramienta(){
        try {
            if (!txtCB.getText().matches("\\d{10}")) {
                throw new Exception("Formato de código de barras no valido, asegurese de que sea un número de 10 digitos");
            }
            if (!VerifyTxt(txtCaracteristicas, cbHerramienta,txtStock,txtTipo,txtStockMin)){
                throw new Exception("Faltan campos por rellenar");
            }
            if (!txtStock.getText().matches("^\\d+$") || !txtStockMin.getText().matches("^\\d+$")){
                throw new Exception("Cantidades incorrectas, revise que contenga solamente números");
            }
            if (txtCB.getText().equals(String.valueOf(cbedit))){
                ResultSet resultSetIDHerramienta = conexion.consultar("SELECT `id_material` FROM `tipo_material` WHERE `material`= ? AND `tipo_material`='Herramienta' LIMIT 1", cbHerramienta.getSelectionModel().getSelectedItem());
                if (!resultSetIDHerramienta.next()){
                    throw new Exception("Selecciona el material");
                }
                if (edit){
                    conexion.insmodelim("UPDATE `herramienta` SET `id_herramienta`= ?,`tipo`= ?,`caracteristicas`= ?,`frecuencia_de_uso`= ?,`cantidad`= ?,`cantidad_min`= ? WHERE `cb_herramienta`= ?",resultSetIDHerramienta.getInt("id_material"), txtTipo.getText(), txtCaracteristicas.getText(), ((RadioButton) toggleGroupFrecuencia.getSelectedToggle()).getText(), txtStock.getText(), txtStockMin.getText(), cbedit);
                    Exito("Actualizado con exito");
                }else {
                    conexion.insmodelim("INSERT INTO `herramienta`(`cb_herramienta`,`id_herramienta`, `tipo`, `caracteristicas`, `frecuencia_de_uso`, `cantidad`, `cantidad_min`) VALUES (?, ?, ?, ?, ?, ?, ?)",txtCB.getText(),resultSetIDHerramienta.getInt("id_material"),txtTipo.getText(),txtCaracteristicas.getText(),((RadioButton) toggleGroupFrecuencia.getSelectedToggle()).getText(),txtStock.getText(),txtStockMin.getText());
                    Exito(cbHerramienta.getSelectionModel().getSelectedItem()+" "+txtTipo.getText()+" agregado");
                }
            }else {
                ResultSet res= conexion.consultar("SELECT herramienta.cb_herramienta FROM herramienta\n" +
                        "                LEFT JOIN material\n" +
                        "                ON herramienta.cb_herramienta = material.cb_material\n" +
                        "                WHERE herramienta.cb_herramienta = ? OR material.cb_material = ?\n" +
                        "                UNION\n" +
                        "                SELECT material.cb_material\n" +
                        "                FROM material\n" +
                        "                LEFT JOIN herramienta\n" +
                        "                ON herramienta.cb_herramienta = material.cb_material\n" +
                        "                WHERE herramienta.cb_herramienta = ? OR material.cb_material = ?\n" +
                        "                AND herramienta.cb_herramienta IS NULL;",txtCB.getText(),txtCB.getText(),txtCB.getText(),txtCB.getText());
                if (res.next()){
                    throw new Exception("Ya existe un articulo con este código de barras");
                }
                ResultSet resultSetIDHerramienta = conexion.consultar("SELECT `id_material` FROM `tipo_material` WHERE `material`= ? AND `tipo_material`='Herramienta' LIMIT 1", cbHerramienta.getSelectionModel().getSelectedItem());
                if (!resultSetIDHerramienta.next()){
                    throw new Exception("Selecciona el material");
                }else {
                    if (edit){
                        conexion.insmodelim("UPDATE `herramienta` SET `cb_herramienta`= ?, `id_herramienta`= ?,`tipo`= ?,`caracteristicas`= ?,`frecuencia_de_uso`= ?,`cantidad`= ?,`cantidad_min`= ? WHERE `cb_herramienta`= ?",txtCB.getText(),resultSetIDHerramienta.getInt("id_material"), txtTipo.getText(), txtCaracteristicas.getText(), ((RadioButton) toggleGroupFrecuencia.getSelectedToggle()).getText(), txtStock.getText(), txtStockMin.getText(), cbedit);
                        Exito("Actualizado con exito");
                    }else {
                        conexion.insmodelim("INSERT INTO `herramienta`(`cb_herramienta`,`id_herramienta`, `tipo`, `caracteristicas`, `frecuencia_de_uso`, `cantidad`, `cantidad_min`) VALUES (?, ?, ?, ?, ?, ?, ?)",txtCB.getText(),resultSetIDHerramienta.getInt("id_material"),txtTipo.getText(),txtCaracteristicas.getText(),((RadioButton) toggleGroupFrecuencia.getSelectedToggle()).getText(),txtStock.getText(),txtStockMin.getText());
                        Exito(cbHerramienta.getSelectionModel().getSelectedItem()+" "+txtTipo.getText()+" agregado");
                    }
                }

            }
            tabPaneHerramientas.getSelectionModel().select(tabSearch);
            tabSearch.setDisable(false);
            tabNew.setDisable(true);
            ActivateBtn(false,true,false,true,false,false);
            ActualizarTabla(conexion.consultar("SELECT * FROM `herramienta` INNER JOIN tipo_material ON herramienta.id_herramienta = tipo_material.id_material;"));
            edit=false;
            cbedit = 0L;

            } catch (Exception e) {
            Error(e.getMessage());
        }
    }
    @FXML private void GuardarHerramienta() throws SQLException {
        if (!txtCB.getText().matches("\\d{10}")){
            Error("Formato de código de barras no valido, asegurese de que sea un número de 10 digitos");
        }else {
            if (VerifyTxt(txtCaracteristicas, cbHerramienta,txtStock,txtTipo,txtStockMin)){
                if (!txtStock.getText().matches("^\\d+$") || !txtStockMin.getText().matches("^\\d+$")){

                    Error("Cantidades incorrectas, revise que contenga solamente números");
                }else {
                    if (txtCB.getText().equals(String.valueOf(cbedit))){
                       /* ResultSet res= conexion.consultar("SELECT herramienta.cb_herramienta FROM herramienta\n" +
                                "                LEFT JOIN material\n" +
                                "                ON herramienta.cb_herramienta = material.cb_material\n" +
                                "                WHERE herramienta.cb_herramienta = ? OR material.cb_material = ?\n" +
                                "                UNION\n" +
                                "                SELECT material.cb_material\n" +
                                "                FROM material\n" +
                                "                LEFT JOIN herramienta\n" +
                                "                ON herramienta.cb_herramienta = material.cb_material\n" +
                                "                WHERE herramienta.cb_herramienta = ? OR material.cb_material = ?\n" +
                                "                AND herramienta.cb_herramienta IS NULL;",txtCodigoBarras.getText(),txtCodigoBarras.getText(),txtCodigoBarras.getText(),txtCodigoBarras.getText());
                        if (res.next()){
                            Error("Ya existe un articulo con este código de barras");
                        }else {*/
                        ResultSet resultSetIDHerramienta = conexion.consultar("SELECT `id_material` FROM `tipo_material` WHERE `material`= ? AND `tipo_material`='Herramienta' LIMIT 1", cbHerramienta.getSelectionModel().getSelectedItem());
                        if (!resultSetIDHerramienta.next()){
                            Error("Selecciona el material");
                        }else {
                            if (edit){
                                conexion.insmodelim("UPDATE `herramienta` SET `id_herramienta`= ?,`tipo`= ?,`caracteristicas`= ?,`frecuencia_de_uso`= ?,`cantidad`= ?,`cantidad_min`= ? WHERE `cb_herramienta`= ?",resultSetIDHerramienta.getInt("id_material"), txtTipo.getText(), txtCaracteristicas.getText(), ((RadioButton) toggleGroupFrecuencia.getSelectedToggle()).getText(), txtStock.getText(), txtStockMin.getText(), cbedit);
                                Exito("Actualizado con exito");
                            }else {
                                conexion.insmodelim("INSERT INTO `herramienta`(`cb_herramienta`,`id_herramienta`, `tipo`, `caracteristicas`, `frecuencia_de_uso`, `cantidad`, `cantidad_min`) VALUES (?, ?, ?, ?, ?, ?, ?)",txtCB.getText(),resultSetIDHerramienta.getInt("id_material"),txtTipo.getText(),txtCaracteristicas.getText(),((RadioButton) toggleGroupFrecuencia.getSelectedToggle()).getText(),txtStock.getText(),txtStockMin.getText());
                                Exito(cbHerramienta.getSelectionModel().getSelectedItem()+" "+txtTipo.getText()+" agregado");
                            }

                            //}
                        }
                    }else {
                        ResultSet res= conexion.consultar("SELECT herramienta.cb_herramienta FROM herramienta\n" +
                                "                LEFT JOIN material\n" +
                                "                ON herramienta.cb_herramienta = material.cb_material\n" +
                                "                WHERE herramienta.cb_herramienta = ? OR material.cb_material = ?\n" +
                                "                UNION\n" +
                                "                SELECT material.cb_material\n" +
                                "                FROM material\n" +
                                "                LEFT JOIN herramienta\n" +
                                "                ON herramienta.cb_herramienta = material.cb_material\n" +
                                "                WHERE herramienta.cb_herramienta = ? OR material.cb_material = ?\n" +
                                "                AND herramienta.cb_herramienta IS NULL;",txtCB.getText(),txtCB.getText(),txtCB.getText(),txtCB.getText());
                        if (res.next()){
                            Error("Ya existe un articulo con este código de barras");
                        }else {
                            ResultSet resultSetIDHerramienta = conexion.consultar("SELECT `id_material` FROM `tipo_material` WHERE `material`= ? AND `tipo_material`='Herramienta' LIMIT 1", cbHerramienta.getSelectionModel().getSelectedItem());
                            if (!resultSetIDHerramienta.next()){
                                Error("Selecciona el material");
                            }else {
                                if (edit){
                                    conexion.insmodelim("UPDATE `herramienta` SET `cb_herramienta`= ?, `id_herramienta`= ?,`tipo`= ?,`caracteristicas`= ?,`frecuencia_de_uso`= ?,`cantidad`= ?,`cantidad_min`= ? WHERE `cb_herramienta`= ?",txtCB.getText(),resultSetIDHerramienta.getInt("id_material"), txtTipo.getText(), txtCaracteristicas.getText(), ((RadioButton) toggleGroupFrecuencia.getSelectedToggle()).getText(), txtStock.getText(), txtStockMin.getText(), cbedit);
                                    Exito("Actualizado con exito");
                                }else {
                                    conexion.insmodelim("INSERT INTO `herramienta`(`cb_herramienta`,`id_herramienta`, `tipo`, `caracteristicas`, `frecuencia_de_uso`, `cantidad`, `cantidad_min`) VALUES (?, ?, ?, ?, ?, ?, ?)",txtCB.getText(),resultSetIDHerramienta.getInt("id_material"),txtTipo.getText(),txtCaracteristicas.getText(),((RadioButton) toggleGroupFrecuencia.getSelectedToggle()).getText(),txtStock.getText(),txtStockMin.getText());
                                    Exito(cbHerramienta.getSelectionModel().getSelectedItem()+" "+txtTipo.getText()+" agregado");
                                }

                            }
                        }
                    }// otra cosa







                }
                tabPaneHerramientas.getSelectionModel().select(tabSearch);
                tabSearch.setDisable(false);
                tabNew.setDisable(true);
                ActivateBtn(false,true,false,true,false,false);
                ActualizarTabla(conexion.consultar("SELECT * FROM `herramienta` INNER JOIN tipo_material ON herramienta.id_herramienta = tipo_material.id_material;"));
                edit=false;
                cbedit = 0L;

            }else {Error("Faltan campos por rellenar");}
        }




























        /*NO JALA ESTO JEJE
        if (VerifyTxt(txtCaracteristicas, cbHerramienta,txtStock,txtTipo,txtStockMin)){
            if (!txtStock.getText().matches("^\\d+$\n") || !txtStockMin.getText().matches("^\\d+$\n")){
                Error("Cantidades incorrectas");
            }else {
                ResultSet resultSetIDHerramienta = conexion.consultar("SELECT `id_material` FROM `tipo_material` WHERE `material`= ? AND `tipo_material`='Herramienta' LIMIT 1", cbHerramienta.getSelectionModel().getSelectedItem());
                if (resultSetIDHerramienta.next()){
                    int id = resultSetIDHerramienta.getInt("id_material");
                    ResultSet resultSetUpdate = conexion.consultar("SELECT * FROM `herramienta` WHERE `cb_herramienta`= ? LIMIT 1", txtCB.getText());
                    if (resultSetUpdate.next()){
                        conexion.insmodelim("UPDATE `herramienta` SET `id_herramienta`= ?,`tipo`= ?,`caracteristicas`= ?,`frecuencia_de_uso`= ?,`cantidad`= ?,`cantidad_min`= ? WHERE `cb_herramienta`= ?",String.valueOf(id), txtTipo.getText(), txtCaracteristicas.getText(), ((RadioButton) toggleGroupFrecuencia.getSelectedToggle()).getText(), txtStock.getText(), txtStockMin.getText(), txtCB.getText());
                        Exito("Actualizado con exito");
                    }else {
                        conexion.insmodelim("INSERT INTO `herramienta`(`cb_herramienta`,`id_herramienta`, `tipo`, `caracteristicas`, `frecuencia_de_uso`, `cantidad`, `cantidad_min`) VALUES (?, ?, ?, ?, ?, ?, ?)",txtCB.getText(),String.valueOf(id),txtTipo.getText(),txtCaracteristicas.getText(),((RadioButton) toggleGroupFrecuencia.getSelectedToggle()).getText(),txtStock.getText(),txtStockMin.getText());

                    }
                    tabPaneHerramientas.getSelectionModel().select(tabSearch);
                    tabSearch.setDisable(false);
                    tabNew.setDisable(true);
                    ActivateBtn(false,true,false,true,false,false);
                    ActualizarTabla(conexion.consultar("SELECT * FROM `herramienta` INNER JOIN tipo_material ON herramienta.id_herramienta = tipo_material.id_material;"));
                    Exito(cbHerramienta.getSelectionModel().getSelectedItem()+" "+txtTipo.getText()+" agregado");
                }else {
                    Error("Selecciona el material");
                }
            }

        }else {
            Error("Faltan campos por rellenar");
        }*/
    }
    @FXML private void EditHerramienta() throws SQLException {
        try {
            if (tableViewHerramientas.getSelectionModel().getSelectedItem() == null){
                throw new Exception("Selecciona un registro");
            }
            Herramienta herramienta= tableViewHerramientas.getSelectionModel().getSelectedItem();
            tabPaneHerramientas.getSelectionModel().select(tabNew);
            tabSearch.setDisable(true);
            tabNew.setDisable(false);
            txtCB.setText(String.valueOf(herramienta.getCb_herramienta()));
            cbHerramienta.getSelectionModel().select(herramienta.getHerramienta());
            txtTipo.setText(herramienta.getTipo());
            txtCaracteristicas.setText(herramienta.getCaracteristicas());
            switch (herramienta.getFrecuencia_de_uso()) {
                case "Bajo" -> toggleGroupFrecuencia.selectToggle(rbBajo);
                case "Medio" -> toggleGroupFrecuencia.selectToggle(rbMedio);
                case "Alto" -> toggleGroupFrecuencia.selectToggle(rbAlto);
            }
            edit = true;
            cbedit = herramienta.getCb_herramienta();
            txtStock.setText(String.valueOf(herramienta.getCantidad()));
            txtStockMin.setText(String.valueOf(herramienta.getCantidad_min()));
            ActivateBtn(true,false,true,false,false,true);
        } catch (Exception e) {
            Error(e.getMessage());
        }
    }
    @FXML private void DeleteHerramienta() throws SQLException {
        try {
            if (tableViewHerramientas.getSelectionModel().getSelectedItem() == null){
                throw new Exception("Selecciona un registro");
            }
            Herramienta h = tableViewHerramientas.getSelectionModel().getSelectedItem();
            if (ConfirmarBorrar("Deseas borrar "+h.getHerramienta()+" "+h.getTipo())){
                conexion.insmodelim("DELETE FROM `herramienta` WHERE `cb_herramienta`= ?",String.valueOf(h.getCb_herramienta()));
                Exito("Registro borrado exitosamente");
                ActualizarTabla(conexion.consultar("SELECT * FROM `herramienta` INNER JOIN tipo_material ON herramienta.id_herramienta = tipo_material.id_material;"));
            }
        } catch (Exception e) {
            Error(e.getMessage());
        }
    }
    @FXML private void CancelHerramienta() throws SQLException {
        txtCB.setText("");
        CleanTextFields();
        ActivateBtn(false,true,false,true,false,false);
        tabPaneHerramientas.getSelectionModel().select(tabSearch);
        tabSearch.setDisable(false);
        tabNew.setDisable(true);
    }

    @FXML private void ExitHerramienta(){
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    @FXML private void GenerateCodeBar() {
        try {
            if (!txtCB.getText().matches("\\d{10}")){
                throw new Exception("Formato de código de barras no valido, asegurese de que sea un número de 10 digitos");
            }
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar Código de Barras");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagen PNG (*.png)", "*.png"));
            fileChooser.setInitialFileName(txtCB.getText()+".png");
            File file = fileChooser.showSaveDialog(new Stage());
            if (file == null) {
                throw new Exception("Se ha cancelado la operación");
            }
            Code39Bean code39Bean = new Code39Bean();
            final int dpi = 150;
            code39Bean.setModuleWidth(UnitConv.in2mm(1.0f / dpi));
            code39Bean.setWideFactor(3);
            code39Bean.doQuietZone(true);
            OutputStream out = new  FileOutputStream(file);
            BitmapCanvasProvider canvas = new BitmapCanvasProvider(out, "image/png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);
            code39Bean.generateBarcode(canvas, txtCB.getText());
            canvas.finish();
            Image image = null;
            try {
                image = new Image(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                Error(e.getMessage());
            }
            imgCodeBar.setImage(image);

        }catch (Exception e){
            Error(e.getMessage());
        }

    }

    @FXML private void PrintCodeBar() {
        try {
            FileChooser imageChooser = new FileChooser();
            imageChooser.setTitle("Seleccionar Código de Barras");
            imageChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imagen PNG (*.png)", "*.png"));

            File codigo_de_barras = imageChooser.showOpenDialog(new Stage());
            if (codigo_de_barras == null) {
                throw new Exception("No se selecciono el código de barras");
            }

            FileChooser documentoChooser = new FileChooser();
            documentoChooser.setTitle("Guardar Documento De Código de Barras");
            documentoChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf"));
            documentoChooser.setInitialFileName("CodigoDeBarras.pdf");

            File file = documentoChooser.showSaveDialog(new Stage());
            if (file == null) {
                throw new Exception("No se selecciono el destino");
            }

            Document documento = new Document(PageSize.A4);
            PdfWriter.getInstance(documento, new FileOutputStream(file));
            documento.open();
            BufferedImage image = ImageIO.read(codigo_de_barras);
            if (rbCBPequeño.isSelected()){
                PdfPTable pdfPTableCB = new PdfPTable(8);
                pdfPTableCB.setWidthPercentage(100);
                com.itextpdf.text.Image barcode = com.itextpdf.text.Image.getInstance(image, null);
                //Para modificar el tamaño y las cantidades se editan estos valores
                barcode.scaleToFit(100, 30);
                for (int i = 0; i < 40; i++) {
                    PdfPCell cell = new PdfPCell(barcode);
                    cell.setPadding(5);
                    cell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfPTableCB.addCell(cell);
                }
                documento.add(pdfPTableCB);
            } else if (rbCBMedio.isSelected()) {
                PdfPTable pdfPTableCB = new PdfPTable(4);
                pdfPTableCB.setWidthPercentage(100);
                com.itextpdf.text.Image barcode = com.itextpdf.text.Image.getInstance(image, null);
                //Para modificar el tamaño y las cantidades se editan estos valores
                barcode.scaleToFit(150, 50);
                for (int i = 0; i < 40; i++) {
                    PdfPCell cell = new PdfPCell(barcode);
                    cell.setPadding(5);
                    cell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfPTableCB.addCell(cell);
                }
                documento.add(pdfPTableCB);
            } else if (rbCBGrande.isSelected()) {
                PdfPTable pdfPTableCB = new PdfPTable(2);
                pdfPTableCB.setWidthPercentage(100);
                com.itextpdf.text.Image barcode = com.itextpdf.text.Image.getInstance(image, null);
                //Para modificar el tamaño se editan estos valores
                barcode.scaleToFit(200, 100);
                for (int i = 0; i < 20; i++) {
                    PdfPCell cell = new PdfPCell(barcode);
                    cell.setPadding(5);
                    cell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfPTableCB.addCell(cell);
                }
                documento.add(pdfPTableCB);
            }
            documento.close();
            Desktop.getDesktop().browse(file.toURI());
        }catch (Exception e){
            Error(e.getMessage());
        }
    }
    @FXML private void Busqueda() throws SQLException {
        String busqueda = txtBusqueda.getText();
        String criterio = "";
        if (rbID.isSelected() && !busqueda.equals("")){
            criterio="cb_herramienta";
        } else if (rbNombre.isSelected() && !busqueda.equals("")) {
            criterio="material";
        }
        if (!busqueda.equals("")){
            ActualizarTabla(conexion.consultar("SELECT * FROM `herramienta` INNER JOIN tipo_material ON herramienta.id_herramienta = tipo_material.id_material WHERE `"+criterio+"` LIKE '%"+busqueda+"%'"));
        }else {
            ActualizarTabla(conexion.consultar("SELECT * FROM `herramienta` INNER JOIN tipo_material ON herramienta.id_herramienta = tipo_material.id_material;"));
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

        Scene dialogScene = new Scene(vbox, 300, 100);
        dialog.setScene(dialogScene);
        dialog.showAndWait();

        return confirmar.get();
    }

    private void ActualizarTabla(ResultSet rsHerramientas) throws SQLException {
        int cont=0;
        tableViewHerramientas.getItems().clear();
        while (rsHerramientas.next()){
            Herramienta h= new Herramienta(rsHerramientas.getLong("cb_herramienta"),
                    rsHerramientas.getString("material"),
                    rsHerramientas.getString("tipo"),
                    rsHerramientas.getString("caracteristicas"),
                    rsHerramientas.getString("frecuencia_de_uso"),
                    rsHerramientas.getInt("cantidad"),
                    rsHerramientas.getInt("cantidad_min"));
            tableViewHerramientas.getItems().add(h);
            cont++;
        }
        lblRegistros.setText("Se cargaron "+cont+" herramientas");
    }

    private void ActivateBtn(boolean New, boolean save, boolean edit, boolean cancel, boolean exit, boolean delete) throws SQLException {
        if (LoginController.resultado.getInt("create_herramienta")==0){
            btnNew.setDisable(true);
        }else {btnNew.setDisable(New);}
        if (LoginController.resultado.getInt("update_herramienta")==0){
            btnEdit.setDisable(true);
        }else {btnEdit.setDisable(edit);}
        if (LoginController.resultado.getInt("delete_herramienta")==0){
            btnDelete.setDisable(true);
        }else {btnDelete.setDisable(delete);}

        btnSave.setDisable(save);
        btnCancel.setDisable(cancel);
        btnExit.setDisable(exit);
    }
   @FXML private Long GenerateNumber() throws SQLException {
        boolean bd=false;
        Random random=new Random();
        long numero = (long)(random.nextDouble()*10000000000L);
        while(!String.valueOf(numero).matches("\\d{10}") && !bd){
            numero = (long)(random.nextDouble()*10000000000L);
            bd= VerifyCB(numero);
        }
        txtCB.setText(String.valueOf(numero));
        return numero;
    }
    private boolean VerifyCB(long num) throws SQLException {
        ResultSet res= conexion.consultar("SELECT herramienta.cb_herramienta FROM herramienta\n" +
                "                LEFT JOIN material\n" +
                "                ON herramienta.cb_herramienta = material.cb_material\n" +
                "                WHERE herramienta.cb_herramienta = ? OR material.cb_material = ?\n" +
                "                UNION\n" +
                "                SELECT material.cb_material\n" +
                "                FROM material\n" +
                "                LEFT JOIN herramienta\n" +
                "                ON herramienta.cb_herramienta = material.cb_material\n" +
                "                WHERE herramienta.cb_herramienta = ? OR material.cb_material = ?\n" +
                "                AND herramienta.cb_herramienta IS NULL;",String.valueOf(num),String.valueOf(num),String.valueOf(num),String.valueOf(num));

        boolean bd=false;
        if (res.next()){
            bd=true;
        }else {
            return bd;
        }
        return bd;
    }
    private void CleanTextFields(){
        txtTipo.setText("");
        txtCaracteristicas.setText("");
        txtStock.setText("");
        txtStockMin.setText("");
    }

    private boolean VerifyTxt(TextArea txtCar, ComboBox<String> cbHerramienta, TextField... campos){
        for (TextField campo : campos){
            if (campo.getText().isEmpty()){
                return false;
            }
        }
        return !txtCar.getText().isEmpty() && cbHerramienta.getSelectionModel().getSelectedIndex() != -1;
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
