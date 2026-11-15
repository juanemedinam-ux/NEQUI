
package co.edu.uniquindio.edu.poo.nequi.model;

import java.math.BigDecimal;
import java.util.List;

public class AnalizadorGastos {

    // CALCULAR PROMEDIO
    public double calcularPromedio(List<Transaccion> historial) {

        return historial.stream().filter(transaccion -> transaccion.getMonto() != null).mapToDouble(transaccion ->
                        transaccion.getMonto().doubleValue())
                .average()
                .orElse(0.0);
    }

    // CALCULAR DESVIACION ESTANDAR
    public double calcularDesviacionEstandar(
            List<Transaccion> historial) {
        if (historial.isEmpty()) {
            return 0.0;
        }

        double promedio = calcularPromedio(historial);

        double sumaDiferencias = historial.stream()
                .filter(transaccion -> transaccion.getMonto() != null)
                .mapToDouble(transaccion -> {
                    double monto = transaccion.getMonto().doubleValue();
                    return Math.pow(monto - promedio, 2);
                } ).sum();

        double varianza = sumaDiferencias / historial.size();

        return Math.sqrt(varianza);
    }

    // CALCULAR LIMITE DE OPERACION NORMAL
    public double calcularUmbral(List<Transaccion> historial) {

        double promedio = calcularPromedio(historial);

        double desviacion = calcularDesviacionEstandar(historial);

        return promedio + 2 * desviacion;
    }

    // DETECTAR UNA TRANSACCION ATIPICA
    public boolean esAtipica(List<Transaccion> historial, BigDecimal nuevoMonto) {
        if (nuevoMonto == null || nuevoMonto.signum() <= 0) {
            throw new IllegalArgumentException("Monto invalido para analizar");
        }

        if (historial.size() < 3) {
            return false;
        }
        double umbral = calcularUmbral(historial);
        return nuevoMonto.doubleValue() > umbral;
    }
}

