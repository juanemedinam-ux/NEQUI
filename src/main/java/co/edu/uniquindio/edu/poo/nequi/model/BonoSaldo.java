package co.edu.uniquindio.edu.poo.nequi.model;

import java.math.BigDecimal;

public class BonoSaldo extends Transaccion {

    private String codigoMonedero;

    public BonoSaldo(String codigoMonedero, BigDecimal monto) {
        super(monto);

        this.codigoMonedero = codigoMonedero;
    }

    @Override
    public boolean involucraMonedero(String codigo) {

        return codigoMonedero.equals(codigo);
    }

    @Override
    public String getDescripcion() {

        return "Bono de saldo por canje de puntos en "
                + codigoMonedero;
    }

    public String getCodigoMonedero() {
        return codigoMonedero;
    }
}
