package co.edu.uniquindio.edu.poo.nequi.model;

public class NotificadorWhatsApp implements  INotificador {
    @Override
    public String getCanal() {
        return "WhatsApp";
    }

    @Override
    public String enviar(Cliente cliente, Notificacion notificacion) {
        return "[WhatsApp a " + cliente.getTelefono() + "] " + notificacion.getMensaje();
    }
}

