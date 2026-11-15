
package co.edu.uniquindio.edu.poo.nequi.viewcontrollers;

import co.edu.uniquindio.edu.poo.nequi.app.Aplicacion;
import co.edu.uniquindio.edu.poo.nequi.controller.MonederoController;
import co.edu.uniquindio.edu.poo.nequi.model.*;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MonederoViewController {

    // COMPONENTES FXML
    @FXML private Label lblCliente;
    @FXML private Label lblCodigo;
    @FXML private Label lblTipo;
    @FXML private Label lblSaldo;
    @FXML private Label lblPuntos;
    @FXML private Label lblRango;
    @FXML private Label lblMensaje;

    @FXML private TextField txtDeposito;
    @FXML private TextField txtRetiro;
    @FXML private TextField txtDestino;
    @FXML private TextField txtTransferencia;

    @FXML private ComboBox<Beneficio> cbBeneficio;
    @FXML private TextArea txtResultados;

    // ATRIBUTOS
    private Aplicacion app;
    private MonederoController monederoController;
    private String identificacion;
    private String codigoMonedero;

    // RECIBIR LA APLICACION
    public void setApp(
            Aplicacion app,
            String identificacion,
            String codigoMonedero) {

        this.app = app;
        this.identificacion = identificacion;
        this.codigoMonedero = codigoMonedero;
        this.monederoController = new MonederoController(app.getNequi());

        cbBeneficio.getItems().setAll(
                Beneficio.DESCUENTO_TRANSFERENCIA,
                Beneficio.RETIROS_SIN_CARGO,
                Beneficio.BONO_SALDO
        );
        cbBeneficio.getSelectionModel().selectFirst();
        cargarDatos();
    }

    // MOSTRAR LOS DATOS DEL MONEDERO
    private void cargarDatos() {

        monederoController.validarMonederoCliente(
                identificacion, codigoMonedero);

        Cliente cliente = monederoController.buscarCliente(
                identificacion);
        Monedero monedero = monederoController.buscarMonedero(
                codigoMonedero);

        lblCliente.setText(cliente.getNombre() + " " + cliente.getApellidos());
        lblCodigo.setText(monedero.getCodigo());
        lblTipo.setText(monedero.getTipo().toString());
        lblSaldo.setText("$ " + monederoController
                .consultarSaldo(codigoMonedero).toPlainString());
        lblPuntos.setText(String.valueOf(
                monederoController.consultarPuntos(identificacion)));
        lblRango.setText(monederoController
                .consultarRango(identificacion).toString());
    }

    // LEER MONTO
    private BigDecimal leerMonto(TextField campo) {

        String texto = campo.getText().trim();

        if (texto.isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar un monto");
        }

        try {
            return new BigDecimal(texto.replace(",", "."));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Monto invalido. Ejemplo: 500.00");
        }
    }

    // DEPOSITAR
    @FXML
    private void depositarEvent() {
        try {
            BigDecimal monto = leerMonto(txtDeposito);
            monederoController.depositar(
                    identificacion, codigoMonedero, monto);
            txtDeposito.clear();
            cargarDatos();
            mostrarMensaje("Deposito realizado correctamente", false);
        } catch (RuntimeException e) {
            mostrarMensaje(e.getMessage(), true);
        }
    }

    // RETIRAR
    @FXML
    private void retirarEvent() {
        try {
            BigDecimal monto = leerMonto(txtRetiro);
            monederoController.retirar(
                    identificacion, codigoMonedero, monto);
            txtRetiro.clear();
            cargarDatos();
            mostrarMensaje("Retiro realizado correctamente", false);
        } catch (RuntimeException e) {
            mostrarMensaje(e.getMessage(), true);
        }
    }

    // TRANSFERIR
    @FXML
    private void transferirEvent() {
        try {
            String destino = txtDestino.getText().trim();

            if (destino.isEmpty()) {
                throw new IllegalArgumentException(
                        "Ingrese el monedero destino");
            }

            BigDecimal monto = leerMonto(txtTransferencia);
            monederoController.transferir(
                    identificacion, codigoMonedero, destino, monto);

            txtDestino.clear();
            txtTransferencia.clear();
            cargarDatos();
            mostrarMensaje("Transferencia realizada correctamente", false);
        } catch (RuntimeException e) {
            mostrarMensaje(e.getMessage(), true);
        }
    }

    // CANJEAR BENEFICIO
    @FXML
    private void canjearBeneficioEvent() {
        try {
            Beneficio beneficio = cbBeneficio.getValue();

            if (beneficio == null) {
                throw new IllegalArgumentException(
                        "Seleccione un beneficio");
            }

            monederoController.canjearBeneficio(
                    identificacion, codigoMonedero, beneficio);
            cargarDatos();
            mostrarMensaje(
                    "Beneficio canjeado: " + beneficio.getNombre(), false);
        } catch (RuntimeException e) {
            mostrarMensaje(e.getMessage(), true);
        }
    }

    // CONSULTAR HISTORIAL
    @FXML
    private void verHistorialEvent() {

        List<Transaccion> historial = monederoController
                .consultarHistorial(codigoMonedero);
        StringBuilder texto = new StringBuilder();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern(
                "dd/MM/yyyy HH:mm");

        texto.append("HISTORIAL DE TRANSACCIONES\n");
        texto.append("Monedero: ").append(codigoMonedero)
                .append("\n\n");

        if (historial.isEmpty()) {
            texto.append("No existen transacciones registradas.");
        } else {
            for (Transaccion transaccion : historial) {
                // USO REAL DE LA INTERFAZ DESCRIBIBLE
                Describible movimiento = transaccion;

                texto.append("Fecha: ")
                        .append(transaccion.getFecha().format(formato))
                        .append("\n");
                texto.append("Operacion: ")
                        .append(movimiento.getDescripcion()).append("\n");
                texto.append("Monto: $ ")
                        .append(transaccion.getMonto()).append("\n");
                texto.append("Comision: $ ")
                        .append(transaccion.getComisionCobrada()).append("\n");
                texto.append("Estado: ")
                        .append(transaccion.getEstado()).append("\n");

                if (transaccion.isSospechosa()) {
                    texto.append("ALERTA: Movimiento atipico\n");
                }
                texto.append("-----------------------------\n");
            }
        }

        txtResultados.setText(texto.toString());
        mostrarMensaje("Historial consultado correctamente", false);
    }

    // CONSULTAR REPORTE DE GASTOS
    @FXML
    private void verReporteEvent() {

        ReporteGastos reporte = monederoController
                .generarReporteGastos(identificacion);
        StringBuilder texto = new StringBuilder();

        texto.append("REPORTE DE GASTOS DEL CLIENTE\n");
        texto.append("Identificacion: ").append(identificacion)
                .append("\n\n");
        texto.append("Total de retiros: $ ")
                .append(reporte.getTotalRetiros()).append("\n");
        texto.append("Total de transferencias: $ ")
                .append(reporte.getTotalTransferencias()).append("\n");
        texto.append("Gastos totales: $ ")
                .append(reporte.getTotalGastos()).append("\n");
        texto.append("Cantidad de operaciones: ")
                .append(reporte.getCantidadOperaciones()).append("\n");
        texto.append("Promedio: ")
                .append(reporte.getPromedio()).append("\n");
        texto.append("Desviacion estandar: ")
                .append(reporte.getDesviacionEstandar()).append("\n");
        texto.append("Movimientos atipicos de gasto: ")
                .append(reporte.getCantidadAtipicas());

        txtResultados.setText(texto.toString());
        mostrarMensaje("Reporte generado correctamente", false);
    }

    // CONSULTAR NOTIFICACIONES Y RECORDATORIOS
    @FXML
    private void verNotificacionesEvent() {

        app.procesarProgramacionesAhora();
        cargarDatos();

        List<Notificacion> notificaciones = new ArrayList<>(
                monederoController.consultarNotificaciones(identificacion));
        notificaciones.addAll(app.consultarRecordatorios(identificacion));
        notificaciones.sort(
                Comparator.comparing(Notificacion::getFecha).reversed());

        StringBuilder texto = new StringBuilder();
        texto.append("NOTIFICACIONES DEL CLIENTE\n\n");

        if (notificaciones.isEmpty()) {
            texto.append("No existen notificaciones registradas.");
        } else {
            for (Notificacion notificacion : notificaciones) {
                texto.append("Tipo: ").append(notificacion.getTipo())
                        .append("\n");
                texto.append("Mensaje: ").append(notificacion.getMensaje())
                        .append("\n");
                texto.append("Fecha: ").append(notificacion.getFecha())
                        .append("\n");
                texto.append("-----------------------------\n");
            }
        }

        txtResultados.setText(texto.toString());
        mostrarMensaje("Notificaciones consultadas", false);
    }

    // ACTUALIZAR INFORMACION
    @FXML
    private void actualizarEvent() {
        cargarDatos();
        mostrarMensaje("Informacion actualizada", false);
    }

    // ABRIR GESTION AVANZADA
    @FXML
    private void gestionAvanzadaEvent() {
        app.openViewGestionAvanzada(
                identificacion, codigoMonedero);
    }

    // VOLVER A CLIENTES
    @FXML
    private void volverEvent() {
        app.openViewClientes();
    }

    // MOSTRAR MENSAJE
    private void mostrarMensaje(String mensaje, boolean error) {
        lblMensaje.setText(mensaje);
        if (error) {
            lblMensaje.setStyle("-fx-text-fill: #c62828;");
        } else {
            lblMensaje.setStyle("-fx-text-fill: #287a45;");
        }
    }
}
