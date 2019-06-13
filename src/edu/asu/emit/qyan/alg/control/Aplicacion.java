package edu.asu.emit.qyan.alg.control;

import java.io.*;
import java.util.*;

import edu.asu.emit.qyan.alg.model.Path;
import edu.asu.emit.qyan.alg.model.VariableGraph;

public class Aplicacion {

	public static ArrayList<FuentesComida> fuentes = new ArrayList<>();
	public static ArrayList<Solicitud> solicitudes = new ArrayList<>();
	public static VariableGraph graph = new VariableGraph("data/test_16");
	public static ArrayList<Float> pi = new ArrayList<>();
	public static ArrayList<String[]> caminos = new ArrayList<>();


	public static void main(String[] args) throws InterruptedException, IOException {

//		crearArchivoCaminos();
		leerArchivoCaminos();

		crearFuenteDeComida(5, true);
		boolean bandera = true;

		for (int i = 0; i < fuentes.size(); i++) {
            ordenarSolicitudes(i, bandera);
            bandera = false;
        }


		for (int i=0; i< 10; i++) {
			primerPaso(5);
			borrarGrafos();
			segundoPaso(5);
			tercerPaso(5);
		}
		elegirConexion();

	}

	private static void borrarGrafos() {
		for (int i = fuentes.size()-1; i >= 0; i--) {
			if (fuentes.get(i).borrar) {
				fuentes.remove(i);
			}
		}
	}

	/**
	 * funcion para leer el archivo y guardar en memoria
	 * @throws IOException
	 */
	private static void leerArchivoCaminos() throws IOException {
		FileReader input = new FileReader("data/Kcaminos");
		BufferedReader bufRead = new BufferedReader(input);
		String linea = bufRead.readLine();

		while (linea != null) {
			String[] variables = linea.split("-");
			variables[2] = variables[2].replace(", [", ";[");
			variables[2] = variables[2].replace("[", "");
			variables[2] = variables[2].replace("]", "");
			variables[2] = variables[2].replace(", ", "");
			caminos.add(variables);
			linea = bufRead.readLine();
		}
	}

	/**
	 * Funcion para crear el archivo de los posibles caminos
	 * @throws IOException
	 */
	private static void crearArchivoCaminos() throws IOException {
		YenTopKShortestPathsAlg yenAlg = new YenTopKShortestPathsAlg(graph);
		PrintWriter writer = new PrintWriter("data/Kcaminos", "UTF-8");

		// en este for hay que poner la cantidad de vertices que tenemos
		for (int i = 0; i <= 5; i++) {
			for (int k = 0; k <= 5; k++) {
				if (i != k) {
					List<Path> shortest_paths_list = yenAlg.get_shortest_paths(graph.get_vertex(i), graph.get_vertex(k), 4);
					List<Path> shortest_paths_list2 = yenAlg.get_shortest_paths(graph.get_vertex(k), graph.get_vertex(i), 4);
					writer.println(i + "-" + k + "-" + shortest_paths_list.toString());
					writer.println(k + "-" + i + "-" + shortest_paths_list2.toString());

				}
			}
		}
		writer.close();
	}

    /**
     * Funcion para crear la lista de solicitudes para cada fuente de comida
     * @param cantSolicitudes
     */
	public static void crearListadeSolicitudes(int cantSolicitudes) {

    }

	/**
	 * Funcion para crear una fuente de comida, y crear las solicitudes pero aun no se definio el orden
	 */
	public static void crearFuenteDeComida(int cantFuente, boolean parametro) throws IOException {

		//crear matriz inicial para todas las fuentes de comida
		// Matriz que representa la red igual al archivo test_16 que se va a utilar al tener los caminos.
		for (int i = 0; i<cantFuente ; i++) {
			int[] vertices = {0, 1, 2, 3, 4, 5};
			GrafoMatriz g = new GrafoMatriz(vertices);
			g.InicializarGrafo(g.grafo);
			g.agregarRuta(0, 1, 1, 3, 5);
			g.agregarRuta(1, 5, 1, 3, 5);
			g.agregarRuta(1, 3, 1, 3, 5);
			g.agregarRuta(1, 2, 1, 3, 5);
			g.agregarRuta(2, 3, 1, 3, 5);
			g.agregarRuta(2, 4, 1, 3, 5);
			g.agregarRuta(3, 5, 1, 3, 5);
			g.agregarRuta(4, 5, 1, 3, 5);

			fuentes.add(new FuentesComida(g));
		}

		FileReader input = new FileReader("data/conexiones");
		BufferedReader bufRead = new BufferedReader(input);

		String linea = bufRead.readLine();

		while (linea != null ) {

			if (linea.trim().equals("")) {
				linea = bufRead.readLine();
				continue;
			}
			String[] str_list = linea.trim().split("\\s*,\\s*");

			int origen = Integer.parseInt(str_list[0]);
			int destino = Integer.parseInt(str_list[1]);
			int fs = Integer.parseInt(str_list[2]);
			int tiempo = Integer.parseInt(str_list[3]);
			int id = Integer.parseInt(str_list[4]);

			int inicio = origen;
			int fin = destino;

			if (parametro) {
				for (int j = 0; j < cantFuente; j++) {
					Solicitud solicitud = new Solicitud(origen, destino, fs, tiempo, id);
					fuentes.get(j).solicitudes.add(solicitud);
				}
			} else {
				Solicitud solicitud = new Solicitud(origen, destino, fs, tiempo, id);
				fuentes.get(fuentes.size()-1).solicitudes.add(solicitud);
			}



			linea = bufRead.readLine();
		}
		bufRead.close();

	}

