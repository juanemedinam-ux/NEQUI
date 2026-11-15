
package co.edu.uniquindio.edu.poo.nequi.app;

import co.edu.uniquindio.edu.poo.nequi.model.*;
import co.edu.uniquindio.edu.poo.nequi.viewcontrollers.ClientesViewController;
import co.edu.uniquindio.edu.poo.nequi.viewcontrollers.MonederoViewController;
import co.edu.uniquindio.edu.poo.nequi.viewcontrollers.GestionAvanzadaViewController;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Aplicacion extends Application {

    private Stage primaryStage;

    // MODELO CENTRAL DE NEQUI, EN MEMORIA
    public static MonederoVirtual nequi =
            new MonederoVirtual("NEQUI");

    private Timeline relojProgramaciones;
    private Set<String> recordatoriosMostrados = new HashSet<>();
    private List<Notificacion> listaRecordatorios = new ArrayList<>();

    // INICIAR LA APLICACION
    @Override
    public void start(Stage primaryStage) {

        this.primaryStage = primaryStage;
        primaryStage.setTitle("NEQUI - Monedero Virtual");
        primaryStage.setResizable(false);

        nequi = new MonederoVirtual("NEQUI");
        nequi.configurarComisiones(new BigDecimal("2.00"), new BigDecimal("1.00"));
        inicializarData();
        openViewClientes();
        iniciarReloj();
    }

    // ABRIR VENTANA DE CLIENTES
    public void openViewClientes() {

        try {
            FXMLLoader loader = new FXMLLoader(
                    Aplicacion.class.getResource(
                            "/co/edu/uniquindio/edu/poo/nequi/clientes.fxml"));

            AnchorPane root = loader.load();
            ClientesViewController controller = loader.getController();
            controller.setApp(this);
            mostrarVentana(root);

        } catch (IOException e) {
            throw new RuntimeException(
                    "No se pudo abrir clientes.fxml", e);
        }
    }

    // ABRIR VENTANA DEL MONEDERO
    public void openViewMonedero(
            String identificacion,
            String codigoMonedero) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    Aplicacion.class.getResource(
                            "/co/edu/uniquindio/edu/poo/nequi/monederoview.fxml"));

            AnchorPane root = loader.load();
            MonederoViewController controller = loader.getController();
            controller.setApp(this, identificacion, codigoMonedero);
            mostrarVentana(root);

        } catch (IOException e) {
            throw new RuntimeException(
                    "No se pudo abrir monederoview.fxml", e);
        }
    }

    // ABRIR GESTION AVANZADA
    public void openViewGestionAvanzada(
            String identificacion,
            String codigoMonedero) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    Aplicacion.class.getResource(
                            "/co/edu/uniquindio/edu/poo/nequi/gestionavanzada.fxml"));

            AnchorPane root = loader.load();
            GestionAvanzadaViewController controller = loader.getController();
            controller.setApp(this, identificacion, codigoMonedero);
            mostrarVentana(root);

        } catch (IOException e) {
            throw new RuntimeException(
                    "No se pudo abrir gestionavanzada.fxml", e);
        }
    }

    // MOSTRAR VENTANA SIN CAMBIAR SU TAMANO FXML
    private void mostrarVentana(AnchorPane root) {

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    // REVISAR PROGRAMACIONES CADA 30 SEGUNDOS
    private void iniciarReloj() {

        relojProgramaciones = new Timeline(
                new KeyFrame(
                        Duration.seconds(30),
                        evento -> procesarProgramacionesAhora()
                )
        );

        relojProgramaciones.setCycleCount(Timeline.INDEFINITE);
        relojProgramaciones.play();
        procesarProgramacionesAhora();
    }

    // REVISAR RECORDATORIOS Y EJECUTAR TRANSACCIONES VENCIDAS
    public String procesarProgramacionesAhora() {

        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formato =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (TransaccionProgramada programacion
                : nequi.consultarProgramaciones()) {

            if (programacion.getEstado() != EstadoProgramacion.ACTIVA) {
                continue;
            }

            LocalDateTime fecha = programacion.getProximaEjecucion();

            // SOLO OPERACIONES DENTRO DE LOS PROXIMOS 30 MINUTOS
            if (!fecha.isAfter(ahora)
                    || fecha.isAfter(ahora.plusMinutes(30))) {
                continue;
            }

            // EVITAR RECORDATORIOS DUPLICADOS
            String clave = programacion.getCodigo() + "|" + fecha;

            if (recordatoriosMostrados.contains(clave)) {
                continue;
            }

            Cliente propietario = buscarPropietario(programacion);

            if (propietario == null) {
                continue;
            }

            recordatoriosMostrados.add(clave);

            String mensaje = programacion.getTipoOperacion()
                    + " por " + programacion.getMonto()
                    + ", programada para " + fecha.format(formato);

            // REGISTRAR EL RECORDATORIO PARA CONSULTARLO DESPUES
            Notificacion recordatorio = new Notificacion(
                    propietario.getIdentificacion(),
                    TipoNotificacion.RECORDATORIO_PROGRAMACION,
                    mensaje
            );

            listaRecordatorios.add(recordatorio);

            // MOSTRAR AVISO AUTOMATICO EN JAVAFX
            Alert aviso = new Alert(Alert.AlertType.INFORMATION);
            aviso.setTitle("Recordatorio NEQUI");
            aviso.setHeaderText("Operacion programada proxima");
            aviso.setContentText(mensaje);
            aviso.show();
        }

        List<String> resultados = nequi.procesarProgramaciones(ahora);

        if (resultados.isEmpty()) {
            return "No hay operaciones vencidas en este momento.";
        }

        return String.join("\n", resultados);
    }

    // IDENTIFICAR AL PROPIETARIO DE LA PROGRAMACION
    private Cliente buscarPropietario(TransaccionProgramada programacion) {

        String codigoMonedero;

        if (programacion.getTipoOperacion()
                == TipoOperacionProgramada.DEPOSITO) {
            codigoMonedero = programacion.getCodigoDestino();
        } else {
            codigoMonedero = programacion.getCodigoOrigen();
        }

        for (Cliente cliente : nequi.getListaClientes()) {
            if (cliente.buscarMonedero(codigoMonedero) != null) {
                return cliente;
            }
        }

        return null;
    }

    // CONSULTAR RECORDATORIOS DE UN CLIENTE
    public List<Notificacion> consultarRecordatorios(String identificacion) {

        if (nequi.buscarCliente(identificacion) == null) {
            throw new IllegalArgumentException("El cliente no existe");
        }

        return listaRecordatorios.stream()
                .filter(notificacion -> notificacion
                        .getIdentificacionCliente().equals(identificacion))
                .toList();
    }

    // DATOS INICIALES DE PRUEBA
    private void inicializarData() {

        Cliente cliente1 = new Cliente(
                "Ana", "Arias", "1001",
                "ana@correo.com", "3001234567");

        Cliente cliente2 = new Cliente(
                "Carlos", "Perez", "1002",
                "carlos@correo.com", "3009876543");

        nequi.registrarCliente(cliente1);
        nequi.registrarCliente(cliente2);

        nequi.crearMonedero("1001", "M01", TipoMonedero.AHORRO);
        nequi.crearMonedero("1002", "M02", TipoMonedero.GASTOS);

        nequi.depositar("M01", new BigDecimal("1000.00"));
        nequi.depositar("M02", new BigDecimal("500.00"));
    }

    // GETTER
    public MonederoVirtual getNequi() {
        return nequi;
    }

    // CERRAR EL RELOJ AL SALIR
    @Override
    public void stop() {
        if (relojProgramaciones != null) {
            relojProgramaciones.stop();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
