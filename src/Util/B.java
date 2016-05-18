/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

/**
 *
 * @author Douglas
 */
import Util.WorkInvocation;
import java.lang.reflect.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class B extends WorkInvocation{
        public void doIt()
        {
                System.out.println("ola!");
        }
        
        @Override
        public void invokeMethod(Method m)
        {
            try {
                m.invoke(this,new Object [0]); // perceba que eu passo this !
            } catch (IllegalAccessException ex) {
                Logger.getLogger(B.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(B.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(B.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
}