package co.edu.uniquindio.edu.poo.nequi.model;
import java.math.BigDecimal;

public enum Rango {
    BRONCE("1.00"),
    PLATA("0.90"),
    ORO("0.75"),
    PLATINO("0.50");

    private final BigDecimal factorComisionTransferencia;

    Rango(String factor) {
        this.factorComisionTransferencia = new BigDecimal(factor);
    }

    public BigDecimal getFactorComisionTransferencia() {
        return factorComisionTransferencia;
    }
}
