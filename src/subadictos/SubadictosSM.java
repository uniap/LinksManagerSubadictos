
package subadictos;

import rmiapi.SubscrManagerAPI;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// http://www.subadictos.net/foros/showthread.php?t=32553

public class SubadictosSM extends UnicastRemoteObject implements SubscrManagerAPI {

    String dirBase, subsFile, historyFile;
    HashSet<String> listaSeries;

    public SubadictosSM() throws RemoteException {
        listaSeries = null;
    }
    
    @Override
    public void setDataDir(String path) throws IOException {
        this.dirBase = path;
        this.subsFile = dirBase + "/subscripciones.txt";
        this.historyFile = dirBase + "/history.txt";        
    }
    
    private ArrayList<String> getLinks(int pageId) throws IOException {
        ArrayList<String> links = new ArrayList<String>();
        URL URLpagina = new URL("http://www.subadictos.net/foros/showthread.php?t=" + pageId);

        URLConnection pg = URLpagina.openConnection();
        pg.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
        try (BufferedReader in = new BufferedReader(new InputStreamReader(pg.getInputStream()))) {
            String txt;
            while ((txt = in.readLine()) != null) {
                int ini, fin;
                try {
                    if (txt.contains("ed2k")) {
                        ini = txt.indexOf("ed2k");
                        fin = txt.indexOf('"', ini);
                        links.add(txt.substring(ini, fin));                    
                    } else if (txt.contains("magnet")) {
                        ini = txt.indexOf("magnet");
                        fin = txt.indexOf('"', ini);                  
                        links.add(txt.substring(ini, fin));                                          
                    }
                }
                catch(java.lang.StringIndexOutOfBoundsException e) { }
            }   
        }
        return links;
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
                        System.out.println("ADVERTENCIA, NOT MATCH " + line);
                    }
                }
                if (!titulo.equalsIgnoreCase(filtro)) {
                    continue;
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

    @Override
    public ArrayList<String> getNewLinksList(int linkType, int season, boolean addToHistory, boolean checkLastOnly) throws IOException {
        ArrayList<String> list = new ArrayList();
        ArrayList<String> suscriptas = this.getSubscriptionList();
        
        String maskSeason = (season == 0) ? null : "S" + String.format("%02d", season);
        
        for (String s : suscriptas) {
            System.out.println("Serie: " + s);
            ArrayList<String> series = this.getSeriesList(s);
            for (int i=0; i<series.size(); i++) {
                if (checkLastOnly && i<series.size()-1) {
                    continue;
                }
                String x = series.get(i);
                String tSerie = x.split(";;;")[1];
                int pageId = Integer.parseInt(x.split(";;;")[0]);
                //System.out.println("  Temporada " + tSerie);
                for (String l : this.getLinks(pageId)) {
                    if (checkExistInFile(l, historyFile) == null) {
                        if (maskSeason != null) {
                            if (!l.contains(maskSeason)) {
                                continue;
                            }
                        }
                        switch(linkType) {
                            case SubscrManagerAPI.LINKS_ED2K:
                                if (!l.contains("ed2k")) {
                                    continue;
                                }
                                break;
                            case SubscrManagerAPI.LINKS_TORRENT:
                                if (!l.contains("magnet")) {
                                    continue;
                                }                                
                                break;    
                        }
                        list.add(l);
                        if (addToHistory == true) {
                            this.addLinkToHistory(l);
                        }
                    }
                }                
            }
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
        for (String s : lista) {
            if (key.contains(s)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addLinkToHistory(String link) throws IOException {
        if (checkExistInFile(link, historyFile) == null) {
            try (FileWriter fr = new FileWriter(new File(historyFile), true)) {
                fr.append(link + "\n");
            } catch (IOException ex) {
                System.err.println(ex.toString());
            }
        }        
    }

    private void refreshLocalSeriesList() throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("ddMMyy");
        String sDate = format.format(new Date());
        String line;
        ArrayList<String> lista;
        String fName = this.dirBase + "/" + sDate + "_series.txt";
        this.listaSeries = new HashSet<String>();
        if (!Files.exists(Paths.get(fName))) {
            System.out.println("Primera ejecucion del dia " + sDate + ". Actualizando estructuras internas.");
            lista = this.downloadList("http://www.subadictos.net/index.php?page=SeriesTodas");
            for (String s : lista) {
                this.listaSeries.add(s);
            }
            lista = this.downloadList("http://www.subadictos.net/index.php?page=TemporadasEnCurso");
            for (String s : lista) {
                this.listaSeries.add(s);
            }
            lista = this.downloadList("http://www.subadictos.net/index.php?page=SeriesTodasHD");
            for (String s : lista) {
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
            BufferedReader in = new BufferedReader(new FileReader(new File(fName)));
            while((line = in.readLine()) != null) {
                this.listaSeries.add(line);
            }
        }
    }

    private ArrayList<String> downloadList(String sUrl) throws IOException {
        URL URLpagina = new URL(sUrl);

        ArrayList<String> lista = new ArrayList<String>();
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
}
