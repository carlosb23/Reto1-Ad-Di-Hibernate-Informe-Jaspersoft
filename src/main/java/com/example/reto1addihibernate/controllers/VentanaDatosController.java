package com.example.reto1addihibernate.controllers;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


import com.example.reto1addihibernate.App;
import com.example.reto1addihibernate.SessionData;
import com.example.reto1addihibernate.domain.Items.Item;
import com.example.reto1addihibernate.domain.Items.ItemDAO;
import com.example.reto1addihibernate.domain.pedido.Pedido;
import com.example.reto1addihibernate.domain.pedido.PedidoDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class VentanaDatosController implements Initializable {
    @FXML
    private TableView tabledato;
    @FXML
    private TableColumn<Item, String> columId;
    @FXML
    private TableColumn<Item, String> columcodigo;
    @FXML
    private TableColumn<Item, String> columcantidad;
    @FXML
    private TableColumn<Item, String> columproduct;
    @FXML
    private Button volver;
    @FXML
    private Button btnItem;

    @FXML
    private Button btnborrarItem;

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final ItemDAO itemDao = new ItemDAO();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        columId.setCellValueFactory((fila)->{
           String id = String.valueOf(fila.getValue().getId());
           return new SimpleStringProperty(id);
        });
        columcodigo.setCellValueFactory((fila)->{
            String codigo = String.valueOf(fila.getValue().getCodigo().getCodigo());
            return new SimpleStringProperty(codigo);
        });
        columcantidad.setCellValueFactory((fila)->{
            String cantidad = String.valueOf(fila.getValue().getCantidad());
            return new SimpleStringProperty(cantidad);
        });
        columproduct.setCellValueFactory((fila)->{
            String product = String.valueOf(fila.getValue().getProducto());
            return new SimpleStringProperty(product);
        });


        SessionData.setCurrentPedido((new PedidoDAO().get(SessionData.getCurrentPedido().getId())));
        tabledato.getItems().addAll(SessionData.getCurrentPedido().getItems());




    }




    @FXML
    public void btnvolverVP(ActionEvent actionEvent) {
        App.ventanaDatos("Views/ventanaPrincipal.fxml");
    }

    @FXML
    public void añadirItem(ActionEvent actionEvent) {
        App.ventanaDatos("Views/ventana_edit_pedido.fxml");
    }

    private double calcularTotal() {
        double total = 0.0;
        for (Item item : SessionData.getCurrentPedido().getItems()) {
            total += item.getCantidad() * item.getProducto().getPrecio();
        }
        return total;
    }
    @FXML
    public void borrarItem(ActionEvent actionEvent) {
        // Obtener el item seleccionado en la tabla (asumiendo que estás mostrando items en una tabla)
        Item itemSeleccionado = (Item) tabledato.getSelectionModel().getSelectedItem();

        if (itemSeleccionado != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setContentText("Desea borrar el item seleccionado?");
            var result = alert.showAndWait().orElse(ButtonType.CANCEL);

            if (result.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                tabledato.getItems().remove(itemSeleccionado);
                itemDao.delete(itemSeleccionado);

                // Actualizar el pedido actual para reflejar el cambio en la base de datos
                Pedido pedidoActual = SessionData.getCurrentPedido();
                pedidoActual.getItems().remove(itemSeleccionado);

                double total = calcularTotal();
                SessionData.getCurrentPedido().setTotal(total);
                pedidoDAO.update(SessionData.getCurrentPedido());
            }
        } else {
            // Manejar el caso en que no hay un item seleccionado
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Por favor, seleccione un item para borrar.");
            alert.showAndWait();
        }

    }

}