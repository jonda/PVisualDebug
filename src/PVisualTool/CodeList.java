/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PVisualTool;

import java.util.ArrayList;

/**
 *
 * @author dahjon
 */
public class CodeList extends ArrayList<CodeRow> {

    String code;

    public CodeList(String code) {
        ArrayList<CodeRowVar> rowGlobalVars = new ArrayList<>();
        ArrayList<CodeRowVar> rowBlockVars = new ArrayList<>();

        this.code = code;
        String[] stringRowArr = code.split("\n");
        for (int i = 0; i < stringRowArr.length; i++) {
            String stringRow = stringRowArr[i];
            CodeRow codeRow = new CodeRow(i + 1, stringRow,rowGlobalVars, rowBlockVars);
            rowGlobalVars = codeRow.getRowGlobalVars();
            rowBlockVars = codeRow.getRowBlockVars();
            add(codeRow);
            System.out.println("codeRow :'" + codeRow+"'");

        }

    }

    public static void main(String[] args) {
        String code = "int a = 2\n"
                + "String b = \"3\"\n"
                + "for(int i=0;i<10; i++){\n"
                + "  circle(30*i, 30,30)\n"
                + "}\n"
                + "\n";
        CodeList cl = new CodeList(code);
        System.out.println("cl::\n " + cl);
        
    }
    
    public StringBuilder getDebugCode(){
        StringBuilder s= new StringBuilder();
        for (int i = 0; i < this.size(); i++) {
            CodeRow cr = this.get(i);
                 s.append(cr.getDebugCode());
                 s.append("//slut på rad"+cr.getRowNr()+"\n");
            
        }
        return s;
    }   
    public String toString(){
        String s="";
        for (int i = 0; i < this.size(); i++) {
            CodeRow cr = this.get(i);
                s += "kodrad: "+cr.getRow() + "\n"
                + "synliga variabler: "+cr.getVariablesString()+"\n" +
                  "showrow: "  +cr.getShowLine()+    "\n"+
                        "allt: "+ cr.getDebugCode();
            
        }
        return s;
    }
}
