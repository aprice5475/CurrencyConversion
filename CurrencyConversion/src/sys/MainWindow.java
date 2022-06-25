package sys;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

public class MainWindow extends JFrame {
	private static final long serialVersionUID = 256638483026278054L;
	private static final String WARNING_MESSAGE = "Error creating or adding panel.\nPlease try again later.";
	private final static String ABOUT_MESSAGE = "Aaron Price-Currency Converter Version 1.0\nThank you for using Currency Converter.\n"
			+ "This application allows you to see how much one currency is worth another.\nChoose any two currencies in the dropdown menu, enter a value, and voila!\n"
			+ "The file menu offers some neat tricks as well.\nYou can export data and reload the application to get real time exchange rates.";
	private CurrencyPanel currencyPanel;

	public MainWindow() {
		super("Currency Converter");
		boolean exceptionThrown = false;
		try {
			currencyPanel = new CurrencyPanel();
			add(currencyPanel);
		} catch (Exception e) {
			exceptionThrown = true;
		}
		if (exceptionThrown) {
			shutdown();
		}
		setJMenuBar(addJMenuBar());
		setResizable(false);
		pack();
		setVisible(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	private JMenuBar addJMenuBar() {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		JMenuBar bar = new JMenuBar();
		JMenu helpMenu = new JMenu("Help"), fileMenu = new JMenu("File");
		JMenuItem exitItem = new JMenuItem("Close Application"), aboutItem = new JMenuItem("About Currency Converter");

		fileMenu.add(exitItem);
		exitItem.addActionListener((e) -> {
			System.exit(0);
		});
		bar.add(fileMenu);
		helpMenu.add(aboutItem);
		aboutItem.addActionListener((e) -> {
			JOptionPane.showMessageDialog(null, ABOUT_MESSAGE);
		});
		bar.add(helpMenu);
		return bar;
	}

	private void shutdown() {
		JOptionPane.showMessageDialog(this, WARNING_MESSAGE);
		System.exit(ABORT);
	}

}
