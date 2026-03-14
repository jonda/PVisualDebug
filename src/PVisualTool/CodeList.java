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

    //String code;
    int blockLevel = 0;
    private boolean funcMode = false;
    private int lastIndexWithImport = -1;
    private int sizeOrSetupRow = -1;
    public final String PV_IMPORT_LINE = "import PVisual.*;\n";
    int delayValue;
    boolean sizeOrSetupRowFound = false;

    public CodeList(String code, int delayValue) {
        this.delayValue = delayValue;
        ArrayList<ArrayList<CodeRowVar>> rowVars = new ArrayList<>();
        rowVars.add(new ArrayList<>());
        //this.code = code;
        String[] stringRowArr = code.split("\n");
        for (int i = 0; i < stringRowArr.length; i++) {
            String stringRow = stringRowArr[i];
            if (stringRow.trim().startsWith("import ")) {
                lastIndexWithImport = i;
                sizeOrSetupRow = i+1;
            }
            if (!sizeOrSetupRowFound && (stringRow.trim().startsWith("size(") || stringRow.trim().startsWith("void setup("))) {
                sizeOrSetupRow = i;
                sizeOrSetupRowFound = true;

                System.out.println("size eller setup hittad sizeOrSetupRow = " + sizeOrSetupRow + " stringRow: " + stringRow);
            }
            final int rowNr = i + 1;
            System.out.println("BlockLevel vid anrop på rad " + rowNr + ": " + blockLevel + ",rowVars.size(): " + rowVars.size());
            CodeRow codeRow = new CodeRow(rowNr, blockLevel, stringRow, rowVars);
            rowVars = new ArrayList<ArrayList<CodeRowVar>>();

            rowVars.addAll(codeRow.getRowVars());
            blockLevel = codeRow.getBlockLevel();
            add(codeRow);
            if (codeRow.funcRow()) {
                funcMode = true;
            }
            System.out.println("codeRow :'" + codeRow + "'");
        }
        for (int i = 1; i < size(); i++) { //Obs det ska vara 1
            if (get(i).blockEnd()) {
                final CodeRow line = get(i);
                final String extraLinjeAttVisa = line.getEscapedRow();
                System.out.println("extraLinjeAttVisa = " + extraLinjeAttVisa);
                get(i - 1).setShowNextLine(extraLinjeAttVisa);
            }
        }
    }

    public static void main(String[] args) {
//        String code = "int a = 2\n"
//                + "String b = \"3\"\n"
//                + "for(int i=0;i<10; i++){\n"
//                + "  circle(30*i, 30,30)\n"
//                + "}\n"
//                + "\n";

String code = "// Skapa ett prgram som räknar ut ekvationen x2/200=300, genom att \n" +
"// först sätta x till noll, och sedan öka x med 0.1 och sedan sätta \n" +
"// y=x*x/200 och köra så länge som y<300\n" +
"\n" +
"size(400, 400);\n" +
"background(0);\n" +
"float  x=0;\n" +
"float y=0;\n" +
"fill(255,0,0);\n" +
"noStroke();\n" +
"while (y<300) {\n" +
"  y=x*x/200;\n" +
"  println(x+\", \"+y);\n" +
"  ellipse(x, height-y, 3, 3);\n" +
"  x=x+.1;\n" +
"}";
//
//String code = "import javax.swing.*;\n" +
//"\n" +
//"String str = JOptionPane.showInputDialog(\"Ange temperatur i fahrenheit \");\n" +
//"float faren = float(str);\n" +
//"float cel = (faren-32) * 5.0/9;\n" +
//"fill(0);\n" +
//"text(faren+\" grader fahrenheit är \"+cel + \" grader celsius\",10,10, 90,90);\n" +
//"";
//        String code = "import javax.swing.JOptionPane;\n"
//                + "size(400, 400);\n"
//                + "int a = 2;\n"
//                + "fill(0,255,255);\n"
//                + "\n"
//                + "while(10>a){\n"
//                + "  //Första whileloopen\n"
//                + "   square(30*a,30*a,30);\n"
//                + "   a = int(JOptionPane.showInputDialog(\"ange a\"));\n"
//                + "}\n"
//                + "for (int i=0; i < 10; i++) {\n"
//                + "  circle(20*i, 20*i, 20+10*i);\n"
//                + "}\n"
//                + "\n"
//                + " a = 14;\n"
//                + "fill(0,0,255);\n"
//                + "while(a>5){\n"
//                + "   square(30*a,30*a,30);\n"
//                + "   a = a - 1;\n"
//                + "}\n"
//                + "fill(255, 0, 0);   \n"
//                + "\n"
//                + "int b = 4;\n"
//                + "if( b > 3 ){\n"
//                + "   square(20*b,20*b,40*b);\n"
//                + "   b--;\n"
//                + "}";
//        String code = "int i = 0;\n"
//                + "void setup() {\n"
//                + "   size(400, 400);\n"
//                + "   \n"
//                + "}\n"
//                + "\n"
//                + "void draw() {\n"
//                + "  \n"
//                + "  circle(5*i,5*i,10*i);\n"
//                + "   i=i+1;\n"
//                + "   if(i==10){\n"
//                + "     noLoop();\n"
//                + "   }\n"
//                + "}";
        CodeList cl = new CodeList(code, 50);
        final StringBuilder res = cl.getDebugCode();
        System.out.println("res:\n " + res);

    }

    StringBuilder getPVCodeLines() {
        StringBuilder ret = new StringBuilder();
        ret.append("pv.setCode(\"");

        for (int i = 0; i < this.size(); i++) {

            CodeRow row = this.get(i);

            ret.append(row.getEscapedRow());
            if (i < this.size() - 1) {
                ret.append("\",\"");
            } else {
                ret.append("\"");
            }
//        ret.append(getNextLineToShow());

        }
        ret.append(");\n");
        return ret;
    }

    public StringBuilder getDebugCode() {
        System.out.println("->getDebugCode lastIndexWithImport: " + lastIndexWithImport + ", sizeOrSetupRow: " + sizeOrSetupRow);
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < this.size(); i++) {
            CodeRow cr = this.get(i);
            if (lastIndexWithImport == i - 1) {
                String CREATE_PV_LINE = "PVisual pv = new PVisual(this, " + delayValue + ");\n";
                s.append(PV_IMPORT_LINE+CREATE_PV_LINE);
                System.out.println("getDebugCode stoppar in import i = " + i + ",cr = " + cr);

            }
            String extraInTheMiddle = "";
            if (sizeOrSetupRow == i) {
                extraInTheMiddle = getPVCodeLines().toString();
                System.out.println("getDebugCode stoppar in setCode i = " + i + ",cr = " + cr + ", , getPVCodeLines() = " + getPVCodeLines());
            }
            final String debugCode = cr.getDebugCode(funcMode, extraInTheMiddle);
            System.out.println("debugCode = " + debugCode);
            s.append(debugCode);
            s.append("//slut på rad" + cr.getRowNr() + "\n");

        }
        return s;
    }

    public String toString() {
        String s = "";
        for (int i = 0; i < this.size(); i++) {
            CodeRow cr = this.get(i);
            s += "kodrad: " + cr.getRow() + "\n"
                    + "synliga variabler: " + cr.getVariablesString() + "\n"
                    + "showrow: " + cr.getShowLine() + "\n"
                    + "allt: " + cr.getDebugCode(funcMode, "");

        }
        return s;
    }

    public boolean isFuncMode() {
        return funcMode;
    }
}
