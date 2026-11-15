
package co.edu.uniquindio.edu.poo.nequi.viewcontrollers;

import co.edu.uniquindio.edu.poo.nequi.app.Aplicacion;
import co.edu.uniquindio.edu.poo.nequi.controller.GestionAvanzadaController;
import co.edu.uniquindio.edu.poo.nequi.model.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GestionAvanzadaViewController {

    // COMPONENTES FXML
    @FXML private Label lblCuenta;
    @FXML private Label lblMensaje;
    @FXML private Label lblRecordatorios;

    @FXML private ComboBox<TipoOperacionProgramada> cbTipoOperacion;
    @FXML private ComboBox<FrecuenciaProgramacion> cbFrecuencia;

    @FXML private TextField txtMonto;
    @FXML private TextField txtDestino;
    @FXML private TextField txtHora;
    @FXML private DatePicker dpFecha;

    @FXML private ListView<String> lstProgramaciones;
    @FXML private ListView<String> lstMovimientos;

    @FXML private TextArea txtDetalleProgramacion;
    @FXML private TextArea txtDetalleMovimiento;

    // ATRIBUTOS
    private Aplicacion app;
    private GestionAvanzadaController controller;

    private String identificacion;
    private String codigoMonedero;

    private List<TransaccionProgramada> programacionesVisibles =
            new ArrayList<>();

    private List<Transaccion> movimientosVisibles =
            new ArrayList<>();

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ====================================
    // RECIBIR LA APLICACION
    // ====================================

    public void setApp(
            Aplicacion app,
            String identificacion,
            String codigoMonedero) {

        this.app = app;
        this.identificacion = identificacion;
        this.codigoMonedero = codigoMonedero;

        this.controller =
                new GestionAvanzadaController(app.getNequi());

        controller.validarMonedero(
                identificacion, codigoMonedero);

        lblCuenta.setText(
                "Cliente: " + identificacion
                        + "   |   Monedero: " + codigoMonedero
        );

        cbTipoOperacion.getItems().setAll(
                TipoOperacionProgramada.values()
        );

        cbFrecuencia.getItems().setAll(
                FrecuenciaProgramacion.values()
        );

        cbTipoOperacion.setValue(
                TipoOperacionProgramada.DEPOSITO
        );

        cbFrecuencia.setValue(
                FrecuenciaProgramacion.UNICA
        );

        dpFecha.setValue(LocalDate.now().plusDays(1));
        txtHora.setText("09:00");

        cambiarTipoEvent();
        actualizarListas();
    }

    // ====================================
    // CAMBIAR TIPO DE OPERACION
    // ====================================

    @FXML
    private void cambiarTipoEvent() {

        boolean transferencia =
                cbTipoOperacion.getValue()
                        == TipoOperacionProgramada.TRANSFERENCIA;

        txtDestino.setDisable(!transferencia);

        if (!transferencia) {
            txtDestino.clear();
        }
    }

    // ====================================
    // LEER MONTO
    // ====================================

    private BigDecimal leerMonto() {

        try {

            return new BigDecimal(
                    txtMonto.getText().trim()
                            .replace(",", ".")
            );

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                    "Ingrese un monto valido, por ejemplo 500.00"
            );
        }
    }

    // ====================================
    // LEER FECHA Y HORA
    // ====================================

    private LocalDateTime leerFecha() {

        if (dpFecha.getValue() == null) {

            throw new IllegalArgumentException(
                    "Seleccione una fecha");
        }

        try {

            LocalTime hora = LocalTime.parse(
                    txtHora.getText().trim(),
                    DateTimeFormatter.ofPattern("HH:mm")
            );

            return LocalDateTime.of(
                    dpFecha.getValue(), hora
            );

        } catch (RuntimeException e) {

            throw new IllegalArgumentException(
                    "La hora debe tener formato HH:mm, por ejemplo 14:30"
            );
        }
    }

    // ====================================
    // PROGRAMAR OPERACION
    // ====================================

    @FXML
    private void programarEvent() {

        try {

            BigDecimal monto = leerMonto();
            LocalDateTime fecha = leerFecha();

            FrecuenciaProgramacion frecuencia =
                    cbFrecuencia.getValue();

            TipoOperacionProgramada tipo =
                    cbTipoOperacion.getValue();

            if (tipo == null || frecuencia == null) {

                throw new IllegalArgumentException(
                        "Seleccione tipo y frecuencia");
            }

            TransaccionProgramada programacion;

            if (tipo == TipoOperacionProgramada.DEPOSITO) {

                programacion = controller.programarDeposito(
                        identificacion,
                        codigoMonedero,
                        monto,
                        fecha,
                        frecuencia
                );

            } else {

                String destino =
                        txtDestino.getText().trim();

                if (destino.isEmpty()) {

                    throw new IllegalArgumentException(
                            "Ingrese el monedero destino");
                }

                programacion =
                        controller.programarTransferencia(
                                identificacion,
                                codigoMonedero,
                                destino,
                                monto,
                                fecha,
                                frecuencia
                        );
            }

            txtMonto.clear();

            actualizarListas();

            mostrarMensaje(
                    "Programacion creada: "
                            + programacion.getCodigo(),
                    false
            );

        } catch (RuntimeException e) {

            mostrarMensaje(e.getMessage(), true);
        }
    }

    // ====================================
    // ACTUALIZAR LAS LISTAS
    // ====================================

    private void actualizarListas() {

        programacionesVisibles =
                controller.consultarProgramaciones(
                        identificacion);

        lstProgramaciones.getItems().clear();

        for (TransaccionProgramada programacion
                : programacionesVisibles) {

            String detalle =
                    programacion.getTipoOperacion()
                            + " | " + programacion.getMonto()
                            + " | "
                            + programacion.getProximaEjecucion()
                            .format(FORMATO)
                            + " | " + programacion.getEstado()
                            + " | "
                            + abreviar(programacion.getCodigo());

            lstProgramaciones.getItems().add(detalle);
        }

        movimientosVisibles =
                controller.consultarMovimientos(
                        identificacion,
                        codigoMonedero);

        lstMovimientos.getItems().clear();

        for (Transaccion transaccion
                : movimientosVisibles) {

            String detalle =
                    transaccion.getClass().getSimpleName()
                            + " | " + transaccion.getMonto()
                            + " | "
                            + transaccion.getFecha().format(FORMATO)
                            + " | " + transaccion.getEstado()
                            + " | "
                            + abreviar(transaccion.getCodigo());

            lstMovimientos.getItems().add(detalle);
        }

        actualizarRecordatorios();
    }

    private String abreviar(String codigo) {

        return codigo.substring(
                0, Math.min(8, codigo.length())
        );
    }

    // ====================================
    // MOSTRAR RECORDATORIOS
    // ====================================

    private void actualizarRecordatorios() {

        LocalDateTime ahora = LocalDateTime.now();

        int proximas = 0;

        for (TransaccionProgramada programacion
                : programacionesVisibles) {

            if (programacion.getEstado()
                    == EstadoProgramacion.ACTIVA

                    && programacion.getProximaEjecucion()
                    .isAfter(ahora)

                    && !programacion.getProximaEjecucion()
                    .isAfter(ahora.plusMinutes(30))) {

                proximas++;
            }
        }

        lblRecordatorios.setText(
                "Recordatorios: " + proximas
                        + " operaciones previstas en los proximos 30 minutos"
        );
    }

    // ====================================
    // ACTUALIZAR
    // ====================================

    @FXML
    private void actualizarEvent() {

        actualizarListas();

        mostrarMensaje(
                "Datos actualizados", false);
    }

    // ====================================
    // DETALLE DE PROGRAMACION
    // ====================================

    @FXML
    private void verDetalleProgramacionEvent() {

        int indice =
                lstProgramaciones.getSelectionModel()
                        .getSelectedIndex();

        if (indice < 0) {

            mostrarMensaje(
                    "Seleccione una programacion", true);

            return;
        }

        TransaccionProgramada p =
                programacionesVisibles.get(indice);

        txtDetalleProgramacion.setText(
                "Codigo: " + p.getCodigo()
                        + "\nTipo: " + p.getTipoOperacion()
                        + "\nOrigen: " + p.getCodigoOrigen()
                        + "\nDestino: " + p.getCodigoDestino()
                        + "\nMonto: " + p.getMonto()
                        + "\nFrecuencia: " + p.getFrecuencia()
                        + "\nProxima ejecucion: "
                        + p.getProximaEjecucion().format(FORMATO)
                        + "\nEstado: " + p.getEstado()
                        + "\nEjecuciones: " + p.getTotalEjecuciones()
                        + "\nUltimo error: " + p.getUltimoError()
        );
    }

    // ====================================
    // CANCELAR PROGRAMACION
    // ====================================

    @FXML
    private void cancelarEvent() {

        try {

            int indice =
                    lstProgramaciones.getSelectionModel()
                            .getSelectedIndex();

            if (indice < 0) {

                throw new IllegalArgumentException(
                        "Seleccione una programacion");
            }

            TransaccionProgramada programacion =
                    programacionesVisibles.get(indice);

            Alert confirmacion =
                    new Alert(Alert.AlertType.CONFIRMATION);

            confirmacion.setHeaderText(
                    "Cancelar programacion");

            confirmacion.setContentText(
                    "¿Cancelar la programacion "
                            + abreviar(programacion.getCodigo())
                            + "?");

            if (confirmacion.showAndWait()
                    .orElse(ButtonType.CANCEL) != ButtonType.OK) {

                return;
            }

            controller.cancelarProgramacion(
                    identificacion,
                    programacion.getCodigo()
            );

            actualizarListas();

            mostrarMensaje(
                    "Programacion cancelada", false);

        } catch (RuntimeException e) {

            mostrarMensaje(e.getMessage(), true);
        }
    }

    // ====================================
    // PROCESAR PROGRAMACIONES VENCIDAS
    // ====================================

    @FXML
    private void procesarAhoraEvent() {

        String resultados =
                app.procesarProgramacionesAhora();

        actualizarListas();

        txtDetalleProgramacion.setText(resultados);

        mostrarMensaje(
                "Comprobacion terminada", false);
    }

    // ====================================
    // DETALLE DE TRANSACCION
    // ====================================

    @FXML
    private void verDetalleMovimientoEvent() {

        int indice =
                lstMovimientos.getSelectionModel()
                        .getSelectedIndex();

        if (indice < 0) {

            mostrarMensaje(
                    "Seleccione un movimiento", true);

            return;
        }

        Transaccion transaccion =
                movimientosVisibles.get(indice);

        txtDetalleMovimiento.setText(
                "Codigo: " + transaccion.getCodigo()
                        + "\nDescripcion: "
                        + transaccion.getDescripcion()
                        + "\nFecha: "
                        + transaccion.getFecha().format(FORMATO)
                        + "\nMonto: " + transaccion.getMonto()
                        + "\nComision: "
                        + transaccion.getComisionCobrada()
                        + "\nEstado: " + transaccion.getEstado()
                        + "\nAtipica: "
                        + (transaccion.isSospechosa() ? "Si" : "No")
        );
    }

    // ====================================
    // REVERTIR TRANSACCION
    // ====================================

    @FXML
    private void revertirEvent() {

        try {

            int indice =
                    lstMovimientos.getSelectionModel()
                            .getSelectedIndex();

            if (indice < 0) {

                throw new IllegalArgumentException(
                        "Seleccione un movimiento");
            }

            Transaccion transaccion =
                    movimientosVisibles.get(indice);

            if (transaccion.getEstado()
                    != EstadoTransaccion.COMPLETADA) {

                throw new IllegalArgumentException(
                        "Esta transaccion ya fue revertida");
            }

            Alert confirmacion =
                    new Alert(Alert.AlertType.CONFIRMATION);

            confirmacion.setHeaderText(
                    "Revertir movimiento");

            confirmacion.setContentText(
                    "¿Revertir "
                            + transaccion.getDescripcion()
                            + " por "
                            + transaccion.getMonto()
                            + "? Esta accion cambia saldos y puntos."
            );

            if (confirmacion.showAndWait()
                    .orElse(ButtonType.CANCEL) != ButtonType.OK) {

                return;
            }

            controller.revertirTransaccion(
                    identificacion,
                    codigoMonedero,
                    transaccion.getCodigo()
            );

            actualizarListas();

            txtDetalleMovimiento.setText(
                    "Movimiento revertido: "
                            + transaccion.getCodigo()
            );

            mostrarMensaje(
                    "Reversion realizada correctamente",
                    false
            );

        } catch (RuntimeException e) {

            mostrarMensaje(e.getMessage(), true);
        }
    }

    // ====================================
    // VOLVER AL MONEDERO
    // ====================================

    @FXML
    private void volverEvent() {

        app.openViewMonedero(
                identificacion,
                codigoMonedero
        );
    }

    // ====================================
    // MOSTRAR MENSAJES
    // ====================================

    private void mostrarMensaje(
            String mensaje,
            boolean error) {

        lblMensaje.setText(mensaje);

        if (error) {

            lblMensaje.setStyle(
                    "-fx-text-fill: #bb2222; -fx-font-size: 11;");

        } else {

            lblMensaje.setStyle(
                    "-fx-text-fill: #267447; -fx-font-size: 11;");
        }
    }
}
