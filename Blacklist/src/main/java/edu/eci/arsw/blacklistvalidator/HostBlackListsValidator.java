/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blacklistvalidator;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;
import edu.eci.arsw.threads.BusquedaThread;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class HostBlackListsValidator {

    private static final int BLACK_LIST_ALARM_COUNT=5;
    private LinkedList<Integer> blackListOcurrences;

    public LinkedList<Integer> getBlackListOcurrences(){
        return blackListOcurrences;
    }
    private AtomicInteger quantBL = new AtomicInteger();

    /**
     * Check the given host's IP address in all the available black lists,
     * and report it as NOT Trustworthy when such IP was reported in at least
     * BLACK_LIST_ALARM_COUNT lists, or as Trustworthy in any other case.
     * The search is not exhaustive: When the number of occurrences is equal to
     * BLACK_LIST_ALARM_COUNT, the search is finished, the host reported as
     * NOT Trustworthy, and the list of the five blacklists returned.
     * @param ipaddress suspicious host's IP address.
     * @return  Blacklists numbers where the given host's IP address was found.
     */
    public List<Integer> checkHost(String ipaddress, int N) throws InterruptedException {

        blackListOcurrences=new LinkedList<>();
        
        int ocurrencesCount=0;
        
        HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
        
        int checkedListsCount=0;
        ArrayList<BusquedaThread> listaThreads = new ArrayList<>();
//        Ahi iba el for ------------------------
        int residuo = skds.getRegisteredServersCount() % N;
        int inicio = 0;
        int fin = 0;

        for (int i = 0; i < N; i++) {
            fin += skds.getRegisteredServersCount() / N;
            BusquedaThread thread = new BusquedaThread(ipaddress, inicio, fin, quantBL);
            inicio = fin;
            listaThreads.add(thread);
            thread.start();
        }
        if(residuo > 0) {
            BusquedaThread thread = new BusquedaThread(ipaddress, inicio, (inicio+residuo), quantBL);
        }


        for(BusquedaThread t: listaThreads){
            t.join();
            ocurrencesCount += t.getOcurrencesCount();
            checkedListsCount += t.getCheckedListsCount();
            blackListOcurrences.addAll(t.getBlackListOcurrences());
        }
        if (ocurrencesCount>=BLACK_LIST_ALARM_COUNT){
            skds.reportAsNotTrustworthy(ipaddress);
        }
        else{
            skds.reportAsTrustworthy(ipaddress);
        }
        LOG.log(Level.INFO, "Checked Black Lists:{0} of {1}", new Object[]{checkedListsCount, skds.getRegisteredServersCount()});
        
        return blackListOcurrences;
    }
    
    
    private static final Logger LOG = Logger.getLogger(HostBlackListsValidator.class.getName());
    
    
    
}
