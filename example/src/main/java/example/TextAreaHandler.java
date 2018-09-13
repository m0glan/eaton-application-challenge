/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example;

import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Handler used for outputting log messages to a TextArea.
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