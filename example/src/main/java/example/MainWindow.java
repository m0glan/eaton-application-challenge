/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example;

import com.moglan.eac.connection.Config;
import com.moglan.eac.connection.TCPClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * GUI used for manipulating the simulation of a server counting the number
 * of messages it receives from a number of clients.
 * 
 * @author Vlad-Adrian Moglan
 */
	public class MainWindow extends javax.swing.JFrame implements Observer {
	    
	    /**
		 * 
		 */
	private static final long serialVersionUID = 927600616316470742L;
	private CentralMonitor centralMonitor;
    private TextAreaHandler textAreaHandler; 
    private ThreadPoolExecutor clientsThreadPool;
    private List<TCPClient> clientTasks;

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
    	MessageCount.get().addObserver(this);
    	setResizable(false);
    	setTitle("Message Counting Server Simulation");
        initComponents();
        initSimulation();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        new javax.swing.JSpinner();
        serverOutputLabel = new javax.swing.JLabel();
        serverOutputScrollPane = new javax.swing.JScrollPane();
        serverOutputTextArea = new javax.swing.JTextArea();
        addClientButton = new javax.swing.JButton();
        removeClientButton = new javax.swing.JButton();
        aboutButton = new JButton("About");
        serverStatusProgressBar = new javax.swing.JProgressBar();
        serverStatusLabel = new javax.swing.JLabel();
        messagesCountTextField = new javax.swing.JTextField();
        messagesCountLabel = new javax.swing.JLabel();
        messagesCountTextField.setEditable(false);
        messagesCountTextField.setText(Integer.toString(0));
        messagesCountTextField.setHorizontalAlignment(SwingConstants.RIGHT);
        informationWindow = new InformationWindow();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        serverOutputLabel.setText("Server Ouput");

        serverOutputTextArea.setColumns(20);
        serverOutputTextArea.setRows(5);
        serverOutputScrollPane.setViewportView(serverOutputTextArea);
        serverOutputTextArea.setEditable(false);

        addClientButton.setText("Add Client");
        addClientButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addClientButtonActionPerformed(evt);
            }
        });

        removeClientButton.setText("Remove Client");
        removeClientButton.setEnabled(false);
        removeClientButton.addActionListener(new java.awt.event.ActionListener() {
        	
        	public void actionPerformed(java.awt.event.ActionEvent evt) {
        		removeClientButtonActionPerformed(evt);
        	}
        	
        });

        serverStatusLabel.setText("Server Status");
        
        serverStatusProgressBar.setMinimum(0);
        serverStatusProgressBar.setMaximum(CentralMonitor.get().getMaxNumberOfConnections());

        messagesCountTextField.setText("0");

        messagesCountLabel.setText("Message Count");
        
        informationWindow.setVisible(false);
        
        aboutButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		informationWindow.setVisible(true);
        	}
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addGap(27)
        			.addGroup(layout.createParallelGroup(Alignment.LEADING)
        				.addGroup(layout.createSequentialGroup()
        					.addGroup(layout.createParallelGroup(Alignment.LEADING)
        						.addComponent(serverStatusLabel)
        						.addComponent(messagesCountLabel))
        					.addPreferredGap(ComponentPlacement.UNRELATED)
        					.addGroup(layout.createParallelGroup(Alignment.LEADING)
        						.addGroup(layout.createSequentialGroup()
        							.addComponent(messagesCountTextField, GroupLayout.PREFERRED_SIZE, 184, GroupLayout.PREFERRED_SIZE)
        							.addGap(23)
        							.addComponent(addClientButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        							.addGap(7)
        							.addComponent(removeClientButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        							.addPreferredGap(ComponentPlacement.RELATED))
        						.addComponent(serverStatusProgressBar, GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)))
        				.addGroup(layout.createSequentialGroup()
        					.addComponent(serverOutputLabel)
        					.addPreferredGap(ComponentPlacement.RELATED, 378, Short.MAX_VALUE)
        					.addComponent(aboutButton)
        					.addPreferredGap(ComponentPlacement.RELATED))
        				.addComponent(serverOutputScrollPane, GroupLayout.DEFAULT_SIZE, 519, Short.MAX_VALUE))
        			.addGap(12))
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(serverOutputLabel)
        				.addComponent(aboutButton))
        			.addGap(7)
        			.addComponent(serverOutputScrollPane, GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(layout.createParallelGroup(Alignment.TRAILING)
        				.addComponent(serverStatusLabel)
        				.addComponent(serverStatusProgressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(messagesCountLabel)
        				.addComponent(messagesCountTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(addClientButton)
        				.addComponent(removeClientButton))
        			.addGap(13))
        );
        getContentPane().setLayout(layout);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    /**
     * Initializes the different simulation elements.
     */
    private void initSimulation() {
    	centralMonitor = CentralMonitor.get();
        textAreaHandler = new TextAreaHandler(this.serverOutputTextArea);
        
        clientsThreadPool = new ThreadPoolExecutor(centralMonitor.getMaxNumberOfConnections(),
            centralMonitor.getMaxNumberOfConnections(), 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>()
        );
        clientTasks = new ArrayList<>();
        
        centralMonitor.getLogger().addHandler(textAreaHandler);
        centralMonitor.start();
    }

    /**
     * Fires when {@code addButton} is clicked and launches the execution of a client if the
     * maximum number of clients has not been attained. Updates the different interface elements
     * accordingly.
     * 
     * @param evt is the event that fired.
     */
    private void addClientButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addClientButtonActionPerformed
        try {
            MeasuringDevice client = new MeasuringDevice("127.0.0.1", Config.PORT);
            CompletableFuture.runAsync(client, 
                    clientsThreadPool);
            
            clientTasks.add(client);
            
            serverStatusProgressBar.setValue(serverStatusProgressBar.getValue() + 1);
            removeClientButton.setEnabled(true);
            
            if (clientsThreadPool.getActiveCount() == clientsThreadPool.getMaximumPoolSize()) {
            	addClientButton.setEnabled(false);
            }
        } catch (IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_addClientButtonActionPerformed
    
    /**
     * Fires when {@code removeButton} is clicked and stops the execution of the client at the
     * end of the list if the list of clients is not empty. Updates the different interface elements
     * accordingly.
     * 
     * @param evt is the event that fired.
     */
    private void removeClientButtonActionPerformed(java.awt.event.ActionEvent evt) {
    	int lastIndex = clientTasks.size() - 1;
    	
    	clientTasks.get(lastIndex).stop();
    	clientTasks.remove(lastIndex);
    	
    	addClientButton.setEnabled(true);
    	serverStatusProgressBar.setValue(serverStatusProgressBar.getValue() - 1);
    	
    	if (clientTasks.isEmpty()) {
    		removeClientButton.setEnabled(false);
    	}
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addClientButton;
    private javax.swing.JScrollPane serverOutputScrollPane;
    private javax.swing.JLabel messagesCountLabel;
    private javax.swing.JTextField messagesCountTextField;
    private javax.swing.JButton removeClientButton;
    private JButton aboutButton;
    private javax.swing.JLabel serverOutputLabel;
    private javax.swing.JTextArea serverOutputTextArea;
    private javax.swing.JLabel serverStatusLabel;
    private javax.swing.JProgressBar serverStatusProgressBar;
    private InformationWindow informationWindow;

	@Override
	public void update(Observable o, Object arg) {
		messagesCountTextField.setText(Integer.toString(MessageCount.get().getCount()));
	}
}
