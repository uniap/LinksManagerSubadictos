
package subadictos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Temporada {
    
    HashMap<String, ArrayList<String>> episodios;
    
    public Temporada() {
        episodios = new HashMap<>();
    }
    
    void addLink(String episodio, String link) {
        ArrayList<String> links;
        
        if (episodios.containsKey(episodio)) {
            links = episodios.get(episodio);
        }
        else {
            links = new ArrayList<>();
        }
        links.add(link);
        episodios.put(episodio, links);
    }
    
    public int getCtdEpisodiosDisponibles() {
        return this.episodios.size();
    }
    
    public Set<String> getListaEpisodios() {
        return this.episodios.keySet();
    }
    
    
    public ArrayList<String> getLinksEpisodio(String episodio) {
        return this.episodios.get(episodio);
    }
    
    private void showDetails() {
        System.out.println("Cantidad de episodios: " + this.getCtdEpisodiosDisponibles());
        for (String key : this.episodios.keySet()) {
            ArrayList<String> links = this.getLinksEpisodio(key);
            System.out.println("Episodio " + key);
            for (String l : links) {
                System.out.println("    link=" + l);
            }            
        }
    }
}
