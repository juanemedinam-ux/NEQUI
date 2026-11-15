
package co.edu.uniquindio.edu.poo.nequi.model;

import java.math.BigDecimal;

public class Transferencia extends Transaccion {

    private String codigoOrigen;
    private String codigoDestino;

    public Transferencia(String codigoOrigen, String codigoDestino, BigDecimal monto) {
        super(monto);

        this.codigoOrigen = codigoOrigen;
        this.codigoDestino = codigoDestino;
    }

    public String getCodigoOrigen() {
        return codigoOrigen;
    }

    public String getCodigoDestino() {
        return codigoDestino;
    }

    @Override
    public boolean involucraMonedero(String codigo) {

        return codigoOrigen.equals(codigo)
                || codigoDestino.equals(codigo);
    }

    @Override
    public String getDescripcion() {

        return "Transferencia de " + codigoOrigen
                + " a " + codigoDestino;
    }
}
