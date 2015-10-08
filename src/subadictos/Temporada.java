
package subadictos;

import java.util.ArrayList;
import java.util.HashMap;

public class Temporada {
    HashMap<String, ArrayList<String>> episodios;
    
    public Temporada() {
        episodios = new HashMap<>();
    }
    
    void addLink(int episodio, String link) {
        ArrayList<String> links;
        String key = "Episodio " + episodio;
        if (episodios.containsKey(key)) {
            links = episodios.get(key);
        }
        else {
            links = new ArrayList<>();
        }
        links.add(link);
        episodios.put(key, links);
    }
    
    public int getCtdEpisodiosDisponibles() {
        return this.episodios.size();
    }
    
    public ArrayList<String> getListaEpisodios(int episodio) {
        return this.episodios.get("Episodio " + episodio);
    }
}
