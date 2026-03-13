/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PVisualTool;

import PVisual.BlockType;
import PVisual.VisualFrame;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author dahjon
 */
public class InsertUtils {

    public final static String START_OF_FUNCTION = "pv.show";
    public final static String REMOVE_MESSAGE = " // Line will be removed when PVisualConfig is closed\n";
    final static Pattern IMPORT_PATTERN = Pattern.compile("import +PVisual.");
    final static Pattern CREATE_PV_PATTERN = Pattern.compile("PVisual +pv *= *new +PVisual *\\( *this *\\)");
    //final static Pattern REMOVE_PV_PATTERN = 
    //PVisual pv = new PVisual(this);


    public static BlockType getBlockType(StringBuilder sb, int origIndex) {
        int startIndex = getStartOfLine(sb, origIndex);
        String line = sb.substring(startIndex, origIndex).trim();
        System.out.println("getBlockType line = " + line);
        if (line.startsWith("for")) {
            return BlockType.FOR;
        } else if (line.startsWith("while")) {
            return BlockType.WHILE;
        } else if (line.startsWith("if")) {
            return BlockType.IF;
        }
        return BlockType.UNKNOWN;
    }

    public static String insertPVisualFunctions(String code, int delayValue) {
       StringBuilder sb = new StringBuilder();
       //insertCreatePv(sb, delayValue);
       CodeList cl = new CodeList(code, delayValue);
       sb.append(cl.getDebugCode());
        

        return sb.toString();
    }

    private static String getEscapedBlockCode(StringBuilder sb, int startBrace, int endBrace) {
        if (endBrace != -1) {
            int startIndex = getStartOfLine(sb, startBrace);

            String bc = sb.substring(startIndex, endBrace + 1);
            bc = bc.replace("\n", "\\n");
            bc = bc.replace("\"", "\\\"");
            return bc;
        }
        return "";

    }

    private static int getStartOfNextLine(StringBuilder sb, int index) {
        int PrevRowInd = sb.indexOf("\n", index) + 1;
        if (PrevRowInd > 0) {
            return PrevRowInd;
        } else {
            sb.append("\n");
            return sb.length() - 1;
        }
    }

    private static int getStartOfLine(StringBuilder sb, int braceInd) {
        int PrevRowInd = sb.lastIndexOf("\n", braceInd) + 1;
        return PrevRowInd;
    }

    public static void main(String[] args) {
        String code = "size(400, 400);\n" +
"fill(255, 0, 0);   \n" +
"for (int i=0; i < 10; i++) {\n" +
"  circle(20*i, 20*i, 20+10*i);\n" +
"}\n" +
"\n" +
"int a = 14;\n" +
"fill(0,0,255);\n" +
"while(a>5){\n" +
"   square(30*a,30*a,30);\n" +
"   a--;\n" +
"}\n" +
"int b = 20;\n" +
"if( b > 3 ){\n" +
"   square(20*b,20*b,10*b);\n" +
"   b--;\n" +
"}\n" +
"";
//        size(400, 400);\n"
//                + "fill(255, 0, 0);   \n"
//                + "for (int i=0; i < 10; i++) {\n"
//                + "  circle(20*i, 20*i, 20+10*i);\n"
//                + "}\n"
//                + "\n"
//                + "int a = 14;\n"
//                + "fill(0,0,255);\n"
//                + "while(a>5){\n"
//                + "   square(30*a,30*a,30);\n"
//                + "   a--;\n"
//                + "}\n"
//                + "if(b>3){\n"
//                + "   square(20*b,20*b,30);\n"
//                + "   b--;\n"
//                + "}\n";
        //      String code = "import PVisual.*;\n" + "PVisual pv = new PVisual(this);\n" + "size(400, 400);\n" + "fill(255, 0, 0);   \n" + "for (int i=0; i < 20; i++) {\n" + "  delay(500);\n" + "  circle(20*i, 20*i, 20+10*i);\n" + "}\n" + "\n" + "";
        String res = insertPVisualFunctions(code, 500);
        System.out.println("res = " + res);
    }


    static void insertCreatePv(StringBuilder sb, int delayValue) {

        Matcher m = CREATE_PV_PATTERN.matcher(sb);
        int index = 0;
        if (!m.find()) {
            Matcher im = IMPORT_PATTERN.matcher(sb);
            if (im.find()) {
                index = im.end();
                System.out.println("insertCreatePv index = " + index);
            } else {
                System.out.println("insertCreatePv: Hittade ingen import");
            }
            int nextLine = getStartOfNextLine(sb, index);
            String CREATE_PV_LINE = "PVisual pv = new PVisual(this, " + delayValue + ");" + REMOVE_MESSAGE;

            sb.insert(nextLine, CREATE_PV_LINE);
        }
    }
}
