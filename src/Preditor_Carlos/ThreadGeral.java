/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Preditor_Carlos;

import Preditor_Carlos.B;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Douglas
 */
public class ThreadGeral implements Runnable{
    
    long timeInit; 
    long timeFinal;
    long varTime;
    long periodo; // Periodo de Amostragem (Deve ser igual aos períodos das funções de transferência em questão)
    
    private Class classe;
    private WorkInvocation objeto;
    private Method metodo;

    private AtomicBoolean enable = new AtomicBoolean(false);
    
    public ThreadGeral(long periodo, Class classe, WorkInvocation objeto, Method metodo) {
        this.periodo = periodo;
        this.classe = classe;
        this.objeto = objeto;
        this.metodo = metodo;
    }
    
    public ThreadGeral(long periodo) {
        this.periodo = periodo;
    }
    
    public synchronized  void setObject(Object object){
        this.classe = object.getClass();
        this.objeto = (WorkInvocation) (object);
    }
    
    public synchronized void setMethod(Method metodo){
        this.metodo = metodo;
    }
    
    public synchronized void killThread(){
        this.enable.set(false);
    }
    
    public synchronized void enableThread(){
        this.enable.set(true);
    }
    
    public synchronized boolean isEnable(){
        return this.enable.get();
    }
    
    @Override
    public void run() {
        if (!this.enable.get()) {
            enableThread();
        }

        while (isEnable()) {
            this.timeInit = System.currentTimeMillis();
            this.objeto.invokeMethod(metodo);
            this.timeFinal = System.currentTimeMillis();
            this.varTime = this.timeFinal - this.timeInit;
            System.out.println("TimeVar = " + this.varTime);
            System.out.println("Periodo = " + this.periodo);
            if (this.varTime <= this.periodo) {
                try {
                    System.out.println("Tempo Gasto = " + this.varTime);
                    Thread.sleep(this.periodo - this.varTime);
                    System.out.println("Tempo Total = " + (System.currentTimeMillis() - this.timeInit));
                } catch (InterruptedException ex) {
                    Logger.getLogger(ThreadGeral.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.out.println("Saiu do Loop");
        }
    }
    
    public static void main(String[] xxx) throws NoSuchMethodException {
        B b = new B();
        ThreadGeral runnable = new ThreadGeral(10);
        Thread thread = new Thread(runnable);
        
        runnable.setObject(b);
        runnable.setMethod(b.getClass().getDeclaredMethod("doIt", new Class[0]));
        thread.start();
        try {
            Thread.sleep(3000);
        } catch (InterruptedException ex) {
            Logger.getLogger(ThreadGeral.class.getName()).log(Level.SEVERE, null, ex);
        }
        runnable.killThread();
    }
    
}
