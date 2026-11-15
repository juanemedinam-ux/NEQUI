
package co.edu.uniquindio.edu.poo.nequi.model;

import co.edu.uniquindio.edu.poo.nequi.exceptions.SaldoInsuficienteException;

import java.math.BigDecimal;
import java.util.Objects;

public class Monedero {

    private String codigo;
    private TipoMonedero tipo;
    private BigDecimal saldo;

    public Monedero(String codigo, TipoMonedero tipo) {

        if (codigo == null || codigo.trim().isEmpty() || tipo == null) {

            throw new IllegalArgumentException("Codigo y tipo son obligatorios");
        }

        this.codigo = codigo.trim();
        this.tipo = tipo;
        this.saldo = new BigDecimal("0.00");
    }

    // AUMENTAR SALDO
    void aumentarSaldo(BigDecimal monto) {

        if (monto == null || monto.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Monto invalido");
        }

        saldo = saldo.add(monto);
    }

    // DISMINUIR SALDO
    void disminuirSaldo(BigDecimal monto) {

        if (monto == null || monto.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Monto invalido");
        }

        if (saldo.compareTo(monto) < 0) {

            throw new SaldoInsuficienteException(
                    "El monedero no tiene saldo suficiente");
        }

        saldo = saldo.subtract(monto);
    }

    public String getCodigo() {
        return codigo;
    }

    public TipoMonedero getTipo() {
        return tipo;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    @Override
    public String toString() {

        return "Monedero{" +
                "codigo='" + codigo + '\'' +
                ", tipo=" + tipo +
                ", saldo=" + saldo +
                '}';
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (o == null || getClass() != o.getClass())
            return false;

        Monedero monedero = (Monedero) o;

        return Objects.equals(codigo, monedero.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(codigo);
    }
}
