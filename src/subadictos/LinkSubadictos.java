/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package subadictos;

/**
 *
 * @author pablo
 */
public class LinkSubadictos {
    String serie;
    int threadId;
    String idCap;
    String link;

    public LinkSubadictos(String serie, int threadId, String idCap, String link) {
        this.serie = serie;
        this.threadId = threadId;
        this.idCap = idCap;
        this.link = link;
    }

    public String getSerie() {
        return serie;
    }

    public int getThreadId() {
        return threadId;
    }

    public String getIdCap() {
        return idCap;
    }

    public String getLink() {
        return link;
    }
}
