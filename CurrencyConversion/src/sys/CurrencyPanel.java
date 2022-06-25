package sys;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 * Panel fits within frame. This class accesses a web page to gather information
 * about the Contains the conversion method.
 *
 * @author apricejr
 *
 */
public class CurrencyPanel extends JPanel {
	private static final long serialVersionUID = -139412121346194196L;
	//Currencies used in program mapped to their real world values.
	private static final Map<String, Double> COUNTRY_CURRENCIES = new HashMap<>();
	//warning message for static initialization/connecting to Internet
	private static final String STATIC_WARNING_MSG = "Failed to Retrieve Information from the Internet.\nPlease Try Again Later.",
			//warning message for invalid values
			WARNING_MSG = "Cannot enter negative numbers!",
			//conversion data web page
			WEB_PAGE_URL = "https://www.x-rates.com/table/?from=USD&amount=1";
	//starting amount
	private static final double START_MONEY = 100.00;
	//Labels to identify fields
	private JLabel amountLabel, startCurrencyLabel, endCurrencyLabel, convertedAmountLabel, blankLabel;
	//Strings for labels
	private String amountString = "Starting Amount: ", selectStartCurrencyString = "Select Starting Currency: ",
			selectConvertedCurrencyString = "Select Converted Currency: ", convertedAmountString = "Converted Amount: ",
			switchString = "Switch Currencies";
	//Fields for data entry
	private JFormattedTextField amountField, convertedAmountField;
	//Menus for currency entry
	private JComboBox<String> currentCurrencyMenu, desiredCurrencyMenu;
	//Button to switch currencies
	private JButton switchButton;
	//Formats to format and parse numbers
	private NumberFormat amountDisplayFormat, amountEditFormat, paymentFormat;
	//For currencies upon reloading panel

	/**
	 * Initializes all currencies.
	 */
	public static void initializeCurrencyData() {

		//adding currencies
		COUNTRY_CURRENCIES.put("British Pound", null);
		COUNTRY_CURRENCIES.put("Euro", null);
		COUNTRY_CURRENCIES.put("Chinese Yuan Renminbi", null);
		COUNTRY_CURRENCIES.put("Indian Rupee", null);
		COUNTRY_CURRENCIES.put("Australian Dollar", null);
		COUNTRY_CURRENCIES.put("Canadian Dollar", null);
		COUNTRY_CURRENCIES.put("Japanese Yen", null);
		COUNTRY_CURRENCIES.put("Mexican Peso", null);
		COUNTRY_CURRENCIES.put("Guyana", null);

		//connecting to web page
		try {
			URL url = new URL(WEB_PAGE_URL);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			Scanner sc = new Scanner(reader);
			StringBuffer page = new StringBuffer();
			while (sc.hasNext()) {
				page.append(sc.nextLine());
			}
			sc.close();
			//currencies that are not found on the web page and need to be omitted from the program
			List<String> badCurrencies = new ArrayList<>();
			//filling in map
			for (String currency : COUNTRY_CURRENCIES.keySet()) {
				int currencyIndex = page.indexOf("<td>" + currency + "</td>");
				if (currencyIndex == -1) {
					badCurrencies.add(currency);
				} else {
					int rateIndex = page.indexOf("<a href", currencyIndex);
					int valueStartIndex = page.indexOf(">", rateIndex) + 1,
							valueEndIndex = page.indexOf("</a>", valueStartIndex);
					String value = page.substring(valueStartIndex, valueEndIndex);
					COUNTRY_CURRENCIES.replace(currency, Double.valueOf(value));
				}
			}
			//insert us dollar as it is not on web page
			COUNTRY_CURRENCIES.put("US Dollar", 1.0);

			//remove bad currencies from currencies to use
			for (String currency : badCurrencies) {
				COUNTRY_CURRENCIES.remove(currency);
			}
			System.out.println(COUNTRY_CURRENCIES);
		} catch (MalformedURLException e) {
			shutdown("Unable to connect to \"" + WEB_PAGE_URL
					+ "\" because of an unknown protocol or error while parsing the string.\nPlease check the URL before trying again.");
		} catch (IOException e) {
			shutdown("Unable to connect to " + WEB_PAGE_URL
					+ " because of a problem with the web page.\nPlease try again later.");
		} catch (Exception e) {
			shutdown();
		}
	}

