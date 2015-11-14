/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package subadictos;

import java.util.ArrayList;

/**
 *
 * @author pablo
 */
class Episodio {
    String idCap;
    ArrayList<String> links;

    public Episodio(String t) {
        this.idCap = t;
        links = new ArrayList();
    }

    public void addLink(String l) {
        this.links.add(l);
    }

    public String getIdCap() {
        return this.idCap;
    }

    public ArrayList<String> getListaLinks() {
        return this.links;
    }
}
