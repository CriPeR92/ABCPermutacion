package edu.asu.emit.qyan.alg.control;

import edu.asu.emit.qyan.alg.model.Path;

import java.util.ArrayList;

public class FuentesComida {

    public GrafoMatriz grafo;
    public ArrayList<String> caminos;
    public ArrayList<Integer> ids;
    public ArrayList<Integer> caminoUtilizado;
    public ArrayList<Solicitud> solicitudes;
    public int fsUtilizados;
    public int modificado;
    public boolean borrar;

    public FuentesComida(GrafoMatriz g) {
        this.grafo = g;
        this.caminos = new ArrayList<>();
        this.ids = new ArrayList<>();
        this.caminoUtilizado = new ArrayList<>();
        this.fsUtilizados = 0;
        this.modificado = 0;
        this.solicitudes = new ArrayList<>();
        this.borrar = false;
    }

}
