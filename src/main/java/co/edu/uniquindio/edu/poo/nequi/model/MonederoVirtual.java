
package co.edu.uniquindio.edu.poo.nequi.model;

import co.edu.uniquindio.edu.poo.nequi.exceptions.OperacionInvalidaException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MonederoVirtual {

    private String nombre;
    private List<Cliente> listaClientes;
    private List<Transaccion> listaTransacciones;
    private List<TransaccionProgramada> listaProgramaciones;
    private List<Notificacion> listaNotificaciones;

    private AnalizadorGastos analizador;
    private final OrdenadorProgramadas ordenador = new OrdenadorProgramadas();

    private BigDecimal comisionTransferencia;
    private BigDecimal comisionRetiro;

    private static final BigDecimal LIMITE_SALDO_BAJO =
            new BigDecimal("100.00");

    public MonederoVirtual(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del sistema es obligatorio");
        }

        this.nombre = nombre.trim();
        this.listaClientes = new ArrayList<>();
        this.listaTransacciones = new ArrayList<>();
        this.listaProgramaciones = new ArrayList<>();
        this.listaNotificaciones = new ArrayList<>();
        this.analizador = new AnalizadorGastos();
        this.comisionTransferencia = new BigDecimal("0.00");
        this.comisionRetiro = new BigDecimal("0.00");
    }

    // CLIENTES

    public void registrarCliente(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException(
                    "El cliente no puede ser nulo");
        }
        if (buscarCliente(cliente.getIdentificacion()) != null) {
            throw new IllegalArgumentException(
                    "Ya existe un cliente con esa identificacion");
        }
        listaClientes.add(cliente);
    }

    public Cliente buscarCliente(String identificacion) {

        return listaClientes.stream().filter(cliente -> cliente.getIdentificacion().equals(identificacion)).findFirst().orElse(null);
    }

    // MONEDEROS

    public Monedero buscarMonedero(String codigo) {
        return listaClientes.stream().flatMap(cliente -> cliente.getListaMonederos().stream()).filter(monedero -> monedero.getCodigo().equals(codigo)).findFirst().orElse(null);
    }


    public void crearMonedero(String identificacion, String codigo, TipoMonedero tipo) {
        Cliente cliente = obtenerCliente(identificacion);
        if (codigo == null || codigo.trim().isEmpty() || tipo == null) {
            throw new IllegalArgumentException("Datos del monedero invalidos");
        }
        if (buscarMonedero(codigo.trim()) != null) {
            throw new IllegalArgumentException("El codigo del monedero ya existe");
        }
        cliente.agregarMonedero(new Monedero(codigo, tipo)); }

    // COMISIONES

    public void configurarComisiones(BigDecimal transferencia, BigDecimal retiro) {
        BigDecimal nuevaTransferencia = validarComision(transferencia);
        BigDecimal nuevoRetiro = validarComision(retiro);
        this.comisionTransferencia = nuevaTransferencia;
        this.comisionRetiro = nuevoRetiro;
    }

    // DEPOSITOS

    public Deposito depositar(String codigoMonedero, BigDecimal monto) {
        Monedero monedero = obtenerMonedero(codigoMonedero);
        Cliente cliente = obtenerClienteDeMonedero(codigoMonedero);
        BigDecimal montoValido = validarMonto(monto);
        int puntos = calcularPuntos(montoValido, 1);

        Deposito deposito = new Deposito(monedero.getCodigo(), montoValido);
        monedero.aumentarSaldo(montoValido);
        listaTransacciones.add(deposito);
        cliente.getSistemaPuntos().registrarPuntos(deposito.getCodigo(), puntos);
        analizarYNotificar(cliente, deposito);
        return deposito;
    }

    // RETIROS

    public Retiro retirar(String codigoMonedero, BigDecimal monto) {
        Monedero monedero = obtenerMonedero(codigoMonedero);
        Cliente cliente = obtenerClienteDeMonedero(codigoMonedero);
        BigDecimal montoValido = validarMonto(monto);
        int puntos = calcularPuntos(montoValido, 2);
        BigDecimal comision = cliente.getSistemaPuntos().isRetiroSinCargoActivo() ? new BigDecimal("0.00") : comisionRetiro;
        monedero.disminuirSaldo(montoValido.add(comision));

        Retiro retiro = new Retiro(monedero.getCodigo(), montoValido);

        // NUEVO: guardar la comision real
        retiro.registrarComision(comision);
        listaTransacciones.add(retiro);
        cliente.getSistemaPuntos().registrarPuntos(retiro.getCodigo(), puntos);
        analizarYNotificar(cliente, retiro);
        revisarSaldoBajo(cliente, monedero);
        return retiro;
    }

    // TRANSFERENCIAS

    public Transferencia transferir(String codigoOrigen, String codigoDestino, BigDecimal monto) {

        Monedero origen = obtenerMonedero(codigoOrigen);
        Monedero destino = obtenerMonedero(codigoDestino);
        if (origen.getCodigo().equals(destino.getCodigo())) {
            throw new IllegalArgumentException("El origen y destino deben ser diferentes");
        }
        Cliente clienteOrigen = obtenerClienteDeMonedero(codigoOrigen);
        BigDecimal montoValido = validarMonto(monto);
        int puntos = calcularPuntos(montoValido, 3);
        BigDecimal comision = comisionTransferencia;

        if (clienteOrigen.getSistemaPuntos().isDescuentoTransferenciaActivo()) {
            comision = comision.multiply(new BigDecimal("0.90"));
            comision = comision.setScale(2, RoundingMode.HALF_UP);
        }
        comision = comision.multiply(clienteOrigen.getSistemaPuntos().getRango().getFactorComisionTransferencia()).setScale(2, RoundingMode.HALF_UP);

        origen.disminuirSaldo(montoValido.add(comision));
        destino.aumentarSaldo(montoValido);
        Transferencia transferencia = new Transferencia(origen.getCodigo(), destino.getCodigo(), montoValido);

        // NUEVO: guardar la comision real
        transferencia.registrarComision(comision);

        listaTransacciones.add(transferencia);
        clienteOrigen.getSistemaPuntos().registrarPuntos(transferencia.getCodigo(), puntos);
        analizarYNotificar(clienteOrigen, transferencia);
        revisarSaldoBajo(clienteOrigen, origen);
        return transferencia;
    }

    // REVERSION DE TRANSACCIONES

    // BUSCAR UNA TRANSACCION POR CODIGO

    public Transaccion buscarTransaccion(String codigo) {

        return listaTransacciones.stream().filter(transaccion -> transaccion.getCodigo().equals(codigo)).findFirst().orElse(null);
    }


    // REVERTIR UNA TRANSACCION

    public void revertirTransaccion(String codigo) {

        Transaccion transaccion = buscarTransaccion(codigo);
        if (transaccion == null) {
            throw new OperacionInvalidaException("No existe la transaccion");
        }

        if (transaccion.getEstado() != EstadoTransaccion.COMPLETADA) {
            throw new OperacionInvalidaException("La transaccion ya fue revertida");
        }

        // Los bonos son resultado de canjes de puntos.
        // No se revierten con este metodo.
        if (transaccion instanceof BonoSaldo) {
            throw new OperacionInvalidaException("Un bono de puntos no se revierte con este metodo");
        }

        Cliente cliente;
        Monedero monederoParaAviso = null;

        // REVERTIR DEPOSITO
        if (transaccion instanceof Deposito deposito) {
            Monedero monedero = obtenerMonedero(deposito.getCodigoMonedero());
            cliente = obtenerClienteDeMonedero(monedero.getCodigo());
            validarPuntosReversion(cliente, transaccion);

            // Quitar el dinero que se habia depositado.
            monedero.disminuirSaldo(deposito.getMonto());
            monederoParaAviso = monedero;

            // REVERTIR RETIRO
        } else if (transaccion instanceof Retiro retiro) {

            Monedero monedero = obtenerMonedero(retiro.getCodigoMonedero());

            cliente = obtenerClienteDeMonedero(monedero.getCodigo());

            validarPuntosReversion(cliente, transaccion);

            // Devolver el dinero y la comision pagada.
            BigDecimal totalDevolver = retiro.getMonto().add(retiro.getComisionCobrada());

            monedero.aumentarSaldo(totalDevolver);

            // REVERTIR TRANSFERENCIA
        } else if (transaccion instanceof Transferencia transferencia) {
            Monedero origen = obtenerMonedero(transferencia.getCodigoOrigen());
            Monedero destino = obtenerMonedero(transferencia.getCodigoDestino());
            cliente = obtenerClienteDeMonedero(origen.getCodigo());
            validarPuntosReversion(cliente, transaccion);

            // Primero quitar el dinero del destino.
            // Si no tiene saldo suficiente, falla
            // sin modificar el monedero de origen.
            destino.disminuirSaldo(transferencia.getMonto());

            // Devolver monto y comision al origen.
            BigDecimal totalDevolver = transferencia.getMonto().add(transferencia.getComisionCobrada());
            origen.aumentarSaldo(totalDevolver);
            monederoParaAviso = destino;

        } else {
            throw new OperacionInvalidaException("Este tipo de transaccion no admite reversion");
        }

        // Recalcular los puntos del cliente
        cliente.getSistemaPuntos().revertirPuntos(transaccion.getCodigo());

        // Cambiar el estado en el historial
        transaccion.marcarRevertida();

        // Revisar si la reversion dejo un saldo bajo
        if (monederoParaAviso != null) {
            Cliente propietario = obtenerClienteDeMonedero(monederoParaAviso.getCodigo());
            revisarSaldoBajo(propietario, monederoParaAviso);
        }
    }

    // VALIDAR EL REGISTRO DE PUNTOS

    private void validarPuntosReversion(Cliente cliente, Transaccion transaccion) {
        boolean existeRegistro = cliente.getSistemaPuntos().tieneRegistroPuntos(transaccion.getCodigo());
        if (!existeRegistro) {
            throw new OperacionInvalidaException("La transaccion no tiene un registro de puntos reversible");
        }
    }

    // SISTEMA DE PUNTOS

    public int consultarPuntos(String identificacion) {
        return obtenerCliente(identificacion).getSistemaPuntos().getPuntosDisponibles();
    }

    public Rango consultarRango(String identificacion) {
        return obtenerCliente(identificacion).getSistemaPuntos().getRango();
    }

    public void canjearBeneficio(String identificacion,String codigoMonedero, Beneficio beneficio) {

        Cliente cliente = obtenerCliente(identificacion);
        Monedero monedero = cliente.buscarMonedero(codigoMonedero);

        if (monedero == null) {
            throw new IllegalArgumentException("El monedero no pertenece al cliente");
        }
        cliente.getSistemaPuntos().canjearBeneficio(beneficio);

        if (beneficio == Beneficio.BONO_SALDO) {
            BigDecimal bono = new BigDecimal("50.00");
            monedero.aumentarSaldo(bono);
            listaTransacciones.add(new BonoSaldo(monedero.getCodigo(), bono));
        }
    }

    // TRANSACCIONES PROGRAMADAS

    public TransaccionProgramada programarDeposito(String codigoDestino, BigDecimal monto, LocalDateTime fechaEjecucion, FrecuenciaProgramacion frecuencia) {

        Monedero destino = obtenerMonedero(codigoDestino);

        BigDecimal montoValido = validarMonto(monto);
        validarFechaProgramacion(fechaEjecucion);
        if (frecuencia == null) {
            throw new IllegalArgumentException("La frecuencia es obligatoria");
        }

        TransaccionProgramada programacion = new TransaccionProgramada(TipoOperacionProgramada.DEPOSITO, null, destino.getCodigo(), montoValido, fechaEjecucion, frecuencia);
        listaProgramaciones.add(programacion);

        return programacion;
    }

    public TransaccionProgramada programarTransferencia(String codigoOrigen, String codigoDestino, BigDecimal monto, LocalDateTime fechaEjecucion, FrecuenciaProgramacion frecuencia) {

        Monedero origen = obtenerMonedero(codigoOrigen);
        Monedero destino = obtenerMonedero(codigoDestino);
        if (origen.getCodigo().equals(destino.getCodigo())) {
            throw new OperacionInvalidaException("No puede transferir al mismo monedero");
        }

        BigDecimal montoValido = validarMonto(monto);
        validarFechaProgramacion(fechaEjecucion);
        if (frecuencia == null) {
            throw new IllegalArgumentException("La frecuencia es obligatoria");
        }

        TransaccionProgramada programacion = new TransaccionProgramada(TipoOperacionProgramada.TRANSFERENCIA, origen.getCodigo(), destino.getCodigo(), montoValido, fechaEjecucion, frecuencia);
        listaProgramaciones.add(programacion);
        return programacion;
    }


    public TransaccionProgramada buscarProgramacion(String codigo) {

        return listaProgramaciones.stream().filter(programacion -> programacion.getCodigo().equals(codigo)).findFirst().orElse(null);
    }

    public void cancelarProgramacion(String codigo) {

        TransaccionProgramada programacion =
                buscarProgramacion(codigo);
        if (programacion == null) {
            throw new IllegalArgumentException("La programacion no existe");
        }
        programacion.cancelar();
    }

    public List<TransaccionProgramada> consultarProgramaciones() {

        return ordenador.ordenarPorFecha(listaProgramaciones);
    }

    public List<String> procesarProgramaciones(LocalDateTime fechaActual) {
        if (fechaActual == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula");
        }

        List<String> resultados = new ArrayList<>();

        List<TransaccionProgramada> pendientes = ordenador.ordenarPorFecha(listaProgramaciones.stream().filter(programacion -> programacion.estaPendiente(fechaActual)).toList());

        for (TransaccionProgramada programacion : pendientes) {
            try {
                if (programacion.getTipoOperacion() == TipoOperacionProgramada.DEPOSITO) {
                    depositar(programacion.getCodigoDestino(), programacion.getMonto());

                } else if (programacion.getTipoOperacion() == TipoOperacionProgramada.TRANSFERENCIA) {
                    transferir(programacion.getCodigoOrigen(), programacion.getCodigoDestino(), programacion.getMonto());
                }
                programacion.registrarEjecucion(fechaActual);
                resultados.add(
                        "Ejecutada: " + programacion.getCodigo());
            } catch (RuntimeException e) {
                programacion.registrarError(e.getMessage());
                resultados.add("Error: " + programacion.getCodigo() + " - " + e.getMessage());
            }
        }

        return resultados;
    }

    private void validarFechaProgramacion(
            LocalDateTime fecha) {

        if (fecha == null ||
                !fecha.isAfter(LocalDateTime.now())) {

            throw new OperacionInvalidaException(
                    "La fecha debe ser futura");
        }
    }

    // ANALISIS DE MOVIMIENTOS

    public List<Transaccion> consultarHistorialCliente(
            String identificacion) {

        Cliente cliente = obtenerCliente(identificacion);

        return listaTransacciones.stream().filter(transaccion -> cliente.getListaMonederos().stream().anyMatch(monedero -> transaccion.involucraMonedero(monedero.getCodigo()))).toList();
    }

    // GENERAR REPORTE DE GASTOS

    public ReporteGastos generarReporteGastos(String identificacion) {
        Cliente cliente = obtenerCliente(identificacion);
        List<Transaccion> gastos = consultarHistorialCliente(identificacion).stream().filter(transaccion ->
                transaccion.getEstado() == EstadoTransaccion.COMPLETADA).filter(transaccion ->
                transaccion instanceof Retiro || (transaccion instanceof Transferencia transferencia && cliente.buscarMonedero(transferencia.getCodigoOrigen()) != null && cliente.buscarMonedero(transferencia.getCodigoDestino()) == null)).toList();

        BigDecimal totalRetiros = gastos.stream()
                .filter(transaccion -> transaccion instanceof Retiro).map(Transaccion::getMonto).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalTransferencias = gastos.stream()
                .filter(transaccion ->
                        transaccion instanceof Transferencia)
                .map(Transaccion::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long cantidadAtipicas = gastos.stream().filter(Transaccion::isSospechosa).count();

        double promedio = analizador.calcularPromedio(gastos);

        double desviacion = analizador.calcularDesviacionEstandar(gastos);

        return new ReporteGastos(identificacion, totalRetiros, totalTransferencias, gastos.size(), cantidadAtipicas, promedio, desviacion
        );
    }

    // CONSULTAR NOTIFICACIONES

    public List<Notificacion> consultarNotificaciones(
            String identificacion) {
        obtenerCliente(identificacion);
        return listaNotificaciones.stream().filter(notificacion -> notificacion.getIdentificacionCliente().equals(identificacion)).toList();
    }

    // ANALIZAR UNA OPERACION REALIZADA

    private void analizarYNotificar(Cliente cliente, Transaccion operacion) {
        List<Transaccion> anteriores =
                consultarHistorialCliente(
                        cliente.getIdentificacion())
                        .stream()
                        .filter(transaccion -> transaccion != operacion && transaccion.getEstado() == EstadoTransaccion.COMPLETADA
                                        && !(transaccion instanceof BonoSaldo)).toList();
        boolean atipica = analizador.esAtipica(anteriores, operacion.getMonto());
        if (atipica) {
            operacion.marcarSospechosa();
            Notificacion notificacion =
                    new Notificacion(cliente.getIdentificacion(), TipoNotificacion.TRANSACCION_ATIPICA,
                            "Movimiento potencialmente atipico de "
                                    + operacion.getMonto()
                                    + " (codigo "
                                    + operacion.getCodigo()
                                    + ")"
                    );

            listaNotificaciones.add(notificacion);
        }
    }

    // GENERAR ALERTA DE SALDO BAJO

    private void revisarSaldoBajo(Cliente cliente, Monedero monedero) {

        if (monedero.getSaldo().compareTo(
                LIMITE_SALDO_BAJO) < 0) {

            Notificacion notificacion =
                    new Notificacion(cliente.getIdentificacion(), TipoNotificacion.SALDO_BAJO,
                            "Saldo bajo en "
                                    + monedero.getCodigo()
                                    + ": "
                                    + monedero.getSaldo());

            listaNotificaciones.add(notificacion);
        }
    }

    // CONSULTAS

    public BigDecimal consultarSaldo(
            String codigoMonedero) {

        return obtenerMonedero(codigoMonedero)
                .getSaldo();
    }

    public List<Transaccion> consultarHistorial(String codigoMonedero) {
        Monedero monedero = obtenerMonedero(codigoMonedero);

        List<Transaccion> historial = new ArrayList<>();

        for (Transaccion transaccion : listaTransacciones) {
            if (transaccion.involucraMonedero(monedero.getCodigo())) {
                historial.add(transaccion);
            }
        }
        return Collections.unmodifiableList(historial);
    }

    // METODOS AUXILIARES

    private Cliente obtenerCliente(String identificacion) {
        Cliente cliente =
                buscarCliente(identificacion);
        if (cliente == null) {
            throw new IllegalArgumentException(
                    "No existe el cliente");
        }
        return cliente;
    }

    private Cliente obtenerClienteDeMonedero(String codigo) {

        return listaClientes.stream().filter(cliente -> cliente.buscarMonedero(codigo) != null).findFirst().orElseThrow(() ->
                        new IllegalArgumentException("No existe el monedero: " + codigo));
    }


    private Monedero obtenerMonedero(String codigo) {
        Monedero monedero = buscarMonedero(codigo);
        if (monedero == null) {
            throw new IllegalArgumentException("No existe el monedero: " + codigo);
        }
        return monedero;
    }

    private int calcularPuntos(BigDecimal monto, int multiplicador) {
        try {
            int gruposDeCien = monto.divideToIntegralValue(new BigDecimal("100")).intValueExact();

            return Math.multiplyExact(
                    gruposDeCien, multiplicador);

        } catch (ArithmeticException e) {

            throw new IllegalArgumentException("El monto supera el limite de puntos", e);
        }
    }

    private BigDecimal validarMonto(BigDecimal monto) {
        if (monto == null || monto.signum() <= 0) {

            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
        try {
            return monto.setScale(2, RoundingMode.UNNECESSARY);

        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("Solo se permiten dos decimales", e);
        }
    }

    private BigDecimal validarComision(BigDecimal monto) {
        if (monto == null || monto.signum() < 0) {
            throw new IllegalArgumentException("La comision no puede ser negativa");
        }
        try {
            return monto.setScale(2, RoundingMode.UNNECESSARY);

        } catch (ArithmeticException e) {
            throw new IllegalArgumentException("La comision admite dos decimales", e);
        }
    }

    private final List<INotificador> notificadores = List.of(new NotificadorWhatsApp(), new NotificadorGmail(), new NotificadorSMS());

    private void publicar(Cliente cliente, Notificacion notificacion) {
        listaNotificaciones.add(notificacion);
        notificadores.forEach(n -> notificacion.agregarEnvio(n.enviar(cliente, notificacion)));
    }

    public String getNombre() { return nombre; }

    public List<Cliente> getListaClientes() { return Collections.unmodifiableList(listaClientes); }

    public List<Transaccion> getListaTransacciones() { return Collections.unmodifiableList(listaTransacciones); }

    public List<TransaccionProgramada> getListaProgramaciones() { return Collections.unmodifiableList(listaProgramaciones); }
}
