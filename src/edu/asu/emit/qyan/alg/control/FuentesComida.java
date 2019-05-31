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
    public ArrayList<Integer> modificado;

    public FuentesComida(GrafoMatriz g) {
        this.grafo = g;
        this.caminos = new ArrayList<>();
        this.ids = new ArrayList<>();
        this.caminoUtilizado = new ArrayList<>();
        this.fsUtilizados = 0;
        this.modificado = new ArrayList<>();
        this.solicitudes = new ArrayList<>();
    }

}
