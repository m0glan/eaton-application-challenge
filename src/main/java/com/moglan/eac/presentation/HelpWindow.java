package com.moglan.eac.presentation;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import java.awt.Toolkit;
import javax.swing.JTextPane;
import java.awt.Font;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Pop-up window used for displaying instructions concerning the simulation.
 * 
 * @author Vlad-Adrian Moglan
 */
public class HelpWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public HelpWindow(int x, int y) {
		setTitle("Help");
		setIconImage(Toolkit.getDefaultToolkit().getImage(HelpWindow.class.getResource("/javax/swing/plaf/metal/icons/Inform.gif")));
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(x, y, 448, 173);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		setVisible(true);
		
		SimpleAttributeSet sa = new SimpleAttributeSet();
		StyleConstants.setAlignment(sa, StyleConstants.ALIGN_JUSTIFIED);
		
		JTextPane aboutTextPanel = new JTextPane();
		aboutTextPanel.setEnabled(false);
		aboutTextPanel.setEditable(false);
		aboutTextPanel.getStyledDocument().setParagraphAttributes(0, 0, sa, false);
		aboutTextPanel.setFont(new Font("Arial", Font.PLAIN, 13));
		aboutTextPanel.setText("The simulation launches a server that is able to accept a maximum number of connections equal to the number of cores of the PC + 1. Adding or removing clients can be done using the respective buttons. The main function of the server is that it can count the number of messages it receives.");
		
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(aboutTextPanel, GroupLayout.PREFERRED_SIZE, 432, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
					.addContainerGap(357, Short.MAX_VALUE)
					.addComponent(closeButton)
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(aboutTextPanel, GroupLayout.PREFERRED_SIZE, 91, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
					.addComponent(closeButton)
					.addContainerGap())
		);
		contentPane.setLayout(gl_contentPane);
	}
}
