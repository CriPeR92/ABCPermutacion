package edu.asu.emit.qyan.alg.control;

public class Solicitud /*implements Comparable<Double>*/ {

    public int origen;
    public int destino;
    public int FS;
    public double FSfalso;
    public int tiempo;
    public int id;

    public Solicitud(int origen, int destino, int FS, int tiempo, int id) {
        this.origen = origen;
        this.destino = destino;
        this.FS = FS;
        this.FSfalso = FS;
        this.tiempo = tiempo;
        this.id = id;
    }

    public double getFSfalso() {
        return FSfalso;
    }

//    @Override
//    public int compareTo(Double o) {
//        return (this.FSfalso - o);
//    }
}
