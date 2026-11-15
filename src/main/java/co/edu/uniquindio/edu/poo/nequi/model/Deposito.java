
package co.edu.uniquindio.edu.poo.nequi.model;

import java.math.BigDecimal;

public class Deposito extends Transaccion {

    private String codigoMonedero;

    public Deposito(String codigoMonedero, BigDecimal monto) {
        super(monto);
        this.codigoMonedero = codigoMonedero;
    }

    public String getCodigoMonedero() {
        return codigoMonedero;
    }

    @Override
    public boolean involucraMonedero(String codigo) {
        return codigoMonedero.equals(codigo);
    }

    @Override
    public String getDescripcion() {
        return "Deposito al monedero " + codigoMonedero;
    }
}
