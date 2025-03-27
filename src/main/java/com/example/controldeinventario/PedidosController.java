package com.example.controldeinventario;

import com.example.controldeinventario.Datos.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PedidosController {
    ZoneId zonaHoraria = ZoneId.of("UTC-6");
    DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    ToggleGroup toggleGroupBusqueda = new ToggleGroup();
    @FXML TabPane tabPaneVentana;
    @FXML Tab tabSearch, tabNew;
    static Conexion conexion;
    @FXML Label lblContador;
    @FXML RadioButton rbID, rbNumControl, rbProfesor, rbMaterial;
    @FXML TextField txtBusqueda;
    @FXML TableView<Pedido> tableViewPedidos = new TableView<>();
    @FXML Button btnNew, btnSave, btnEdit, btnDelete, btnCancel, btnExit;
    @FXML TextField txtID, txtNumControl, txtFecha, txtProfesor, txtMateria, txtBusquedaID, txtNombre;
    @FXML CheckBox checkBoxNA1,checkBoxNA2,checkBoxNA3;
    @FXML CheckBox checkBoxPendiente, checkBoxEntregado;
    public static ObservableList<Registro> productos = FXCollections.observableArrayList();
    @FXML
    TableView<Registro> tableViewPedidoMaterial = new TableView<>();
    TableColumn<Pedido, Integer> tableColumnIDPedido = new TableColumn<>("ID Pedido");
    TableColumn<Registro,Long> tableColumnCB = new TableColumn<>("Código de barras");
    TableColumn<Pedido, String> tableColumnNombrePersona = new TableColumn<>("Nombre");
    TableColumn<Pedido, String> tableColumnNumControl = new TableColumn<>("Número de control");
    TableColumn tableColumnEstado = new TableColumn<>("Estado");
    TableColumn<Registro,String> tableColumnEstadoIndividual = new TableColumn<>("Estado");
    TableColumn <Pedido, Date> tableColumnFecha = new TableColumn<>("Fecha");
    TableColumn<Pedido, String> tableColumnProfesor = new TableColumn<>("Profesor");
    TableColumn<Pedido, String> tableColumnMateria = new TableColumn<>("Materia");
    public Stage ventanaSecundaria = new Stage();
    TableColumn<Registro, Integer> tableColumnNumero = new TableColumn<>("No");
    TableColumn<Registro,String> tableColumnNombre = new TableColumn<>("Nombre");
    TableColumn<Registro,String> tableColumnModelo = new TableColumn<>("Modelo");
    TableColumn<Registro,Double> tableColumnValor = new TableColumn<>("Valor");
    TableColumn<Registro,String> tableColumnMedida = new TableColumn<>("Medida");
    TableColumn<Registro,String> tableColumnBtnMinus = new TableColumn<>("     ");
    TableColumn<Registro,Integer> tableColumnItemCount = new TableColumn<>("Cantidad");
    TableColumn<Registro,String> tableColumnBtnPlus = new TableColumn<>("      ");
    TableColumn<Registro,String> tableColumnBtnDelete = new TableColumn<>("        ");

    Callback<TableColumn<Registro, Integer>, TableCell<Registro, Integer>> celdaNo =
            objectStringTableColumn -> {
                return new TableCell<Registro, Integer>() {
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
    Callback<TableColumn<Registro,String>, TableCell<Registro,String>> celdaMinus=
            objectStringTableColumn -> {
                return new TableCell<Registro,String>(){
                    Button btnMinus = new Button("-");

                    @Override
                    protected void updateItem(String s, boolean b) {
                        if (b){
                            setGraphic(null);
                            setText(null);
                        }else {
                            btnMinus.setDisable(productos.get(getIndex()).isEntregado());
                            btnMinus.setOnAction(event -> {
                                if (!productos.get(getIndex()).isEntregado()){
                                    Registro r = productos.get(getIndex());

                                    if ((r.getCantidad()-1) <=0 && r.getId_registro() ==0){
                                        productos.remove(getIndex());
                                    }else if ((r.getCantidad()-1)>=1){
                                        r.setCantidad(r.getCantidad()-1);
                                        productos.set(getIndex(),r);
                                    }
                                }else {
                                    btnMinus.setDisable(true);
                                }



                            });
                            setGraphic(btnMinus);
                            setText(null);
                        }


                    }
                };
            };

    Callback<TableColumn<Registro,String>, TableCell<Registro,String>> celdaPlus=
            objectStringTableColumn -> {
                return new TableCell<Registro,String>(){
                    Button btnPlus = new Button("+");

                    @Override
                    protected void updateItem(String s, boolean b) {
                        if (b){
                            setGraphic(null);
                            setText(null);
                        }else {
                            btnPlus.setDisable(productos.get(getIndex()).isEntregado());
                            btnPlus.setOnAction(event -> {
                                if (!productos.get(getIndex()).isEntregado()){
                                    Registro r = productos.get(getIndex());
                                    try {
                                        if (VerificarCantidad(r.getCb(),r.getCantidad()+1)){
                                            r.setCantidad(r.getCantidad()+1);
                                            if (r.getCantidad() <=0 && r.getId_registro()==0){
                                                productos.remove(getIndex());
                                            }else {
                                                productos.set(getIndex(),r);
                                            }
                                        }
                                    } catch (SQLException e) {
                                        throw new RuntimeException(e);
                                    }
                                }else {
                                    btnPlus.setDisable(true);
                                }


                            });
                            setGraphic(btnPlus);
                            setText(null);
                        }


                    }
                };
            };

    Callback<TableColumn<Registro,String>, TableCell<Registro,String>> celdaDelete=
            objectStringTableColumn -> {
                return new TableCell<Registro,String>(){
                    Button btnDlte = new Button("Quitar");

                    @Override
                    protected void updateItem(String s, boolean b) {
                        if (b){
                            setGraphic(null);
                            setText(null);
                        }else {
                            btnDlte.setDisable(productos.get(getIndex()).isEntregado() || productos.get(getIndex()).getId_registro() !=0);
                            btnDlte.setOnAction(event -> {
                                if (!productos.get(getIndex()).isEntregado()){
                                    productos.remove(getIndex());
                                }else {
                                    btnDelete.setDisable(true);
                                }

                            });
                            setGraphic(btnDlte);
                            setText(null);
                        }


                    }
                };
            };


    Callback<TableColumn<Registro,String>, TableCell<Registro,String>> celdaEstado=
            objectStringTableColumn -> {
                return new TableCell<>() {
                    CheckBox checkBox = new CheckBox("");

                    @Override
                    protected void updateItem(String s, boolean b) {
                        if (b) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            try {
                                if (LoginController.resultado.getInt("crud_pedido")==0){
                                    checkBox.setDisable(true);
                                }
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                            checkBox.setSelected(tableViewPedidos.getItems().get(getIndex()).getEstado().equals("Entregado"));
                            ResultSet rsEstado = conexion.consultar("SELECT * FROM `pedido_material` WHERE `id_pedido`= ? AND `estado`='Pendiente'", String.valueOf(tableViewPedidos.getItems().get(getIndex()).getId_pedido()));
                            try {
                                checkBox.setSelected(!rsEstado.next());
                            }catch (SQLException e) {
                                throw new RuntimeException(e);
                            }

                            checkBox.setOnAction(event -> {
                                        if (checkBox.isSelected()){
                                            ResultSet rsArticulos = conexion.consultar("SELECT `cb_material`,`id_pedido`,`cantidad`,`estado` FROM `pedido_material` WHERE `id_pedido`= ? AND `estado`='Pendiente'", String.valueOf(tableViewPedidos.getItems().get(getIndex()).getId_pedido()));
                                            conexion.insmodelim("UPDATE `pedido` SET `estado`='Entregado' WHERE `id_pedido`= ?",String.valueOf(tableViewPedidos.getItems().get(getIndex()).getId_pedido()));
                                            try {
                                                while (rsArticulos.next()){
                                                    ResultSet rsArticulo = conexion.consultar("SELECT * FROM `material` INNER JOIN tipo_material ON material.id_material = tipo_material.id_material WHERE cb_material= ?", String.valueOf(rsArticulos.getLong("cb_material")));
                                                    ResultSet rsHerramienta = conexion.consultar("SELECT * FROM `herramienta` INNER JOIN tipo_material ON herramienta.id_herramienta = tipo_material.id_material WHERE cb_herramienta= ?",String.valueOf(rsArticulos.getLong("cb_material")));
                                                    if (rsArticulo.next() && rsArticulos.getString("estado").equals("Pendiente")){
                                                        conexion.insmodelim("UPDATE `pedido_material` SET`estado`='Entregado' WHERE `cb_material`= ? AND id_pedido= ?", String.valueOf(rsArticulos.getLong("cb_material")), String.valueOf(rsArticulos.getInt("id_pedido")));
                                                        conexion.insmodelim("UPDATE `material` SET `cantidad`= ? WHERE cb_material= ?", String.valueOf((rsArticulo.getInt("cantidad")+rsArticulos.getInt("cantidad"))), String.valueOf(rsArticulos.getLong("cb_material")));
                                                    }else if (rsHerramienta.next() && rsArticulos.getString("estado").equals("Pendiente")){
                                                        conexion.insmodelim("UPDATE `herramienta` SET `cantidad`= ? WHERE `cb_herramienta`= ?", String.valueOf((rsHerramienta.getInt("cantidad")+rsArticulos.getInt("cantidad"))), String.valueOf(rsHerramienta.getLong("cb_herramienta")));
                                                        conexion.insmodelim("UPDATE `pedido_material` SET`estado`='Entregado' WHERE `cb_material`= ? AND id_pedido= ?", String.valueOf(rsArticulos.getLong("cb_material")),String.valueOf(rsArticulos.getInt("id_pedido")));
                                                    }
                                                }

                                            }catch (SQLException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }else {
                                            ResultSet rsArticulos = conexion.consultar("SELECT `cb_material`,`id_pedido`,`cantidad`,`estado` FROM `pedido_material` WHERE `id_pedido`= ? AND `estado`='Entregado'", String.valueOf(tableViewPedidos.getItems().get(getIndex()).getId_pedido()));
                                            conexion.insmodelim("UPDATE `pedido` SET `estado`='Pendiente' WHERE `id_pedido`= ?",String.valueOf(tableViewPedidos.getItems().get(getIndex()).getId_pedido()));
                                            try {
                                                while (rsArticulos.next()){
                                                    ResultSet rsArticulo = conexion.consultar("SELECT * FROM `material` INNER JOIN tipo_material ON material.id_material = tipo_material.id_material WHERE cb_material= ?",String.valueOf(rsArticulos.getLong("cb_material")));
                                                    ResultSet rsHerramienta = conexion.consultar("SELECT * FROM `herramienta` INNER JOIN tipo_material ON herramienta.id_herramienta = tipo_material.id_material WHERE cb_herramienta= ?",String.valueOf(rsArticulos.getLong("cb_material")));
                                                    if (rsArticulo.next() && rsArticulos.getString("estado").equals("Entregado")){
                                                        conexion.insmodelim("UPDATE `pedido_material` SET`estado`='Pendiente' WHERE `cb_material`= ?",String.valueOf(rsArticulos.getLong("cb_material")));
                                                        conexion.insmodelim("UPDATE `material` SET `cantidad`= ? WHERE cb_material= ?", String.valueOf((rsArticulo.getInt("cantidad")-rsArticulos.getInt("cantidad"))), String.valueOf(rsArticulos.getLong("cb_material")));
                                                    }else if (rsHerramienta.next() && rsArticulos.getString("estado").equals("Entregado")){
                                                        conexion.insmodelim("UPDATE `pedido_material` SET`estado`='Pendiente' WHERE `cb_material`= ?", String.valueOf(rsArticulos.getLong("cb_material")));
                                                        conexion.insmodelim("UPDATE `herramienta` SET `cantidad`= ? WHERE `cb_herramienta`= ?", String.valueOf((rsHerramienta.getInt("cantidad")-rsArticulos.getInt("cantidad"))), String.valueOf(rsHerramienta.getLong("cb_herramienta")));
                                                    }
                                                }

                                            }catch (SQLException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    }
                            );
                            setGraphic(checkBox);
                            setText(null);
                        }


                    }
                };
            };

    Callback<TableColumn<Registro,String>, TableCell<Registro,String>> celdaEstadoIndividual=
            objectStringTableColumn -> {
                return new TableCell<>() {
                    CheckBox checkBox = new CheckBox("");

                    @Override
                    protected void updateItem(String s, boolean b) {
                        if (b) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            checkBox.setSelected(productos.get(getIndex()).isEntregado());
                            checkBox.setDisable(productos.get(getIndex()).getId_registro()==0);
                            checkBox.setOnAction(event -> {
                                ObservableList<Registro> productosRESP = FXCollections.observableArrayList();
                                productosRESP.clear();
                                productosRESP.addAll(productos);
                                productos.clear();
                                productos.addAll(productosRESP);

                                if (checkBox.isSelected()){
                                    checkBox.setDisable(productos.get(getIndex()).isEntregado());
                                    checkBox.setDisable(false);
                                    productos.get(getIndex()).setEntregado(true);

                                        }else {
                                    productos.get(getIndex()).setEntregado(false);

                                        }
                                    }
                            );
                            setGraphic(checkBox);
                            setText(null);
                        }


                    }
                };
            };





    @FXML protected void initialize() throws SQLException {
        Platform.runLater(() -> {
            txtBusqueda.requestFocus();
            txtBusqueda.selectEnd();
        });
        ActivateBtn(false,true,false,true, false);
        rbID.setToggleGroup(toggleGroupBusqueda);
        rbMaterial.setToggleGroup(toggleGroupBusqueda);
        rbNumControl.setToggleGroup(toggleGroupBusqueda);
        rbProfesor.setToggleGroup(toggleGroupBusqueda);
        conexion= new Conexion();
        productos = FXCollections.observableArrayList();
        tableColumnNumero.setCellFactory(celdaNo);
        tableColumnNumero.setMaxWidth(40);
        tableColumnCB.setCellValueFactory(new PropertyValueFactory<Registro,Long>("cb"));
        tableColumnNombre.setCellValueFactory(new PropertyValueFactory<Registro,String>("nombre"));
        tableColumnModelo.setCellValueFactory(new PropertyValueFactory<Registro,String>("tipo"));
        tableColumnValor.setCellValueFactory(new PropertyValueFactory<Registro,Double>("valor"));
        tableColumnMedida.setCellValueFactory(new PropertyValueFactory<Registro,String>("unidad_medida"));
        tableColumnBtnMinus.setCellFactory(celdaMinus);
        tableColumnBtnMinus.setMaxWidth(30);
        tableColumnItemCount.setCellValueFactory(new PropertyValueFactory<Registro,Integer>("cantidad"));
        tableColumnBtnPlus.setCellFactory(celdaPlus);
        tableColumnBtnPlus.setMaxWidth(35);
        tableColumnBtnDelete.setCellFactory(celdaDelete);
        tableColumnEstadoIndividual.setCellFactory(celdaEstadoIndividual);

        tableViewPedidoMaterial.getColumns().addAll( tableColumnNumero,tableColumnCB,tableColumnNombre,tableColumnModelo,tableColumnValor,tableColumnMedida,tableColumnBtnMinus, tableColumnItemCount,tableColumnBtnPlus,tableColumnBtnDelete,tableColumnEstadoIndividual);
        tableViewPedidoMaterial.setItems(productos);

        tableColumnIDPedido.setCellValueFactory(new PropertyValueFactory<Pedido, Integer>("id_pedido"));
        tableColumnNombrePersona.setCellValueFactory(new PropertyValueFactory<Pedido, String>("nombre_persona"));
        tableColumnNumControl.setCellValueFactory(new PropertyValueFactory<Pedido, String>("num_control"));
        tableColumnEstado.setCellFactory(celdaEstado);
        tableColumnFecha.setCellValueFactory(new PropertyValueFactory<Pedido, Date>("fecha"));
        tableColumnProfesor.setCellValueFactory(new PropertyValueFactory<Pedido, String>("profesor"));
        tableColumnMateria.setCellValueFactory(new PropertyValueFactory<Pedido, String>("materia"));

        tableViewPedidos.getColumns().addAll(tableColumnIDPedido,tableColumnNombrePersona,tableColumnNumControl,tableColumnEstado,tableColumnFecha,tableColumnProfesor,tableColumnMateria);
        ActualizarTabla(conexion.consultar("SELECT * FROM `pedido`"));
    }

    @FXML private void NewPedido() throws SQLException {
        Platform.runLater(() -> {
            txtNumControl.requestFocus();
            txtNumControl.selectEnd();
        });
        productos.clear();
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zonaHoraria);

        ActivateBtn(false,false,true,false, true);
        tabPaneVentana.getSelectionModel().select(tabNew);
        tabNew.setDisable(false);
        tabSearch.setDisable(true);
        CleanTextFields();
        txtFecha.setText(zonedDateTime.format(formato));


    }
    @FXML private void SavePedido(){
        try{
            if(productos.isEmpty()){
                throw new Exception("No se ha seleccionado ningún articulo");
            }
            if (!VerifyTxt(txtNumControl, txtProfesor, txtMateria , txtNombre)){
                throw new Exception("Faltan campos por rellenar");
            }
            ResultSet rsEdit = conexion.consultar("SELECT * FROM `pedido` WHERE `id_pedido`= ?",txtID.getText());
            if (rsEdit.next()){//Editar
                conexion.insmodelim("UPDATE `pedido` SET `nombre_persona`= ?,`num_control`= ?,`profesor`= ?,`materia`= ? WHERE `id_pedido`= ?", txtNombre.getText(), txtNumControl.getText(), txtProfesor.getText(), txtMateria.getText(), txtID.getText());
                ResultSet rsPMateriales= conexion.consultar("SELECT * FROM `pedido_material` WHERE `id_pedido`= ?",txtID.getText());
                conexion.insmodelim("DELETE FROM `pedido_material` WHERE `id_pedido`= ?",txtID.getText());
                while (rsPMateriales.next()){
                    Registro registroBD = new Registro(rsPMateriales.getLong("cb_material"),rsPMateriales.getInt("id_registro"),rsPMateriales.getInt("cantidad"),(rsPMateriales.getString("estado")).equals("Entregado"));
                    ActualizarCantidadesBD(registroBD);
                }
                Exito("Pedido actualizado correctamente");
            }else {//Guardar alumno, dia de la semana, materia etc.
                conexion.insmodelim("INSERT INTO `pedido`(`nombre_persona`, `num_control`, `estado`, `fecha`, `profesor`, `materia`) VALUES " +
                        "(?, ?, 'Pendiente', ?, ?, ?)",txtNombre.getText(),txtNumControl.getText(),txtFecha.getText(),txtProfesor.getText(),txtMateria.getText());
                ResultSet rsID= conexion.consultar("SELECT `id_pedido` FROM pedido ORDER BY `id_pedido` DESC LIMIT 1;");
                if (rsID.next()){
                    int id = rsID.getInt("id_pedido");
                    for (Registro registro : productos){
                        conexion.insmodelim("INSERT INTO `pedido_material`(`id_pedido`,`cb_material`, `cantidad`,`estado`) VALUES (?, ?, ?, ?)",id,registro.getCb(),registro.getCantidad(), (registro.isEntregado() ? "Entregado" : "Pendiente"));//SE OCUPA CAMBIAR ESTO/CREO QUE YA JALA NO SE OCUPA MAS
                        ResultSet rsArticulo = conexion.consultar("SELECT * FROM `material` INNER JOIN tipo_material ON material.id_material = tipo_material.id_material WHERE cb_material= ?",registro.getCb());
                        ResultSet rsHerramienta = conexion.consultar("SELECT * FROM `herramienta` INNER JOIN tipo_material ON herramienta.id_herramienta = tipo_material.id_material WHERE cb_herramienta= ?",registro.getCb());
                        if (rsArticulo.next()){
                            conexion.insmodelim("UPDATE `material` SET `cantidad`= ? WHERE cb_material= ?",(rsArticulo.getInt("cantidad")-registro.getCantidad()), registro.getCb());
                        }else if (rsHerramienta.next()){
                            conexion.insmodelim("UPDATE `herramienta` SET `cantidad`= ? WHERE `cb_herramienta`= ?", (rsHerramienta.getInt("cantidad")-registro.getCantidad()),registro.getCb());

                        }

                    }
                }
                if (txtNumControl.getText().matches("\\d{2}[cC][gG]\\d{4}")) {
                    Map<DayOfWeek, String> daysOfWeek = new HashMap<>();
                    daysOfWeek.put(DayOfWeek.MONDAY, "Lunes");
                    daysOfWeek.put(DayOfWeek.TUESDAY, "Martes");
                    daysOfWeek.put(DayOfWeek.WEDNESDAY, "Miércoles");
                    daysOfWeek.put(DayOfWeek.THURSDAY, "Jueves");
                    daysOfWeek.put(DayOfWeek.FRIDAY, "Viernes");
                    daysOfWeek.put(DayOfWeek.SATURDAY, "Sábado");
                    daysOfWeek.put(DayOfWeek.SUNDAY, "Domingo");

                    String diaSemanaEspanol = daysOfWeek.get(LocalDateTime.parse(txtFecha.getText(), formato).toLocalDate().getDayOfWeek());
                    LocalTime horaInicio = LocalDateTime.parse(txtFecha.getText(), formato).toLocalTime().withMinute(0).withSecond(0);
                    LocalTime horaFin = LocalDateTime.parse(txtFecha.getText(), formato).toLocalTime().withHour(LocalDateTime.parse(txtFecha.getText(), formato).toLocalTime().getHour()+1).withMinute(0).withSecond(0);
                    ResultSet rsAlumno= conexion.consultar("SELECT * FROM `alumnos` WHERE `num_control`= ?",txtNumControl.getText());
                    if (!rsAlumno.next()){
                        conexion.insmodelim("INSERT INTO `alumnos`(`num_control`, `nombre_alumno`) VALUES (?, ?)",txtNumControl.getText(),txtNombre.getText());
                        conexion.insmodelim("INSERT INTO `materia`(`num_control`, `dia`, `hora_inicio`, `hora_fin`, `profesor`,`nom_materia`) VALUES (?, ?, ?, ?, ?, ?)",txtNumControl.getText(),diaSemanaEspanol,String.valueOf(horaInicio),String.valueOf(horaFin),txtProfesor.getText(),txtMateria.getText());
                    }else {
                        ResultSet rsMateria= conexion.consultar("SELECT * FROM `materia` WHERE `num_control`= ? AND `dia`= ? AND `hora_inicio`= ? AND `hora_fin`= ? AND `profesor`= ? AND `nom_materia`= ?", txtNumControl.getText(), diaSemanaEspanol, String.valueOf(horaInicio), String.valueOf(horaFin), txtProfesor.getText(), txtMateria.getText());//FALTA
                        if (!rsMateria.next()){
                            conexion.insmodelim("INSERT INTO `materia`(`num_control`, `dia`, `hora_inicio`, `hora_fin`, `profesor`,`nom_materia`) VALUES (?, ?, ?, ?, ?, ?)",txtNumControl.getText(),diaSemanaEspanol,String.valueOf(horaInicio),String.valueOf(horaFin),txtProfesor.getText(),txtMateria.getText());
                        }
                    }
                }
                Exito("Pedido agregado correctamente");
            }
            tabPaneVentana.getSelectionModel().select(tabSearch);
            tabSearch.setDisable(false);
            tabNew.setDisable(true);
            ActivateBtn(false,true,false,true, false);
            ActualizarTabla(conexion.consultar("SELECT * FROM `pedido`"));
            CleanTextFields();
        } catch (Exception e) {
            Error(e.getMessage());
        }
    }
    private void ActualizarCantidadesBD(Registro registroBD) throws SQLException {
        Iterator<Registro> iterator = productos.iterator();
        while (iterator.hasNext()){
            Registro registroT= iterator.next();
            if (registroT.getId_registro() == registroBD.getId_registro()){
                ResultSet rsArticulo = conexion.consultar("SELECT `tipo`,`cantidad`,`valor`,`unidad_de_medida`,tipo_material.material FROM `material` INNER JOIN tipo_material ON material.id_material = tipo_material.id_material WHERE cb_material= ?",registroBD.getCb());
                if (rsArticulo.next()) {
                    if (!registroBD.isEntregado() && registroT.isEntregado()){
                        conexion.insmodelim("UPDATE `material` SET `cantidad`= ? WHERE `cb_material`= ?",(rsArticulo.getInt("cantidad")+ registroT.getCantidad()),registroBD.getCb());
                    } else if ((registroBD.isEntregado() && !registroT.isEntregado()) ) {
                        conexion.insmodelim("UPDATE `material` SET `cantidad`= ? WHERE `cb_material`= ?",(rsArticulo.getInt("cantidad")- registroT.getCantidad()), registroBD.getCb());
                    } else if (registroBD.isEntregado() && registroT.isEntregado()) {
                        conexion.insmodelim("UPDATE `material` SET `cantidad`= ? WHERE `cb_material`= ?",(rsArticulo.getInt("cantidad")-(registroBD.getCantidad()- registroT.getCantidad())), registroBD.getCb());
                    } else if (!registroBD.isEntregado() && !registroT.isEntregado()) {
                        conexion.insmodelim("UPDATE `material` SET `cantidad`= ? WHERE `cb_material`= ?",(rsArticulo.getInt("cantidad")+(registroBD.getCantidad()- registroT.getCantidad())),registroBD.getCb());
                    }
                } else {
                    ResultSet rsHerramienta = conexion.consultar("SELECT tipo_material.material,`tipo`,`cantidad` FROM `herramienta` INNER JOIN tipo_material ON herramienta.id_herramienta = tipo_material.id_material WHERE cb_herramienta= ?",registroBD.getCb());
                    if (rsHerramienta.next()) {
                        if ((!registroBD.isEntregado() && registroT.isEntregado()) ){
                            conexion.insmodelim("UPDATE `herramienta` SET `cantidad`= ? WHERE `cb_herramienta`= ?", (rsHerramienta.getInt("cantidad")+ registroT.getCantidad()), registroBD.getCb());
                        } else if ((registroBD.isEntregado() && !registroT.isEntregado()) ) {
                            conexion.insmodelim("UPDATE `herramienta` SET `cantidad`= ? WHERE `cb_herramienta`= ?", (rsHerramienta.getInt("cantidad")- registroT.getCantidad()), registroBD.getCb());
                        } else if (registroBD.isEntregado() && registroT.isEntregado()) {
                            conexion.insmodelim("UPDATE `herramienta` SET `cantidad`= ? WHERE `cb_herramienta`= ?", (rsHerramienta.getInt("cantidad")-(registroBD.getCantidad()- registroT.getCantidad())), registroBD.getCb());
                        } else if (!registroBD.isEntregado() && !registroT.isEntregado()) {
                            conexion.insmodelim("UPDATE `herramienta` SET `cantidad`= ? WHERE `cb_herramienta`= ?", (rsHerramienta.getInt("cantidad")+ (registroBD.getCantidad()-registroT.getCantidad())), registroBD.getCb());

                        }
                    }
                }
                conexion.insmodelim("INSERT INTO `pedido_material`( `id_pedido`, `cb_material`, `cantidad`, `estado`) VALUES (?, ?, ?, ?)",txtID.getText(),registroT.getCb(),registroT.getCantidad(),(registroT.isEntregado() ? "Entregado" : "Pendiente"));
            } else if (registroT.getId_registro()==0) {// X > P
                ResultSet rsArticulo = conexion.consultar("SELECT `tipo`,`cantidad`,`valor`,`unidad_de_medida`,tipo_material.material FROM `material` INNER JOIN tipo_material ON material.id_material = tipo_material.id_material WHERE cb_material= ?", registroT.getCb());
                ResultSet rsHerramienta = conexion.consultar("SELECT tipo_material.material,`tipo`,`cantidad` FROM `herramienta` INNER JOIN tipo_material ON herramienta.id_herramienta = tipo_material.id_material WHERE cb_herramienta= ?",registroT.getCb());
                if (rsArticulo.next()){
                    conexion.insmodelim("UPDATE `material` SET `cantidad`= ? WHERE `cb_material`= ?",(rsArticulo.getInt("cantidad")- registroT.getCantidad())+registroT.getCb());
                } else if (rsHerramienta.next()) {
                    conexion.insmodelim("UPDATE `herramienta` SET `cantidad`= ? WHERE `cb_herramienta`= ?",(rsHerramienta.getInt("cantidad")- registroT.getCantidad()),registroT.getCb());
                }
                conexion.insmodelim("INSERT INTO `pedido_material`( `id_pedido`, `cb_material`, `cantidad`, `estado`) VALUES (?, ?, ?, ?)", txtID.getText(),registroT.getCb(),registroT.getCantidad(),(registroT.isEntregado() ? "Entregado" : "Pendiente"));
                iterator.remove();
            }
        }





    }
    @FXML private void EditPedido() {
        try {
            productos.clear();
            if (tableViewPedidos.getSelectionModel().getSelectedItem() == null){
                throw new Exception("Seleccione un registro");
            }
            Pedido pedido= tableViewPedidos.getSelectionModel().getSelectedItem();
            ResultSet rsPedido = conexion.consultar("SELECT * FROM `pedido` WHERE `id_pedido`= ?",pedido.getId_pedido());
            if (rsPedido.next()){
                tabPaneVentana.getSelectionModel().select(tabNew);
                tabSearch.setDisable(true);
                tabNew.setDisable(false);
                txtID.setText(String.valueOf(rsPedido.getInt("id_pedido")));
                txtFecha.setText(String.valueOf(rsPedido.getDate("fecha")));
                txtMateria.setText(rsPedido.getString("materia"));
                txtNumControl.setText(rsPedido.getString("num_control"));
                txtProfesor.setText(rsPedido.getString("profesor"));
                txtNombre.setText(rsPedido.getString("nombre_persona"));
                if (txtNumControl.getText().equals("N/A")){
                    checkBoxNA1.setSelected(true);
                    txtNumControl.setDisable(true);
                }
                if (txtProfesor.getText().equals("N/A")){
                    checkBoxNA2.setSelected(true);
                    txtProfesor.setDisable(true);
                }
                if (txtMateria.getText().equals("N/A")){
                    checkBoxNA3.setSelected(true);
                    txtMateria.setDisable(true);
                }
                ResultSet rsArticulos = conexion.consultar("SELECT * FROM `pedido_material` WHERE `id_pedido`= ?",txtID.getText());
                while (rsArticulos.next()){
                    ResultSet rsArticulo = conexion.consultar("SELECT `tipo`,`cantidad`,`valor`,`unidad_de_medida`,tipo_material.material FROM `material` INNER JOIN tipo_material ON material.id_material = tipo_material.id_material WHERE cb_material= ?",rsArticulos.getLong("cb_material"));
                    if (rsArticulo.next()){
                        Registro registro = new Registro(rsArticulos.getLong("cb_material"),rsArticulos.getInt("id_registro"),rsArticulo.getString("material"),rsArticulo.getString("tipo"),rsArticulo.getString("valor"),rsArticulo.getString("unidad_de_medida"),rsArticulos.getInt("cantidad"), (rsArticulos.getString("estado").equals("Entregado")));
                        AgregarMaterial(registro);
                        continue;
                    }
                    ResultSet rsHerramienta = conexion.consultar("SELECT tipo_material.material,`tipo`,`cantidad` FROM `herramienta` INNER JOIN tipo_material ON herramienta.id_herramienta = tipo_material.id_material WHERE cb_herramienta= ?",rsArticulos.getLong("cb_material"));
                    if (rsHerramienta.next()){
                        Registro registro = new Registro(rsArticulos.getLong("cb_material"),rsArticulos.getInt("id_registro"),rsHerramienta.getString("material"), rsHerramienta.getString("tipo"),rsArticulos.getInt("cantidad"),(rsArticulos.getString("estado").equals("Entregado")));
                        AgregarMaterial(registro);
                    }
                }
            }
            ActivateBtn(true,false,true,false, true);
        } catch (Exception e) {
            Error(e.getMessage());
        }
    }
    @FXML private void DeletePedido() {
        try {
            if (tableViewPedidos.getSelectionModel().getSelectedItem() == null){
                throw new Exception("Seleccione un registro");
            }
            Pedido pedido = tableViewPedidos.getSelectionModel().getSelectedItem();
            if (ConfirmarBorrar("Deseas borrar este pedido?")){
                ResultSet rsMateriales = conexion.consultar("SELECT * FROM `pedido_material` WHERE `id_pedido`= ?", pedido.getId_pedido());
                while (rsMateriales.next()){
                    if (rsMateriales.getString("estado").equals("Pendiente")){
                        ResultSet rsArticulo = conexion.consultar("SELECT `tipo`,`cantidad`,`valor`,`unidad_de_medida`,tipo_material.material FROM `material` INNER JOIN tipo_material ON material.id_material = tipo_material.id_material WHERE cb_material= ?",rsMateriales.getLong("cb_material"));
                        if (rsArticulo.next()){
                            conexion.insmodelim("UPDATE `material` SET `cantidad`= ? WHERE `cb_material`= ?", (rsArticulo.getInt("cantidad")+rsMateriales.getInt("cantidad")), rsMateriales.getLong("cb_material"));
                            continue;
                        }
                        ResultSet rsHerramienta = conexion.consultar("SELECT tipo_material.material,`tipo`,`cantidad` FROM `herramienta` INNER JOIN tipo_material ON herramienta.id_herramienta = tipo_material.id_material WHERE cb_herramienta= ?",rsMateriales.getLong("cb_material"));
                        if (rsHerramienta.next()){
                            conexion.insmodelim("UPDATE `herramienta` SET `cantidad`= ? WHERE `cb_herramienta`= ?", (rsHerramienta.getInt("cantidad")+rsMateriales.getInt("cantidad")), rsMateriales.getLong("cb_material"));
                        }
                    }

                }
                conexion.insmodelim("DELETE FROM `pedido_material` WHERE `id_pedido`= ?",pedido.getId_pedido());
                conexion.insmodelim("DELETE FROM `pedido` WHERE `id_pedido`= ?",pedido.getId_pedido());
                Exito("Pedido borrado exitosamente");
                ActualizarTabla(conexion.consultar("SELECT * FROM `pedido`"));
            }
        }catch (Exception e){
            Error(e.getMessage());
        }
    }
    @FXML private void CanecelPedido() throws SQLException {
        txtID.setText("");
        CleanTextFields();
        ActivateBtn(false,true,false,true, false);
        tabPaneVentana.getSelectionModel().select(tabSearch);
        tabSearch.setDisable(false);
        tabNew.setDisable(true);
    }
    @FXML private void ExitPedido(){
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.close();
    }

    @FXML private void Busqueda() throws SQLException {
        String busqueda = txtBusqueda.getText();
        String estado = DarEstado();
        String criterio = "";
        if (rbID.isSelected() || rbNumControl.isSelected() || rbProfesor.isSelected()){
            if (rbID.isSelected() && !busqueda.equals("")){
                criterio="id_pedido";
            } else if (rbNumControl.isSelected() && !busqueda.equals("")) {
                criterio="num_control";
            } else if (rbProfesor.isSelected() && !busqueda.equals("")) {
                criterio="profesor";
            }
            if (!busqueda.equals("") && !criterio.equals("")){
                ActualizarTabla(conexion.consultar("SELECT * FROM `pedido` WHERE `"+criterio+"` LIKE '%"+busqueda+"%' AND `estado` LIKE '%"+estado+"%'"));
            }else {
                ActualizarTabla(conexion.consultar("SELECT * FROM `pedido` WHERE `estado` LIKE '%"+estado+"%'"));
            }
        }else {// PAl rato
            tableViewPedidos.getItems().clear();
            if (!busqueda.equals("")){
                ResultSet rsHerramienta = conexion.consultar("SELECT * FROM `herramienta` INNER JOIN tipo_material ON herramienta.id_herramienta = tipo_material.id_material WHERE material LIKE'%"+busqueda+"%'");
                while (rsHerramienta.next()){
                    ResultSet rsIDPedido= conexion.consultar("SELECT `id_pedido` FROM `pedido_material` WHERE `cb_material`='"+rsHerramienta.getLong("cb_herramienta")+"'");
                    while (rsIDPedido.next()){
                        ResultSet rsPedido = conexion.consultar("SELECT * FROM `pedido` WHERE `id_pedido`='"+rsIDPedido.getInt("id_pedido")+"'");
                        if (rsPedido.next()){
                            Pedido pedido = new Pedido(rsPedido.getInt("id_pedido"), rsPedido.getString("nombre_persona"),rsPedido.getString("num_control"),
                                    rsPedido.getString("estado"),rsPedido.getDate("fecha"),rsPedido.getString("profesor"),
                                    rsPedido.getString("materia"));
                            tableViewPedidos.getItems().add(pedido);
                        }
                    }
                }

                ResultSet rsArticulo = conexion.consultar("SELECT * FROM `material` INNER JOIN tipo_material ON material.id_material = tipo_material.id_material WHERE material LIKE '%"+busqueda+"%'");
                while (rsArticulo.next()){
                    ResultSet rsIDPedido= conexion.consultar("SELECT `id_pedido` FROM `pedido_material` WHERE `cb_material`='"+rsArticulo.getLong("cb_material")+"'");
                    while (rsIDPedido.next()){
                        ResultSet rsPedido = conexion.consultar("SELECT * FROM `pedido` WHERE `id_pedido`='"+rsIDPedido.getInt("id_pedido")+"'");
                        if (rsPedido.next()){
                            Pedido pedido = new Pedido(rsPedido.getInt("id_pedido"), rsPedido.getString("nombre_persona"),rsPedido.getString("num_control"),
                                    rsPedido.getString("estado"),rsPedido.getDate("fecha"),rsPedido.getString("profesor"),
                                    rsPedido.getString("materia"));
                            tableViewPedidos.getItems().add(pedido);
                        }
                    }
                }
            }else {
                ActualizarTabla(conexion.consultar("SELECT * FROM `pedido`"));
            }

        }

    }

    private String DarEstado(){
        if (checkBoxEntregado.isSelected() && checkBoxPendiente.isSelected()){return "";}
        else if (checkBoxEntregado.isSelected() && !checkBoxPendiente.isSelected()){return "Entregado";}
        else if (!checkBoxEntregado.isSelected() && checkBoxPendiente.isSelected()){return "Pendiente";}
        return "";
    }


    private void ActualizarTabla(ResultSet rsPedido) throws SQLException {
        int cont=0;
        tableViewPedidos.getItems().clear();
        while (rsPedido.next()){
            Pedido pedido = new Pedido(rsPedido.getInt("id_pedido"), rsPedido.getString("nombre_persona"),rsPedido.getString("num_control"),
                    rsPedido.getString("estado"),rsPedido.getDate("fecha"),rsPedido.getString("profesor"),
                    rsPedido.getString("materia"));
            tableViewPedidos.getItems().add(pedido);
            cont++;
        }
        lblContador.setText("Se cargaron "+cont+" pedidos");

    }

    private boolean VerifyTxt(TextField... campos){
        for (TextField campo : campos){
            if (campo.getText().equals("")){
                return false;
            }
        }
        return true;
    }
    public void AgregarMaterial(Registro a) throws SQLException {
        if (productos != null){
            boolean cantidadPlus=false;
                for (int x=0; x<productos.size();x++){
                    if (productos.get(x).getCb().equals(a.getCb())){
                        cantidadPlus=true;
                        Registro r = productos.get(x);
                        if (VerificarCantidad(r.getCb(),r.getCantidad()+1)){
                            r.setCantidad((r.getCantidad()+1));
                            if (r.getCantidad() <=0){
                                productos.remove(x);
                            }else {
                                productos.set(x,r);

                            }
                        }
                    }
                }
                if (!cantidadPlus){
                    productos.add(a);
                }
        }
    }

    @FXML public void EscaneoBusqueda() throws SQLException {
        try {
            if (txtBusquedaID.getText().matches("\\d{10}")){
                ResultSet rsArticulo = conexion.consultar("SELECT * FROM `material` INNER JOIN tipo_material ON material.id_material = tipo_material.id_material WHERE cb_material= ?",txtBusquedaID.getText());
                if (rsArticulo.next()){
                    if (rsArticulo.getInt("cantidad") == 0){
                        txtBusquedaID.setText("");
                        throw new Exception("No hay ninguna cantidad de este articulo");
                    }
                    Registro registro = new Registro(rsArticulo.getLong("cb_material"),rsArticulo.getString("material"),rsArticulo.getString("tipo"),rsArticulo.getString("valor"), rsArticulo.getString("unidad_de_medida"),1,false);
                    AgregarMaterial(registro);
                    txtBusquedaID.setText("");

                }else {
                    ResultSet rsHerramienta = conexion.consultar("SELECT * FROM `herramienta` INNER JOIN tipo_material ON herramienta.id_herramienta = tipo_material.id_material WHERE cb_herramienta= ?",txtBusquedaID.getText());
                    if (rsHerramienta.next()){
                        if (rsHerramienta.getInt("cantidad") ==0){
                            txtBusquedaID.setText("");
                            throw new Exception("No hay ninguna cantidad de este articulo");
                        }
                        Registro registro = new Registro(rsHerramienta.getLong("cb_herramienta"),rsHerramienta.getString("material"),rsHerramienta.getString("tipo"), 1,false);
                        AgregarMaterial(registro);
                        txtBusquedaID.setText("");
                    }
                }
                txtBusquedaID.setText("");
            }

        } catch (Exception e) {
            Error(e.getMessage());
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
    @FXML private void NumControlSearch() throws SQLException {
        if (txtNumControl.getText().matches("\\d{2}[cC][gG]\\d{4}")){
            Map<DayOfWeek, String> daysOfWeek = new HashMap<>();
            daysOfWeek.put(DayOfWeek.MONDAY, "Lunes");
            daysOfWeek.put(DayOfWeek.TUESDAY, "Martes");
            daysOfWeek.put(DayOfWeek.WEDNESDAY, "Miércoles");
            daysOfWeek.put(DayOfWeek.THURSDAY, "Jueves");
            daysOfWeek.put(DayOfWeek.FRIDAY, "Viernes");
            daysOfWeek.put(DayOfWeek.SATURDAY, "Sábado");
            daysOfWeek.put(DayOfWeek.SUNDAY, "Domingo");

            String diaSemanaEspanol = daysOfWeek.get(LocalDateTime.parse(txtFecha.getText(), formato).toLocalDate().getDayOfWeek());
            LocalTime horaInicio = LocalDateTime.parse(txtFecha.getText(), formato).toLocalTime().withMinute(0).withSecond(0);
            LocalTime horaFin = LocalDateTime.parse(txtFecha.getText(), formato).toLocalTime().withHour(LocalDateTime.parse(txtFecha.getText(), formato).toLocalTime().getHour()+1).withMinute(0).withSecond(0);
            //System.out.println("entra a busqueda, num de control valido");
            ResultSet rsNumControl = conexion.consultar("SELECT * FROM `alumnos` WHERE `num_control`= ?",txtNumControl.getText());
            if (rsNumControl.next()){
                txtNombre.setText(rsNumControl.getString("nombre_alumno"));
                ResultSet rsMateria= conexion.consultar("SELECT * FROM `materia` WHERE `num_control`= ? AND `dia`= ? AND `hora_inicio`= ? AND `hora_fin`= ? LIMIT 1", txtNumControl.getText(), diaSemanaEspanol, String.valueOf(horaInicio), String.valueOf(horaFin) );
                if (rsMateria.next()){
                    txtProfesor.setText(rsMateria.getString("profesor"));
                    txtMateria.setText(rsMateria.getString("nom_materia"));
                    Platform.runLater(() -> {
                        txtBusquedaID.requestFocus();
                        txtBusquedaID.selectEnd();
                    });
                }
            }

        }
    }
    @FXML private void CheckBoxChange1(){
        if (checkBoxNA1.isSelected()){
            txtNumControl.setText("N/A");
            txtNumControl.setDisable(true);
        }else{
            txtNumControl.setText("");
            txtNumControl.setDisable(false);
        }
    }
    @FXML private void CheckBoxChange2(){
        if (checkBoxNA2.isSelected()) {
            txtProfesor.setText("N/A");
            txtProfesor.setDisable(true);
        }else {
            txtProfesor.setText("");
            txtProfesor.setDisable(false);
        }
    }
    @FXML private void CheckBoxChange3(){
        if (checkBoxNA3.isSelected()) {
            txtMateria.setText("N/A");
            txtMateria.setDisable(true);
        }else {
            txtMateria.setText("");
            txtMateria.setDisable(false);
        }
    }
    @FXML private void BuscarProducto() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ConsultarMaterial.fxml"));
        Parent root = fxmlLoader.load();
        ventanaSecundaria.setScene(new Scene(root));
        ventanaSecundaria.showAndWait();
    }

    private void ActivateBtn(boolean New, boolean save, boolean edit, boolean cancel, boolean delete) throws SQLException {
        if (LoginController.resultado.getInt("crud_pedido")==0){
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
        btnExit.setDisable(false);
    }

    private void CleanTextFields(){
        txtNumControl.setDisable(false);
        txtProfesor.setDisable(false);
        txtMateria.setDisable(false);
        checkBoxNA1.setSelected(false);
        checkBoxNA2.setSelected(false);
        checkBoxNA3.setSelected(false);
        txtNombre.setText("");
        txtNumControl.setText("");
        txtFecha.setText("");
        txtMateria.setText("");
        txtProfesor.setText("");
        txtBusquedaID.setText("");
    }

    public boolean VerificarCantidad(Long cb, int cantidadActual) throws SQLException {
        ResultSet rsCantidadMinA = conexion.consultar("SELECT `cantidad` FROM `material` WHERE cb_material= ? LIMIT 1", cb);
        ResultSet rsCantidadMinH = conexion.consultar("SELECT `cantidad` FROM `herramienta` WHERE cb_herramienta= ? LIMIT 1", cb);
        if (rsCantidadMinA.next()){
            if (cantidadActual > rsCantidadMinA.getInt("cantidad")){
                Error("Ya se ha alcanzado el limite de registros");
                return false;

            }
        }
        if (rsCantidadMinH.next()){
            if (cantidadActual > rsCantidadMinH.getInt("cantidad")){
                Error("Ya se ha alcanzado el limite de registros");
                return false;

            }
        }
        return true;
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


