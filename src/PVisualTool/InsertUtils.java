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
    public final static String IMPORT_LINE = "import PVisual.*;" + REMOVE_MESSAGE;
    final static Pattern IMPORT_PATTERN = Pattern.compile("import +PVisual.");
    final static Pattern CREATE_PV_PATTERN = Pattern.compile("PVisual +pv *= *new +PVisual *\\( *this *\\)");
    //final static Pattern REMOVE_PV_PATTERN = 
    //PVisual pv = new PVisual(this);

    static String removePVisualFunctions(String code) {
        StringBuilder sb = new StringBuilder(code);

        int foundIndex = sb.indexOf(REMOVE_MESSAGE);
        while (foundIndex >= 0) {
            int start = getStartOfLine(sb, foundIndex);
            int end = getStartOfNextLine(sb, foundIndex);
            System.out.println("start = " + start + ", end = " + end + ", sb.length() = " + sb.length());
            System.out.println("sb.substring(start, end): " + sb.substring(start, end));
            sb.delete(start, end);

            foundIndex = sb.indexOf(REMOVE_MESSAGE, start);
            System.out.println("foundIndex = " + foundIndex + ", sb.length() = " + sb.length());
        }
        return sb.toString();

    }

    static private boolean checkIfAlreadyThere(StringBuilder sb, int origIndex) {
        System.out.println("->checkIfAlreadyThere:  origIndex = " + origIndex);
        int startIndex = getStartOfLine(sb, origIndex - 2);
        int endIndex = getStartOfNextLine(sb, origIndex) - 1;

        if (endIndex == -1) {
            endIndex = sb.length() - 1;
        }

        System.out.println("startIndex = " + startIndex + ", endIndex = " + endIndex);
        System.out.println("sb.length() = " + sb.length());
        if (endIndex > startIndex) {
            String line = sb.subSequence(startIndex, endIndex).toString();
            line = line.trim();
            System.out.println("line = " + line);
            return line.startsWith(START_OF_FUNCTION);
        }
        return false;
    }

    public static BlockType getBlockType(StringBuilder sb, int origIndex) {
        int startIndex = getStartOfLine(sb, origIndex);
        String line = sb.substring(startIndex, origIndex).trim();
        System.out.println("getBlockType line = " + line);
        if (line.startsWith("for")) {
            return BlockType.FOR;
        } else if (line.startsWith("while")) {
            return BlockType.WHILE;
        }
        return BlockType.UNKNOWN;
    }

    public static String insertPVisualFunctions(String code,int delayValue) {
        StringBuilder sb = new StringBuilder(code);
        insertImport(sb);
        insertCreatePv(sb, delayValue);
        int braceInd = sb.indexOf("{");

        while (braceInd > 0) {
            int curIndex = handleBlock(code, sb, braceInd);
            if (curIndex == -1) {
                braceInd = -1;
            } else {
                braceInd = sb.indexOf("{", curIndex);
            }
        }
        return sb.toString();
    }

    private static String getEscapedBlockCode(StringBuilder sb, int startBrace, int endBrace) {
        if (endBrace != -1) {
            int startIndex = getStartOfLine(sb, startBrace);

            String bc = sb.substring(startIndex, endBrace+1);
            bc = bc.replace("\n", "\\n");
            bc = bc.replace("\"", "\\\"");
            return bc;
        }
        return "";

    }

    private static int handleBlock(String code, StringBuilder sb, int braceInd) {
        BlockType type = getBlockType(sb, braceInd);
        System.out.println("type = " + type);
        String indexVariableName = "";  
        if (type == BlockType.FOR) {
            indexVariableName = VisualFrame.findIndexVariable(code);
        } else if (type == BlockType.WHILE) {
            indexVariableName = VisualFrame.extractVariable(code);
        }
        //sb.insert(PrevRowInd+1, "pv.show();\n");
        int endBraceIndex = sb.indexOf("}", braceInd);
        System.out.println("braceInd = " + braceInd + ", endBraceIndex = " + endBraceIndex);
        if (endBraceIndex != -1) {
            String blockCode = getEscapedBlockCode(sb, braceInd, endBraceIndex);
            System.out.println("blockCode = '" + blockCode + "' ::end blockCode");
            String textToInsert = "  pv.show(\"" + blockCode + "\", " + indexVariableName + ", BlockType." + type.name() + ");" + REMOVE_MESSAGE;
            final int lastInBlockIndex = getStartOfLine(sb, endBraceIndex);

            if (!checkIfAlreadyThere(sb, lastInBlockIndex)) {
                sb.insert(lastInBlockIndex, textToInsert);
                endBraceIndex += textToInsert.length();
            }
            if (type == BlockType.FOR || type == BlockType.WHILE) {
                if (type == BlockType.FOR) {
                    textToInsert = "  pv.showAfterFor();" + REMOVE_MESSAGE;
                }
                final int afterBlockIndex = getStartOfNextLine(sb, endBraceIndex);
                System.out.println("afterBlockIndex = " + afterBlockIndex);
                if (!checkIfAlreadyThere(sb, afterBlockIndex + 3)) {
                    sb.insert(afterBlockIndex, textToInsert);
                }
                endBraceIndex+=textToInsert.length();
            }
            System.out.println("endBraceIndex = " + endBraceIndex);
        }
        return endBraceIndex;
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
        String code = "size(400, 400);\n"
                + "fill(255, 0, 0);   \n"
                + "for (int i=0; i < 10; i++) {\n"
                + "  circle(20*i, 20*i, 20+10*i);\n"
                + "}\n"
                + "\n"
                + "int a = 14;\n"
                + "fill(0,0,255);\n"
                + "while(a>5){\n"
                + "   square(30*a,30*a,30);\n"
                + "   a--;\n"
                + "}";
        //      String code = "import PVisual.*;\n" + "PVisual pv = new PVisual(this);\n" + "size(400, 400);\n" + "fill(255, 0, 0);   \n" + "for (int i=0; i < 20; i++) {\n" + "  delay(500);\n" + "  circle(20*i, 20*i, 20+10*i);\n" + "}\n" + "\n" + "";
        String res = insertPVisualFunctions(code, 500);
        System.out.println("res = " + res);
    }

    static void insertImport(StringBuilder sb) {

        Matcher m = IMPORT_PATTERN.matcher(sb);

        if (!m.find()) {
            sb.insert(0, IMPORT_LINE);
        }
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
            String CREATE_PV_LINE = "PVisual pv = new PVisual(this, "+delayValue+");" + REMOVE_MESSAGE;
            
            sb.insert(nextLine, CREATE_PV_LINE);
        }
    }
}
