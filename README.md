LinksManagerSubadictos

** Documento en construccion **

LinksManagerSubadictos es un programa pensado para facilitar el seguimiento de series de TV, mediante la
automatizacion de la obtencion de links de descarga para descargas P2P, utilizando como fuente el sitio
http://www.subadictos.com

Puede ser utilizado por linea de comandos, interactivamente o puede ser invocado programaticamente via RMI.

Usos:

**General**

java -jar LinksManagerSubadictos.jar addSus|delSus|listSeries|listSus|getNewLinks \<arg\>

**Visualizacion y busqueda de series disponibles**

java -jar LinksManagerSubadictos.jar listSeries \<filtro\>

donde \<filtro\> puede ser el nombre exacto de la serie o una parte del nombre, utilizando el caracter * al
final del mismo. Pueden incluirse multiples palatras, en este caso, entre comillas dobles.

**Suscribirse a una serie**

java -jar LinksManagerSubadictos.jar addSus \<serie\>

donde \<serie\> debe ser el nombre exacto (NO INCLUIR *) de la serie como figura en el resultado de una
busqueda.

**Eliminar una suscripcion**

java -jar LinksManagerSubadictos.jar delSus \<serie\>

**Ver las suscripciones activas**

java -jar LinksManagerSubadictos.jar listSus \<serie\>

**Obtener nuevos links de todas las series suscriptas**

java -jar LinksManagerSubadictos.jar getNewLinks \<tipo_link\>

donde \<tipo_link\> es el tipo de link P2P que se quiere obtener, con las siguientes opciones:

 \<tipo_link\> := [ed2k|torrent|*]