    /**
     * Funcion para ordenar las solicitudes y luego asignarlas al grafo
     * @param fuenteDeComida
     */
	public static void ordenarSolicitudes(int fuenteDeComida, boolean fsFalso) {

        if (fsFalso) {

            Collections.sort(fuentes.get(fuenteDeComida).solicitudes, new Comparator<Solicitud>() {
                public int compare(Solicitud p1, Solicitud p2) {
                    return Double.compare(p1.getFSfalso(), p2.getFSfalso());
                }
            });
            Collections.reverse(fuentes.get(fuenteDeComida).solicitudes);

        } else {
			for (int i = 0; i < fuentes.get(fuenteDeComida).solicitudes.size(); i++) {
				Random rand = new Random();
				double alpha = (double)(Math.random() * 2 - 1);
				fuentes.get(fuenteDeComida).solicitudes.get(i).FSfalso = fuentes.get(fuenteDeComida).solicitudes.get(i).FSfalso * alpha;
			}

			Collections.sort(fuentes.get(fuenteDeComida).solicitudes, new Comparator<Solicitud>() {
				public int compare(Solicitud p1, Solicitud p2) {
					return Double.compare(p1.getFSfalso(), p2.getFSfalso());
				}
			});
			Collections.reverse(fuentes.get(fuenteDeComida).solicitudes);

        }

	    for (int i = 0; i < fuentes.get(fuenteDeComida).solicitudes.size(); i++) {

	        Solicitud listaCaminosPrimera = fuentes.get(fuenteDeComida).solicitudes.get(i);
	        String listaCaminos = null;

			for (int k = 0; k < caminos.size(); k++) {
				if (caminos.get(k)[0].equals(String.valueOf(listaCaminosPrimera.origen)) && caminos.get(k)[1].equals(String.valueOf(listaCaminosPrimera.destino))) {
					listaCaminos = caminos.get(k)[2];
					break;
				}
        	}

            BuscarSlot r = new BuscarSlot(fuentes.get(fuenteDeComida).grafo, listaCaminos);
            resultadoSlot res = r.concatenarCaminos(listaCaminosPrimera.FS,0, 0);


            if (res !=null) {
                //guardar caminos utilizados y el numero de camino utilizado
                fuentes.get(fuenteDeComida).caminoUtilizado.add(res.caminoUtilizado);
                fuentes.get(fuenteDeComida).caminos.add(res.camino);
                fuentes.get(fuenteDeComida).ids.add(listaCaminosPrimera.id);
                Asignacion asignar = new Asignacion(fuentes.get(fuenteDeComida).grafo, res);
                asignar.marcarSlotUtilizados(listaCaminosPrimera.id);
            }
            else {
                /**
                 * Si es que se bloqueo y no encontro un camino se guardara los datos de la conexion y la palabra bloqueado
                 */
                fuentes.get(fuenteDeComida).caminoUtilizado.add(99);
                fuentes.get(fuenteDeComida).caminos.add("Bloqueado:" + listaCaminosPrimera.origen + listaCaminosPrimera.destino +listaCaminosPrimera.FS);
                fuentes.get(fuenteDeComida).ids.add(listaCaminosPrimera.id);
                System.out.println("No se encontró camino posible y se guarda la informacion de la conexion.");
            }


        }

    }

	/**
	 * funcion para calcular los FS de todas las fuentes de comida
	 */

