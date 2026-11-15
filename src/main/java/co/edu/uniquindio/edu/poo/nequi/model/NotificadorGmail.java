package co.edu.uniquindio.edu.poo.nequi.model;

public class NotificadorGmail implements  INotificador {
    @Override
    public String getCanal() {
        return "Gmail";
    }

    @Override
    public String enviar(Cliente cliente, Notificacion notificacion) {
        return "[Gmail a " + cliente.getCorreo () + "] " + notificacion.getMensaje();
    }
}
