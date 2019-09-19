# Sistema de votacion

Trabajo practico especial realizado para la materia Programacion de objetos distribuidos.

## Instrucciones

Instalar Maven


	$ sudo apt-get install maven


Clonar el repositorio

	$ git clone https://github.com/BRitorto/TP-POD

Desde la carpeta raiz ejecutar

	$ ./run.sh

## Clientes

Para correr cada cliente se requiere una serie de parametros.

	$ cd client/target/rmi-client-1.0-SNAPSHOT

Cliente de fiscalizacion

	$ ./run-fiscal.sh -serverAddress=xx.xx.xx.xx:yyyy -id=​pollingPlaceNumber -party=​partyName​

Cliente de votacion

	$ ./run-vote.sh -serverAddress=​xx.xx.xx.xx:yyyy​ -csvPath=​fileName

Cliente de consulta

	$ ./run-query.sh -serverAddress=​xx.xx.xx.xx:yyyy​ [ -state=​stateName​ |-id=​pollingPlaceNumber​ ] -outPath=​fileName​

Cliente de administracion:

	$ ./run-management.sh -serverAddress=​xx.xx.xx.xx:yyyy​ -action=​actionName

## Integrantes
  - Martina Scomazzon
  - Bianca Ritorto
  - Esteban Kramer
  - Oliver Balfour


## License
[MIT](https://choosealicense.com/licenses/mit/)