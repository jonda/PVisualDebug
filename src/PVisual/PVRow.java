/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PVisual;

/**
 *
 * @author dahjon
 */
public class PVRow implements Comparable<PVRow>{
    int rowNr;
    String origCode;
    String code1;
    String code2;

    public PVRow(int rowNr, String origCode, String code1, String code2) {
        this.rowNr = rowNr;
        this.origCode = origCode;
        this.code1 = code1;
        this.code2 = code2;
    }

    public int getRowNr() {
        return rowNr;
    }

    public String getOrigCode() {
        return origCode;
    }

    public String getCode1() {
        return code1;
    }

    public String getCode2() {
        return code2;
    }

    
    
    @Override
    public int compareTo(PVRow t) {
        return rowNr-t.rowNr;
    }
    
    
}
