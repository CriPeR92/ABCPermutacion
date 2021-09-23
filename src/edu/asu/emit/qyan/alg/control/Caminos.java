package edu.asu.emit.qyan.alg.control;

public class Caminos {

    public String origen;
    public String destino;
    public String caminos;

    public Caminos() {
        this.caminos = null;
        this.origen = null;
        this.destino = null;
    }

    public Caminos(String origen, String destino, String caminos) {
        this.origen = origen;
        this.destino = destino;
        this.caminos = caminos;
    }


}
