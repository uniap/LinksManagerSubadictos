package subadictos;

import java.io.IOException;
import java.rmi.Remote;
import java.util.ArrayList;

public interface SubscrManagerAPI extends Remote {
    final int LINKS_ED2K      = 0;
    final int LINKS_TORRENT   = 1;
    final int LINKS_ANY       = 2;
    
    public void setDataDir(String path) throws IOException;
    public void addSubscription(String key) throws IOException;
    public void removeSubscription(String key) throws IOException;
    public ArrayList<String> getSeriesList(String filtro) throws IOException;
    public ArrayList<String> getSubscriptionList() throws IOException;
    public ArrayList<String> getNewLinksList(int tipo, int season, boolean addToHistory, boolean checkLastOnly) throws IOException;    
    public void addLinkToHistory(String link) throws IOException;
}
