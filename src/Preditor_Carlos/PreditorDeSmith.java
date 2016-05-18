///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package Preditor_Carlos;
//
//import ScannerThread.ScanSmithPredictor;
//import Preditor_Carlos.WorkInvocation;
//import controler.PidControler;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.concurrent.atomic.AtomicBoolean;
//import java.util.concurrent.atomic.AtomicReference;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import jcontrolsystem.TimeSignal;
//import jcontrolsystem.TransFunction;
//import jcontrolsystem.delay2;
//import smithpredictorcontrol.SmithPredictor;
//
///**
// *
// * @author Douglas
// */
//public class PreditorDeSmith extends WorkInvocation{
//    
//    AtomicReference<Double> reference = new AtomicReference(); // Referencia a ser Seguida
//    AtomicBoolean conected = new AtomicBoolean(false); // Flag para parar a Thread
//    AtomicBoolean Manual = new AtomicBoolean(false); // Abrir ou fechar a malha
//    
//    AtomicReference<Double> outputPlant = new AtomicReference<>(); // Leitura Segura da Saída da Planta
//    AtomicReference<Double> outputFixed = new AtomicReference<>(); // Leitura Segura da Saída do Preditor
//    AtomicReference<Double> erroDePredição = new AtomicReference<>(); //// Leitura Segura do Erro de Predição
//    
//    long timeInit; 
//    long timeFinal;
//    long tempo;
//    long periodo; // Periodo de Amostragem (Deve ser igual aos períodos das funções de transferência em questão)
//    
//    SmithPredictor Preditor; // Preditor de Smith que deve ser Simulado
//    PidControler Controlador; // Controlador da Planta (De preferência deve estar sintonizado)
//    
//    AtomicBoolean started = new AtomicBoolean(false); // Impedir que seja feita a leitura das variáveis antes de serem definidas
//    
//    
//    TimeSignal<Number> SP = new TimeSignal<>();
//    TimeSignal<Number> MV = new TimeSignal<>();
//    TimeSignal<Number> PV = new TimeSignal<>();
//    TimeSignal<Number> PV_Corrigida = new TimeSignal<>();
//    TimeSignal<Number> Erro = new TimeSignal<>();
//    /**
//     * Método contrutor da classe
//     * @param Preditor Preditor de Smith Utilizado na simulação
//     * @param Controlador Controlador PID de toda a malha de controle
//     * @param reference Referência Inicial a ser seguida pelo sistema em malha fechada
//     * @param periodo_em_segundos Período de Amostragem da Thread
//     */
//    public PreditorDeSmith(SmithPredictor Preditor, PidControler Controlador, double reference, double periodo_em_segundos) {
//        this.Preditor = Preditor;
//        this.Controlador = Controlador;
//        this.reference.set(reference);
//        this.periodo = (long)periodo_em_segundos*1000;
//        this.tempo = (long) 0.0;
//        firstStep();
//    }
//    
//    /**
//     * Método para Matar a Thread
//     * @param conect Para Matar a thread, defina 'conect' como false
//     */
//    public void setConection(boolean conect){
//        this.conected.set(conect);
//    }
//    
//    /**
//     * Verifica se a Thread está 'Conectada'. 
//     * @return Caso o retorno seja false,
//     * ou a Thread já morreu ou esta é sua última execução
//     */
//    public boolean isConected(){
//        return this.conected.get();
//    }
//    
//    /**
//     * Muda os parâmetros da planta Real. 
//     * @param plantareal Planta que deve substituir a Real atual.
//     */
//    public void setPlantaReal(TransFunction plantareal){
//        this.Preditor.setPlantaReal(plantareal);
//    }
//    
//    /**
//     * Muda os parâmetros da planta Real. 
//     * @param plantaEstimada Planta que deve substituir a Estimada atual.
//     */
//    public void setPlantaEstimada(TransFunction plantaEstimada){
//        this.Preditor.setPlantaEstimada(plantaEstimada);
//    }
//    
//    /**
//     * Muda os Parâmetros do Filtro do Preditor
//     * @param filtro Novo filtro para o Preditor de Smith.
//     * Caso o preditor não tenha filtro, o mesmo assume valor 1.
//     */
//    public void setFiltro(TransFunction filtro){
//        this.Preditor.setFiltro(filtro);
//    }
//    
//    /**
//     * Define se o Preditor de Smith será filtrado ou não
//     * @param seletor Caso False, o Preditor não terá Filtro
//     * Caso True, o Preditor irá trabalhar com o filtro previamente definido.
//     * Não existindo um Filtro já definido, a planta irá continuar a trabalhar sem um Filtro.
//     * Para trabalhar com um filtro, é necessário primeiro definir um filtro pelo construtor ou 
//     * usar o método setFiltro(TransFunction filtro), para depois usar selectFiltro(true)
//     */
//    public void selectFiltro(boolean seletor){
//        this.Preditor.SelectFiltro(seletor);
//    }
//    
//    /**
//     * Muda os parâmetros do Atraso Real
//     * @param delayreal Novo Atraso Real
//     */
//    public void setDelayReal(delay2 delayreal){
//        this.Preditor.setDelayReal(delayreal);
//    }
//    
//    /**
//     * Muda os Parâmetros do Atraso Estimado
//     * @param delayEstimado Novo atraso Estimado
//     */
//    public void setDelayEstimado(delay2 delayEstimado){
//        this.Preditor.setDelayEstimado(delayEstimado);
//    }
//    
//    /**
//     * Define a Referência a ser seguida
//     * @param referencia Caso a planta esteja em malha fechada, a planta tenderá a seguir este valor.
//     * Caso a planta esteja em malha aberta, este será o valor do sinal de Controle
//     */
//    public void setReference(double referencia){
//        this.reference.set(referencia);
//        System.out.println("Mudou a referencia");
//    }
//    
//    /**
//     * Lê a referencia que está sendo seguida
//     * @return Caso a planta esteja em malha fechada, a planta tenderá a seguir este valor.
//     * Caso a planta esteja em malha aberta, este será o valor do sinal de Controle
//     */
//    public Double getReference(){
//        return this.reference.get();
//    }
//    
//    /**
//     * Fecha a Malha de Controle
//     */
//    public void closeLoop(){
//        this.Manual.set(false);
//    }
//    
//    /**
//     * Abre a malha de Controle - No primeiro instante, o sinal de referência será a última
//     * MV aplicada ao sistema. Isso permanecerá até uma nova orientação de referência
//     */
//    public void openLoop(){
//        this.Manual.set(true);
//        setReference(Controlador.controlSignal());
//    }
//    
//    /**
//     * Verifica Se a malha está em Manual
//     * @return Se a malha estiver em Manual(Aberta), retornará true. Malha fechada retorna False
//     */
//    synchronized public boolean getManual(){
//        return this.Manual.get();
//    }
//    
//    /**
//     * Retorna a Saída da Planta Real
//     * @return Retorna a Última Saída Calculada da Planta Real
//     * Só deve ser realizada quando o método isReadyToRead() retornar um valor True
//     */
//    synchronized public double getOutputPlant(){
//        return this.outputPlant.get();
//    }
//    
//    /**
//     * Retorna a Saída do Preditor de Smith, seja ele Filtrado ou Não
//     * @return Retorna a Última Saída Calculada do Preditor de Smith.
//     * Só deve ser realizada quando o método isReadyToRead() retornar um valor True
//     */
//     synchronized public double getOutputPredictor(){
//        return this.outputFixed.get();
//    }
//    
//    /**
//     * Erro de Predição
//     * @return Retorna o últime erro de Predição calculado
//     * Só deve ser realizada quando o método isReadyToRead() retornar um valor True
//     */
//    synchronized public double getErroDePredição(){
//        return this.erroDePredição.get();
//    }
//    
//    /**
//     * Verifica se já é possível ler as saídas proveninetes do Preditor
//     * @return Caso os valores ainda não tenham sido Calculados, será retornado False.
//     * Quando retornar True, os metodos de leitura das variáveis poderão ser utilizados com segurança
//     */
//    synchronized public boolean isReadyToRead(){
//        return this.started.get();
//    }
//    
//    private void firstStep(){
//        Controlador.setError(0.0);
//        Preditor.setInput(new TimeSignal<>(this.tempo, Controlador.controlSignal()));
//        Controlador.setError(this.reference.get() - Preditor.getOutputSmithPredictor().getLastAmostra().doubleValue());
//    }
//    
//    public void step(){
//        
//        this.timeInit = System.currentTimeMillis();
//            double SetPoint = getReference();
//            
//            if(getManual()){
//                MV.addAmostra(this.tempo, SetPoint);
//            } else {
//                MV.addAmostra(this.tempo, Controlador.controlSignal());
//            }
//                    
//            Preditor.setInput(MV);
//            PV.addAmostra(this.tempo, Preditor.getOutputPlant().getLastAmostra().doubleValue());
//            PV_Corrigida.addAmostra(this.tempo, Preditor.getOutputSmithPredictor().getLastAmostra().doubleValue());
//
//            SP = new TimeSignal(this.tempo, SetPoint);
//            Erro = (TimeSignal<Number>) SP.subtSignals(new TimeSignal<>(this.tempo, PV_Corrigida.getLastAmostra().doubleValue()));
//            Controlador.setError(Erro.getLastAmostra().doubleValue());
//            
//            System.out.println("Passo " + this.tempo);
//            System.out.println("SP = " + SetPoint);
//            System.out.println("MV = " + MV.getLastAmostra().doubleValue());
//            System.out.println("PV = " + PV.getLastAmostra().doubleValue());
//            System.out.println("PV Corrigida = " + PV_Corrigida.getLastAmostra().doubleValue());
//            System.out.println("");
//            
//            this.outputPlant.set(PV.getLastAmostra().doubleValue());
//            this.outputFixed.set(PV_Corrigida.getLastAmostra().doubleValue());
//            this.erroDePredição.set(Preditor.getPredictionError().getLastAmostra().doubleValue());
//            
//            this.started.set(true);
//            
//            this.timeFinal = System.currentTimeMillis();
//            try {
//                Thread.sleep(this.periodo - (this.timeFinal-this.timeInit));
////                System.out.println("Tempo Gasto = " + (this.timeFinal-this.timeInit));
////                System.out.println("Tempo Total = " + (System.currentTimeMillis() - this.timeInit));
//            } catch (InterruptedException ex) {
//                Logger.getLogger(ScanSmithPredictor.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        
//        this.tempo = this.tempo + this.periodo;
//    }
//    
//    @Override
//    public void invokeMethod(Method m) {
//        try {
//            m.invoke(this, new Object [0]);
//        } catch (IllegalAccessException ex) {
//            Logger.getLogger(PreditorDeSmith.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IllegalArgumentException ex) {
//            Logger.getLogger(PreditorDeSmith.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (InvocationTargetException ex) {
//            Logger.getLogger(PreditorDeSmith.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//}
