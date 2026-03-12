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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    private String showNextLine = null;

    public void setShowNextLine(String showNextLine) {
        this.showNextLine = showNextLine;
    }

    public CodeRow(int rowNr, int blockLevelIn, String row, ArrayList<ArrayList<CodeRowVar>> rowVarsIn) {
        this.rowNr = rowNr;
        this.blockLevel = blockLevelIn;
        this.rowVars.addAll(rowVarsIn);
        ArrayList<CodeRowVar> thisRowVars = rowVars.get(rowVars.size() - 1);
        rowVars.remove(rowVars.size() - 1);
        System.out.println("->CodeRow "+rowNr+" ---------------------------------------------------");
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
        StringBuilder ret = new StringBuilder();
        ret.append("pv.show(");
        ret.append(rowNr);
        ret.append(",");
        ret.append(getVariablesString());
        ret.append(", ");
        ret.append(getRowWithInsertedOrigVariables());
        ret.append(", ");
        ret.append(tempVarsAndCode.getDebugStringExpression());
        ret.append(");\n");
        return ret.toString();
    }

    String getNextLineToShow(){
        if(showNextLine!=null){
            return "+\"\\n"+showNextLine+"\"";
        }
        return "";
    }
    public String getRowWithInsertedOrigVariables() {
        System.out.println("->getRowWithInsertedOrigVariables rowNr: " + rowNr);
        List<CodeRowVar> allVars = new ArrayList<>();
        for (int i = this.blockLevel; i >= 0; i--) {
            if (i < rowVars.size()) {
                allVars.addAll(rowVars.get(i));
            }
        }
    
        // Do NOT trim the row, to preserve indentation.
    
        // Pattern for for-loops: for(init; condition; update)
        Pattern forPattern = Pattern.compile("^(\\s*for\\s*\\([^;]*;)([^;]*)(;[^)]*\\).*)$");
        Matcher forMatcher = forPattern.matcher(row);
        if (forMatcher.find()) {
            String part1 = forMatcher.group(1);
            String condition = forMatcher.group(2);
            String part2 = forMatcher.group(3);
            
            String processedCondition = buildStringExpression(condition, allVars, null);
            
            String finalExpr = "\"" + escape(part1) + "\" + " + processedCondition + " + \"" + escape(part2) + "\"";
            return cleanupStringExpression(finalExpr);
        }
    
        // Pattern for if/while: if(condition) or while(condition)
        Pattern ifWhilePattern = Pattern.compile("^(\\s*(?:if|while)\\s*\\()([^)]*)(\\).*)$");
        Matcher ifWhileMatcher = ifWhilePattern.matcher(row);
        if (ifWhileMatcher.find()) {
            String part1 = ifWhileMatcher.group(1);
            String condition = ifWhileMatcher.group(2);
            String part2 = ifWhileMatcher.group(3);
    
            String processedCondition = buildStringExpression(condition, allVars, null);
    
            String finalExpr = "\"" + escape(part1) + "\" + " + processedCondition + " + \"" + escape(part2) + "\"";
            return cleanupStringExpression(finalExpr);
        }
    
        // Fallback for simple assignments etc.
        String assignedVar = null;
        String[] parts = row.split("=");
        if (parts.length > 1 && !row.matches(".*[<>=!]=.*")) {
            String lhs = parts[0].trim();
            String[] tokens = lhs.split("\\s+");
            assignedVar = tokens[tokens.length - 1];
        }
        return buildStringExpression(row, allVars, assignedVar);
    }
    
    private String buildStringExpression(String input, List<CodeRowVar> allVars, String assignedVar) {
        StringBuilder result = new StringBuilder("\"");
        boolean inString = false;
        boolean inCharLiteral = false;
    
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
    
            if (c == '"' && (i == 0 || input.charAt(i - 1) != '\\')) {
                inString = !inString;
            }
            if (c == '\'' && (i == 0 || input.charAt(i - 1) != '\\')) {
                inCharLiteral = !inCharLiteral;
            }
    
            if (!inString && !inCharLiteral && Character.isJavaIdentifierStart(c)) {
                StringBuilder identifier = new StringBuilder();
                identifier.append(c);
                int j = i + 1;
                while (j < input.length() && Character.isJavaIdentifierPart(input.charAt(j))) {
                    identifier.append(input.charAt(j));
                    j++;
                }
                String idString = identifier.toString();
                boolean isVar = false;
    
                if (!idString.equals(assignedVar)) {
                    for (CodeRowVar var : allVars) {
                        if (var.getVarName().equals(idString)) {
                            isVar = true;
                            break;
                        }
                    }
                }
    
                if (isVar) {
                    result.append("\"+").append(idString).append("+\"");
                } else {
                    result.append(idString);
                }
                i = j - 1; 
            } else {
                result.append(escapeChar(c));
            }
        }
    
        result.append("\"");
        return cleanupStringExpression(result.toString());
    }
    
    private String escape(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }
    
    private String escapeChar(char c) {
        if (c == '"') {
            return "\\\"";
        }
        if (c == '\\') {
            return "\\\\";
        }
        return String.valueOf(c);
    }
    
    private String cleanupStringExpression(String expr) {
        String cleaned = expr.replace("+\"\"+", "+");
        if (cleaned.equals("\"\"")) return cleaned;
    
        if (cleaned.startsWith("\"\"+")) {
            cleaned = cleaned.substring(3);
        }
        if (cleaned.endsWith("+\"\"")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        return cleaned.isEmpty() ? "\"\"" : cleaned;
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
                    + row  + "\n"  //+ "//vanlig  funcMode: " + funcMode + " blockLevel: " + blockLevel + "\n"
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
        String s = "circle(r*i, 30,30);";
        //String s = "a = int(JOptionPane.showInputDialog(\"ange a\"));";
        ArrayList<ArrayList<CodeRowVar>> vars = new ArrayList<>();
        vars.add(new ArrayList<>());
        vars.get(0).add(new CodeRowVar("int", "i", "20"));
        CodeRow r = new CodeRow(1, 0, s, vars);
        System.out.println(r.getDebugCode(false).toString());

    }
}
