# reporte-indices

*programa que ejecuta consultas contra una base de datos oracle y graba el resultado en excel*

la primera versión (tag 1.0) es un programa que genera un archivo excel con los indices de una base de datos oracle y grabar el resultado en un excel.

la segunda versión (tag 2.0) tiene una configuración en xml para ejecutar una consulta genérica y grabar los resultados en un excel. graba un archivo por cada condicion secundaria (que típicamente corresponde a un mes) y tiene una opción en el xml para grabar una pestaña por semana.

usa el thin client, osea que no lee el tnsnames, hay que poner en la configuración la dirección y el sid de la instancia a la que se va a conectar.

empezó siendo un programa para leer correos de una cuenta de google, por eso es que los primeros commits son sobre un lector de correos, pero luego de un commit errado se convirtió en el consultador de oracle.