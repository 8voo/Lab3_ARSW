
### Escuela Colombiana de Ingeniería
### Arquitecturas de Software - ARSW
### Enrique Gonzales
### Sergio Otero
## Ejercicio Introducción al paralelismo - Hilos - Caso BlackListSearch


### Dependencias:
####   Lecturas:
*  [Threads in Java](http://beginnersbook.com/2013/03/java-threads/)  (Hasta 'Ending Threads')
*  [Threads vs Processes]( http://cs-fundamentals.com/tech-interview/java/differences-between-thread-and-process-in-java.php)

### Descripción
  Este ejercicio contiene una introducción a la programación con hilos en Java, además de la aplicación a un caso concreto.
  

**Parte I - Introducción a Hilos en Java**

1. De acuerdo con lo revisado en las lecturas, complete las clases CountThread, para que las mismas definan el ciclo de vida de un hilo que imprima por pantalla los números entre A y B.
2. Complete el método __main__ de la clase CountMainThreads para que:
    1. Cree 3 hilos de tipo CountThread, asignándole al primero el intervalo [0..99], al segundo [99..199], y al tercero [200..299].
    2. Inicie los tres hilos con 'start()'.
    3. Ejecute y revise la salida por pantalla. 
    4. Cambie el incio con 'start()' por 'run()'. Cómo cambia la salida?, por qué?.
   - Cuando usamos Start(), las funciones corren al tiempo, por lo tanto, los output salen en un orden distinto al esperado. 
   ![image](https://user-images.githubusercontent.com/98104282/185035283-5bf87237-f5b6-4380-a06e-e992f211ee33.png)


   - Mientras que con run() se corren todos en el orden dado.
   ![image](https://user-images.githubusercontent.com/98104282/185035481-bd4408ac-7b2a-4b1f-869a-54e686ed42eb.png)


**Parte II - Ejercicio Black List Search**


Para un software de vigilancia automática de seguridad informática se está desarrollando un componente encargado de validar las direcciones IP en varios miles de listas negras (de host maliciosos) conocidas, y reportar aquellas que existan en al menos cinco de dichas listas. 

Dicho componente está diseñado de acuerdo con el siguiente diagrama, donde:

- HostBlackListsDataSourceFacade es una clase que ofrece una 'fachada' para realizar consultas en cualquiera de las N listas negras registradas (método 'isInBlacklistServer'), y que permite también hacer un reporte a una base de datos local de cuando una dirección IP se considera peligrosa. Esta clase NO ES MODIFICABLE, pero se sabe que es 'Thread-Safe'.

- HostBlackListsValidator es una clase que ofrece el método 'checkHost', el cual, a través de la clase 'HostBlackListDataSourceFacade', valida en cada una de las listas negras un host determinado. En dicho método está considerada la política de que al encontrarse un HOST en al menos cinco listas negras, el mismo será registrado como 'no confiable', o como 'confiable' en caso contrario. Adicionalmente, retornará la lista de los números de las 'listas negras' en donde se encontró registrado el HOST.

![](img/Model.png)

Al usarse el módulo, la evidencia de que se hizo el registro como 'confiable' o 'no confiable' se dá por lo mensajes de LOGs:

INFO: HOST 205.24.34.55 Reported as trustworthy

INFO: HOST 205.24.34.55 Reported as NOT trustworthy


Al programa de prueba provisto (Main), le toma sólo algunos segundos análizar y reportar la dirección provista (200.24.34.55), ya que la misma está registrada más de cinco veces en los primeros servidores, por lo que no requiere recorrerlos todos. Sin embargo, hacer la búsqueda en casos donde NO hay reportes, o donde los mismos están dispersos en las miles de listas negras, toma bastante tiempo.

Éste, como cualquier método de búsqueda, puede verse como un problema [vergonzosamente paralelo](https://en.wikipedia.org/wiki/Embarrassingly_parallel), ya que no existen dependencias entre una partición del problema y otra.

Para 'refactorizar' este código, y hacer que explote la capacidad multi-núcleo de la CPU del equipo, realice lo siguiente:

1. Cree una clase de tipo Thread que represente el ciclo de vida de un hilo que haga la búsqueda de un segmento del conjunto de servidores disponibles. Agregue a dicha clase un método que permita 'preguntarle' a las instancias del mismo (los hilos) cuantas ocurrencias de servidores maliciosos ha encontrado o encontró.

2. Agregue al método 'checkHost' un parámetro entero N, correspondiente al número de hilos entre los que se va a realizar la búsqueda (recuerde tener en cuenta si N es par o impar!). Modifique el código de este método para que divida el espacio de búsqueda entre las N partes indicadas, y paralelice la búsqueda a través de N hilos. Haga que dicha función espere hasta que los N hilos terminen de resolver su respectivo sub-problema, agregue las ocurrencias encontradas por cada hilo a la lista que retorna el método, y entonces calcule (sumando el total de ocurrencuas encontradas por cada hilo) si el número de ocurrencias es mayor o igual a _BLACK_LIST_ALARM_COUNT_. Si se da este caso, al final se DEBE reportar el host como confiable o no confiable, y mostrar el listado con los números de las listas negras respectivas. Para lograr este comportamiento de 'espera' revise el método [join](https://docs.oracle.com/javase/tutorial/essential/concurrency/join.html) del API de concurrencia de Java. Tenga también en cuenta:

    * Dentro del método checkHost Se debe mantener el LOG que informa, antes de retornar el resultado, el número de listas negras revisadas VS. el número de listas negras total (línea 60). Se debe garantizar que dicha información sea verídica bajo el nuevo esquema de procesamiento en paralelo planteado.

    * Se sabe que el HOST 202.24.34.55 está reportado en listas negras de una forma más dispersa, y que el host 212.24.24.55 NO está en ninguna lista negra.
Dividir la cantidad de servidores entre N para saber el rango, el for se debe pasar al thread


**Parte II.I Para discutir la próxima clase (NO para implementar aún)**

La estrategia de paralelismo antes implementada es ineficiente en ciertos casos, pues la búsqueda se sigue realizando aún cuando los N hilos (en su conjunto) ya hayan encontrado el número mínimo de ocurrencias requeridas para reportar al servidor como malicioso. Cómo se podría modificar la implementación para minimizar el número de consultas en estos casos?, qué elemento nuevo traería esto al problema?

**Parte III - Evaluación de Desempeño**

A partir de lo anterior, implemente la siguiente secuencia de experimentos para realizar las validación de direcciones IP dispersas (por ejemplo 202.24.34.55), tomando los tiempos de ejecución de los mismos (asegúrese de hacerlos en la misma máquina):

1. Un solo hilo.

	![image](https://user-images.githubusercontent.com/98189066/185267116-dcb033d7-3472-4f51-b1e6-d7eb3a504526.png)
	
2. Tantos hilos como núcleos de procesamiento (haga que el programa determine esto haciendo uso del [API Runtime](https://docs.oracle.com/javase/7/docs/api/java/lang/Runtime.html)). (8 nucleos)

	![image](https://user-images.githubusercontent.com/98189066/185271319-0e7ae837-3247-47ef-ae03-a10ea4b17d07.png)

3. Tantos hilos como el doble de núcleos de procesamiento. (16 nucleos)

	![image](https://user-images.githubusercontent.com/98189066/185271444-01ecf5be-4827-41a3-a63e-8bd785f4d77a.png)

4. 50 hilos.

	![image](https://user-images.githubusercontent.com/98189066/185271464-a9658e98-677e-4921-a973-144078c8b6cd.png)


5. 100 hilos.

	![image](https://user-images.githubusercontent.com/98189066/185271523-5d35abcd-1873-4656-9aa8-089116247301.png)


Al iniciar el programa ejecute el monitor jVisualVM, y a medida que corran las pruebas, revise y anote el consumo de CPU y de memoria en cada caso. ![](img/jvisualvm.png)

Con lo anterior, y con los tiempos de ejecución dados, haga una gráfica de tiempo de solución vs. número de hilos. Analice y plantee hipótesis con su compañero para las siguientes preguntas (puede tener en cuenta lo reportado por jVisualVM):

![image](https://user-images.githubusercontent.com/98189066/185272214-aa50def4-be9b-4379-930d-a6458c5ec450.png)

Como podemos ver en la grafica, el tiempo es menor a medida que se incrementa el numero de hilos. Esto debido a que se divide el rango de busqueda en los distintos hilos, reduciendo la carga que tiene cada uno de ellos. Sin embargo, hay un punto en el que el cambio no es tan significativo. Con esto podemos concluir que hay un punto en el que no es indispensable usar tal cantidad de hilos.

**Parte IV - Ejercicio Black List Search**

1. Según la [ley de Amdahls](https://www.pugetsystems.com/labs/articles/Estimating-CPU-Performance-using-Amdahls-Law-619/#WhatisAmdahlsLaw?):

	![](img/ahmdahls.png), donde _S(n)_ es el mejoramiento teórico del desempeño, _P_ la fracción paralelizable del algoritmo, y _n_ el número de hilos, a mayor _n_, mayor debería ser dicha mejora. Por qué el mejor desempeño no se logra con los 500 hilos?, cómo se compara este desempeño cuando se usan 200?. 
	
	Debido a la ley de Amdahls, podemos confirmar la hipotesis dada anteriormente, en la cual se dice que no se aumenta el desempeño haciendo uso de mas hilos con menos frecuencia. Ya que hay un punto en el que depende de la propia capacidad del procesador

2. Cómo se comporta la solución usando tantos hilos de procesamiento como núcleos comparado con el resultado de usar el doble de éste?.
Cuando se usan tantos números de hilos como números de núcleos el tiempo es de 20s, cuando se  16 hilos es decir el doble de hilos por numero de núcleos el tiempo de ejecución baja a 10s, en este caso el trabajo se distribuye de una mejor manera y puede ser una buena opción para proyectos de mayor escala

3. De acuerdo con lo anterior, si para este problema en lugar de 100 hilos en una sola CPU se pudiera usar 1 hilo en cada una de 100 máquinas hipotéticas, la ley de Amdahls se aplicaría mejor?. Si en lugar de esto se usaran c hilos en 100/c máquinas distribuidas (siendo c es el número de núcleos de dichas máquinas), se mejoraría?. Explique su respuesta.

No mejoraria, pues si se usa solo un hilo en cada pc. Por lo tanto no se utiliza el 100% de la capacidad del procesador de la maquima. En cambio, si usamos los nucleos del procesador del computador, se estaria aprovechando al maximo la potencia del pc.


