
package subadictos;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import rmiapi.SubscrManagerAPI;

// Args: addSus|delSus|listSeries|listSus|getNewLinks <arg>

public class main {
    public static void main(String[] args) throws IOException {
        SubadictosSM sm = new SubadictosSM();
        sm.setDataDir("data");
        boolean standalone = false;
        int port = 2099;
        try {               
            Registry registry = LocateRegistry.getRegistry(port);
            registry.bind("SubscriptionManager", sm);
        } catch (RemoteException ex) {
            System.out.println("No hay RMI server en " + port);
            standalone = true;
        } catch (AlreadyBoundException ex) {
            System.out.println("Puerto " + port + " no disponible.");
            standalone = true;
        }
        
        if (standalone) {
            
            checkArgs(args, 1, "Uso: addSus|delSus|listSeries|listSus|getNewLinks <arg>");
            
            ArrayList<String> result = null;
            switch(args[0]) {

                case "addSus":
                    checkArgs(args, 2, "Uso: addSus <arg>");
                    sm.addSubscription(args[1]);
                    break;
                    
                case "delSus":
                    checkArgs(args, 2, "Uso: delSus <arg>");
                    sm.removeSubscription(args[1]);
                    break;
                    
                case "listSeries":
                    checkArgs(args, 2, "Uso: listSeries <arg>");                 
                    result = sm.getSeriesList(args[1]);
                    break;
                    
                case "listSus":          
                    result = sm.getSubscriptionList();                    
                    break;
                    
                case "getNewLinks":
                    checkArgs(args, 2, "Uso: getNewLinks <tipo_link>");
                    int arg1;
                    if (args[1].equalsIgnoreCase("ed2k")) {
                        arg1 = SubscrManagerAPI.LINKS_ED2K;
                    } else if (args[1].equalsIgnoreCase("torrent")) {
                        arg1 = SubscrManagerAPI.LINKS_TORRENT;
                    } else {
                        arg1 = SubscrManagerAPI.LINKS_ANY;
                    }
                    result = sm.getNewLinks(arg1, 0, true);
                    break;
                    
                default:
                    System.out.println("Uso: addSus|delSus|listSeries|listSus|getNewLinks <arg>");
                    System.exit(1);
            }
            
            if (result != null) {
                for (String l : result) {
                    System.out.println(l);
                }
            }
            System.out.println("Fin!");
            System.exit(0);            
        }
    }
    
    public static void checkArgs(String args[], int ctdArgs, String msg) {
        if (args.length < ctdArgs) {
            System.out.println(msg);
            System.exit(1);
        }            
    }
}
