
package co.edu.uniquindio.edu.poo.nequi.viewcontrollers;

import co.edu.uniquindio.edu.poo.nequi.app.Aplicacion;
import co.edu.uniquindio.edu.poo.nequi.controller.ClientesController;

import co.edu.uniquindio.edu.poo.nequi.model.Cliente;
import co.edu.uniquindio.edu.poo.nequi.model.Monedero;
import co.edu.uniquindio.edu.poo.nequi.model.TipoMonedero;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ClientesViewController {

    // CAMPOS PARA REGISTRAR CLIENTES
    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtApellidos;

    @FXML
    private TextField txtIdentificacion;

    @FXML
    private TextField txtCorreo;

    @FXML
    private TextField txtTelefono;

    // CAMPOS PARA CREAR MONEDEROS
    @FXML
    private TextField txtCodigoMonedero;

    @FXML
    private ComboBox<String> cbClientes;

    @FXML
    private ComboBox<TipoMonedero> cbTipoMonedero;

    @FXML
    private ComboBox<String> cbMonederos;

    @FXML
    private Label lblMensaje;

    private Aplicacion app;
    private ClientesController clientesController;

    // RECIBIR LA APLICACION
    public void setApp(Aplicacion app) {

        this.app = app;

        this.clientesController =
                new ClientesController(app.getNequi());

        cbTipoMonedero.getItems().setAll(
                TipoMonedero.values()
        );

        cbTipoMonedero.getSelectionModel().selectFirst();

        cargarClientes();
    }

    // CARGAR CLIENTES EN EL COMBOBOX
    private void cargarClientes() {

        cbClientes.getItems().clear();

        for (Cliente cliente :
                clientesController.obtenerClientes()) {

            cbClientes.getItems().add(
                    cliente.getIdentificacion()
            );
        }

        if (!cbClientes.getItems().isEmpty()) {

            cbClientes.getSelectionModel().selectFirst();

        } else {

            cbClientes.setValue(null);
        }

        cargarMonederos();
    }

    // CARGAR MONEDEROS DEL CLIENTE
    private void cargarMonederos() {

        cbMonederos.getItems().clear();

        String identificacion =
                cbClientes.getValue();

        if (identificacion == null) {
            return;
        }

        for (Monedero monedero :
                clientesController.obtenerMonederos(
                        identificacion)) {

            cbMonederos.getItems().add(
                    monedero.getCodigo()
            );
        }

        if (!cbMonederos.getItems().isEmpty()) {

            cbMonederos.getSelectionModel().selectFirst();
        }
    }

    // EVENTO PARA REGISTRAR UN CLIENTE
    @FXML
    private void registrarClienteEvent() {

        try {

            String identificacion =
                    txtIdentificacion.getText().trim();

            clientesController.registrarCliente(
                    txtNombre.getText(),
                    txtApellidos.getText(),
                    identificacion,
                    txtCorreo.getText(),
                    txtTelefono.getText()
            );

            cargarClientes();

            cbClientes.setValue(identificacion);
            cargarMonederos();

            limpiarCamposCliente();

            mostrarMensaje(
                    "Cliente registrado correctamente",
                    false
            );

        } catch (RuntimeException e) {

            mostrarMensaje(e.getMessage(), true);
        }
    }

    // EVENTO PARA CREAR UN MONEDERO
    @FXML
    private void crearMonederoEvent() {

        try {

            String identificacion =
                    cbClientes.getValue();

            TipoMonedero tipo =
                    cbTipoMonedero.getValue();

            if (identificacion == null || tipo == null) {

                throw new IllegalArgumentException(
                        "Seleccione cliente y tipo de monedero");
            }

            String codigo =
                    txtCodigoMonedero.getText().trim();

            clientesController.crearMonedero(
                    identificacion,
                    codigo,
                    tipo
            );

            cargarMonederos();

            cbMonederos.setValue(codigo);

            txtCodigoMonedero.clear();

            mostrarMensaje(
                    "Monedero creado correctamente",
                    false
            );

        } catch (RuntimeException e) {

            mostrarMensaje(e.getMessage(), true);
        }
    }

    // EVENTO AL CAMBIAR CLIENTE
    @FXML
    private void seleccionarClienteEvent() {

        if (clientesController != null) {
            cargarMonederos();
        }
    }

    // EVENTO PARA ABRIR EL MONEDERO
    @FXML
    private void abrirMonederoEvent() {

        String identificacion =
                cbClientes.getValue();

        String codigoMonedero =
                cbMonederos.getValue();

        if (identificacion == null ||
                codigoMonedero == null) {

            mostrarMensaje(
                    "Seleccione un cliente y un monedero",
                    true
            );

            return;
        }

        app.openViewMonedero(
                identificacion,
                codigoMonedero
        );
    }

    // LIMPIAR FORMULARIO
    private void limpiarCamposCliente() {

        txtNombre.clear();
        txtApellidos.clear();
        txtIdentificacion.clear();
        txtCorreo.clear();
        txtTelefono.clear();
    }

    // MOSTRAR MENSAJE EN LA VENTANA
    private void mostrarMensaje(
            String mensaje,
            boolean error) {

        lblMensaje.setText(mensaje);

        if (error) {

            lblMensaje.setStyle(
                    "-fx-text-fill: #c62828;");
        } else {

            lblMensaje.setStyle(
                    "-fx-text-fill: #287a45;");
        }
    }
}
