
package co.edu.uniquindio.edu.poo.nequi.controller;

import co.edu.uniquindio.edu.poo.nequi.model.Beneficio;
import co.edu.uniquindio.edu.poo.nequi.model.Cliente;
import co.edu.uniquindio.edu.poo.nequi.model.Deposito;
import co.edu.uniquindio.edu.poo.nequi.model.Monedero;
import co.edu.uniquindio.edu.poo.nequi.model.MonederoVirtual;
import co.edu.uniquindio.edu.poo.nequi.model.Notificacion;
import co.edu.uniquindio.edu.poo.nequi.model.Rango;
import co.edu.uniquindio.edu.poo.nequi.model.ReporteGastos;
import co.edu.uniquindio.edu.poo.nequi.model.Retiro;
import co.edu.uniquindio.edu.poo.nequi.model.Transaccion;
import co.edu.uniquindio.edu.poo.nequi.model.Transferencia;

import java.math.BigDecimal;
import java.util.List;

public class MonederoController {

    private MonederoVirtual nequi;

    // CONSTRUCTOR
    public MonederoController(MonederoVirtual nequi) {
        this.nequi = nequi;
    }

    // BUSCAR CLIENTE
    public Cliente buscarCliente(String identificacion) {
        return nequi.buscarCliente(identificacion);
    }

    // BUSCAR MONEDERO
    public Monedero buscarMonedero(String codigo) {
        return nequi.buscarMonedero(codigo);
    }

    // VALIDAR QUE EL MONEDERO SEA DEL CLIENTE
    public void validarMonederoCliente(
            String identificacion,
            String codigoMonedero) {

        Cliente cliente = buscarCliente(identificacion);

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "El cliente no existe");
        }

        if (cliente.buscarMonedero(codigoMonedero) == null) {
            throw new IllegalArgumentException(
                    "El monedero no pertenece al cliente");
        }
    }

    // CONSULTAR SALDO
    public BigDecimal consultarSaldo(String codigo) {
        return nequi.consultarSaldo(codigo);
    }

    // CONSULTAR PUNTOS
    public int consultarPuntos(String identificacion) {
        return nequi.consultarPuntos(identificacion);
    }

    // CONSULTAR RANGO
    public Rango consultarRango(String identificacion) {
        return nequi.consultarRango(identificacion);
    }

    // DEPOSITAR DINERO
    public Deposito depositar(
            String identificacion,
            String codigoMonedero,
            BigDecimal monto) {

        validarMonederoCliente(
                identificacion, codigoMonedero);

        return nequi.depositar(codigoMonedero, monto);
    }

    // RETIRAR DINERO
    public Retiro retirar(
            String identificacion,
            String codigoMonedero,
            BigDecimal monto) {

        validarMonederoCliente(
                identificacion, codigoMonedero);

        return nequi.retirar(codigoMonedero, monto);
    }

    // TRANSFERIR DINERO
    public Transferencia transferir(
            String identificacion,
            String codigoOrigen,
            String codigoDestino,
            BigDecimal monto) {

        validarMonederoCliente(
                identificacion, codigoOrigen);

        return nequi.transferir(
                codigoOrigen, codigoDestino, monto);
    }

    // CONSULTAR HISTORIAL
    public List<Transaccion> consultarHistorial(
            String codigoMonedero) {

        return nequi.consultarHistorial(codigoMonedero);
    }

    // GENERAR REPORTE
    public ReporteGastos generarReporteGastos(
            String identificacion) {

        return nequi.generarReporteGastos(identificacion);
    }

    // CONSULTAR NOTIFICACIONES
    public List<Notificacion> consultarNotificaciones(
            String identificacion) {

        return nequi.consultarNotificaciones(
                identificacion);
    }

    // CANJEAR BENEFICIO
    public void canjearBeneficio(
            String identificacion,
            String codigoMonedero,
            Beneficio beneficio) {

        nequi.canjearBeneficio(
                identificacion,
                codigoMonedero,
                beneficio);
    }
}
