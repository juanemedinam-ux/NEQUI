
package co.edu.uniquindio.edu.poo.nequi.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Notificacion {

    private String codigo;
    private String identificacionCliente;
    private TipoNotificacion tipo;
    private String mensaje;
    private LocalDateTime fecha;

    public Notificacion(String identificacionCliente, TipoNotificacion tipo, String mensaje) {

        if (identificacionCliente == null || identificacionCliente.isBlank() || tipo == null || mensaje == null || mensaje.isBlank()) {

            throw new IllegalArgumentException("Datos de notificacion invalidos");
        }

        this.codigo = UUID.randomUUID().toString();
        this.identificacionCliente = identificacionCliente;
        this.tipo = tipo;
        this.mensaje = mensaje;
        this.fecha = LocalDateTime.now();
    }

    private final List<String> envios = new ArrayList<>();

    public void agregarEnvio(String envio) { envios.add(envio); }

    public List<String> getEnvios() { return Collections.unmodifiableList(envios); }

    public String getCodigo() {
        return codigo;
    }

    public String getIdentificacionCliente() {
        return identificacionCliente;
    }

    public TipoNotificacion getTipo() {
        return tipo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    @Override
    public String toString() {

        return "Notificacion{" +
                "tipo=" + tipo +
                ", mensaje='" + mensaje + '\'' +
                ", fecha=" + fecha +
                '}';
    }
}