	public static void calcularFS(int fuentesComida) {

		int indiceMayor = 0;

		// for para recorrer todas las fuentes de comida
		for (int i = 0; i < fuentesComida; i++) {

			// for para recorrer las filas de un grafo
			for (int k = 0; k < fuentes.get(i).grafo.grafo.length; k++) {
				// for para recorrer las columnas de un grafo
				for (int j = 0; j < fuentes.get(i).grafo.grafo.length; j++) {
					// for para recorrer el array de listafs (cada enlace del grafo)
					for (int p = 0; p < fuentes.get(i).grafo.grafo[k][j].listafs.length; p++){
						if (fuentes.get(i).grafo.grafo[k][j].listafs[p].libreOcupado == 1) {
                            if (indiceMayor < p) {
                                indiceMayor = p;
                            }
						}
					}
				}
			}
			fuentes.get(i).fsUtilizados = indiceMayor;
			indiceMayor = 0;

		}

	}

	/**
	 * funcion para calcular los FS para una fuente de comida
	 */

	public static int calcularFsUno(int nroGrafo) {

		int indiceMayor = 0;

		// for para recorrer las filas de un grafo
		for (int k = 0; k < fuentes.get(nroGrafo).grafo.grafo.length; k++) {
			// for para recorrer las columnas de un grafo
			for (int j = 0; j < fuentes.get(nroGrafo).grafo.grafo.length; j++) {
				// for para recorrer el array de listafs (cada enlace del grafo)
				for (int p = 0; p < fuentes.get(nroGrafo).grafo.grafo[k][j].listafs.length; p++){
					if (fuentes.get(nroGrafo).grafo.grafo[k][j].listafs[p].libreOcupado == 1) {
					    if (indiceMayor < p) {
                            indiceMayor = p;
                        }
					}
				}
			}
		}

		return indiceMayor;
	}

	/**
	 En el primer paso vamos a utilizar a las abejas empleadas para cambiar soluciones de las fuentes de comida si es que tienen
	 mejor resultado
	 **/
	public static void primerPaso(int cantFuentes) {
		calcularFS(cantFuentes);
		boolean bandera = false;

		//calcular Vij para cada fuente de comida
		for (int i = 0; i < cantFuentes; i++) {
			int contador = 0;
			Random rand = new Random();

			int j = rand.nextInt(fuentes.get(i).solicitudes.size() - 1);
			int k = rand.nextInt(fuentes.get(i).solicitudes.size() - 1);

			while (j == k) {
				k = rand.nextInt(fuentes.get(i).solicitudes.size() - 1);
			}
			while (contador < 3) {
				bandera = crearNuevoGrafo(j, k, i);
				if (bandera) {
					fuentes.get(i).borrar = true;
					contador = 4;
					fuentes.get(i).modificado = 0;
				} else {
					fuentes.get(fuentes.size()-1).borrar = true;
					fuentes.get(i).modificado = fuentes.get(i).modificado + 1;
					contador++;
				}
			}
		}

	}

