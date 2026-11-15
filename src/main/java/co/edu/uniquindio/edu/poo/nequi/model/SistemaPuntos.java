
package co.edu.uniquindio.edu.poo.nequi.model;

import co.edu.uniquindio.edu.poo.nequi.exceptions.PuntosInsuficientesException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class SistemaPuntos {

    private int puntosGanados;
    private int puntosCanjeados;

    private Map<String, Integer> puntosPorTransaccion;

    private boolean descuentoTransferenciaActivo;
    private LocalDate retiroSinCargoHasta;

    public SistemaPuntos() {

        this.puntosGanados = 0;
        this.puntosCanjeados = 0;
        this.puntosPorTransaccion = new HashMap<>();
        this.descuentoTransferenciaActivo = false;
        this.retiroSinCargoHasta = null;
    }

    // REGISTRAR PUNTOS DE UNA TRANSACCION
    public void registrarPuntos(String codigoTransaccion, int puntos) {

        if (codigoTransaccion == null ||
                codigoTransaccion.isBlank() ||
                puntos < 0) {

            throw new IllegalArgumentException("Codigo o puntos invalidos");
        }

        if (puntosPorTransaccion.containsKey(codigoTransaccion)) {

            throw new IllegalArgumentException("La transaccion ya registro sus puntos");
        }

        try {

            int total = Math.addExact(puntosGanados, puntos);

            puntosPorTransaccion.put(codigoTransaccion, puntos);
            puntosGanados = total;

        } catch (ArithmeticException e) {

            throw new IllegalArgumentException("Demasiados puntos", e);
        }
    }

    // REVERTIR PUNTOS DE UNA TRANSACCION
    public void revertirPuntos(String codigoTransaccion) {

        Integer puntos = puntosPorTransaccion.remove(codigoTransaccion);

        if (puntos == null) {
            throw new IllegalArgumentException("No hay puntos asociados a esa transaccion");
        }

        puntosGanados -= puntos;
    }

    // COMPROBAR REGISTRO DE PUNTOS
    public boolean tieneRegistroPuntos(String codigoTransaccion) {

        return puntosPorTransaccion.containsKey(
                codigoTransaccion);
    }

    // CANJEAR BENEFICIOS
    public void canjearBeneficio(Beneficio beneficio) {

        if (beneficio == null) {

            throw new IllegalArgumentException("Beneficio invalido");
        }

        if (beneficio == Beneficio.DESCUENTO_TRANSFERENCIA && descuentoTransferenciaActivo) {

            throw new IllegalStateException("El descuento ya se encuentra activo");
        }

        int costo = beneficio.getCostoPuntos();

        if (getPuntosDisponibles() < costo) {

            throw new PuntosInsuficientesException("No tiene puntos suficientes");
        }

        puntosCanjeados += costo;

        if (beneficio == Beneficio.DESCUENTO_TRANSFERENCIA) {

            descuentoTransferenciaActivo = true;

        } else if (beneficio == Beneficio.RETIROS_SIN_CARGO) {

            LocalDate hoy = LocalDate.now();

            LocalDate inicio = retiroSinCargoHasta != null && retiroSinCargoHasta.isAfter(hoy) ? retiroSinCargoHasta : hoy;

            retiroSinCargoHasta = inicio.plusMonths(1);
        }
    }

    // PUNTOS DISPONIBLES PARA CANJEAR
    public int getPuntosDisponibles() {

        return Math.max(0, puntosGanados - puntosCanjeados);
    }

    // PUNTOS PENDIENTES POR COMPENSAR
    public int getDeudaPuntos() {

        return Math.max(0, puntosCanjeados - puntosGanados);
    }

    // CALCULAR RANGO SEGUN PUNTOS ACUMULADOS
    public Rango getRango() {

        int puntos = puntosGanados;

        if (puntos <= 500) {
            return Rango.BRONCE;
        }

        if (puntos <= 1000) {
            return Rango.PLATA;
        }

        if (puntos <= 5000) {
            return Rango.ORO;
        }

        return Rango.PLATINO;
    }

    // CONSULTAR BENEFICIOS ACTIVOS
    public boolean isDescuentoTransferenciaActivo() {

        return descuentoTransferenciaActivo;
    }

    public boolean isRetiroSinCargoActivo() {

        return retiroSinCargoHasta != null && LocalDate.now().isBefore(retiroSinCargoHasta);
    }

    public LocalDate getRetiroSinCargoHasta() {
        return retiroSinCargoHasta;
    }

    public int getPuntosGanados() {
        return puntosGanados;
    }

    public int getPuntosCanjeados() {
        return puntosCanjeados;
    }

    public int getPuntosDeTransaccion(String codigo) {

        return puntosPorTransaccion.getOrDefault(
                codigo, 0);
    }
}
