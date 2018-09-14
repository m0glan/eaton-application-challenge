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

/**
 * Main frame of the application.
 * 
 * @author Vlad-Adrian Moglan
 */
public class MainWindow extends JFrame implements Observer {

	private static final long serialVersionUID = 1L;
	
	private JPanel mainPanel;
	private JProgressBar serverStatusProgressBar;
	private JTextField messageCountTextField;
	private JButton startSimulationButton;
	private JButton stopSimulationButton;
	private JButton addClientButton;
	private JButton removeClientButton;
	private JButton helpButton;
	private JPanel simulationStatusBar;
	private JLabel statusLabel;
	
	/**
	 * Launches the application.
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
	 * Creates the frame and initializes all of its components.
	 */
	public MainWindow() {
		setResizable(false);
		setTitle("Message Counting Server Simulation");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 499, 231);
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
		
		JPanel simulationControlPanel = new JPanel();
		
		JLabel activeClientsLabel = new JLabel("Active Clients");
		
		serverStatusProgressBar = new JProgressBar();
		serverStatusProgressBar.setMinimum(0);
		serverStatusProgressBar.setMaximum(Simulation.get().getServerMaximumNumberOfConnections());
		serverStatusProgressBar.setEnabled(false);
		
		addClientButton = new JButton("Add Client");
		addClientButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Simulation.get().addClientTask();
					
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
		
		JLabel messageCountLabel = new JLabel("Message Count");
		
		messageCountTextField = new JTextField();
		messageCountTextField.setEditable(false);
		messageCountTextField.setColumns(10);
		messageCountTextField.setText(Integer.toString(0));
		messageCountTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		
		helpButton = new JButton("Help");
		helpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new HelpWindow(getX() + 25, getY() + 25);
			}
		});
		GroupLayout gl_simulationControlPanel = new GroupLayout(simulationControlPanel);
		gl_simulationControlPanel.setHorizontalGroup(
			gl_simulationControlPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_simulationControlPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_simulationControlPanel.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_simulationControlPanel.createSequentialGroup()
							.addComponent(addClientButton, GroupLayout.PREFERRED_SIZE, 115, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(removeClientButton))
						.addComponent(activeClientsLabel)
						.addComponent(serverStatusProgressBar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_simulationControlPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(messageCountLabel, GroupLayout.PREFERRED_SIZE, 96, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_simulationControlPanel.createParallelGroup(Alignment.TRAILING)
							.addComponent(helpButton)
							.addComponent(messageCountTextField, GroupLayout.PREFERRED_SIZE, 213, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap(82, Short.MAX_VALUE))
		);
		gl_simulationControlPanel.setVerticalGroup(
			gl_simulationControlPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_simulationControlPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_simulationControlPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(activeClientsLabel)
						.addComponent(messageCountLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_simulationControlPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_simulationControlPanel.createSequentialGroup()
							.addComponent(serverStatusProgressBar, GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_simulationControlPanel.createParallelGroup(Alignment.BASELINE)
								.addComponent(addClientButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(removeClientButton)
								.addComponent(helpButton, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)))
						.addComponent(messageCountTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		simulationControlPanel.setLayout(gl_simulationControlPanel);
		
		simulationStatusBar = new JPanel();
		GroupLayout gl_mainPanel = new GroupLayout(mainPanel);
		gl_mainPanel.setHorizontalGroup(
			gl_mainPanel.createParallelGroup(Alignment.TRAILING)
				.addComponent(simulationControlPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
				.addComponent(simulationSwitchPanel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
				.addComponent(simulationStatusBar, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
		);
		gl_mainPanel.setVerticalGroup(
			gl_mainPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_mainPanel.createSequentialGroup()
					.addComponent(simulationSwitchPanel, GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(simulationControlPanel, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(simulationStatusBar, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE))
		);
		
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
	 * Restores the initial state of the different GUI components
	 */
	private void reset() {
		messageCountTextField.setEnabled(false);
		startSimulationButton.setEnabled(true);
		stopSimulationButton.setEnabled(false);
		addClientButton.setEnabled(false);
		removeClientButton.setEnabled(false);
		serverStatusProgressBar.setEnabled(false);
		statusLabel.setText("Simulation is stopped.");
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
			statusLabel.setText("Simulation is running on port " + simulation.getServerPort() + "...");
		}
	}
	
}
