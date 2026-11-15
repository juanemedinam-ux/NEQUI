
package co.edu.uniquindio.edu.poo.nequi.model;

public class Beneficio {

    public static final Beneficio DESCUENTO_TRANSFERENCIA =
            new Beneficio("DESCUENTO_TRANSFERENCIA",
                    "10% de descuento en comision de transferencia", 100);

    public static final Beneficio RETIROS_SIN_CARGO = new Beneficio("RETIROS_SIN_CARGO",
            "Un mes sin comision de retiro", 500);

    public static final Beneficio BONO_SALDO = new Beneficio("BONO_SALDO",
            "Bono de 50 unidades de saldo", 1000);

    private String codigo;
    private String nombre;
    private int costoPuntos;

    private Beneficio(String codigo, String nombre, int costoPuntos) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.costoPuntos = costoPuntos;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCostoPuntos() {
        return costoPuntos;
    }

    @Override
    public String toString() {

        return nombre + " (" + costoPuntos + " puntos)";
    }
}
