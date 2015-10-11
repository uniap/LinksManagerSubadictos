##LinksManagerSubadictos

LinksManagerSubadictos es un programa gratuito pensado para facilitar el seguimiento particula de series de TV, mediante la
automatizacion de la obtencion de links para descargas P2P, utilizando como fuente el sitio
http://www.subadictos.com

Puede ser utilizado por linea de comandos, interactivamente o puede ser invocado programaticamente via RMI.


###Sobre los links

Un link dentro de una página web (denominado también enlace, vínculo, hipervínculo o, hiperenlace) es un elemento que hace referencia a otro recurso.
Los links son simples enlaces que direccionan hacia cierta información o activan determinados contenidos, pero que en ningún caso constituyen reproducciones de los contenidos a los cuales enlaza.
LinksManagerSubadictos es un programa que brinda información sobre enlaces ed2k o torrent. Tales enlaces son de libre circulación por Internet y accesibles desde cualquier buscador.
LinksManagerSubadictos no almacena películas, series, ni ningún contenido ilegal.
Un link no vulnera el derecho de reproducción; las direcciones URL son meros hechos que no están protegidos por el derecho de autor por no implicar la realización de una copia de una obra.


###Usos:

####Sintaxis general

java -jar LinksManagerSubadictos.jar addSus|delSus|listSeries|listSus|getNewLinks \<arg\>

####Visualizacion y busqueda de series disponibles

java -jar LinksManagerSubadictos.jar listSeries \<filtro\>

donde \<filtro\> puede ser el nombre exacto de la serie o una parte del nombre, utilizando el caracter % al
final del mismo. Pueden incluirse multiples palatras, en este caso, entre comillas dobles.

####Suscribirse a una serie

java -jar LinksManagerSubadictos.jar addSus \<serie\>

donde \<serie\> debe ser el nombre exacto (NO INCLUIR %) de la serie como figura en el resultado de una
busqueda.

Luego de suscribir una serie, se recibiran los nuevos links publicados para la misma via "getNewLinks"

####Eliminar una suscripcion

java -jar LinksManagerSubadictos.jar delSus \<serie\>

No se recibiran nuevos links de la serie especificada.

####Ver las suscripciones activas

java -jar LinksManagerSubadictos.jar listSus \<serie\>

Muestra la lista de series que estan suscriptas

####Obtener nuevos links de todas las series suscriptas

java -jar LinksManagerSubadictos.jar getNewLinks \<tipo_link\>

donde \<tipo_link\> es el tipo de link P2P que se quiere obtener, con las siguientes opciones:

 \<tipo_link\> := [ ed2k | torrent | % ]

Ejecuta la busqueda de nuevos links para cada una de las series suscriptas para su incorporacion
en algun programa de descargas P2P.