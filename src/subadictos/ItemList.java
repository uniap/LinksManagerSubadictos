package subadictos;

/**
 * Created by pablo on 02/10/15.
 */
public class ItemList {
    int pageId;
    String tituloServie;

    public ItemList(int id, String ti) {
        this.pageId = id;
        this.tituloServie = ti;
    }

    public int getPageId() {
        return pageId;
    }

    public String getTituloServie() {
        return this.tituloServie;
    }
}
