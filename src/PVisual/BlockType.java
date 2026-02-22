/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package PVisual;

/**
 *
 * @author dahjon
 */
public enum BlockType {
    FOR("for-loop", "Indexvariabel"),
    WHILE("while-loop", "Jämförelsevariabel"),
    IF("if-sats", "Jämförelsevariabel"),
    FUNCTION("Funktion", "------"), UNKNOWN("Okänt block", "----");

    private final String fullName;
    private final String VariabelBeteckning;

    private BlockType(String name, String VariabelBeteckning) {
        this.fullName = name;
        this.VariabelBeteckning = VariabelBeteckning;
    }

    public String getFullName() {
        return fullName;
    }

    public String getVariabelBeteckning() {
        return VariabelBeteckning;
    }

    
};
