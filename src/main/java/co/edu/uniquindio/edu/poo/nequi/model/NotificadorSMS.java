package co.edu.uniquindio.edu.poo.nequi.model;

public class NotificadorSMS implements INotificador {
    @Override
    public String getCanal() {
        return "SMS";
    }

    @Override
    public String enviar(Cliente cliente, Notificacion notificacion) {
        return "[SMS a " + cliente.getTelefono() + "] " + notificacion.getMensaje();
    }
}
