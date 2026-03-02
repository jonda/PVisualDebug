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
    private boolean funcMode = false;

    public CodeList(String code) {
        ArrayList<ArrayList<CodeRowVar>> rowVars = new ArrayList<>();
        rowVars.add(new ArrayList<>());
        this.code = code;
        String[] stringRowArr = code.split("\n");
        for (int i = 0; i < stringRowArr.length; i++) {
            String stringRow = stringRowArr[i];
            final int rowNr = i + 1;
            System.out.println("BlockLevel vid anrop på rad "+rowNr+": "+blockLevel + ",rowVars.size(): "+rowVars.size());
            CodeRow codeRow = new CodeRow(rowNr, blockLevel,stringRow,rowVars);
            rowVars = new ArrayList<ArrayList<CodeRowVar>>();

            rowVars.addAll(codeRow.getRowVars());
            blockLevel = codeRow.getBlockLevel();
            add(codeRow);
            if(codeRow.funcRow()){
                funcMode = true;
            }
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
        
//        String code = "int i = 0;\n" +
//"void setup() {\n" +
//"   size(400, 400);\n" +
//"   \n" +
//"}\n" +
//"\n" +
//"void draw() {\n" +
//"  \n" +
//"  circle(5*i,5*i,10*i);\n" +
//"   i=i+1;\n" +
//"   if(i==10){\n" +
//"     noLoop();\n" +
//"   }\n" +
//"}";
        CodeList cl = new CodeList(code);
        final StringBuilder res = cl.getDebugCode();
        System.out.println("res:\n " + res);
        
    }
    
    public StringBuilder getDebugCode(){
        StringBuilder s= new StringBuilder();
        for (int i = 0; i < this.size(); i++) {
            CodeRow cr = this.get(i);
                 s.append(cr.getDebugCode(funcMode));
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
                        "allt: "+ cr.getDebugCode(funcMode);
            
        }
        return s;
    }

    public boolean isFuncMode() {
        return funcMode;
    }
}
