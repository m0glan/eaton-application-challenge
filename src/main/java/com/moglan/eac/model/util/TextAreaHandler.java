package com.moglan.eac.model.util;

import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Handler used for displaying logger messages on a {@code JTextArea}.
 * 
 * @author Vlad-Adrian Moglan
 */
public class TextAreaHandler extends StreamHandler {
    
    private final JTextArea textArea;
    
    public TextAreaHandler(JTextArea textArea) {
       this.textArea = textArea;
    }
    
    public JTextArea getTextArea() { return textArea; }
    
    @Override
    public void publish(LogRecord record) {
        super.publish(record);
        flush();
        
        SwingUtilities.invokeLater(() -> {
            textArea.append(getFormatter().format(record));
        });
    }
    
}
