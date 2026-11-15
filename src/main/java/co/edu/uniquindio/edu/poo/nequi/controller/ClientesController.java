
package co.edu.uniquindio.edu.poo.nequi.controller;

import co.edu.uniquindio.edu.poo.nequi.model.Cliente;
import co.edu.uniquindio.edu.poo.nequi.model.Monedero;
import co.edu.uniquindio.edu.poo.nequi.model.MonederoVirtual;
import co.edu.uniquindio.edu.poo.nequi.model.TipoMonedero;

import java.util.List;

public class ClientesController {

    private MonederoVirtual nequi;

    // CONSTRUCTOR
    public ClientesController(MonederoVirtual nequi) {

        this.nequi = nequi;
    }

    // REGISTRAR CLIENTE
    public void registrarCliente(
            String nombre,
            String apellidos,
            String identificacion,
            String correo,
            String telefono) {

        Cliente cliente = new Cliente(
                nombre,
                apellidos,
                identificacion,
                correo,
                telefono
        );

        nequi.registrarCliente(cliente);
    }

    // CREAR MONEDERO
    public void crearMonedero(
            String identificacion,
            String codigo,
            TipoMonedero tipo) {

        nequi.crearMonedero(
                identificacion,
                codigo,
                tipo
        );
    }

    // CONSULTAR CLIENTES
    public List<Cliente> obtenerClientes() {

        return nequi.getListaClientes();
    }

    // CONSULTAR MONEDEROS DE UN CLIENTE
    public List<Monedero> obtenerMonederos(
            String identificacion) {

        Cliente cliente =
                nequi.buscarCliente(identificacion);

        if (cliente == null) {

            throw new IllegalArgumentException(
                    "No existe el cliente");
        }

        return cliente.getListaMonederos();
    }
}
