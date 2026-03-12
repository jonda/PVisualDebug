/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PVisual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author dahjon
 */
public class PVRowList extends ArrayList<PVRow> {
    
    public void setCode(String... code){
        clear();
        //String [] codeArr = code.split("\n");
        for (int i = 0; i < code.length; i++) {
            String rowString = code[i];
            System.out.println("PVRowList i:"+i+ ": rowString = " + rowString);
            add(new PVRow((i+1), rowString, "", ""));
        }
        
    }
    
    
    void set(PVRow row) {
        removeRowIfExists(row.getRowNr());
        add(row);
        Collections.sort(this);
    }

    void removeRowIfExists(int rowNr){
        for (int i = 0; i < this.size(); i++) {
            PVRow row = this.get(i);
            if(row.getRowNr()==rowNr){
                remove(i);
            }
        }
    }
    
//    String getOrigCode(){
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < this.size(); i++) {
//            PVRow row = this.get(i);
//            sb.append(row.getRowNr());
//            sb.append(' ');
//            sb.append(row.getOrigCode());
//            sb.append('\n');
//        }
//        return sb.toString();
//    }
//    
    String getCode1(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.size(); i++) {
            PVRow row = this.get(i);
            sb.append(row.getRowNr());
            sb.append(' ');
            sb.append(row.getCode1());
            sb.append('\n');
        }
        return sb.toString();
    }
    String getCode2(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.size(); i++) {
            PVRow row = this.get(i);
            sb.append(row.getRowNr());
            sb.append(' ');
            sb.append(row.getCode2());
            sb.append('\n');
        }
        return sb.toString();
    }

    void setDebugInfo(int rowNr, String code1, String code2) {
        int i = rowNr-1;
        PVRow row = get(i);
        row.setDebugInfo(rowNr, code1, code2);
        
        
    }
    
    
}
