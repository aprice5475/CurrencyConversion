package sys;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

public class MyFrame extends JFrame {
	private static final String WARNING_MESSAGE = "Error creating or adding panel.\nPlease try again later.";

	public MyFrame() {
		super("Currency Converter");
		boolean ex = false;
		try {
			add(new MyPanel());
		} catch (Exception e) {
			ex = true;
		}
		if (ex) {
			shutdown();
		}
		setResizable(false);
		pack();
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	private void shutdown() {
		JOptionPane.showMessageDialog(null, WARNING_MESSAGE);
		System.exit(ABORT);
	}
}
