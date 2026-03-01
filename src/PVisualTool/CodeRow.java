/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PVisualTool;

import PVisual.BlockType;
import PVisual.VisualFrame;
import PVisualTool.ExpressionExtractor.TempVarsAndCodeLine;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author dahjon
 */
public class CodeRow {

    private String row;

    private TempVarsAndCodeLine tempVarsAndCode;
    private ArrayList<CodeRowVar> rowGlobalVars = new ArrayList<>();
    private ArrayList<CodeRowVar> rowBlockVars = new ArrayList<>();
    int blockLevel;
    private BlockType blockType = BlockType.UNKNOWN;
    private boolean meaningLess = false;
    private final int rowNr;

    public CodeRow(int rowNr, int blockLevel, String row, ArrayList<CodeRowVar> prevRowGlobalVars, ArrayList<CodeRowVar> prevRowBlockVars) {
        this.rowNr = rowNr;
        this.blockLevel = blockLevel;
        System.out.println("->CodeRow---------------------------------------------------");
        this.row = row;
        String rowTr = row.trim();
        if (rowTr.startsWith("//")
                || rowTr.startsWith("/*")
                || rowTr.isBlank()
                || rowTr.startsWith("import ")) {
            System.out.println("rad: " + rowNr + " är meningslös");
            meaningLess = true;
        }
        if (prevRowBlockVars == null) {
            prevRowBlockVars = new ArrayList<>();
        }
        if (prevRowGlobalVars == null) {
            prevRowGlobalVars = new ArrayList<>();
        }
        this.rowGlobalVars.addAll(prevRowGlobalVars);
        System.out.println("CodeRow efter addAll rowGlobalVars = " + rowGlobalVars);

        System.out.println("CodeRow efter addAll rowBlockVars = " + rowBlockVars);
        if (blockStart()) {
            blockLevel++;
//            blockType = InsertUtils.getBlockType(new StringBuilder(row), 0);
//            if (blockType == BlockType.FOR) {
//                rowBlockVars.add(new CodeRowVar("int", VisualFrame.extractVariable(row), "NULL"));
//            }
        }
        if (blockEnd()) {
            blockLevel--;
            System.out.println("Block end lägger inte till block vars");
        } else {
            System.out.println("CodeRow före rowBlockVars = " + rowBlockVars);
            this.rowBlockVars.addAll(prevRowBlockVars);
        }
        if (blockLevel == 0) {
            addLocalVars(row, blockLevel, rowGlobalVars);
        } else {
            addLocalVars(row, blockLevel, rowBlockVars);
        }
        System.out.println("CodeRow efter addLocalVars(); rowBlockVars = " + rowBlockVars);
        ExpressionExtractor extractor = new ExpressionExtractor();

        tempVarsAndCode = extractor.extractExpressions(row, rowNr);

    }

    boolean blockStart() {
        return row.contains("{");
    }

    boolean blockEnd() {
        return row.contains("}");
    }

    public int getBlockLevel() {
        return blockLevel;
    }

    String getExtraLines() {
        String ret = "";
        List<String> tempVars = tempVarsAndCode.getTempVariableDeclarations();
        for (int i = 0; i < tempVars.size(); i++) {
            String ar = tempVars.get(i) + "\n";
            ret += ar;

        }

        return ret;
    }

    String getEscapedRow() {

        String ret = row.replace("\n", "\\n");
        ret = ret.replace("\"", "\\\"");
        System.out.println("<- getEscapedRow ret = " + ret);
        return ret;
    }

    String getShowLine() {
        StringBuilder ret = new StringBuilder("pv.show(");
        ret.append(rowNr);
        ret.append(",");
        ret.append(getVariablesString());
        ret.append(", \"");
        ret.append(getEscapedRow());
        ret.append("\", \"");
        ret.append(getRowWithInsertedOrigVariables());
        ret.append("\", ");
        ret.append(tempVarsAndCode.getDebugStringExpression());
        ret.append(");\n");
        return ret.toString();
    }

