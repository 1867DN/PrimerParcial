package PrimerParcial.dao;

import java.util.List;

public interface GenericDAO<T> {
    void crear(T t) throws Exception;
    T buscarPorId(int id) throws Exception;
    List<T> listar() throws Exception;
    void actualizar(T t) throws Exception;
    void eliminar(int id) throws Exception;
}