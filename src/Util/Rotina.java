/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author Douglas
 */
public class Rotina{
    
    private AtomicBoolean enable = new AtomicBoolean(false);
    
    public synchronized void killThread(){
        this.enable.set(false);
    }
    
    public synchronized void enableThread(){
        this.enable.set(true);
    }
    
    public synchronized boolean isEnable(){
        return this.enable.get();
    }
    
}
