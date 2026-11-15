
package co.edu.uniquindio.edu.poo.nequi.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Cliente {

    private String nombre;
    private String apellidos;
    private String identificacion;
    private String correo;
    private String telefono;
    private List<Monedero> listaMonederos;
    private SistemaPuntos sistemaPuntos;

    public Cliente(String nombre, String apellidos, String identificacion, String correo, String telefono) {

        if (esVacio(nombre) || esVacio(apellidos) || esVacio(identificacion) || esVacio(correo) || esVacio(telefono)) {

            throw new IllegalArgumentException("Todos los datos del cliente son obligatorios");
        }

        this.nombre = nombre.trim();
        this.apellidos = apellidos.trim();
        this.identificacion = identificacion.trim();
        this.correo = correo.trim();
        this.telefono = telefono.trim();

        this.listaMonederos = new ArrayList<>();
        this.sistemaPuntos = new SistemaPuntos();
    }

    private boolean esVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    // AGREGAR MONEDERO
    public void agregarMonedero(Monedero monedero) {

        if (monedero == null || buscarMonedero(monedero.getCodigo()) != null) {

            throw new IllegalArgumentException("Monedero invalido o repetido");
        }

        listaMonederos.add(monedero);
    }

    // BUSCAR MONEDERO
    public Monedero buscarMonedero(String codigo) {

        return listaMonederos.stream().filter(monedero -> monedero.getCodigo().equals(codigo)).findFirst().orElse(null);
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {

        if (esVacio(nombre)) {
            throw new IllegalArgumentException("Nombre invalido");
        }

        this.nombre = nombre.trim();
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {

        if (esVacio(apellidos)) {
            throw new IllegalArgumentException("Apellidos invalidos");
        }

        this.apellidos = apellidos.trim();
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {

        if (esVacio(correo)) {
            throw new IllegalArgumentException("Correo invalido");
        }

        this.correo = correo.trim();
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {

        if (esVacio(telefono)) {
            throw new IllegalArgumentException("Telefono invalido");
        }

        this.telefono = telefono.trim();
    }

    public SistemaPuntos getSistemaPuntos() {
        return sistemaPuntos;
    }

    public List<Monedero> getListaMonederos() {

        return Collections.unmodifiableList(listaMonederos);
    }

    @Override
    public String toString() {

        return "Cliente{" +
                "nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", identificacion='" + identificacion + '\'' +
                ", correo='" + correo + '\'' +
                ", telefono='" + telefono + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Cliente cliente = (Cliente) o;

        return Objects.equals(
                identificacion, cliente.identificacion);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identificacion);
    }
}
