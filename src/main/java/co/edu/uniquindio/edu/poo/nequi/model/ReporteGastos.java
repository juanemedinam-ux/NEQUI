
package co.edu.uniquindio.edu.poo.nequi.model;

import java.math.BigDecimal;

public class ReporteGastos {

    private String identificacionCliente;
    private BigDecimal totalRetiros;
    private BigDecimal totalTransferencias;
    private long cantidadOperaciones;
    private long cantidadAtipicas;

    private double promedio;
    private double desviacionEstandar;

    public ReporteGastos(String identificacionCliente, BigDecimal totalRetiros, BigDecimal totalTransferencias, long cantidadOperaciones, long cantidadAtipicas, double promedio, double desviacionEstandar) {

        this.identificacionCliente = identificacionCliente;
        this.totalRetiros = totalRetiros;
        this.totalTransferencias = totalTransferencias;
        this.cantidadOperaciones = cantidadOperaciones;
        this.cantidadAtipicas = cantidadAtipicas;
        this.promedio = promedio;
        this.desviacionEstandar = desviacionEstandar;
    }

    public BigDecimal getTotalGastos() {

        return totalRetiros.add(totalTransferencias);
    }

    public String getIdentificacionCliente() {
        return identificacionCliente;
    }

    public BigDecimal getTotalRetiros() {
        return totalRetiros;
    }

    public BigDecimal getTotalTransferencias() {
        return totalTransferencias;
    }

    public long getCantidadOperaciones() {
        return cantidadOperaciones;
    }

    public long getCantidadAtipicas() {
        return cantidadAtipicas;
    }

    public double getPromedio() {
        return promedio;
    }

    public double getDesviacionEstandar() {
        return desviacionEstandar;
    }

    @Override
    public String toString() {

        return "ReporteGastos{" +
                "identificacionCliente='" +
                identificacionCliente + '\'' +
                ", totalRetiros=" + totalRetiros +
                ", totalTransferencias=" + totalTransferencias +
                ", totalGastos=" + getTotalGastos() +
                ", cantidadOperaciones=" +
                cantidadOperaciones +
                ", cantidadAtipicas=" + cantidadAtipicas +
                ", promedio=" + promedio +
                ", desviacionEstandar=" +
                desviacionEstandar +
                '}';
    }
}