	private static boolean crearNuevoGrafo(int j, int k, int nroGrafo) {

		int[] vertices = {0, 1, 2, 3, 4, 5};
		GrafoMatriz g = new GrafoMatriz(vertices);
		g.InicializarGrafo(g.grafo);
		g.agregarRuta(0, 1, 1, 3, 5);
		g.agregarRuta(1, 5, 1, 3, 5);
		g.agregarRuta(1, 3, 1, 3, 5);
		g.agregarRuta(1, 2, 1, 3, 5);
		g.agregarRuta(2, 3, 1, 3, 5);
		g.agregarRuta(2, 4, 1, 3, 5);
		g.agregarRuta(3, 5, 1, 3, 5);
		g.agregarRuta(4, 5, 1, 3, 5);

		fuentes.add(new FuentesComida(g));

		Solicitud auxiliar = new Solicitud(fuentes.get(nroGrafo).solicitudes.get(k).origen, fuentes.get(nroGrafo).solicitudes.get(k).destino, fuentes.get(nroGrafo).solicitudes.get(k).FS, fuentes.get(nroGrafo).solicitudes.get(k).tiempo, fuentes.get(nroGrafo).solicitudes.get(k).id);

		for (int i = 0; i < fuentes.get(nroGrafo).solicitudes.size(); i++) {
			Solicitud solicitud = new Solicitud(fuentes.get(nroGrafo).solicitudes.get(i).origen, fuentes.get(nroGrafo).solicitudes.get(i).destino, fuentes.get(nroGrafo).solicitudes.get(i).FS, fuentes.get(nroGrafo).solicitudes.get(i).tiempo, fuentes.get(nroGrafo).solicitudes.get(i).id);
			fuentes.get(fuentes.size()-1).solicitudes.add(solicitud);
		}

		fuentes.get(fuentes.size()-1).solicitudes.get(k).origen = fuentes.get(fuentes.size()-1).solicitudes.get(j).origen;
		fuentes.get(fuentes.size()-1).solicitudes.get(k).destino = fuentes.get(fuentes.size()-1).solicitudes.get(j).destino;
		fuentes.get(fuentes.size()-1).solicitudes.get(k).id = fuentes.get(fuentes.size()-1).solicitudes.get(j).id;
		fuentes.get(fuentes.size()-1).solicitudes.get(k).tiempo = fuentes.get(fuentes.size()-1).solicitudes.get(j).tiempo;
		fuentes.get(fuentes.size()-1).solicitudes.get(k).FS = fuentes.get(fuentes.size()-1).solicitudes.get(j).FS;
		fuentes.get(fuentes.size()-1).solicitudes.get(k).FSfalso = fuentes.get(fuentes.size()-1).solicitudes.get(j).FSfalso;

		fuentes.get(fuentes.size()-1).solicitudes.get(j).origen = auxiliar.origen;
		fuentes.get(fuentes.size()-1).solicitudes.get(j).destino = auxiliar.destino;
		fuentes.get(fuentes.size()-1).solicitudes.get(j).id = auxiliar.id;
		fuentes.get(fuentes.size()-1).solicitudes.get(j).tiempo = auxiliar.tiempo;
		fuentes.get(fuentes.size()-1).solicitudes.get(j).FS = auxiliar.FS;
		fuentes.get(fuentes.size()-1).solicitudes.get(j).FSfalso = auxiliar.FSfalso;

		for (int p = 0; p < fuentes.get(fuentes.size()-1).solicitudes.size(); p++) {

			Solicitud listaCaminosPrimera = fuentes.get(fuentes.size() - 1).solicitudes.get(p);
			String listaCaminos = null;

			for (int l = 0; l < caminos.size(); l++) {
				if (caminos.get(l)[0].equals(String.valueOf(listaCaminosPrimera.origen)) && caminos.get(l)[1].equals(String.valueOf(listaCaminosPrimera.destino))) {
					listaCaminos = caminos.get(l)[2];
					break;
				}
			}

			BuscarSlot r = new BuscarSlot(fuentes.get(fuentes.size()-1).grafo, listaCaminos);
			resultadoSlot res = r.concatenarCaminos(listaCaminosPrimera.FS, 0, 0);


			if (res != null) {
				//guardar caminos utilizados y el numero de camino utilizado
				fuentes.get(fuentes.size()-1).caminoUtilizado.add(res.caminoUtilizado);
				fuentes.get(fuentes.size()-1).caminos.add(res.camino);
				fuentes.get(fuentes.size()-1).ids.add(listaCaminosPrimera.id);
				Asignacion asignar = new Asignacion(fuentes.get(fuentes.size()-1).grafo, res);
				asignar.marcarSlotUtilizados(listaCaminosPrimera.id);
			} else {
				/**
				 * Si es que se bloqueo y no encontro un camino se guardara los datos de la conexion y la palabra bloqueado
				 */
				fuentes.get(fuentes.size()-1).caminoUtilizado.add(99);
				fuentes.get(fuentes.size()-1).caminos.add("Bloqueado:" + listaCaminosPrimera.origen + listaCaminosPrimera.destino + listaCaminosPrimera.FS);
				fuentes.get(fuentes.size()-1).ids.add(listaCaminosPrimera.id);
				System.out.println("No se encontró camino posible y se guarda la informacion de la conexion.");
			}

		}

		int pi = calcularFsUno(fuentes.size()-1);
		fuentes.get(fuentes.size()-1).fsUtilizados = pi;
		int bloqueadosViejo = 0;
		int bloqueadosNuevo = 0;
		for (int m = 0; m < fuentes.get(nroGrafo).solicitudes.size(); m++) {
			if (fuentes.get(nroGrafo).caminos.get(m).contains("Bloqueado")) {
				bloqueadosViejo++;
			}
			if (fuentes.get(fuentes.size()-1).caminos.get(m).contains("Bloqueado")) {
				bloqueadosNuevo++;
			}
		}

		if (bloqueadosNuevo < bloqueadosViejo) {
			return true;
		} else if (bloqueadosViejo < bloqueadosNuevo) {
			return false;
		} else {
			if (pi < fuentes.get(nroGrafo).fsUtilizados) {
				return true;
			} else {
				return false;
			}
		}


	}