    public String getRowWithInsertedOrigVariables() {
        List<CodeRowVar> allVars = new ArrayList<>();
        allVars.addAll(rowBlockVars);
        allVars.addAll(rowGlobalVars);

        String assignedVar = null;
        String[] parts = row.split("=");
        if (parts.length > 1) {
            String lhs = parts[0].trim();
            String[] tokens = lhs.split("\\s+");
            assignedVar = tokens[tokens.length - 1];
        }

        StringBuilder result = new StringBuilder();
        boolean inString = false;
        boolean inCharLiteral = false;
        boolean inLineComment = false;

        for (int i = 0; i < row.length(); i++) {
            char c = row.charAt(i);

            if (!inString && !inCharLiteral && c == '/' && i + 1 < row.length() && row.charAt(i + 1) == '/') {
                inLineComment = true;
            }

            if (inLineComment) {
                result.append(c);
                continue;
            }

            if (c == '"' && (i == 0 || row.charAt(i - 1) != '\\')) {
                inString = !inString;
            }

            if (c == '\'' && (i == 0 || row.charAt(i - 1) != '\\')) {
                inCharLiteral = !inCharLiteral;
            }

            if (inString || inCharLiteral) {
                result.append(c);
                continue;
            }

            if (Character.isJavaIdentifierStart(c)) {
                StringBuilder identifier = new StringBuilder();
                identifier.append(c);
                int j = i + 1;
                while (j < row.length() && Character.isJavaIdentifierPart(row.charAt(j))) {
                    identifier.append(row.charAt(j));
                    j++;
                }

                String idString = identifier.toString();
                String value = null;

                if (idString.equals(assignedVar)) {
                    // This is the variable being assigned to, so don't replace it.
                } else {
                    for (CodeRowVar var : allVars) {
                        if (var.getVarName().equals(idString)) {
                            value = var.getValue();
                            break;
                        }
                    }
                }

                if (value != null) {
                    result.append(value);
                } else {
                    result.append(idString);
                }
                i = j - 1;
            } else {
                result.append(c);
            }
        }

        String resultString = result.toString();
        return resultString.replace("\n", "\\n").replace("\"", "\\\"");
    }

    public static void addLocalVars(String row, int blockLevel, ArrayList<CodeRowVar> rowBlockVars) {
        String varName = VisualFrame.findIndexVariable(row);
        if (!varName.isEmpty()) {
            CodeRowVar newVar = new CodeRowVar(CodeRowVar.INDEXVARIABLE, varName, "");

            System.out.println("På raden fanns indexvaiabel: '" + newVar + "'");
            rowBlockVars.add(newVar);

        } else {
            String assArr[] = row.split("=");
            if (assArr.length > 1) {

                String[] creaArr = assArr[0].split(" ");
                if (creaArr.length > 1) {
                    final String varType = creaArr[0].trim();
                    varName = creaArr[1].trim();
                    if (varName.length() >= 1) {
                        CodeRowVar newVar = new CodeRowVar(varType, varName, assArr[1].trim().replace(";", ""));

                        System.out.println("På raden fanns: '" + newVar + "'");
                        rowBlockVars.add(newVar);
                    } else {
                        System.out.println("addLocalVars: hittat variabelnamn var tomt");
                    }
                }
            }
        }
    }

    String getDebugCode() {
        String ret;
        if (meaningLess) {
            ret = row;

        } else {
            ret = getExtraLines()
                    +row
                    + getShowLine()
                     ;
        }
        return ret;
    }

    public String getRow() {
        return row;
    }

    public int getRowNr() {
        return rowNr;
    }

    public ArrayList<CodeRowVar> getRowGlobalVars() {
        return rowGlobalVars;
    }

    public ArrayList<CodeRowVar> getRowBlockVars() {
        return rowBlockVars;
    }

    String getVariablesString() {
        System.out.println("rowGlobalVars = " + rowGlobalVars);
        System.out.println("rowBlockVars = " + rowBlockVars);
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < rowGlobalVars.size(); i++) {
            CodeRowVar v = rowGlobalVars.get(i);
            addVarToShowString(s, v.getshowRow());

        }
        System.out.println("getVariablesString efter lagt till globals = " + s);
        for (int i = 0; i < rowBlockVars.size(); i++) {
            CodeRowVar v = rowBlockVars.get(i);
            addVarToShowString(s, v.getshowRow());
            System.out.println("inniti loop s = " + s);
        }
//        if(s.length()>=2){
//            s = s.substring(0,s.length()-2);
//        }
        //System.out.println("getVariablesString näst sist = " + s);

        //s.append("+\"");
        if (s.isEmpty()) {
            s.append("\"\"");
        }
        System.out.println("getVariablesString sist = " + s);
        return s.toString();
    }

    void addVarToShowString(StringBuilder showString, String varName) {
        System.out.println("->addVarToShowString showString.length() = " + showString.length());
        if (!varName.isBlank()) {
            if (showString.length() != 0) {
                showString.append("+\", ");
            } else {
                showString.append("\"");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Var name är blankt, det ska inte hända: " + varName);
        }
        showString.append(varName);
    }

    public String toString() {
        return getVariablesString();
    }

    public static void main(String[] args) {
        //String s = "int apa = 2;";
        //String s = "circle(r*i, 30,30);";
        String s = "a = int(JOptionPane.showInputDialog(\"ange a\"));";
        final ArrayList<CodeRowVar> vars = new ArrayList<>();
        vars.add(new CodeRowVar("int", "i", "20"));
        CodeRow r = new CodeRow(1,0, s, vars, null);
        System.out.println(r.getDebugCode());

    }
}