	/**
	 * Creates a CurrencyPanel
	 */
	public CurrencyPanel() {
		super(new BorderLayout());
		initializeCurrencyData();
		setup();
		//create labels
		amountLabel = new JLabel(amountString);
		startCurrencyLabel = new JLabel(selectStartCurrencyString);
		endCurrencyLabel = new JLabel(selectConvertedCurrencyString);
		convertedAmountLabel = new JLabel(convertedAmountString);
		blankLabel = new JLabel("");

		//create the text fields
		amountField = new JFormattedTextField(new DefaultFormatterFactory(new NumberFormatter(amountDisplayFormat),
				new NumberFormatter(paymentFormat), new NumberFormatter(amountEditFormat)));
		amountField.setValue(START_MONEY);
		amountField.setColumns(10);
		amountField.addActionListener((e) -> updateConvertedAmountField());
		convertedAmountField = new JFormattedTextField(paymentFormat);
		convertedAmountField.setEditable(false);

		//set label-field pairs
		amountLabel.setLabelFor(amountField);
		startCurrencyLabel.setLabelFor(currentCurrencyMenu);
		endCurrencyLabel.setLabelFor(desiredCurrencyMenu);
		convertedAmountLabel.setLabelFor(convertedAmountField);
		blankLabel.setLabelFor(switchButton);

		//Lay out labels in a panel
		JPanel labelPane = new JPanel(new GridLayout(0, 1));
		labelPane.add(amountLabel);
		labelPane.add(startCurrencyLabel);
		labelPane.add(endCurrencyLabel);
		labelPane.add(blankLabel);
		labelPane.add(convertedAmountLabel);

		//Lay out fields and menus in a panel
		JPanel inputPane = new JPanel(new GridLayout(0, 1));
		inputPane.add(amountField);
		inputPane.add(currentCurrencyMenu);
		inputPane.add(desiredCurrencyMenu);
		inputPane.add(switchButton);
		inputPane.add(convertedAmountField);

		//Place panels in current panel, labels left, input on right
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		add(labelPane, BorderLayout.CENTER);
		add(inputPane, BorderLayout.LINE_END);
		//update convertedAmount
		updateConvertedAmountField();
	}

	/*
	 * set up currency options and create and set up number formats and menus
	 */
	private void setup() {
		Vector<String> v = sortedCurrencies();
		amountDisplayFormat = NumberFormat.getCurrencyInstance();
		amountDisplayFormat.setMinimumFractionDigits(0);
		amountEditFormat = NumberFormat.getNumberInstance();
		paymentFormat = NumberFormat.getCurrencyInstance();
		currentCurrencyMenu = new JComboBox<>(v);
		currentCurrencyMenu.setSelectedItem("US Dollar");
		currentCurrencyMenu.addActionListener((e) -> updateConvertedAmountField());

		desiredCurrencyMenu = new JComboBox<>(v);
		desiredCurrencyMenu.setSelectedItem("Euro");
		desiredCurrencyMenu.addActionListener((e) -> updateConvertedAmountField());

		switchButton = new JButton(switchString);
		switchButton.addActionListener((e) -> switchCurrencies());
	}

	/**
	 * Returns a vector of currencies sorted in alphabetical order.
	 *
	 * @return Vector
	 */
	private Vector<String> sortedCurrencies() {
		List<String> sortCurr = new ArrayList<>(COUNTRY_CURRENCIES.keySet());
		Collections.sort(sortCurr);
		return new Vector<>(sortCurr);
	}

	/**
	 * Process to shutdown application using a generic warning message.
	 */
	private static void shutdown() {
		shutdown(STATIC_WARNING_MSG);
	}

	/**
	 * Shuts down application with a specific warning message.
	 *
	 * @param warning String
	 */
	private static void shutdown(String warning) {
		JOptionPane.showMessageDialog(null, warning);
		System.exit(ABORT);
	}

	private void switchCurrencies() {
		int oldIndex = currentCurrencyMenu.getSelectedIndex();
		currentCurrencyMenu.setSelectedIndex(desiredCurrencyMenu.getSelectedIndex());
		desiredCurrencyMenu.setSelectedIndex(oldIndex);
	}

	private void updateConvertedAmountField() {
		System.out.println("Change currency");
		Double value = Double.valueOf(amountField.getValue() + "");
		//no unknown or negative values
		if ((value == null) || (value < 0)) {
			JOptionPane.showMessageDialog(this, WARNING_MSG);
			amountField.setValue(START_MONEY);
		} else {
			String startCurrency = (String) currentCurrencyMenu.getSelectedItem(),
					endCurrency = (String) desiredCurrencyMenu.getSelectedItem();
			convertedAmountField.setValue(convert(value, startCurrency, endCurrency));
		}
	}

	/**
	 * Converts value from one currency to another.
	 *
	 * @param value           double money to be converted
	 * @param currentCurrency String current currency
	 * @param desiredCurrency String desired currency
	 * @return value double converted to desired currency
	 */
	private double convert(double value, String currentCurrency, String desiredCurrency) {
		return value * (COUNTRY_CURRENCIES.get(desiredCurrency) / COUNTRY_CURRENCIES.get(currentCurrency));
	}

	/**
	 *
	 * @return conversion data for export
	 */
	String getCurrencyData() {
		updateConvertedAmountField();//make sure data is entered
		return amountField.getValue() + " " + currentCurrencyMenu.getSelectedItem() + " = "
				+ convertedAmountField.getValue() + " " + desiredCurrencyMenu.getSelectedItem();
	}

}
