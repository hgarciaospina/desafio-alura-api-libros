package com.alura.cursos.desafio.service;

public interface IConvierteDatos {

    <T> T obtenerDatos(String json, Class<T>  clase);
}
