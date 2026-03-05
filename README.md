# 📚 Sistema de Consulta de Libros -- Gutendex API (Java)

## 📖 Descripción del Proyecto

Aplicación Java de consola que consume la **API pública Gutendex** para
consultar libros del proyecto **Project Gutenberg**.

Permite:

-   Mostrar **Top 10 libros más descargados**
-   **Buscar libros por título**
-   Mostrar **estadísticas de descargas**
-   Visualizar **autores, idiomas y número de descargas**

El proyecto utiliza **Java Streams**, **programación funcional** y
buenas prácticas de manejo de errores.

------------------------------------------------------------------------

# 🌐 API Utilizada

## Gutendex API

https://gutendex.com/

Endpoint principal utilizado:

    https://gutendex.com/books/

Ejemplo de respuesta:

``` json
{
 "results":[
   {
     "title":"Pride and Prejudice",
     "authors":[
       {
         "name":"Jane Austen",
         "birth_year":1775,
         "death_year":1817
       }
     ],
     "languages":["en"],
     "download_count":48291
   }
 ]
}
```

------------------------------------------------------------------------

# 🏗 Arquitectura

    principal
    model
    service

## principal

Clase **Principal.java**

Responsabilidades:

-   Mostrar menú
-   Consumir servicios
-   Procesar datos con Streams
-   Mostrar resultados formateados

------------------------------------------------------------------------

## model

### Datos

Representa la respuesta principal del JSON.

### DatosLibros

Representa un libro:

-   título
-   autores
-   idiomas
-   número de descargas

### DatosAutor

Representa un autor:

-   nombre
-   año de nacimiento
-   año de muerte

Regla:

Si el autor está vivo **no se muestra fecha de muerte**.

------------------------------------------------------------------------

## service

### ConsumoAPI

Consume la API usando:

-   HttpClient
-   HttpRequest
-   HttpResponse

Método:

    String obtenerDatos(String url)

------------------------------------------------------------------------

### ConvierteDatos

Convierte JSON a objetos Java usando **Jackson**.

Método:

    <T> T obtenerDatos(String json, Class<T> clase)

------------------------------------------------------------------------

# ⚙ Funcionalidades

## 1 Top 10 libros más descargados

Uso de Streams:

``` java
libros.stream()
.sorted(Comparator.comparing(DatosLibros::numeroDescargas).reversed())
.limit(10)
.toList();
```

Proceso:

1.  Convertir lista a Stream
2.  Ordenar por descargas
3.  Limitar a 10 resultados

------------------------------------------------------------------------

# 🔎 2 Buscar libro por título

Ejemplo de URL:

    https://gutendex.com/books/?search=romeo

Implementación:

``` java
Optional<DatosLibros> libro =
libros.stream()
.filter(l -> l.titulo().toLowerCase().contains(texto))
.findFirst();
```

------------------------------------------------------------------------

# 📊 3 Estadísticas con Streams

Uso de:

    DoubleSummaryStatistics

Implementación:

``` java
DoubleSummaryStatistics stats =
libros.stream()
.mapToDouble(DatosLibros::numeroDescargas)
.summaryStatistics();
```

Permite obtener:

-   Total
-   Suma
-   Promedio
-   Máximo
-   Mínimo

Métodos:

    getCount()
    getSum()
    getAverage()
    getMax()
    getMin()

------------------------------------------------------------------------

# 🧠 Streams usados

  Operación      Método
  -------------- -------------------
  Ordenar        sorted
  Filtrar        filter
  Limitar        limit
  Buscar         findFirst
  Estadísticas   summaryStatistics

------------------------------------------------------------------------

# 🛡 Buenas prácticas

✔ Manejo de errores\
✔ Validación de API\
✔ Uso de Optional\
✔ Streams funcionales\
✔ Código documentado

------------------------------------------------------------------------

# 🚀 Ejecución

Compilar:

    mvn clean install

Ejecutar:

    java Main

------------------------------------------------------------------------

# 📌 Tecnologías

-   Java 17
-   Java Streams
-   Jackson
-   HTTP Client
-   REST API

------------------------------------------------------------------------

# 👨‍💻 Autor: Henry García Ospina

Proyecto académico para práctica de consumo de APIs con Java.
