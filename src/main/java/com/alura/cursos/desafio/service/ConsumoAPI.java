package com.alura.cursos.desafio.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * ==========================================================
 * ConsumoAPI
 * ==========================================================
 *
 * Clase responsable de realizar peticiones HTTP hacia APIs externas.
 *
 * En este proyecto se utiliza principalmente para consumir
 * la API pública de OMDb (Open Movie Database).
 *
 * RESPONSABILIDADES:
 * ✔ Construir una petición HTTP GET hacia una URL externa.
 * ✔ Ejecutar la petición utilizando HttpClient de Java.
 * ✔ Retornar el cuerpo de la respuesta en formato String (JSON).
 * ✔ Manejar correctamente excepciones de red o interrupciones.
 *
 * CARACTERÍSTICAS TÉCNICAS:
 *
 * - Utiliza el cliente HTTP nativo de Java (Java 11+)
 *      java.net.http.HttpClient
 *
 * - Maneja las excepciones:
 *      IOException
 *      InterruptedException
 *
 * - Implementa buenas prácticas:
 *      ✔ Restauración del estado de interrupción del hilo
 *      ✔ Manejo seguro de errores
 *      ✔ Validación de parámetros
 *
 * IMPORTANTE
 *
 * Esta clase NO procesa el JSON.
 * Solo realiza el consumo de la API.
 *
 * La conversión JSON → Objetos Java es responsabilidad
 * de la clase:
 *
 *      ConvierteDatos
 *
 * FLUJO DE USO EN EL PROYECTO
 *
 * Runner / Principal
 *        ↓
 *     ConsumoAPI
 *        ↓
 *   JSON (String)
 *        ↓
 *   ConvierteDatos
 *        ↓
 *   Objetos Java
 *
 */
public class ConsumoAPI {

    /**
     * Cliente HTTP reutilizable.
     *
     * Se define como atributo para evitar crear una nueva
     * instancia en cada petición.
     */
    private final HttpClient client = HttpClient.newHttpClient();


    /**
     * Realiza una petición HTTP GET hacia la URL especificada.
     *
     * @param url Dirección completa del endpoint a consumir.
     *
     * @return String con el cuerpo de la respuesta (JSON).
     *         Si ocurre un error se retorna null.
     *
     * EJEMPLO DE USO
     *
     * String json = consumoAPI.obtenerDatos(
     *      "https://www.omdbapi.com/?t=Breaking+Bad&apikey=123"
     * );
     *
     */
    public String obtenerDatos(String url) {

        // Validación básica de seguridad
        if (url == null || url.isBlank()) {
            System.out.println("⚠ URL inválida para consumo de API.");
            return null;
        }

        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();

        }
        catch (IOException e) {

            System.out.println("❌ Error de conexión al consumir la API.");
            System.out.println("Detalle: " + e.getMessage());

        }
        catch (InterruptedException e) {

            /*
             * Buena práctica:
             * restaurar el estado de interrupción del hilo.
             */
            Thread.currentThread().interrupt();

            System.out.println("⚠ La petición HTTP fue interrumpida.");
            System.out.println("Detalle: " + e.getMessage());

        }
        catch (Exception e) {

            System.out.println("❌ Error inesperado al consumir la API.");
            System.out.println("Detalle: " + e.getMessage());

        }

        // En caso de error se retorna null
        return null;
    }
}