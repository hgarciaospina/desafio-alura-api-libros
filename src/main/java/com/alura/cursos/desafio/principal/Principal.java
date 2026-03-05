package com.alura.cursos.desafio.principal;

import com.alura.cursos.desafio.model.Datos;
import com.alura.cursos.desafio.model.DatosAutor;
import com.alura.cursos.desafio.model.DatosLibros;
import com.alura.cursos.desafio.service.ConsumoAPI;
import com.alura.cursos.desafio.service.ConvierteDatos;

import java.text.NumberFormat;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;

/**
 * ==========================================================
 * PRINCIPAL
 * ==========================================================
 *
 * Clase principal responsable de ejecutar la aplicación
 * desde consola e interactuar con el usuario.
 *
 * RESPONSABILIDADES
 *
 * ✔ Mostrar un menú interactivo
 * ✔ Consumir la API pública Gutendex
 * ✔ Convertir respuestas JSON a objetos Java
 * ✔ Procesar los datos obtenidos
 * ✔ Mostrar información de forma clara y estructurada
 *
 * FUNCIONALIDADES
 *
 * 1️⃣ Mostrar los 10 libros más descargados
 * 2️⃣ Buscar libros por título
 * 3️⃣ Mostrar estadísticas de descargas
 *
 * TECNOLOGÍAS UTILIZADAS
 *
 * ✔ Java Streams
 * ✔ Optional
 * ✔ DoubleSummaryStatistics
 * ✔ API HTTP Client
 * ✔ Programación funcional
 * ✔ Manejo defensivo de errores
 *
 */
public class Principal {

    private static final String URL_BASE = "https://gutendex.com/books/";

    private final ConsumoAPI consumoAPI = new ConsumoAPI();
    private final ConvierteDatos convierteDatos = new ConvierteDatos();

    private final Scanner teclado = new Scanner(System.in);

    /**
     * Método principal que ejecuta el menú del sistema.
     */
    public void muestraMenu() {

        int opcion;

        do {

            mostrarEncabezado();
            mostrarOpciones();

            opcion = leerOpcion();

            switch (opcion) {

                case 1 -> mostrarTop10Libros();

                case 2 -> buscarLibroPorTitulo();

                case 3 -> mostrarEstadisticasDescargas();

                case 0 -> System.out.println("\n👋 Gracias por usar el sistema.");

                default -> System.out.println("⚠ Opción inválida.");

            }

        } while (opcion != 0);
    }

    /**
     * Muestra el encabezado visual del sistema.
     */
    private void mostrarEncabezado() {

        System.out.println("\n============================================================");
        System.out.println("           SISTEMA DE CONSULTA DE LIBROS");
        System.out.println("                 GUTENDEX API");
        System.out.println("============================================================");
    }

    /**
     * Muestra las opciones disponibles para el usuario.
     */
    private void mostrarOpciones() {

        System.out.println("""
                1️⃣  Mostrar Top 10 libros más descargados
                2️⃣  Buscar libro por título
                3️⃣  Mostrar estadísticas de descargas
                0️⃣  Salir
                
                Seleccione una opción:
                """);
    }

