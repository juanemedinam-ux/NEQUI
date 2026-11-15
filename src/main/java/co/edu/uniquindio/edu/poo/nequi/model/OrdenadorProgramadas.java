package co.edu.uniquindio.edu.poo.nequi.model;

import java.util.ArrayList;
import java.util.List;

public class OrdenadorProgramadas {
    // Ordenamiento por insercion: estable, O(n^2) peor caso, O(n) casi ordenada
    public List<TransaccionProgramada> ordenarPorFecha(
            List<TransaccionProgramada> lista) {

        List<TransaccionProgramada> copia = new ArrayList<>(lista);

        for (int i = 1; i < copia.size(); i++) {
            TransaccionProgramada actual = copia.get(i);
            int j = i - 1;

            while (j >= 0 && copia.get(j).getProximaEjecucion()
                    .isAfter(actual.getProximaEjecucion())) {
                copia.set(j + 1, copia.get(j));
                j--;
            }
            copia.set(j + 1, actual);
        }
        return copia;
    }
}

