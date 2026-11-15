package co.edu.uniquindio.edu.poo.nequi.model;

import co.edu.uniquindio.edu.poo.nequi.exceptions.OperacionInvalidaException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class TransaccionProgramada {

    private String codigo;
    private TipoOperacionProgramada tipoOperacion;

    private String codigoOrigen;
    private String codigoDestino;

    private BigDecimal monto;
    private LocalDateTime proximaEjecucion;

    private FrecuenciaProgramacion frecuencia;
    private EstadoProgramacion estado;

    private int totalEjecuciones;
    private String ultimoError;

    public TransaccionProgramada(TipoOperacionProgramada tipoOperacion, String codigoOrigen, String codigoDestino,
                                 BigDecimal monto, LocalDateTime proximaEjecucion, FrecuenciaProgramacion frecuencia) {

        if (tipoOperacion == null || codigoDestino == null || codigoDestino.isBlank() || monto == null ||
                monto.signum() <= 0 || proximaEjecucion == null || frecuencia == null) {

            throw new IllegalArgumentException("Datos de programacion invalidos");
        }

        if (tipoOperacion == TipoOperacionProgramada.TRANSFERENCIA && (codigoOrigen == null || codigoOrigen.isBlank())) {

            throw new IllegalArgumentException("La transferencia necesita un origen");
        }

        this.codigo = UUID.randomUUID().toString();

        this.tipoOperacion = tipoOperacion;
        this.codigoOrigen = codigoOrigen;
        this.codigoDestino = codigoDestino;
        this.monto = monto;
        this.proximaEjecucion = proximaEjecucion;
        this.frecuencia = frecuencia;
        this.estado = EstadoProgramacion.ACTIVA;
        this.totalEjecuciones = 0;
        this.ultimoError = null;
    }

    // VERIFICAR SI DEBE EJECUTARSE
    public boolean estaPendiente(LocalDateTime fechaActual) {

        return estado == EstadoProgramacion.ACTIVA
                && !proximaEjecucion.isAfter(fechaActual);
    }

    // REGISTRAR EJECUCION EXITOSA
    public void registrarEjecucion(LocalDateTime fechaActual) {

        totalEjecuciones++;
        ultimoError = null;

        if (frecuencia == FrecuenciaProgramacion.UNICA) {
            estado = EstadoProgramacion.COMPLETADA;
            return;
        }

        // Avanzar hasta una fecha posterior al corte.
        // Se ejecuta una vez por cada procesamiento.
        do {

            switch (frecuencia) {

                case DIARIA:
                    proximaEjecucion =
                            proximaEjecucion.plusDays(1);
                    break;

                case SEMANAL:
                    proximaEjecucion =
                            proximaEjecucion.plusWeeks(1);
                    break;

                case MENSUAL:
                    proximaEjecucion =
                            proximaEjecucion.plusMonths(1);
                    break;

                default:
                    throw new OperacionInvalidaException(
                            "Frecuencia no permitida");
            }

        } while (!proximaEjecucion.isAfter(fechaActual));
    }

    // REGISTRAR UN ERROR DE EJECUCION
    public void registrarError(String mensaje) {

        this.ultimoError = mensaje;
    }

    // CANCELAR PROGRAMACION
    public void cancelar() {

        if (estado != EstadoProgramacion.ACTIVA) {

            throw new OperacionInvalidaException("La programacion ya no esta activa");
        }

        estado = EstadoProgramacion.CANCELADA;
    }

    public String getCodigo() {
        return codigo;
    }

    public TipoOperacionProgramada getTipoOperacion() {
        return tipoOperacion;
    }

    public String getCodigoOrigen() {
        return codigoOrigen;
    }

    public String getCodigoDestino() {
        return codigoDestino;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public LocalDateTime getProximaEjecucion() {
        return proximaEjecucion;
    }

    public FrecuenciaProgramacion getFrecuencia() {
        return frecuencia;
    }

    public EstadoProgramacion getEstado() {
        return estado;
    }

    public int getTotalEjecuciones() {
        return totalEjecuciones;
    }

    public String getUltimoError() {
        return ultimoError;
    }

    @Override
    public String toString() {

        return "TransaccionProgramada{" +
                "codigo='" + codigo + '\'' +
                ", tipoOperacion=" + tipoOperacion +
                ", monto=" + monto +
                ", proximaEjecucion=" + proximaEjecucion +
                ", frecuencia=" + frecuencia +
                ", estado=" + estado +
                '}';
    }
}
