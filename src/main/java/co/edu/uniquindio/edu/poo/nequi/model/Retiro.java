
package co.edu.uniquindio.edu.poo.nequi.model;

import java.math.BigDecimal;

public class Retiro extends Transaccion {

    private String codigoMonedero;

    public Retiro(String codigoMonedero, BigDecimal monto) {
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
        return "Retiro del monedero " + codigoMonedero;
    }
}
