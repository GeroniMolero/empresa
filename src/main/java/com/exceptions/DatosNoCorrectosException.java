package com.exceptions;

public class DatosNoCorrectosException extends Exception{
    /**
     * Excepcion que se usara durante el programa en caso de que algun dato no este
     * correcto
     * @param message 
     */
    public DatosNoCorrectosException(String message) {
        super(message);
    }
    
}
