/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.threads;


import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author hcadavid
 */
public class CountThreadsMain {
    
    public static void main(String a[]){
        CountThread threat1 = new CountThread(0, 99);
        CountThread threat2 = new CountThread(100, 199);
        CountThread threat3 = new CountThread(200, 299);

        threat1.start();
        threat2.start();
        threat3.start();
//
//        threat1.run();
//        threat2.run();
//        threat3.run();
    }
    
}
