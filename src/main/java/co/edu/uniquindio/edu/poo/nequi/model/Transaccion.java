
package co.edu.uniquindio.edu.poo.nequi.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Transaccion implements Describible {

    private String codigo;
    private BigDecimal monto;
    private LocalDateTime fecha;
    private EstadoTransaccion estado;

    private boolean sospechosa;
    private BigDecimal comisionCobrada;
    private boolean comisionRegistrada;

    public Transaccion(BigDecimal monto) {
        if (monto == null || monto.signum() <= 0) {

            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }

        this.codigo = UUID.randomUUID().toString();
        this.monto = monto;
        this.fecha = LocalDateTime.now();
        this.estado = EstadoTransaccion.COMPLETADA;
        this.sospechosa = false;
        this.comisionCobrada = new BigDecimal("0.00");
        this.comisionRegistrada = false;
    }

    // METODOS ABSTRACTOS
    public abstract boolean involucraMonedero(
            String codigoMonedero);

    // METODO DEFINIDO POR LA INTERFAZ
    @Override
    public abstract String getDescripcion();

    // MARCAR TRANSACCION SOSPECHOSA
    void marcarSospechosa() {
        sospechosa = true;
    }

    // REGISTRAR COMISION
    void registrarComision(BigDecimal comision) {

        if (comisionRegistrada || comision == null || comision.signum() < 0) {

            throw new IllegalArgumentException("Comision invalida");
        }

        this.comisionCobrada = comision;
        this.comisionRegistrada = true;
    }

    // REVERTIR EL ESTADO
    void marcarRevertida() {

        if (estado != EstadoTransaccion.COMPLETADA) {

            throw new IllegalStateException("La transaccion ya fue revertida");
        }

        estado = EstadoTransaccion.REVERTIDA;
    }

    public String getCodigo() {
        return codigo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public EstadoTransaccion getEstado() {
        return estado;
    }

    public boolean isSospechosa() {
        return sospechosa;
    }

    public BigDecimal getComisionCobrada() {
        return comisionCobrada;
    }
}
