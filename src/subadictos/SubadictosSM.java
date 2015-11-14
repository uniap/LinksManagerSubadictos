
package subadictos;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import rmiapi.LogServices;
import rmiapi.SubscrManagerAPI;

// http://www.subadictos.net/foros/showthread.php?t=32553

public class SubadictosSM extends UnicastRemoteObject implements SubscrManagerAPI {
    
    String dirBase, subsFile, historyFile;
    HashSet<String> listaSeries;
    LogServices log;
    int logToken;
    int modo;
    
    public SubadictosSM() throws RemoteException, IOException {
        listaSeries = null;
        if (!Files.exists(Paths.get("./data"))) {
            Files.createDirectory(Paths.get("./data"));
        }
        this.log = null;   
        this.logToken = 0;
        this.modo = SubadictosSM.STANDALONE;
    }
    
    @Override
    public void runAsModule(int rmiPort) throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(rmiPort);
            this.log = (LogServices) registry.lookup("LogServer");
            logToken = this.log.registerForLog("logs/jeopardy.log", "subscr");
            this.modo = SubadictosSM.MODULE;
        } catch (NotBoundException | AccessException ex) {
            this.log = null;
            this.logToken = 0;
            this.modo = SubscrManagerAPI.STANDALONE;
        }
    }
    
    private void log(String msg) {
        if (this.modo == SubadictosSM.STANDALONE ) {
            System.out.println(msg);
        }
        else {
            try {
                this.log.writeLog(this.logToken, msg);
            } catch (RemoteException ex) {
                System.err.println(msg);
            }
        }
    }
    

    @Override
    public void setDataDir(String path) throws IOException {
        this.dirBase = path;
        this.subsFile = dirBase + "/subscripciones.txt";
        this.historyFile = dirBase + "/history.txt";
    }

    @Override
    public void addSubscription(String key) throws IOException {
        if (checkExistInFile(key, subsFile) == null) {
            try (FileWriter fr = new FileWriter(new File(subsFile), true)) {
                fr.append(key + "\n");
            } catch (IOException ex) {
                System.err.println(ex.toString());
            }
        }
    }

    @Override
    public void removeSubscription(String key) throws IOException {
        File inputFile = new File(subsFile);
        File tempFile = new File(dirBase + "/temp_subscripciones.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                String currentLine;
                while((currentLine = reader.readLine()) != null) {
                    String trimmedLine = currentLine.trim();
                    if(trimmedLine.contains(key)) continue;
                    writer.write(currentLine + "\n");
                }
            }
            tempFile.renameTo(inputFile);
        }
    }

    @Override
    public ArrayList<String> getSeriesList(String filtro) throws IOException {
        ArrayList<String> list = new ArrayList();

        this.refreshLocalSeriesList();

        Pattern p1 = Pattern.compile("[0-9]+;;;(.+):(.*)");
        Pattern p2 = Pattern.compile("[0-9]+;;;(.+)");
        Iterator<String> it = this.listaSeries.iterator();
        while(it.hasNext()) {
            String line = it.next();
            if (!filtro.equals("%")) {
                String titulo;
                Matcher m1 = p1.matcher(line);
                if (m1.matches()) {
                  titulo = m1.group(1);
                }
                else {
                    Matcher m2 = p2.matcher(line);
                    if (m2.matches()) {
                        titulo = m2.group(1);
                    }
                    else {
                        titulo = "NONE";
                        this.log("ADVERTENCIA, NOT MATCH " + line);
                    }
                }
                if (!filtro.contains("%")) {
                    if (!titulo.equalsIgnoreCase(filtro)) {
                        continue;
                    }
                }
                else {
                    if (!titulo.contains(filtro.replace("%", ""))) {
                        continue;
                    }
                }
            }
            list.add(line);
        }
        return list;
    }

    @Override
    public ArrayList<String> getSubscriptionList() throws IOException {
        String line;
        ArrayList<String> list = new ArrayList();
        BufferedReader in = new BufferedReader(new FileReader(new File(subsFile)));
        while((line = in.readLine()) != null) {
            list.add(line);
        }
        return list;
    }

    public String checkExistInFile(String txt, String fp) {
        String found = null;
        BufferedReader in;
        try {
            in = new BufferedReader(new FileReader(new File(fp)));
            String line;
            while((line = in.readLine()) != null) {
                if (line.contains(txt)) {
                    found = line;
                    break;
                }
            }
            in.close();
        } catch (IOException ex) {
            found = null;
        }
        return found;
    }

    public boolean checkPertenencia(String key, ArrayList<String> lista) {
        return lista.stream().anyMatch((s) -> (key.contains(s)));
    }

    @Override
    public void addLinkToHistory(String serie, int threadId, String idEpisodio, String link) throws IOException {
        String row = serie + ";" + threadId + ";" + idEpisodio;
        if (checkExistInFile(row, historyFile) == null) {
            try (FileWriter fr = new FileWriter(new File(historyFile), true)) {
                fr.append(row + "\n");
            } catch (IOException ex) {
                this.log(ex.toString());
            }
        }
    }

    @Override
    public boolean checkLinkInHistory(String serie, int threadId, String episodio, String link) throws IOException {
        boolean exists;
        String row = serie + ";" + threadId + ";" + episodio;
        exists = checkExistInFile(row, historyFile) != null;
        return exists;
    }    
    
    private void refreshLocalSeriesList() throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("ddMMyy");
        String sDate = format.format(new Date());
        String fName = this.dirBase + "/" + sDate + "_series.txt";
        this.listaSeries = new HashSet<>();
        if (!Files.exists(Paths.get(fName))) {
            this.log("Primera ejecucion del dia " + sDate + ". Actualizando estructuras internas.");
            for (String s : this.downloadList("http://www.subadictos.net/index.php?page=SeriesTodas")) {
                this.listaSeries.add(s);
            }
            for (String s : this.downloadList("http://www.subadictos.net/index.php?page=TemporadasEnCurso")) {
                this.listaSeries.add(s);
            }
            for (String s : this.downloadList("http://www.subadictos.net/index.php?page=SeriesTodasHD")) {
                this.listaSeries.add(s);
            }
            for (String s : this.downloadList("http://www.subadictos.net/index.php?page=SeriesProximas")) {
                this.listaSeries.add(s);
            }
            for (String s : this.downloadList("http://www.subadictos.net/index.php?page=HDSeriesCurso")) {
                this.listaSeries.add(s);
            }            
                                 
            try (FileWriter fr = new FileWriter(new File(fName), true)) {
                Iterator<String> it = this.listaSeries.iterator();
                while (it.hasNext()) {
                    fr.write(it.next() + "\n");
                }
            } catch (IOException ex) {
                System.err.println(ex.toString());
            }
        }
        else {
            // Ya se bajo previamente la lista de series, ahora se levanta a memoria
            String line;
            BufferedReader in = new BufferedReader(new FileReader(new File(fName)));
            while((line = in.readLine()) != null) {
                this.listaSeries.add(line);
            }
        }
    }

    private ArrayList<String> downloadList(String sUrl) throws IOException {
        URL URLpagina = new URL(sUrl);

        ArrayList<String> lista = new ArrayList<>();
        URLConnection pg = URLpagina.openConnection();
        pg.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(pg.getInputStream(), "windows-1252"))) {
            String txt;
            Pattern pattern = Pattern.compile("[0-9]+.*t=([0-9]+).*blank>(.*)</a>.*");
            while ((txt = in.readLine()) != null) {
                txt = txt.replace("\t", "").trim();
                Matcher matcher = pattern.matcher(txt);
                if (matcher.matches()) {
                    String pageNum = matcher.group(1);
                    String titSerie = matcher.group(2).replace("(+)", "");
                    lista.add(pageNum + ";;;" + titSerie);
                }
            }
        }
        return lista;
    }

    private Temporada getLinks2(String serie, int pageId) throws IOException {
        Temporada temporada = new Temporada();
        URL URLpagina = new URL("http://www.subadictos.net/foros/showthread.php?t=" + pageId);

        Pattern p = Pattern.compile(".+<a href=\"([ed2k|magnet])(.*)\" target=\"_blank\"><b>(.*)</b></a>.*");
        
        URLConnection pg = URLpagina.openConnection();
        pg.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(pg.getInputStream()))) {
            String txt;
            while ((txt = in.readLine()) != null) {
                Matcher m = p.matcher(txt);
                if (m.matches()) {
                    String episodio = m.group(3);
                    temporada.addLink(episodio, m.group(1) + m.group(2));
                }
            }
        }
        return temporada;
    }    
    
    @Override
    public ArrayList<String> getNewLinks(int lType, int season, boolean checkLastOnly) throws IOException {
        ArrayList<LinkSubadictos> links = this.getNewLinksList(lType, checkLastOnly);
        ArrayList<String> sLinks = new ArrayList();
        for (LinkSubadictos l : links) {
            sLinks.add(l.getLink());
            this.addLinkToHistory(l.getSerie(), l.getThreadId(), l.getIdCap(), l.getLink());
        }
        return sLinks;
    }    
    
    private ArrayList<LinkSubadictos> getNewLinksList(int lType, boolean checkLastOnly) throws IOException {
        ArrayList<LinkSubadictos> list = new ArrayList();
        ArrayList<String> suscriptas = this.getSubscriptionList();

        for (String s : suscriptas) {
            this.log("Verificando " + s);
            ArrayList<String> series = this.getSeriesList(s);
            for (int i=0; i<series.size(); i++) {
                if (checkLastOnly && i<series.size()-1) {
                    continue;
                }
                
                String x = series.get(i);
                int pageId = Integer.parseInt(x.split(";;;")[0]);
                
                this.log("Serie: " + x);
                Temporada t = this.getLinks2(s, pageId);
                
                for (Episodio e : t.getListaEpisodios()) {
                    if (this.checkLinkInHistory(s, pageId, e.getIdCap(), null) == false) {
                        this.log("Nuevo episodio: " + e.getIdCap());
                        ArrayList<String> links = e.getListaLinks();
                        for (String se : links) {
                            String tLink = se.split(":")[0];
                            if (lType == SubscrManagerAPI.LINKS_ANY) {
                                list.add(new LinkSubadictos(s, pageId, e.getIdCap(), se));
                                break;
                            }
                            else if (lType == SubscrManagerAPI.LINKS_ED2K && tLink.equalsIgnoreCase("ed2k")) {       
                                list.add(new LinkSubadictos(s, pageId, e.getIdCap(), se));
                                break;                            
                            }
                            else if (lType == SubscrManagerAPI.LINKS_TORRENT && tLink.equalsIgnoreCase("magnet")) {
                                list.add(new LinkSubadictos(s, pageId, e.getIdCap(), se));
                                break;                                
                            }   
                            else {
                                this.log("MAL getNewLinksList! " + se);
                            }
                        }
                    }
                }
            }
        }        
        return list;
    }
}
