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

		crearFuenteDeComida(5);
		boolean bandera = true;
		for (int i = 0; i < fuentes.size(); i++) {
            ordenarSolicitudes(i, bandera);
            bandera = false;
        }


		for (int i=0; i< 10; i++) {
			primerPaso(5);
			segundoPaso(5);
			tercerPaso(5);
		}
		elegirConexion();

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
	public static void crearFuenteDeComida(int cantFuente) throws IOException {

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


			for (int j = 0; j < cantFuente; j++) {
                Solicitud solicitud = new Solicitud(origen, destino, fs, tiempo, id);
                fuentes.get(j).solicitudes.add(solicitud);
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

        }


	    for (int i = 0; i < fuentes.get(fuenteDeComida).solicitudes.size(); i++) {

	        Solicitud listaCaminos = fuentes.get(fuenteDeComida).solicitudes.get(i);

        }



//        String[] str_list = listaCaminos.trim().split("\\s*,\\s*");
//
//
//        for (int k = 0; k < caminos.size(); k++) {
//            if (caminos.get(k)[0].equals(str_list[0]) && caminos.get(k)[1].equals(str_list[1])) {
//                listaCaminos = caminos.get(k)[2];
//                break;
//            }
//        }

//        for (int j = 0; j < cantFuente; j++) {
//            BuscarSlot r = new BuscarSlot(fuentes.get(j).grafo, listaCaminos);
//            resultadoSlot res = r.concatenarCaminos(fs,0, 0);


//            if (res !=null) {
//                //guardar caminos utilizados y el numero de camino utilizado
//                fuentes.get(j).caminoUtilizado.add(res.caminoUtilizado);
//                fuentes.get(j).caminos.add(res.camino);
//                fuentes.get(j).ids.add(id);
//                fuentes.get(j).modificado.add(0);
//                Asignacion asignar = new Asignacion(fuentes.get(j).grafo, res);
//                asignar.marcarSlotUtilizados(id);
//            }
//            else {
//                /**
//                 * Si es que se bloqueo y no encontro un camino se guardara los datos de la conexion y la palabra bloqueado
//                 */
//                fuentes.get(j).caminoUtilizado.add(99);
//                fuentes.get(j).caminos.add("Bloqueado:" + str_list[0] + str_list[1] + str_list[2]);
//                fuentes.get(j).ids.add(id);
//                fuentes.get(j).modificado.add(0);
//                System.out.println("No se encontró camino posible y se guarda la informacion de la conexion.");
//            }
//        }

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

		//System.out.println("asd");

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

		//calcular Vij para cada fuente de comida
		for (int i = 0; i < cantFuentes; i++) {
			Random rand = new Random();
			double alpha = (double)(Math.random() * 2 - 1);
			int j = rand.nextInt(fuentes.get(i).caminos.size()-1);
			int k = rand.nextInt(fuentes.get(i).grafo.nodos - 2);

			while (j == k) {
				k = rand.nextInt(fuentes.get(i).grafo.nodos - 2);
			}

			double nroCaminoAserUtilizado = (fuentes.get(i).caminoUtilizado.get(j)) + alpha * ((fuentes.get(i).caminoUtilizado.get(j)) - (fuentes.get(k).caminoUtilizado.get(j)));
			int caminoAUsar = (int) nroCaminoAserUtilizado;
			borrarConexion(j, i, caminoAUsar);
		}

	}

	/**
	 * Primero vamos a eliminar la conexion actual para volver a buscar un lugar para la misma
	 * @param nroCamino
	 * @param nroGrafo
	 */

	public static void borrarConexion(int nroCamino, int nroGrafo, int nroCaminoAUsar) {

		String camino = String.valueOf(fuentes.get(nroGrafo).caminos.get(nroCamino));
		Boolean reasignarSioSi = false;

		String caminoFinal = camino;
		System.out.println("el camino es: " + caminoFinal);

        int inicioSolicitud = 0;
        int finSolicitud = 0;
        int inicio = 0;
        int longitud = 0;

        if (!caminoFinal.contains("Bloqueado")) {
            inicioSolicitud = (int)caminoFinal.charAt(0) - 48;
            finSolicitud = (int)caminoFinal.charAt(caminoFinal.length()-1) - 48;


            Boolean bandera = true;

            for (int p = 0; p < caminoFinal.length() - 1; p++) {

                int primer = (int) caminoFinal.charAt(p) - 48;
                int segundo = (int) caminoFinal.charAt(p + 1) - 48;

                for (int k = 0; k < fuentes.get(nroGrafo).grafo.grafo[0][0].listafs.length; k++) {
                    if (fuentes.get(nroGrafo).grafo.grafo[primer][segundo].listafs[k].id == fuentes.get(nroGrafo).ids.get(nroCamino)) {
                        if (p == 0) {
                            if (bandera) {
                                inicio = k;
                                bandera = false;
                            }

                            longitud = longitud + 1;
                        }

                        fuentes.get(nroGrafo).grafo.grafo[primer][segundo].listafs[k].id = 0;
                        fuentes.get(nroGrafo).grafo.grafo[primer][segundo].listafs[k].libreOcupado = 0;
                        fuentes.get(nroGrafo).grafo.grafo[segundo][primer].listafs[k].id = 0;
                        fuentes.get(nroGrafo).grafo.grafo[segundo][primer].listafs[k].libreOcupado = 0;
                    }
                }
            }
        } else {
            String[] lista = caminoFinal.split(":");
            inicioSolicitud = (int)lista[1].charAt(0) - 48;
            finSolicitud = (int)lista[1].charAt(1) - 48;
            longitud = (int)lista[1].charAt(2) - 48;
			reasignarSioSi = true;
        }


		Boolean reasignar = asginar(inicioSolicitud, finSolicitud, nroGrafo, longitud, fuentes.get(nroGrafo).ids.get(nroCamino), nroCaminoAUsar, reasignarSioSi);

		if (reasignar) {
			Integer val = fuentes.get(nroGrafo).modificado.get(nroCamino);
			fuentes.get(nroGrafo).modificado.set(nroCamino, val + 1);

			fuentes.get(nroGrafo).caminos.remove(fuentes.get(nroGrafo).caminos.size()-1);
			fuentes.get(nroGrafo).ids.remove(fuentes.get(nroGrafo).ids.size()-1);
			fuentes.get(nroGrafo).caminoUtilizado.remove(fuentes.get(nroGrafo).ids.size()-1);
			fuentes.get(nroGrafo).modificado.remove(fuentes.get(nroGrafo).ids.size()-1);
			// volver a como estaba
			for (int p = 0; p < caminoFinal.length()-1; p++) {

				int primer = (int)caminoFinal.charAt(p) - 48;
				int segundo = (int)caminoFinal.charAt(p+1) - 48;

				for (int k=0; k < fuentes.get(nroGrafo).grafo.grafo[0][0].listafs.length; k++) {
					if (k == inicio)
						for (int j=0; j < longitud; j++ ) {
							fuentes.get(nroGrafo).grafo.grafo[primer][segundo].listafs[k].id = fuentes.get(nroGrafo).ids.get(nroCamino);
							fuentes.get(nroGrafo).grafo.grafo[primer][segundo].listafs[k].libreOcupado = 1;
							fuentes.get(nroGrafo).grafo.grafo[segundo][primer].listafs[k].id = fuentes.get(nroGrafo).ids.get(nroCamino);
							fuentes.get(nroGrafo).grafo.grafo[segundo][primer].listafs[k].libreOcupado = 1;
						}
				}
			}

		} else {
			fuentes.get(nroGrafo).caminos.remove(nroCamino);
			fuentes.get(nroGrafo).ids.remove(nroCamino);
			fuentes.get(nroGrafo).fsUtilizados = calcularFsUno(nroGrafo);
			fuentes.get(nroGrafo).modificado.remove(nroCamino);
			fuentes.get(nroGrafo).caminoUtilizado.remove(nroCamino);
		}


	}

	/**
	 * Funcion para asignar una conexion nueva
	 * @param inicio
	 * @param fin
	 * @param nroGrafo
	 * @param cantFs
	 * @param id
	 * @return
	 */

	public static Boolean asginar(int inicio, int fin, int nroGrafo, int cantFs, int id, int caminoAUsar, Boolean reasignar) {

		String listaCaminos = "";

		String inicioSolicitud = String.valueOf(inicio);
		String finSolicitud = String.valueOf(fin);


		for (int k = 0; k < caminos.size(); k++) {
			if (caminos.get(k)[0].equals(inicioSolicitud) && caminos.get(k)[1].equals(finSolicitud)) {
				listaCaminos = caminos.get(k)[2];
				break;
			}
		}

		BuscarSlot r = new BuscarSlot(fuentes.get(nroGrafo).grafo, listaCaminos);
		resultadoSlot res = r.concatenarCaminos(cantFs,3, caminoAUsar);


		if (res !=null) {
			//guardar caminos utilizados
			fuentes.get(nroGrafo).caminoUtilizado.add(res.caminoUtilizado);
			fuentes.get(nroGrafo).caminos.add(res.camino);
			fuentes.get(nroGrafo).ids.add(id);
			fuentes.get(nroGrafo).modificado.add(0);
			Asignacion asignar = new Asignacion(fuentes.get(nroGrafo).grafo, res);
			asignar.marcarSlotUtilizados(id);
		}
		else {
			/**
			 * Si es que se bloqueo y no encontro un camino se guardara los datos de la conexion y la palabra bloqueado
			 */
			fuentes.get(nroGrafo).caminoUtilizado.add(99);
			fuentes.get(nroGrafo).caminos.add("Bloqueado:" + inicio + fin + cantFs);
			fuentes.get(nroGrafo).ids.add(id);
			fuentes.get(nroGrafo).modificado.add(0);
			System.out.println("No se encontró camino posible.");
		}

		int fsNuevo = calcularFsUno(nroGrafo);

		if (reasignar) {
			return false;
		}

		if (fsNuevo > fuentes.get(nroGrafo).fsUtilizados) {
			return true;
		}

		return false;

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
		for (int p = 0; p < cantFuentes; p++) {

			float nectar = rand.nextFloat();

			for (int i=0; i < cantFuentes; i++) {
				suma = suma + pi.get(i);

				if (suma >= nectar) {
                    double alpha = (double)(Math.random() * 2 - 1);
                    int j = rand.nextInt(fuentes.get(i).caminos.size()-1);
                    int k = rand.nextInt(fuentes.get(i).grafo.nodos - 2);

                    while (j == k) {
                        k = rand.nextInt(fuentes.get(i).grafo.nodos - 2);
                    }

                    double nroCaminoAserUtilizado = (fuentes.get(i).caminoUtilizado.get(j)) + alpha * ((fuentes.get(i).caminoUtilizado.get(j)) - (fuentes.get(k).caminoUtilizado.get(j)));
                    int caminoAUsar = (int) nroCaminoAserUtilizado;
					borrarConexion(j, i, caminoAUsar);
					suma = 0;
					i = cantFuentes;
				}
			}

		}


	}

	/**
	 * En el tercer paso vamos a verificar si existen fuentes de comida abandonadas y vamos a guardar la mejor fuente de comida o solucion hasta el momento
	 */

	public static void tercerPaso(int cantFuentes) throws IOException {

		for (int i=0; i < cantFuentes; i++) {
			for (int p = 0; p < fuentes.get(i).modificado.size(); p++) {
				if (fuentes.get(i).modificado.get(p) >= 2) {

					String origen = String.valueOf(fuentes.get(i).caminos.get(p).charAt(0));
					String destino = String.valueOf(fuentes.get(i).caminos.get(p).charAt(fuentes.get(i).caminos.get(p).length()-1));


					String listaCaminos = "";
					for (int l = 0; l < caminos.size(); l++) {
						if (caminos.get(l)[0].equals(origen) && caminos.get(l)[1].equals(destino)) {
							listaCaminos = caminos.get(l)[2];
							break;
						}
					}

					String[] lista = listaCaminos.split(";");


					Random rand = new Random();
					double alpha = (double)(Math.random() * 2 - 1);

					int j = 1;
					int u = lista.length-1;

					double nroCaminoAserUtilizado = alpha * (u-j);
					int caminoAUsar = (int) nroCaminoAserUtilizado;
					borrarConexion(j, i, caminoAUsar);


				}
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
