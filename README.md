# Sistema de votacion

Trabajo practico especial realizado para la materia Programacion de Objetos Distribuidos.

Sistema remoto thread-safe para realizar una elección, contabilizando votos, definiendo a los ganadores y permitiendo la fiscalización de los mismos.

# Instrucciones #

Instalar Maven

	$ sudo apt-get install maven

### Correr servidor y registry ##
Descomprimir el código fuente en en el Desktop si se quiere usar el script que corre el registry y el server automáticamente.

Desde la carpeta raiz ejecutar (En MAC OSX)

	$ ./run.sh

En Linux

    $ ./run-linux.sh

Si se ejecutan por separado, se debe correr en una terminal:

    $ ./run-registry.sh
    
Y en otra:

    $./run-server.sh
    
## Correr Clientes ##
Para correr cada client se tiene que parar en la carpeta descomprimida dentro de:
/client/target/rmi-client-1.0-SNAPSHOT 

### Management client:

    $./run-management.sh -serverAddress={ip}:{port} -actionName={action}

ip: 127.0.0.1

puerto: 0

action: open | state | close

### Voting client:

    $./run-vote.sh -serverAddress={ip}:{port} -votesPath={filepath}

ip: 127.0.0.1

puerto: 0

filepath: debe ser el filepath absoluto de donde cargar el archivo .csv con los votos

### Fiscal client:

    $./run-fiscal.sh -serverAddress={ip}:{port} -id={id} -party={party}

ip: 127.0.0.1

puerto: 0

id: el id de la mesa en la que se registra el fiscal

party: el partido del fiscal

### Query client:

    $./run-query.sh -serverAddress={ip}:{port} -state={state} -id={id} -outPath={filepath}

ip: 127.0.0.1

puerto: 0

state: si se quiere consultar el estado de la votación por estado se debe escribir el nombre del estado.

id: si en cambio se quiere consultar el estado de la votación por mesa, se debe escribir el id de lla. 

**Si no se completa el id y state, se consulta el estado de la votación nacional**

party: el partido del fiscal

Ejemplo:

    $./run-query.sh -serverAddress=127.0.0.1 -outPath=./result.csv

# Integrantes
  - Martina Scomazzon
  - Bianca Ritorto
  - Esteban Kramer
  - Oliver Balfour

## License
[MIT](https://choosealicense.com/licenses/mit/)
