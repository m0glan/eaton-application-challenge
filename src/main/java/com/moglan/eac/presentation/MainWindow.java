package com.moglan.eac.presentation;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.moglan.eac.application.Simulation;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.GridLayout;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 * Main frame launched at application startup.
 * 
 * @author Vlad-Adrian Moglan
 */
public class MainWindow extends JFrame implements Observer {

	private static final long serialVersionUID = 1L;
	
	private JPanel mainPanel;
	private JProgressBar serverStatusProgressBar;
	private JButton startSimulationButton;
	private JButton stopSimulationButton;
	private JButton addClientButton;
	private JButton removeClientButton;
	private JPanel simulationStatusBar;
	private JLabel statusLabel;
	private JTextField messageCountTextField;
	private JSlider frequencySlider;
	
	/**
	 * Program entry point.
	 * 
	 * @param args are the arguments passed to the program from a terminal/console
	 */
	public static void main(String[] args) {
		try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } catch (Exception e) {
	    	JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    }
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Creates the frame and initializes its components.
	 */
	public MainWindow() {
		setResizable(false);
		setTitle("Message Counting Server Simulation");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 499, 315);
		mainPanel = new JPanel();
		mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(mainPanel);
		
		JPanel simulationSwitchPanel = new JPanel();
		
		stopSimulationButton = new JButton("Stop");
		stopSimulationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Simulation.get().stopSimulation();
				reset();
			}
		});
		stopSimulationButton.setEnabled(false);
		
		startSimulationButton = new JButton("Start");
		startSimulationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Simulation.get().startSimulation();
				startSimulationButton.setEnabled(false);
				stopSimulationButton.setEnabled(true);
				addClientButton.setEnabled(true);
				messageCountTextField.setEnabled(true);
				serverStatusProgressBar.setEnabled(true);
			}
		});
		
		JPanel clientsControlPanel = new JPanel();
		
		JLabel activeClientsLabel = new JLabel("Active Clients");
		
		serverStatusProgressBar = new JProgressBar();
		serverStatusProgressBar.setMinimum(0);
		serverStatusProgressBar.setMaximum(Simulation.get().getServerMaximumNumberOfConnections());
		serverStatusProgressBar.setEnabled(false);
		
		addClientButton = new JButton("Add Client");
		addClientButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Simulation.get().addClientTask(frequencySlider.getValue());
					
					removeClientButton.setEnabled(true);
					serverStatusProgressBar.setValue(Simulation.get().getServerActiveCount());
					
					if (Simulation.get().getServerActiveCount() 
							== Simulation.get().getServerMaximumNumberOfConnections()) {
						addClientButton.setEnabled(false);
					}
				} catch (IOException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		removeClientButton = new JButton("Remove Client");
		removeClientButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Simulation.get().removeClientTask();
				
				addClientButton.setEnabled(true);
				serverStatusProgressBar.setValue(Simulation.get().getServerActiveCount());
				
				if (Simulation.get().getServerActiveCount() == 0) {
					removeClientButton.setEnabled(false);
				}
			}
		});
		GroupLayout gl_clientsControlPanel = new GroupLayout(clientsControlPanel);
		gl_clientsControlPanel.setHorizontalGroup(
			gl_clientsControlPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_clientsControlPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_clientsControlPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(serverStatusProgressBar, GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE)
						.addGroup(gl_clientsControlPanel.createSequentialGroup()
							.addComponent(addClientButton, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(removeClientButton))
						.addComponent(activeClientsLabel))
					.addContainerGap())
		);
		gl_clientsControlPanel.setVerticalGroup(
			gl_clientsControlPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_clientsControlPanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(activeClientsLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(serverStatusProgressBar, GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_clientsControlPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(addClientButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(removeClientButton))
					.addContainerGap())
		);
		clientsControlPanel.setLayout(gl_clientsControlPanel);
		
		simulationStatusBar = new JPanel();
		
		JPanel messageControlPanel = new JPanel();
		GroupLayout gl_mainPanel = new GroupLayout(mainPanel);
		gl_mainPanel.setHorizontalGroup(
			gl_mainPanel.createParallelGroup(Alignment.LEADING)
				.addComponent(clientsControlPanel, GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
				.addComponent(simulationSwitchPanel, GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
				.addComponent(messageControlPanel, GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
				.addComponent(simulationStatusBar, GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
		);
		gl_mainPanel.setVerticalGroup(
			gl_mainPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_mainPanel.createSequentialGroup()
					.addComponent(simulationSwitchPanel, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(clientsControlPanel, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(messageControlPanel, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
					.addComponent(simulationStatusBar, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE))
		);
		
		messageCountTextField = new JTextField();
		messageCountTextField.setText("0");
		messageCountTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		messageCountTextField.setEnabled(false);
		messageCountTextField.setEditable(false);
		messageCountTextField.setColumns(10);
		
		JLabel label = new JLabel("Message Count");
		
		JLabel sendingFrequencyLabel = new JLabel("Sending Frequency");
		
		frequencySlider = new JSlider();
		frequencySlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Simulation.get().setMessageSendingFrequency(((JSlider) e.getSource()).getValue());
			}
		});
		frequencySlider.setMinimum(Simulation.get().getMinimumSendingFrequency());
		frequencySlider.setMaximum(Simulation.get().getMaximumSendingFrequency());
		
		GroupLayout gl_messageControlPanel = new GroupLayout(messageControlPanel);
		gl_messageControlPanel.setHorizontalGroup(
			gl_messageControlPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_messageControlPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_messageControlPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(frequencySlider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(sendingFrequencyLabel))
					.addPreferredGap(ComponentPlacement.RELATED, 97, Short.MAX_VALUE)
					.addGroup(gl_messageControlPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(messageCountTextField, GroupLayout.PREFERRED_SIZE, 162, GroupLayout.PREFERRED_SIZE)
						.addComponent(label))
					.addContainerGap())
		);
		gl_messageControlPanel.setVerticalGroup(
			gl_messageControlPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_messageControlPanel.createSequentialGroup()
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addGroup(gl_messageControlPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(sendingFrequencyLabel)
						.addComponent(label))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_messageControlPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(frequencySlider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(messageCountTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(41))
		);
		messageControlPanel.setLayout(gl_messageControlPanel);
		
		statusLabel = new JLabel("Simulation is stopped.");
		GroupLayout gl_simulationStatusBar = new GroupLayout(simulationStatusBar);
		gl_simulationStatusBar.setHorizontalGroup(
			gl_simulationStatusBar.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_simulationStatusBar.createSequentialGroup()
					.addContainerGap()
					.addComponent(statusLabel)
					.addContainerGap(490, Short.MAX_VALUE))
		);
		gl_simulationStatusBar.setVerticalGroup(
			gl_simulationStatusBar.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_simulationStatusBar.createSequentialGroup()
					.addComponent(statusLabel)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		simulationStatusBar.setLayout(gl_simulationStatusBar);
		simulationSwitchPanel.setLayout(new GridLayout(0, 2, 0, 0));
		simulationSwitchPanel.add(startSimulationButton);
		simulationSwitchPanel.add(stopSimulationButton);
		mainPanel.setLayout(gl_mainPanel);
		
		Simulation.get().addObserver(this);
		
		reset();
	}
	
	/**
	 * Restores the different GUI components to their initial state.
	 */
	private void reset() {
		startSimulationButton.setEnabled(true);
		stopSimulationButton.setEnabled(false);
		addClientButton.setEnabled(false);
		removeClientButton.setEnabled(false);
		serverStatusProgressBar.setEnabled(false);
		statusLabel.setText("Simulation is stopped.");
		frequencySlider.setEnabled(false);
		frequencySlider.setValue(Simulation.get().getMaximumSendingFrequency());
	}
	
	/**
	 * Method called whenever the state of the {@code Simulation} singleton
	 * is changed.
	 */
	@Override
	public void update(Observable observable, Object arg) {
		Simulation simulation = (Simulation) observable;
		
		messageCountTextField.setText(Long.toString(simulation.getServerMessageCount()));
		serverStatusProgressBar.setValue(simulation.getServerActiveCount());
		messageCountTextField.setText(Long.toString(Simulation.get().getServerMessageCount()));
		
		if (simulation.isRunning()) {
			frequencySlider.setEnabled(true);
			
			statusLabel.setText("Simulation is running on port " + simulation.getServerPort() + "...");
		}
	}
}
