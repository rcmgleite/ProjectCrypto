package src.gui;

import javax.swing.JApplet;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JPanel;

import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Color;

import com.sun.xml.internal.ws.api.Component;
import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class AppletCrypto extends JApplet {

	private JButton bt_start_aes  = null;
	private JButton bt_start_rsa  = null;
	private JButton bt_start_hmac = null;
	private JButton bt_start_dsa  = null;

	private JPanel jPanel = null;

	private JTextField txtProjectCrypto;

	/**
	 * This method initializes 
	 * 
	 */
	public AppletCrypto() {
		super();
		
	}

	/**
	 * This method initializes this
	 * 
	 */
	public void init() {
        this.setSize(new Dimension(300, 300));
        this.setContentPane(getJPanel());
			
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setForeground(new Color(0, 0, 0));
			jPanel.setBackground(Color.LIGHT_GRAY);
			jPanel.setLayout(null);

			jPanel.add(getBt_start_aes());
			jPanel.add(getBt_start_rsa());			
			jPanel.add(getBt_start_dsa());			
			jPanel.add(getBt_start_hmac());
			
			txtProjectCrypto = new JTextField();
			txtProjectCrypto.setHorizontalAlignment(SwingConstants.CENTER);
			txtProjectCrypto.setBackground(Color.LIGHT_GRAY);
			txtProjectCrypto.setFont(new Font("Tahoma", Font.BOLD, 16));
			txtProjectCrypto.setForeground(Color.BLACK);
			txtProjectCrypto.setText("Project Crypto - v1.0");
			txtProjectCrypto.setBounds(59, 34, 184, 22);
			jPanel.add(txtProjectCrypto);
			txtProjectCrypto.setColumns(10);

		}
		return jPanel;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */	
	private JButton getBt_start_aes() {
		if (bt_start_aes == null) {
			bt_start_aes = new JButton();
			bt_start_aes.setBounds(59, 77, 184, 38);
			bt_start_aes.setText("AES Encryption");
			bt_start_aes.setFont(new Font("Tahoma", Font.PLAIN, 14));
			bt_start_aes.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					FrameAES frame = new FrameAES();
					frame.setVisible(true);
				}
			});
		}
		return bt_start_aes;
	}
		

	private JButton getBt_start_rsa() {
		if (bt_start_rsa == null) {
			bt_start_rsa = new JButton();
			bt_start_rsa.setBounds(59, 128, 184, 38);
			bt_start_rsa.setText("RSA Encryption");
			bt_start_rsa.setFont(new Font("Tahoma", Font.PLAIN, 14));
			bt_start_rsa.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					FrameRSA frame = new FrameRSA();
					frame.setVisible(true);
				}
			});
		}
		return bt_start_rsa;
	}

	
	private JButton getBt_start_dsa() {
		if (bt_start_dsa == null) {
			bt_start_dsa = new JButton();
			bt_start_dsa.setBounds(59, 179, 184, 38);
			bt_start_dsa.setText("DSA Signature");
			bt_start_dsa.setFont(new Font("Tahoma", Font.PLAIN, 14));
			bt_start_dsa.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					FrameDSA frame = new FrameDSA();
					frame.setVisible(true);
				}
			});
		}
		return bt_start_dsa;
	}
		

	private JButton getBt_start_hmac() {
		if (bt_start_hmac == null) {
			bt_start_hmac = new JButton();
			bt_start_hmac.setBounds(59, 230, 184, 38);
			bt_start_hmac.setText("HMAC Authentication");
			bt_start_hmac.setFont(new Font("Tahoma", Font.PLAIN, 14));
			bt_start_hmac.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					FrameHMAC frame = new FrameHMAC();
					frame.setVisible(true);
				}
			});
		}
		return bt_start_hmac;
	}
} 
