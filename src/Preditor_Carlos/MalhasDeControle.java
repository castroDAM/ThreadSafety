///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package Preditor_Carlos;
//
//import ScannerThread.PreditorDeSmith;
//import controler.PidControler;
//import controler.PidForm;
//import controler.PidType;
//import jcontrolsystem.TransFunction;
//import jcontrolsystem.delay2;
//import smithpredictorcontrol.SmithPredictor;
//
///**
// *
// * @author Douglas
// */
//public class MalhasDeControle {
//    
//    public static void main(String[] args) throws NoSuchMethodException, InterruptedException {
//     
//        // Planta Real
//        Double[] numReal = {0.2242, 0.1304, 0.001346};
//        Double[] denReal = {1.0, -0.6748, 0.03067, 0.00001973};
//        TransFunction plantaReal = new TransFunction(numReal, denReal, 1.0);
//        delay2 atrasoReal = new delay2(1, 1.0, 0.0);
//
//        // Planta Estimada
//        Double[] numEst = {0.2665, 0.1246, 0.009479};
//        Double[] denEst = {1.0, -0.5076, -0.0877, -0.00412};
//        TransFunction plantaEst = new TransFunction(numEst, denEst, 1.0);
//        delay2 atrasoEst = new delay2(1, 1.0, 0.0);
//
//        SmithPredictor Preditor = new SmithPredictor(plantaReal, plantaEst, atrasoReal, atrasoEst);
//
//        // Definindo o Controlador
//        PidControler controlador = new PidControler(PidType.P, PidForm.IDEAL, false, 1.0, 0.0, 0.0, 1.0, 0.0) {
//        };
//        controlador.setKp(1.7177);
//        
//        ThreadGeral threadPS = new ThreadGeral(2000);
//        
//        PreditorDeSmith PS = new PreditorDeSmith(Preditor, controlador, 0.2, 1);
//        
//        threadPS.setObject(PS);
//        threadPS.setMethod(PS.getClass().getDeclaredMethod("step", new Class[0]));
//        
//        Thread thread = new Thread(threadPS);
//        thread.start();
//        Thread.sleep(20000);
//        PS.setReference(0.5);
//        Thread.sleep(1000);
//        threadPS.killThread();
//    }   
//}
