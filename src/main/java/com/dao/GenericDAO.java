package com.dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz genérica para operaciones CRUD básicas (Patrón DAO).
 * Define el contrato que deben cumplir los DAOs del sistema.
 * 
 * @param <T> Tipo de entidad que maneja el DAO
 * @param <K> Tipo de la clave primaria de la entidad
 */
public interface GenericDAO<T, K> {
    
    /**
     * Obtiene una entidad por su identificador
     * @param id Identificador de la entidad
     * @return Entidad encontrada o null si no existe
     * @throws SQLException si hay error en la base de datos
     */
    T obtenerPorId(K id) throws SQLException;
    
    /**
     * Lista todas las entidades
     * @return Lista de entidades
     * @throws SQLException si hay error en la base de datos
     */
    List<T> listarTodos() throws SQLException;
    
    /**
     * Guarda una nueva entidad
     * @param entidad Entidad a guardar
     * @return true si se guardó correctamente
     * @throws SQLException si hay error en la base de datos
     */
    boolean guardar(T entidad) throws SQLException;
    
    /**
     * Actualiza una entidad existente
     * @param entidad Entidad a actualizar
     * @return true si se actualizó correctamente
     * @throws SQLException si hay error en la base de datos
     */
    boolean actualizar(T entidad) throws SQLException;
    
    /**
     * Elimina una entidad por su identificador
     * @param id Identificador de la entidad a eliminar
     * @return true si se eliminó correctamente
     * @throws SQLException si hay error en la base de datos
     */
    boolean eliminar(K id) throws SQLException;
}
