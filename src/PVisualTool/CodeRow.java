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
    //private ArrayList<ArrayList<CodeRowVar>> rowGlobalVars = new ArrayList<>();
    private ArrayList<ArrayList<CodeRowVar>> rowVars = new ArrayList<>();
    int blockLevel;
    private BlockType blockType = BlockType.UNKNOWN;
    private boolean meaningLess = false;
    private final int rowNr;

    public CodeRow(int rowNr, int blockLevelIn, String row, ArrayList<ArrayList<CodeRowVar>> rowVarsIn) {
        this.rowNr = rowNr;
        this.blockLevel = blockLevelIn;
        this.rowVars.addAll(rowVarsIn);
        ArrayList<CodeRowVar> thisRowVars = rowVars.get(rowVars.size() - 1);
        rowVars.remove(rowVars.size() - 1);
        System.out.println("->CodeRow---------------------------------------------------");
        System.out.println("row = " + row);
        this.row = row;
        String rowTr = row.trim();
        if (rowTr.startsWith("//")
                || rowTr.startsWith("/*")
                || rowTr.isBlank()
                || rowTr.startsWith("import ")) {
            System.out.println("rad: " + rowNr + " är meningslös");
            meaningLess = true;
        }
//        if (prevRowBlockVars == null) {
//            prevRowBlockVars = new ArrayList<>();
//        }
//        if (prevRowGlobalVars == null) {
//            prevRowGlobalVars = new ArrayList<>();
//        }
//        this.rowGlobalVars.addAll(prevRowGlobalVars);
        System.out.println("CodeRow rowVars = " + rowVars);
        System.out.println("CodeRow thisRowVars = " + thisRowVars);

        //System.out.println("CodeRow efter addAll rowBlockVars = " + rowVars);
        if (blockEnd()) {

            this.blockLevel--;
            //rowVars.remove(rowVars.size() - 1);
            System.out.println("Block end blockLevel: " + blockLevel + ",(rowVars.size()-1 ) =  " + (rowVars.size() - 1));
        } //        else {
        else {
            rowVars.add(new ArrayList<CodeRowVar>(thisRowVars));

        }
        if (blockStart()) {
            this.blockLevel++;
//            blockType = InsertUtils.getBlockType(new StringBuilder(row), 0);

            if ((rowVars.size() - 1) < blockLevel) {
                rowVars.add(new ArrayList<CodeRowVar>());
                System.out.println("la till en rad (rowVars.size()-1 ) =  (" + (rowVars.size() - 1));

            }
//            String indexVariable = PVUtils.findIndexVariable(row);
//            if(!row.isBlank()){
//           
//                rowVars.get(blockLevel).add(new CodeRowVar("int", indexVariable, "NULL"));
//                blockType = BlockType.FOR;
//                
//            }

            System.out.println("Block start blockLevel: " + blockLevel + ",(rowVars.size()-1 ) =  " + (rowVars.size() - 1));
            //            System.out.println("CodeRow före rowBlockVars = " + rowVars);
            //        }

            ArrayList<CodeRowVar> funkVars = PVUtils.getFunctionParameters(this.row);
            if (funkVars != null) {
                blockType = BlockType.FUNCTION;
                System.out.println("Detta är en funktion!");
                rowVars.get(blockLevel).addAll(funkVars);
            } else {
                System.out.println("(rowVars.size()-1 < blockLevel =  (" + (rowVars.size() - 1) + " < " + blockLevel + ")");

            }
        }
        
        addVars(row, blockLevel, rowVars.get(blockLevel));

        System.out.println("CodeRow efter addLocalVars(); rowBlockVars = " + rowVars);
        ExpressionExtractor extractor = new ExpressionExtractor();

        tempVarsAndCode = extractor.extractExpressions(row, rowNr);
        System.out.println("CodeRow sist rowVars = " + rowVars);

    }

    boolean funcRow() {
        return blockType == BlockType.FUNCTION;
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
        // Flatten the list of variables, giving priority to inner scopes.
        List<CodeRowVar> allVars = new ArrayList<>();
        // Iterate from the current block level downwards to handle variable shadowing correctly.
        for (int i = this.blockLevel; i >= 0; i--) {
            if (i < rowVars.size()) {
                allVars.addAll(rowVars.get(i));
            }
        }

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

    public static void addVars(String row, int blockLevel, ArrayList<CodeRowVar> rowBlockVars) {
        String varName = PVUtils.findIndexVariable(row);
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

    String getDebugCode(boolean funcMode) {
        String ret;
        if (meaningLess || (funcMode && blockLevel == 0)) {
            ret = row + "//tom  funcMode: " + funcMode + " blockLevel: " + blockLevel + "\n";

        } else {
            ret = getExtraLines()
                    + row   //+ "//vanlig  funcMode: " + funcMode + " blockLevel: " + blockLevel + "\n"
                    + getShowLine();
        }
        return ret;
    }

    public String getRow() {
        return row;
    }

    public int getRowNr() {
        return rowNr;
    }

    public ArrayList<ArrayList<CodeRowVar>> getRowVars() {
        return rowVars;
    }

    String getVariablesString() {
        StringBuilder s = new StringBuilder();
        for (ArrayList<CodeRowVar> varList : rowVars) {
            for (CodeRowVar v : varList) {
                addVarToShowString(s, v.getshowRow());
            }
        }

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
        ArrayList<ArrayList<CodeRowVar>> vars = new ArrayList<>();
        vars.add(new ArrayList<>());
        vars.get(0).add(new CodeRowVar("int", "i", "20"));
        CodeRow r = new CodeRow(1, 0, s, vars);
        System.out.println(r.getDebugCode(false).toString());

    }
}
