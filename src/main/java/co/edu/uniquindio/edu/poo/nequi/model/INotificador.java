package co.edu.uniquindio.edu.poo.nequi.model;

public interface INotificador {
    String getCanal();
    String enviar(Cliente cliente, Notificacion notificacion);
}
