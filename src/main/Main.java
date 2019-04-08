package main;

import util.Automata;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import util.FuncionTransicion;
import util.ParOrdenado;

public class Main {
    private Automata a1;
    private String msg, estadoInicial;
    private ArrayList<String> estados, alfabeto, estadosFinales, clon;
    private ArrayList<FuncionTransicion> transiciones;
    private ArrayList<ParOrdenado> listaPares;
    private ParOrdenado par, par1;
    
    public Main(){
        a1 = crearAutomata(1);
        msg =  "Automata ingresado: \n"+ a1;
        clon = (ArrayList)a1.getEstados().clone();
        clon.remove(a1.getEstadoInicial());
        for (int i = 0; i < a1.getEstados().size(); i++) {
            for (int j = 0; j < clon.size(); j++) {
                if(checkCompatibilidad(a1.getEstados().get(i), clon.get(j)) && 
                        !a1.getEstados().get(i).equals(clon.get(j))){
                    if(compararAutomatas(a1.getEstados().get(i), clon.get(j))){
                        //Se reduce el automata
                        reducirAutomata(a1.getEstados().get(i), clon.get(j));
                    }
                }
            }
        }
        JOptionPane.showMessageDialog(null, msg);
        msg = "Automata reducido: \n" + a1;
        JOptionPane.showMessageDialog(null, msg);
    }
    
    public Automata crearAutomata(int i){
        estados = getEstados(i);
        alfabeto = getAlfabeto(i);
        estadosFinales = getEstadosFinales(i);
        estadoInicial = getEstadoIncial(i);
        transiciones = getFuncionesTransicion();
        return new Automata(estados, alfabeto, estadoInicial, estadosFinales, transiciones);
    }
    
    public boolean compararAutomatas(String estadoInicial1, String estadoInicial2){
        
        boolean key = true;
        listaPares = new ArrayList<>();
        listaPares.add(new ParOrdenado(estadoInicial1, estadoInicial2));
        do{
            par = null;
            for (int i = 0; i < listaPares.size(); i++) {
                if(!listaPares.get(i).isChecked()){
                    par = listaPares.get(i);
                    break;
                }
            }
            if(par == null){
                key = false;
                break;
            }else {
                for (int i = 0; i < alfabeto.size(); i++) {
                    par1 = new ParOrdenado(a1.getEstado(par.getM1(), alfabeto.get(i)), a1.getEstado(par.getM2(), alfabeto.get(i)));
                    if(checkCompatibilidad(par1.getM1(), par1.getM2())){
                        if(!isOnList(par1)){
                            listaPares.add(par1);
                        }
                    }else{
                        key = false;
                        break;
                    }
                }
                par.setChecked(true);
            }
        }while(checkListaPares());
        return key;
    }
    
    public void reducirAutomata(String estado, String eliminado){
        ArrayList<FuncionTransicion> lista = a1.getTransiciones();
        ArrayList<FuncionTransicion> removed = new ArrayList();
        FuncionTransicion fun = null;
        for (int i = 0; i < lista.size(); i++) {
            fun = lista.get(i);
            if( fun.getEstadoInicial().equals(eliminado) ){
                removed.add(fun);
            }
            if(fun.getEstadoFinal().equals(eliminado) ){
                fun.setEstadoFinal(estado);
            }
        }
        lista.removeAll(removed);
        a1.getEstados().remove(eliminado);
        a1.getEstadosFinales().remove(eliminado);
        clon.remove(eliminado);
    }
    
    public ArrayList<String> getEstados(int i){
        ArrayList<String> lista = new ArrayList<>();
        String ph;
        String[] arr;
        msg = "Ingresa el conjunto de estados para el automata "+i+"\n"
                + "(los estados deben de ir separados por un espacio unicamente)";
        ph = JOptionPane.showInputDialog(msg);
        arr = ph.split(" ");
        for (int j = 0; j < arr.length; j++) {
            if(!arr[j].equals(""))
                lista.add(arr[j]);
        }
        return lista;
    }
    
    public boolean checkCompatibilidad(String estado1, String estado2){
        return (a1.esEstadoFinal(estado1) && a1.esEstadoFinal(estado2)) || 
                (!a1.esEstadoFinal(estado1) && !a1.esEstadoFinal(estado2));
    }
    
    public ArrayList<String> getAlfabeto(int i){
        ArrayList<String> lista = new ArrayList<>();
        String ph;
        String[] arr;
        msg = "Ingresa el alfabeto para el automata "+i+"\n"
                + "(los simbolos deben de ir separados por un espacio unicamente)";
        ph = JOptionPane.showInputDialog(msg);
        arr = ph.split(" ");
        for (int j = 0; j < arr.length; j++) {
            if(!arr[j].equals(""))
                lista.add(arr[j]);
        }
        return lista;
    }
    
    public ArrayList<String> getEstadosFinales(int i){
        ArrayList<String> lista = new ArrayList<>();
        String ph;
        String[] arr;
        msg = "Ingresa el conjunto de estados finales para el automata "+i+"\n"
                + "(los estados deben de ir separados por un espacio unicamente)";
        do{
            lista.clear();
            ph = JOptionPane.showInputDialog(msg);
            arr = ph.split(" ");
            for (int j = 0; j < arr.length; j++) {
                if(!arr[j].equals(""))
                    lista.add(arr[j]);
            }
        }while(!estados.containsAll(lista));
        return lista;
    }
    
    public String getEstadoIncial(int i){
        String estado;
        msg = "Ingresa el estado inicial para el automata "+i+"\n"
                + "(Solo se acepta un solo estado)";
        do{
            estado = JOptionPane.showInputDialog(msg).trim();
        }while(!estados.contains(estado));
        return estado;
    }
    
    public ArrayList<FuncionTransicion> getFuncionesTransicion(){
        ArrayList<FuncionTransicion> lista = new ArrayList<>();
        String ph;
        for (int i = 0; i < estados.size(); i++) {
            for (int j = 0; j < alfabeto.size(); j++) {
                msg = "Ingresa el estado destino de la operacion:\n"
                        + estados.get(i) + " X " + alfabeto.get(j) + " -> ?\n"
                        + "(El estado debe existir en el conjunto de estados)";
                do{
                    ph = JOptionPane.showInputDialog(msg).trim();
                }while(!estados.contains(ph));
                lista.add(new FuncionTransicion(estados.get(i), alfabeto.get(j), ph));
            }
        }
        return lista;
    }
    
    public boolean checkListaPares(){
        boolean check = false;
        for (int i = 0; i < listaPares.size(); i++) {
            if(!listaPares.get(i).isChecked())
                check = true;
        }
        return check;
    }
    
    public boolean isOnList(ParOrdenado par){
        boolean check = false;
        ParOrdenado ph;
        for (int i = 0; i < listaPares.size(); i++) {
            ph = listaPares.get(i);
            if(ph.getM1().equals(par.getM1()) && ph.getM2().equals(par.getM2()))
                check = true;
        }
        return check;
    }
    
    public static void main(String[] args) {
        new Main();
    }
}
