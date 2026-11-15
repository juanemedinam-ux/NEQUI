

package co.edu.uniquindio.edu.poo.nequi.model;

import co.edu.uniquindio.edu.poo.nequi.exceptions.SaldoInsuficienteException;
import co.edu.uniquindio.edu.poo.nequi.exceptions.PuntosInsuficientesException;
import co.edu.uniquindio.edu.poo.nequi.exceptions.OperacionInvalidaException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class MonederoVirtualTest {

    private static final Logger LOG =
            Logger.getLogger(MonederoVirtualTest.class.getName());

    // Objetos que vamos a reutilizar
    private MonederoVirtual nequi;
    private Cliente ana;
    private Cliente carlos;

    // =========================================
    // METODO PARA INICIALIZAR LOS DATOS
    // =========================================

    @BeforeEach
    public void setUp() {

        nequi = new MonederoVirtual("NEQUI");

        ana = new Cliente(
                "Ana", "Arias", "123",
                "ana@correo.com", "3001234567"
        );

        carlos = new Cliente(
                "Carlos", "Perez", "456",
                "carlos@correo.com", "3009876543"
        );

        nequi.registrarCliente(ana);
        nequi.registrarCliente(carlos);

        nequi.crearMonedero(
                "123", "M01", TipoMonedero.AHORRO
        );

        nequi.crearMonedero(
                "456", "M02", TipoMonedero.GASTOS
        );
    }

    // =========================================
    // 1. REGISTRAR CLIENTE DUPLICADO
    // =========================================

    @Test
    void registrarClienteDuplicadoTest() {

        LOG.info("Inicio registrarClienteDuplicadoTest");

        assertThrows(
                IllegalArgumentException.class,
                () -> nequi.registrarCliente(
                        new Cliente(
                                "Pedro", "Diaz", "123",
                                "pedro@correo.com", "3001111111"
                        )
                )
        );

        assertEquals(2, nequi.getListaClientes().size());

        LOG.info("Fin registrarClienteDuplicadoTest");
    }

    // =========================================
    // 2. CREAR MONEDERO
    // =========================================

    @Test
    void crearMonederoTest() {

        LOG.info("Inicio crearMonederoTest");

        nequi.crearMonedero(
                "123", "M03", TipoMonedero.GASTOS
        );

        Monedero monedero =
                nequi.buscarMonedero("M03");

        assertNotNull(monedero);

        assertEquals(
                TipoMonedero.GASTOS,
                monedero.getTipo()
        );

        assertEquals(
                new BigDecimal("0.00"),
                monedero.getSaldo()
        );

        LOG.info("Fin crearMonederoTest");
    }

    // =========================================
    // 3. REALIZAR DEPOSITO
    // =========================================

    @Test
    void depositoTest() {

        LOG.info("Inicio depositoTest");

        nequi.depositar(
                "M01", new BigDecimal("1000")
        );

        assertEquals(
                new BigDecimal("1000.00"),
                nequi.consultarSaldo("M01")
        );

        assertEquals(
                1,
                nequi.consultarHistorial("M01").size()
        );

        LOG.info("Fin depositoTest");
    }

    // =========================================
    // 4. REALIZAR RETIRO
    // =========================================

    @Test
    void retiroTest() {

        LOG.info("Inicio retiroTest");

        nequi.depositar(
                "M01", new BigDecimal("1000")
        );

        nequi.retirar(
                "M01", new BigDecimal("250")
        );

        assertEquals(
                new BigDecimal("750.00"),
                nequi.consultarSaldo("M01")
        );

        assertEquals(
                2,
                nequi.consultarHistorial("M01").size()
        );

        LOG.info("Fin retiroTest");
    }

    // =========================================
    // 5. RETIRO SIN SALDO SUFICIENTE
    // =========================================

    @Test
    void retiroSinSaldoTest() {

        LOG.info("Inicio retiroSinSaldoTest");

        assertThrows(
                SaldoInsuficienteException.class,
                () -> nequi.retirar(
                        "M01", new BigDecimal("100")
                )
        );

        assertEquals(
                new BigDecimal("0.00"),
                nequi.consultarSaldo("M01")
        );

        assertEquals(
                0,
                nequi.getListaTransacciones().size()
        );

        LOG.info("Fin retiroSinSaldoTest");
    }

    // =========================================
    // 6. REALIZAR TRANSFERENCIA
    // =========================================

    @Test
    void transferenciaTest() {

        LOG.info("Inicio transferenciaTest");

        nequi.depositar(
                "M01", new BigDecimal("1000")
        );

        nequi.transferir(
                "M01", "M02", new BigDecimal("350")
        );

        assertEquals(
                new BigDecimal("650.00"),
                nequi.consultarSaldo("M01")
        );

        assertEquals(
                new BigDecimal("350.00"),
                nequi.consultarSaldo("M02")
        );

        LOG.info("Fin transferenciaTest");
    }

    // =========================================
    // 7. TRANSFERENCIA AL MISMO MONEDERO
    // =========================================

    @Test
    void transferenciaAlMismoMonederoTest() {

        LOG.info("Inicio transferenciaAlMismoMonederoTest");

        nequi.depositar(
                "M01", new BigDecimal("100")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> nequi.transferir(
                        "M01", "M01", new BigDecimal("50")
                )
        );

        assertEquals(
                new BigDecimal("100.00"),
                nequi.consultarSaldo("M01")
        );

        LOG.info("Fin transferenciaAlMismoMonederoTest");
    }

    // =========================================
    // 8. VALIDAR MONTO INVALIDO
    // =========================================

    @Test
    void montoInvalidoTest() {

        LOG.info("Inicio montoInvalidoTest");

        assertThrows(
                IllegalArgumentException.class,
                () -> nequi.depositar(
                        "M01", new BigDecimal("0")
                )
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> nequi.depositar(
                        "M01", new BigDecimal("1.999")
                )
        );

        assertEquals(
                new BigDecimal("0.00"),
                nequi.consultarSaldo("M01")
        );

        LOG.info("Fin montoInvalidoTest");
    }

    // =========================================
    // 9. CANJEAR BENEFICIO SIN PUNTOS
    // =========================================

    @Test
    void canjeSinPuntosTest() {

        LOG.info("Inicio canjeSinPuntosTest");

        assertThrows(
                PuntosInsuficientesException.class,
                () -> nequi.canjearBeneficio(
                        "123",
                        "M01",
                        Beneficio.BONO_SALDO
                )
        );

        assertEquals(
                0,
                nequi.consultarPuntos("123")
        );

        assertEquals(
                new BigDecimal("0.00"),
                nequi.consultarSaldo("M01")
        );

        LOG.info("Fin canjeSinPuntosTest");
    }

    // =========================================
    // 10. FECHA DE PROGRAMACION INVALIDA
    // =========================================

    @Test
    void fechaProgramacionInvalidaTest() {

        LOG.info("Inicio fechaProgramacionInvalidaTest");

        LocalDateTime fechaPasada =
                LocalDateTime.now().minusDays(1);

        assertThrows(
                OperacionInvalidaException.class,
                () -> nequi.programarDeposito(
                        "M01",
                        new BigDecimal("100"),
                        fechaPasada,
                        FrecuenciaProgramacion.UNICA
                )
        );

        assertEquals(
                0,
                nequi.getListaProgramaciones().size()
        );

        LOG.info("Fin fechaProgramacionInvalidaTest");
    }
}
