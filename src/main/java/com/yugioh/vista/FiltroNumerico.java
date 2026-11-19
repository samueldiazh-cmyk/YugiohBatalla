package com.yugioh.vista;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Impide escribir o pegar letras en los campos numéricos (ATQ / DEF)
 * y limita la cantidad de dígitos.
 */
public class FiltroNumerico extends DocumentFilter {

    private final int maxDigitos;

    public FiltroNumerico(int maxDigitos) {
        this.maxDigitos = maxDigitos;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String texto, AttributeSet attr)
            throws BadLocationException {
        replace(fb, offset, 0, texto, attr);
    }

    @Override
    public void replace(FilterBypass fb, int offset, int longitud, String texto, AttributeSet attrs)
            throws BadLocationException {
        if (texto == null) {
            return;
        }
        String digitos = texto.replaceAll("\\D", "");
        int disponible = maxDigitos - (fb.getDocument().getLength() - longitud);
        if (disponible <= 0 && !digitos.isEmpty()) {
            java.awt.Toolkit.getDefaultToolkit().beep();
            return;
        }
        if (digitos.length() > disponible) {
            digitos = digitos.substring(0, disponible);
        }
        if (!digitos.equals(texto)) {
            java.awt.Toolkit.getDefaultToolkit().beep();
        }
        super.replace(fb, offset, longitud, digitos, attrs);
    }
}
