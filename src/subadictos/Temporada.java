
package subadictos;

import java.util.ArrayList;

public class Temporada {
        
    ArrayList<Episodio> episodios;
    
    public Temporada() {
        episodios = new ArrayList<>();
    }
    
    public void addLink(String episodio, String link) {
        boolean existente = false;
        for (Episodio e : this.episodios) {
            if (e.getIdCap().equals(episodio)) {
                e.addLink(link);
                existente = true;
            }
        }
        if (existente == false) {
            Episodio n = new Episodio(episodio);
            n.addLink(link);
            this.episodios.add(n);
        }
    }
    
    public int getCtdEpisodiosDisponibles() {
        return this.episodios.size();
    }
    
    public ArrayList<Episodio> getListaEpisodios() {
        return this.episodios;
    }
    
    
    public ArrayList<String> getLinksEpisodio(String episodio) {
        for (Episodio e: this.episodios) {
            if (e.getIdCap().equals(episodio)) {
                return e.getListaLinks();
            }
        }
        return null;
    }
    
    public void showDetails() {
        for (Episodio e : this.episodios) {
            System.out.println("Episodio: " + e.getIdCap());
            for (String link : e.getListaLinks()) {
                System.out.println("> link: " + link);
            }
        }
    }
}
