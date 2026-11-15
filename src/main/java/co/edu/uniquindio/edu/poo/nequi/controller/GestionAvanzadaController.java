
package co.edu.uniquindio.edu.poo.nequi.controller;

import co.edu.uniquindio.edu.poo.nequi.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GestionAvanzadaController {

    private MonederoVirtual nequi;

    // CONSTRUCTOR
    public GestionAvanzadaController(MonederoVirtual nequi) {
        this.nequi = nequi;
    }

    // BUSCAR CLIENTE
    private Cliente obtenerCliente(String identificacion) {

        Cliente cliente = nequi.buscarCliente(identificacion);

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "El cliente no existe");
        }

        return cliente;
    }

    // VALIDAR PROPIETARIO DEL MONEDERO
    public void validarMonedero(
            String identificacion,
            String codigoMonedero) {

        Cliente cliente = obtenerCliente(identificacion);

        if (cliente.buscarMonedero(codigoMonedero) == null) {
            throw new IllegalArgumentException(
                    "El monedero no pertenece al cliente");
        }
    }

    // PROGRAMAR UN DEPOSITO
    public TransaccionProgramada programarDeposito(
            String identificacion,
            String codigoMonedero,
            BigDecimal monto,
            LocalDateTime fecha,
            FrecuenciaProgramacion frecuencia) {

        validarMonedero(identificacion, codigoMonedero);

        return nequi.programarDeposito(
                codigoMonedero,
                monto,
                fecha,
                frecuencia
        );
    }

    // PROGRAMAR UNA TRANSFERENCIA
    public TransaccionProgramada programarTransferencia(
            String identificacion,
            String codigoOrigen,
            String codigoDestino,
            BigDecimal monto,
            LocalDateTime fecha,
            FrecuenciaProgramacion frecuencia) {

        validarMonedero(identificacion, codigoOrigen);

        return nequi.programarTransferencia(
                codigoOrigen,
                codigoDestino,
                monto,
                fecha,
                frecuencia
        );
    }

    // CONSULTAR PROGRAMACIONES DEL CLIENTE
    public List<TransaccionProgramada> consultarProgramaciones(
            String identificacion) {

        Cliente cliente = obtenerCliente(identificacion);

        List<TransaccionProgramada> propias =
                new ArrayList<>();

        for (TransaccionProgramada programacion
                : nequi.consultarProgramaciones()) {

            String codigoMonedero;

            if (programacion.getTipoOperacion()
                    == TipoOperacionProgramada.DEPOSITO) {

                codigoMonedero =
                        programacion.getCodigoDestino();

            } else {

                codigoMonedero =
                        programacion.getCodigoOrigen();
            }

            if (cliente.buscarMonedero(codigoMonedero) != null) {
                propias.add(programacion);
            }
        }

        return propias;
    }

    // CANCELAR UNA PROGRAMACION
    public void cancelarProgramacion(
            String identificacion,
            String codigo) {

        boolean encontrada = false;

        for (TransaccionProgramada programacion
                : consultarProgramaciones(identificacion)) {

            if (programacion.getCodigo().equals(codigo)) {
                encontrada = true;
                break;
            }
        }

        if (!encontrada) {
            throw new IllegalArgumentException(
                    "No puedes cancelar esta programacion");
        }

        nequi.cancelarProgramacion(codigo);
    }

    // CONSULTAR MOVIMIENTOS DEL MONEDERO
    public List<Transaccion> consultarMovimientos(
            String identificacion,
            String codigoMonedero) {

        validarMonedero(identificacion, codigoMonedero);

        List<Transaccion> movimientos =
                new ArrayList<>(
                        nequi.consultarHistorial(codigoMonedero)
                );

        // MOSTRAR PRIMERO LOS MAS RECIENTES
        movimientos.sort(
                (a, b) -> b.getFecha().compareTo(a.getFecha())
        );

        return movimientos;
    }

    // REVERTIR UN MOVIMIENTO
    public void revertirTransaccion(
            String identificacion,
            String codigoMonedero,
            String codigoTransaccion) {

        validarMonedero(identificacion, codigoMonedero);

        Transaccion seleccionada = null;

        for (Transaccion transaccion
                : nequi.consultarHistorial(codigoMonedero)) {

            if (transaccion.getCodigo()
                    .equals(codigoTransaccion)) {

                seleccionada = transaccion;
                break;
            }
        }

        if (seleccionada == null) {
            throw new IllegalArgumentException(
                    "El movimiento no pertenece al monedero");
        }

        // NO PERMITIR REVERTIR TRANSFERENCIAS
        // RECIBIDAS DESDE ESTE MONEDERO.
        if (seleccionada instanceof Transferencia transferencia
                && !transferencia.getCodigoOrigen()
                .equals(codigoMonedero)) {

            throw new IllegalArgumentException(
                    "Solo puedes revertir transferencias enviadas desde este monedero");
        }

        nequi.revertirTransaccion(codigoTransaccion);
    }
}
