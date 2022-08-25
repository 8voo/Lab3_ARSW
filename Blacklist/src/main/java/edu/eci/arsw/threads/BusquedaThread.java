package edu.eci.arsw.threads;

import edu.eci.arsw.blacklistvalidator.HostBlackListsValidator;
import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

import java.util.LinkedList;
import java.util.List;

public class BusquedaThread extends Thread{

    private String ip;
    private static final int BLACK_LIST_ALARM_COUNT=5;
    private HostBlackListsValidator validator;
    private int ocurrencesCount=0;



    private int checkedListsCount=0;
    private int inicio;
    private int fin;

    private LinkedList<Integer> blackListOcurrences=new LinkedList<>();
    private HostBlacklistsDataSourceFacade skds=HostBlacklistsDataSourceFacade.getInstance();
    public BusquedaThread(String ip, int inicio, int fin){
        this.ip = ip;
        this.inicio = inicio;
        this.fin = fin;
    }

    public int getOcurrencesCount() {
        return ocurrencesCount;
    }
    public int getCheckedListsCount() {
        return checkedListsCount;
    }
    public LinkedList<Integer> getBlackListOcurrences() {
        return blackListOcurrences;
    }
    @Override
    public void run(){
        for (int i=inicio;i<fin && ocurrencesCount<BLACK_LIST_ALARM_COUNT;i++){
            checkedListsCount++;
            if (skds.isInBlackListServer(i, ip)){
                blackListOcurrences.add(i);
                ocurrencesCount++;
            }
        }
    }
}