    /**
     * Lee la opción ingresada por el usuario validando errores.
     */
    private int leerOpcion() {

        try {
            return Integer.parseInt(teclado.nextLine());
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * Consulta la API y retorna los datos obtenidos.
     */
    private Optional<Datos> obtenerDatosDesdeAPI(String url) {

        String json = consumoAPI.obtenerDatos(url);

        if (json == null || json.isBlank()) {

            System.out.println("\n❌ No fue posible obtener datos desde la API.");
            System.out.println("Verifique su conexión a internet.");

            return Optional.empty();
        }

        Datos datos = convierteDatos.obtenerDatos(json, Datos.class);

        if (datos == null || datos.resultados() == null) {

            System.out.println("\n⚠ La API respondió pero no retornó resultados.");

            return Optional.empty();
        }

        return Optional.of(datos);
    }

    /**
     * Muestra el TOP 10 de libros más descargados.
     */
    private void mostrarTop10Libros() {

        System.out.println("\n📊 CONSULTANDO DATOS...\n");

        Optional<Datos> datosOptional = obtenerDatosDesdeAPI(URL_BASE);

        if (datosOptional.isEmpty()) return;

        List<DatosLibros> libros = datosOptional.get().resultados();

        NumberFormat formato = NumberFormat.getInstance(new Locale("es", "CO"));

        System.out.println("============================================================");
        System.out.println("           TOP 10 LIBROS MÁS DESCARGADOS");
        System.out.println("============================================================");

        libros.stream()
                .sorted(Comparator.comparing(DatosLibros::numeroDescargas).reversed())
                .limit(10)
                .toList()
                .forEach(libro -> mostrarLibro(libro, formato));

        System.out.println("============================================================");
    }

    /**
     * Permite buscar un libro por coincidencia parcial del título.
     *
     * La búsqueda es CASE INSENSITIVE.
     */
    private void buscarLibroPorTitulo() {

        System.out.println("\n🔎 Ingrese parte del título del libro:");

        String tituloBusqueda = teclado.nextLine().trim();

        if (tituloBusqueda.isBlank()) {

            System.out.println("⚠ Debe ingresar un texto válido.");
            return;
        }

        String urlBusqueda = URL_BASE + "?search=" + tituloBusqueda.replace(" ", "+");

        Optional<Datos> datosOptional = obtenerDatosDesdeAPI(urlBusqueda);

        if (datosOptional.isEmpty()) return;

        Optional<DatosLibros> libroEncontrado = datosOptional.get()
                .resultados()
                .stream()
                .filter(libro ->
                        libro.titulo()
                                .toLowerCase()
                                .contains(tituloBusqueda.toLowerCase()))
                .findFirst();

        libroEncontrado.ifPresentOrElse(

                libro -> {

                    System.out.println("\n📚 LIBRO ENCONTRADO");
                    System.out.println("============================================================");

                    NumberFormat formato = NumberFormat.getInstance(new Locale("es", "CO"));

                    mostrarLibro(libro, formato);

                    System.out.println("============================================================");
                },

                () -> System.out.println("\n❌ No se encontró ningún libro con ese título.")
        );
    }

    /**
     * ==========================================================
     * ESTADÍSTICAS DE DESCARGAS
     * ==========================================================
     *
     * Utiliza DoubleSummaryStatistics para calcular:
     *
     * ✔ Número total de libros
     * ✔ Total de descargas
     * ✔ Promedio de descargas
     * ✔ Máximo número de descargas
     * ✔ Mínimo número de descargas
     */
    private void mostrarEstadisticasDescargas() {

        System.out.println("\n📊 GENERANDO ESTADÍSTICAS...\n");

        Optional<Datos> datosOptional = obtenerDatosDesdeAPI(URL_BASE);

        if (datosOptional.isEmpty()) return;

        List<DatosLibros> libros = datosOptional.get().resultados();

        DoubleSummaryStatistics estadisticas = libros.stream()
                .filter(libro -> libro.numeroDescargas() != null)
                .mapToDouble(DatosLibros::numeroDescargas)
                .summaryStatistics();

        NumberFormat formato = NumberFormat.getInstance(new Locale("es", "CO"));

        System.out.println("============================================================");
        System.out.println("             ESTADÍSTICAS DE DESCARGAS");
        System.out.println("============================================================");

        System.out.printf("📚 Total de libros analizados: %d%n",
                estadisticas.getCount());

        System.out.printf("⬇ Total de descargas: %s%n",
                formato.format(estadisticas.getSum()));

        System.out.printf("📊 Promedio de descargas: %s%n",
                formato.format(estadisticas.getAverage()));

        System.out.printf("🚀 Libro más descargado: %s%n",
                formato.format(estadisticas.getMax()));

        System.out.printf("📉 Libro menos descargado: %s%n",
                formato.format(estadisticas.getMin()));

        System.out.println("============================================================\n");
    }

    /**
     * Muestra la información formateada de un libro.
     */
    private void mostrarLibro(DatosLibros libro, NumberFormat formato) {

        System.out.println("------------------------------------------------------------");

        System.out.printf("📚 Título: %s%n", libro.titulo());

        if (libro.numeroDescargas() != null) {

            System.out.printf("⬇ Descargas: %s%n",
                    formato.format(libro.numeroDescargas()));
        }

        if (libro.idiomas() != null && !libro.idiomas().isEmpty()) {

            System.out.printf("🌎 Idiomas: %s%n",
                    String.join(", ", libro.idiomas()));
        }

        mostrarAutores(libro.autores());

        System.out.println("------------------------------------------------------------\n");
    }

    /**
     * Muestra la información de los autores de un libro.
     *
     * REGLAS:
     * ✔ Si el autor está vivo NO se muestra la fecha de muerte
     * ✔ Si no hay autores se indica apropiadamente
     */
    private void mostrarAutores(List<DatosAutor> autores) {

        if (autores == null || autores.isEmpty()) {

            System.out.println("Autor(es): Información no disponible");
            return;
        }

        System.out.println("Autor(es):");

        autores.forEach(autor -> {

            System.out.printf("   • %s%n", autor.nombre());

            if (autor.fechaNacimiento() != null) {

                System.out.printf("      Nacimiento: %s%n",
                        autor.fechaNacimiento());
            }

            if (autor.fechaMuerte() != null) {

                System.out.printf("      Fallecimiento: %s%n",
                        autor.fechaMuerte());
            }
        });
    }
}