	/**
	En el segundo paso vamos a seleccionar una fuente de comida utilizando la ruleta para cambiar su solucion y verificar si es mejor
	 **/
	public static void segundoPaso(int cantFuentes) {

		Random rand = new Random();
		float sumatoria = 0;
		float prueba;
		float suma = 0;

		//primero se calcula todos los pi de todas las fuentes de comida
		for (int i = 0; i<cantFuentes; i++) {
			sumatoria = sumatoria + fuentes.get(i).fsUtilizados;
		}

		// se agregan los valores de pi
		for (int j = 0; j<cantFuentes; j++) {
			prueba = fuentes.get(j).fsUtilizados / sumatoria;
			pi.add(prueba);
		}

		// se va cambiar un resultado dependiendo de la ruleta
		for (int i = 0; i < cantFuentes; i++) {
			boolean bandera = false;
			float nectar = rand.nextFloat();
				suma = suma + pi.get(i);

				if (suma >= nectar) {
					int contador = 0;

					int j = rand.nextInt(fuentes.get(i).solicitudes.size() - 1);
					int k = rand.nextInt(fuentes.get(i).solicitudes.size() - 1);

					while (j == k) {
						k = rand.nextInt(fuentes.get(i).solicitudes.size() - 1);
					}
					while (contador < 3) {
						bandera = crearNuevoGrafo(j, k, i);
						if (bandera) {
							fuentes.get(i).borrar = true;
							contador = 4;
							fuentes.get(i).modificado = 0;
						} else {
							fuentes.get(fuentes.size()-1).borrar = true;
							fuentes.get(i).modificado = fuentes.get(i).modificado + 1;
							contador++;
						}
					}
				}
			borrarGrafos();

		}


	}

	/**
	 * En el tercer paso vamos a verificar si existen fuentes de comida abandonadas y vamos a guardar la mejor fuente de comida o solucion hasta el momento
	 */

	public static void tercerPaso(int cantFuentes) throws IOException {

		for (int i=0; i < cantFuentes; i++) {

			if (fuentes.get(i).modificado >= 9) {
				fuentes.remove(i);
				crearFuenteDeComida(1, false);
				ordenarSolicitudes(fuentes.size()-1, false);
				fuentes.get(fuentes.size()-1).fsUtilizados = calcularFsUno(fuentes.size()-1);
			}

		}
	}

	public static void elegirConexion() {

		int cantBloqueados = 0;
		int cantBloqueadosNuevo = 0;

		int[] vertices = {0, 1, 2, 3, 4, 5};
		GrafoMatriz g = new GrafoMatriz(vertices);
		g.InicializarGrafo(g.grafo);
		g.agregarRuta(0, 1, 1, 3, 5);
		g.agregarRuta(1, 5, 1, 3, 5);
		g.agregarRuta(1, 3, 1, 3, 5);
		g.agregarRuta(1, 2, 1, 3, 5);
		g.agregarRuta(2, 3, 1, 3, 5);
		g.agregarRuta(2, 4, 1, 3, 5);
		g.agregarRuta(3, 5, 1, 3, 5);
		g.agregarRuta(4, 5, 1, 3, 5);
		FuentesComida resultadoFinal = new FuentesComida(g);

		for (int i = 0; i < fuentes.size(); i++) {
			cantBloqueados = 0;
			cantBloqueadosNuevo = 0;

			if (i == 0) {
				resultadoFinal = fuentes.get(i);
				int sumatoria;

			} else {
				for (int j = 0; j < resultadoFinal.caminoUtilizado.size(); j++) {
					if (resultadoFinal.caminoUtilizado.get(j) == 99) {
						cantBloqueados++;
					}
				}
				for (int k = 0; k < fuentes.get(i).caminoUtilizado.size(); k++) {
					if (fuentes.get(i).caminoUtilizado.get(k) == 99) {
						cantBloqueadosNuevo++;
					}
				}
				if (cantBloqueadosNuevo < cantBloqueados) {
					resultadoFinal = fuentes.get(i);
				} else if (cantBloqueados == cantBloqueadosNuevo && resultadoFinal.fsUtilizados > fuentes.get(i).fsUtilizados) {
					resultadoFinal = fuentes.get(i);
				}
			}

		}

		for (int l = 0; l < resultadoFinal.caminoUtilizado.size(); l++) {
			if (resultadoFinal.caminoUtilizado.get(l) == 99) {
				cantBloqueados++;
			}
		}

		System.out.println("El mejor resultado tiene el indice mayor utilizado en: " + resultadoFinal.fsUtilizados + " y una cantidad de conexiones bloqueadas igual a: " + cantBloqueados);

	}

}
