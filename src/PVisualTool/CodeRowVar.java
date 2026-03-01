/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PVisualTool;

/**
 *
 * @author dahjon
 */
public class CodeRowVar {
    private String type;
    private String varName;
    private String value;

    public CodeRowVar(String type, String varName, String value) {
        this.type = type;
        this.varName = varName;
        this.value = value;
    }


    public String getVarName() {
        return varName;
    }

    public String getValue() {
        return value;
    }
    
    public String getAssignmentRow(){
        return varName+" = "+value;
    }
    
    public String getshowRow(){
        return varName +" = \"+"+varName;
    }
    public String getshowRowSingle(){
        return "\""+varName +" = \"+"+varName;
    }
    
    
    public String getDefinitionRowWithAssignment(){
        return type + " "+getAssignmentRow();
    }

    @Override
    public String toString() {
        return getDefinitionRowWithAssignment();
    }
    
    
    
}
