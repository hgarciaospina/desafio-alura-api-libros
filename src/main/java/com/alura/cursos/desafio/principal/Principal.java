package com.alura.cursos.desafio.principal;

import com.alura.cursos.desafio.model.Datos;
import com.alura.cursos.desafio.service.ConsumoAPI;
import com.alura.cursos.desafio.service.ConvierteDatos;
public class Principal {
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConvierteDatos convierteDatos = new ConvierteDatos();
    public void muestraMenu(){
        var json = consumoAPI.obtenerDatos(URL_BASE);
        System.out.println(json);
        var datos = convierteDatos.obtenerDatos(json, Datos.class);
        System.out.println(datos);
    }
}
