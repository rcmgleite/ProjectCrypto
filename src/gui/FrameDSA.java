package src.gui;

import src.algorithms.*;

import java.awt.BorderLayout;

import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.Rectangle;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.GridBagLayout;
import java.awt.Color;
import javax.swing.JRadioButton;
import java.awt.Font;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import javax.swing.JTextPane;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class FrameDSA extends JFrame {
	
	private static final long serialVersionUID = 1L;
	private JPanel jContentPane = null;
	private JButton bt_sign = null;
	private JButton bt_decrypt = null;
	private JLabel jLabel_message;
	private JLabel jLabel_privatekey;
	private JLabel jLabel_publickey;
	private JLabel jLabel_hash;
	private JLabel jLabel_signature;
	private JLabel jLabel_mode;
	
	private JPanel jPanel_Input = null;
	private JTextArea ta_message;
	private JTextArea ta_privatekey;
	private JTextArea ta_publickey;
	private JTextArea ta_hash;
	private JTextArea ta_signature;
	private JComboBox cbox_mode;

	/**
	 * This is the default constructor
	 */
	public FrameDSA() {
		super();
		initialize();
	}
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(620, 500);
		this.setContentPane(getJContentPane());
		this.setTitle("DSA Signature");
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			
			jContentPane.add(getBt_sign(), null);
			jContentPane.add(getBt_decrypt(), null);
			jContentPane.add(getJPanel_Input(), null);

			jLabel_mode = new JLabel();
			jLabel_mode.setBounds(22, 402, 105, 28);
			jContentPane.add(jLabel_mode);
			jLabel_mode.setFont(new Font("Tahoma", Font.BOLD, 14));
			jLabel_mode.setText("Hash:");
			jContentPane.add(getCbox_mode());
						
		}
		return jContentPane;
	}
	
	private void dsa_sign() {
		// hash
		ta_hash.setText(cbox_mode.getSelectedItem().toString().equals("SHA-1") 
				? DSA.sha1(ta_message.getText()) : DSA.sha256(ta_message.getText()));
		
		// sign
		String signature = DSA.sign(ta_message.getText(), ta_privatekey.getText(), cbox_mode.getSelectedItem().toString());
		ta_signature.setText(signature);
	}


	private void dsa_verify() {
		ta_message.setText(DSA.verify(ta_message.getText(), ta_signature.getText(), ta_publickey.getText(), 
				cbox_mode.getSelectedItem().toString()));
	}

	
	/**
	 * This method initializes bt_encript
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBt_sign() {
		if (bt_sign == null) {
			bt_sign = new JButton();
			bt_sign.setFont(new Font("Tahoma", Font.BOLD, 14));
			bt_sign.setBounds(new Rectangle(262, 402, 113, 29));
			bt_sign.setText("Sign");
			bt_sign.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dsa_sign();
				}
			});
		}
		return bt_sign;
	}

	
	/**
	 * This method initializes bt_decript	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getBt_decrypt() {
		if (bt_decrypt == null) {
			bt_decrypt = new JButton();
			bt_decrypt.setFont(new Font("Tahoma", Font.BOLD, 14));
			bt_decrypt.setBounds(new Rectangle(452, 402, 113, 29));
			bt_decrypt.setText("Verify");
			bt_decrypt.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					dsa_verify();
				}
			});
		}
		return bt_decrypt;
	}
	
	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel_Input() {
		if (jPanel_Input == null) {
			jPanel_Input = new JPanel();
			jPanel_Input.setBorder(new TitledBorder(new LineBorder(new Color(171, 173, 179)), "DSA Parametes", TitledBorder.CENTER, TitledBorder.TOP, null, new Color(0, 0, 0)));
			jPanel_Input.setToolTipText("");
			jPanel_Input.setLayout(null);
			jPanel_Input.setBounds(new Rectangle(12, 25, 578, 354));

			jLabel_message = new JLabel();
			jLabel_message.setFont(new Font("Tahoma", Font.BOLD, 14));
			jLabel_message.setBounds(12, 37, 81, 28);
			jPanel_Input.add(jLabel_message);
			jLabel_message.setText("Message:");

			jLabel_privatekey = new JLabel();
			jLabel_privatekey.setFont(new Font("Tahoma", Font.BOLD, 14));
			jLabel_privatekey.setBounds(12, 106, 90, 28);
			jPanel_Input.add(jLabel_privatekey);
			jLabel_privatekey.setText("Private Key:");

			jLabel_publickey = new JLabel();
			jLabel_publickey.setFont(new Font("Tahoma", Font.BOLD, 14));
			jLabel_publickey.setBounds(12, 153, 90, 28);
			jPanel_Input.add(jLabel_publickey);
			jLabel_publickey.setText("Public Key:");
			
			jLabel_hash = new JLabel();
			jLabel_hash.setFont(new Font("Tahoma", Font.BOLD, 14));
			jLabel_hash.setBounds(12, 204, 66, 28);
			jPanel_Input.add(jLabel_hash);
			jLabel_hash.setText("Hash:");
			
			jLabel_signature = new JLabel();
			jLabel_signature.setFont(new Font("Tahoma", Font.BOLD, 14));
			jLabel_signature.setBounds(12, 274, 90, 28);
			jPanel_Input.add(jLabel_signature);
			jLabel_signature.setText("Signature:");
			
			jPanel_Input.add(getTa_message());
			jPanel_Input.add(getTa_privatekey());
			jPanel_Input.add(getTa_publickey());
			jPanel_Input.add(getTa_hash());
			jPanel_Input.add(getTa_signature());
			jPanel_Input.add(getTa_publickey());
		}
		return jPanel_Input;
	}
	
	private JTextArea getTa_message() {
		if (ta_message == null) {
			ta_message = new JTextArea();
			ta_message.setLineWrap(true);
			ta_message.setFont(new Font("Tahoma", Font.PLAIN, 12));
			ta_message.setBounds(116, 27, 450, 51);
		}
		return ta_message;
	}

	private JTextArea getTa_privatekey() {
		if (ta_privatekey == null) {
			ta_privatekey = new JTextArea();
			ta_privatekey.setLineWrap(true);
			ta_privatekey.setFont(new Font("Tahoma", Font.PLAIN, 12));
			ta_privatekey.setBounds(116, 102, 450, 38);;
		}
		return ta_privatekey;
	}

	private JTextArea getTa_publickey() {
		if (ta_publickey == null) {
			ta_publickey = new JTextArea();
			ta_publickey.setLineWrap(true);
			ta_publickey.setFont(new Font("Tahoma", Font.PLAIN, 12));
			ta_publickey.setBounds(116, 149, 450, 38);
		}
		return ta_publickey;
	}
	
	private JTextArea getTa_hash() {
		if (ta_hash == null) {
			ta_hash = new JTextArea();
			ta_hash.setLineWrap(true);
			ta_hash.setFont(new Font("Tahoma", Font.PLAIN, 12));
			ta_hash.setBounds(116, 200, 450, 38);
		}
		return ta_hash;
	}
	
	private JTextArea getTa_signature() {
		if (ta_signature == null) {
			ta_signature = new JTextArea();
			ta_signature.setLineWrap(true);
			ta_signature.setFont(new Font("Tahoma", Font.PLAIN, 12));
			ta_signature.setBounds(116, 251, 450, 90);
		}
		return ta_signature;
	}

	private JComboBox getCbox_mode() {
		if (cbox_mode == null) {
			cbox_mode = new JComboBox();
			cbox_mode.setModel(new DefaultComboBoxModel(new String[] {"SHA-256", "SHA-1"}));
			cbox_mode.setFont(new Font("Tahoma", Font.BOLD, 14));
			cbox_mode.setBounds(90, 402, 91, 29);
		}
		return cbox_mode;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
