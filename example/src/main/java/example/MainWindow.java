package example;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import javax.swing.JTextArea;
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
import javax.swing.JScrollPane;

public class MainWindow extends JFrame implements Observer {
	
	private TextAreaHandler textAreaHandler;

	private JPanel contentPane;
	private JProgressBar serverStatusProgressBar;
	private JTextField messageCountTextField;
	private JButton startSimulationButton;
	private JButton stopSimulationButton;
	private JButton addClientButton;
	private JButton removeClientButton;
	private JButton helpButton;
	private JScrollPane scrollPane;
	private JTextArea serverLogTextArea;
	
	/**
	 * Launch the application.
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
	 * Create the frame.
	 */
	public MainWindow() {
		setResizable(false);
		setTitle("Message Counting Server Simulation");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 672, 315);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		
		JLabel serverLogLabel = new JLabel("Server Log");
		
		JLabel serverStatusLabel = new JLabel("Server Status");
		
		serverStatusProgressBar = new JProgressBar();
		serverStatusProgressBar.setMinimum(0);
		serverStatusProgressBar.setMaximum(CentralMonitor.get().getMaxNumberOfConnections());
		serverStatusProgressBar.setEnabled(false);
		
		JLabel messageCountLabel = new JLabel("Message Count");
		
		messageCountTextField = new JTextField();
		messageCountTextField.setEditable(false);
		messageCountTextField.setColumns(10);
		messageCountTextField.setText(Integer.toString(0));
		messageCountTextField.setHorizontalAlignment(SwingConstants.RIGHT);
		
		startSimulationButton = new JButton("Start");
		startSimulationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Simulation.get().startSimulation();
				startSimulationButton.setEnabled(false);
				stopSimulationButton.setEnabled(true);
				addClientButton.setEnabled(true);
				messageCountTextField.setEnabled(true);
				serverLogTextArea.setEnabled(true);
				serverStatusProgressBar.setEnabled(true);
			}
		});
		
		stopSimulationButton = new JButton("Stop");
		stopSimulationButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Simulation.get().stopSimulation();
				reset();
			}
		});
		stopSimulationButton.setEnabled(false);
		
		addClientButton = new JButton("Add Client");
		addClientButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Simulation.get().addClientTask();
					
					removeClientButton.setEnabled(true);
					serverStatusProgressBar.setValue(Simulation.get().getNumActiveConnections());
					
					if (Simulation.get().getNumActiveConnections() 
							== CentralMonitor.get().getMaxNumberOfConnections()) {
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
				serverStatusProgressBar.setValue(Simulation.get().getNumActiveConnections());
				
				if (Simulation.get().getNumActiveConnections() == 0) {
					removeClientButton.setEnabled(false);
				}
			}
		});
		
		helpButton = new JButton("Help");
		helpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Not yet implemented.", "About", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		scrollPane = new JScrollPane();
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(serverLogLabel)
								.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 511, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
								.addComponent(startSimulationButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(stopSimulationButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(addClientButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(removeClientButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(helpButton, Alignment.TRAILING))
							.addContainerGap(137, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(serverStatusProgressBar, GroupLayout.PREFERRED_SIZE, 379, GroupLayout.PREFERRED_SIZE)
								.addComponent(serverStatusLabel))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(messageCountLabel)
								.addComponent(messageCountTextField, GroupLayout.PREFERRED_SIZE, 125, GroupLayout.PREFERRED_SIZE))
							.addContainerGap(259, Short.MAX_VALUE))))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(19)
					.addComponent(serverLogLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(startSimulationButton)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(stopSimulationButton)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(addClientButton)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(removeClientButton))
						.addComponent(scrollPane))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(serverStatusLabel)
								.addComponent(messageCountLabel))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(serverStatusProgressBar, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE))
						.addComponent(messageCountTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED, 30, Short.MAX_VALUE)
					.addComponent(helpButton)
					.addContainerGap())
		);
		
		serverLogTextArea = new JTextArea();
		serverLogTextArea.setText((String) null);
		serverLogTextArea.setEnabled(false);
		serverLogTextArea.setEditable(false);
		scrollPane.setViewportView(serverLogTextArea);
		contentPane.setLayout(gl_contentPane);
		
		textAreaHandler = new TextAreaHandler(serverLogTextArea);
		
		CentralMonitor.get().getLogger().addHandler(textAreaHandler);
		
		Controller.get().addObserver(this);
		
		reset();
	}
	
	/**
	 * Restores the initial state of the different GUI components
	 */
	private void reset() {
		messageCountTextField.setText("0");
		messageCountTextField.setEnabled(false);
		startSimulationButton.setEnabled(true);
		stopSimulationButton.setEnabled(false);
		addClientButton.setEnabled(false);
		removeClientButton.setEnabled(false);
		serverStatusProgressBar.setValue(0);
		serverStatusProgressBar.setEnabled(false);
		
		new java.util.Timer().schedule( 
		        new java.util.TimerTask() {
		            @Override
		            public void run() {
		            	serverLogTextArea.setText(null);
		        		serverLogTextArea.setEnabled(false);
		            }
		        }, 
		        2000 
		);
	}
	
	@Override
	public void update(Observable o, Object arg) {
		messageCountTextField.setText(Long.toString(CentralMonitor.get().getNumRecvMessages()));
	}
	
}
