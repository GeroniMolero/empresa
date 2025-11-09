package com.conexion;
 
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
 
import javax.sql.DataSource;
 
import org.apache.commons.dbcp2.BasicDataSource;
 
/**
 * Patrón Singleton (thread-safe) para gestión de conexiones a base de datos.
 * Implementa inicialización perezosa con doble verificación.
 * Configuración externalizada desde application.properties.
 */
public class Conexion {
    
    // Instancia única del DataSource (volatile para thread-safety)
    private static volatile BasicDataSource dataSource = null;
    
    // Propiedades de configuración
    private static Properties properties = null;
    
    // Constructor privado para evitar instanciación
    private Conexion() {
        throw new IllegalStateException("Clase de utilidad - no instanciable");
    }
    
    /**
     * Carga las propiedades de configuración desde application.properties
     * @return Properties con la configuración
     */
    private static Properties loadProperties() {
        if (properties == null) {
            synchronized (Conexion.class) {
                if (properties == null) {
                    properties = new Properties();
                    try (InputStream input = Conexion.class.getClassLoader()
                            .getResourceAsStream("application.properties")) {
                        if (input == null) {
                            throw new RuntimeException("No se encontró application.properties");
                        }
                        properties.load(input);
                    } catch (IOException ex) {
                        throw new RuntimeException("Error cargando configuración", ex);
                    }
                }
            }
        }
        return properties;
    }
    
    /**
     * Obtiene el DataSource único (Singleton con double-check locking)
     * Configuración desde application.properties
     * @return DataSource configurado
     */
    private static DataSource getDataSource() {
        if (dataSource == null) {
            synchronized (Conexion.class) {
                if (dataSource == null) {
                    Properties props = loadProperties();
                    
                    dataSource = new BasicDataSource();
                    dataSource.setDriverClassName(props.getProperty("db.driver"));
                    dataSource.setUsername(props.getProperty("db.username"));
                    dataSource.setPassword(props.getProperty("db.password"));
                    dataSource.setUrl(props.getProperty("db.url"));
                    
                    // Pool configuration
                    dataSource.setInitialSize(Integer.parseInt(
                        props.getProperty("db.pool.initialSize", "5")));
                    dataSource.setMaxIdle(Integer.parseInt(
                        props.getProperty("db.pool.maxIdle", "10")));
                    dataSource.setMaxTotal(Integer.parseInt(
                        props.getProperty("db.pool.maxTotal", "20")));
                    dataSource.setMinIdle(Integer.parseInt(
                        props.getProperty("db.pool.minIdle", "5")));
                    dataSource.setMaxWaitMillis(5000);
                }
            }
        }
        return dataSource;
    }
    
    /**
     * Obtiene una conexión del pool
     * @return Connection
     * @throws SQLException si hay error al obtener la conexión
     */
    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }
    
    /**
     * Cierra el DataSource y libera recursos
     * @throws SQLException si hay error al cerrar
     */
    public static void closeDataSource() throws SQLException {
        if (dataSource != null) {
            synchronized (Conexion.class) {
                if (dataSource != null) {
                    dataSource.close();
                    dataSource = null;
                }
            }
        }
    }
}
