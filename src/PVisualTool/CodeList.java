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
    int blockLevel = 0;

    public CodeList(String code) {
        ArrayList<CodeRowVar> rowGlobalVars = new ArrayList<>();
        ArrayList<CodeRowVar> rowBlockVars = new ArrayList<>();

        this.code = code;
        String[] stringRowArr = code.split("\n");
        for (int i = 0; i < stringRowArr.length; i++) {
            String stringRow = stringRowArr[i];
            CodeRow codeRow = new CodeRow(i + 1, blockLevel,stringRow,rowGlobalVars, rowBlockVars);
            rowGlobalVars = codeRow.getRowGlobalVars();
            rowBlockVars = codeRow.getRowBlockVars();
            blockLevel = codeRow.getBlockLevel();
            add(codeRow);
            System.out.println("codeRow :'" + codeRow+"'");

        }

    }

    public static void main(String[] args) {
//        String code = "int a = 2\n"
//                + "String b = \"3\"\n"
//                + "for(int i=0;i<10; i++){\n"
//                + "  circle(30*i, 30,30)\n"
//                + "}\n"
//                + "\n";
        
        String code = "import javax.swing.JOptionPane;\n" +
"size(400, 400);\n" +
"int a = 2;\n" +
"fill(0,255,255);\n" +
"\n" +
"while(10>a){\n" +
"  //Första whileloopen\n" +
"   square(30*a,30*a,30);\n" +
"   a = int(JOptionPane.showInputDialog(\"ange a\"));\n" +
"}\n" +
"for (int i=0; i < 10; i++) {\n" +
"  circle(20*i, 20*i, 20+10*i);\n" +
"}\n" +
"\n" +
" a = 14;\n" +
"fill(0,0,255);\n" +
"while(a>5){\n" +
"   square(30*a,30*a,30);\n" +
"   a = a - 1;\n" +
"}\n" +
"fill(255, 0, 0);   \n" +
"\n" +
"int b = 4;\n" +
"if( b > 3 ){\n" +
"   square(20*b,20*b,40*b);\n" +
"   b--;\n" +
"}";
        CodeList cl = new CodeList(code);
        final StringBuilder res = cl.getDebugCode();
        System.out.println("res:\n " + res);
        
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
