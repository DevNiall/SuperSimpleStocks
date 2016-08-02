package com.github.devniall.SuperSimpleStocks;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.logging.*;

/**
 * Console based example of the Super Simple Stocks application.
 *
 * @author @DevNiall
 *
 */
public class SuperSimpleStocks {

	private static final Logger logger = Logger.getLogger(SuperSimpleStocks.class.getName());

	private static HashMap<String, Stock> stocks = new HashMap<>();
	private static ArrayList<Trade> trades = new ArrayList<>();
	private static MathContext mc = new MathContext(2, RoundingMode.HALF_UP);

	/**
	 * Calculates the Dividend yield for a given stock and price
	 *
	 * @param stock
	 * @param price
	 * @return The dividend yield
	 */
	public static BigDecimal calculateDividendYield(Stock stock, BigDecimal price) {
		BigDecimal dividend = null;

		switch (stock.getType()) {
		case COMMON:
			dividend = stock.getLastDividend().divide(price, mc);
			break;

		case PREFERRED:
			Stock sp = stock;
			dividend = sp.getFixedDividend().multiply(sp.getParValue(), mc).divide(price, mc);
			break;

		default:
			logger.warning("Unsupported Stock type.");
		}

		return dividend;
	}

	/**
	 * Calculates the GBCE All Share Index using the geometric mean of prices
	 * for all stocks
	 *
	 * @return GBCE All Share Index
	 */
	public static double calculateGBCEAllShareIndex() {
		BigDecimal prices = new BigDecimal(1);
		for (Trade t : trades) {
			prices = prices.multiply(t.getPrice());
		}
		double gbceAllShareIndex = Math.pow(prices.doubleValue(), 1.0 / trades.size());
		return gbceAllShareIndex;
	}

	/**
	 * Calculate the P/E Ratio for a given stock and price
	 *
	 * @param stock
	 * @param price
	 * @return The P/E ratio
	 */
	public static BigDecimal calculatePERatio(Stock stock, BigDecimal price) {
		BigDecimal dividend = calculateDividendYield(stock, price);
		BigDecimal peRatio = price.divide(dividend, 2, RoundingMode.HALF_UP);
		return peRatio;
	}

	/**
	 * Calculates the stock price based on trades recorded for a given stock and
	 * time period
	 *
	 * @param stock
	 * @param offset
	 * @return Stock price over a given period
	 */
	public static BigDecimal calculateStockPrice(Stock stock, Date offset) {
		BigDecimal stockPrice = new BigDecimal(0);
		BigDecimal tradePriceTime = new BigDecimal(0);
		int tradeQuantityTime = 0;
		for (Trade t : trades) {
			if (t.getStock().equals(stock) && t.getTimestamp().after(offset)) {
				BigDecimal bd = t.getPrice().multiply(new BigDecimal(t.getQuantity()));
				tradePriceTime = tradePriceTime.add(bd);
				tradeQuantityTime += t.getQuantity();
			}
		}
		stockPrice = tradePriceTime.divide(new BigDecimal(tradeQuantityTime), MathContext.DECIMAL128);
		return stockPrice;
	}

	/**
	 * Helper method to populate stocks
	 */
	public static void loadSampleData() {

		// TEA
		Stock stock = new Stock(StockType.COMMON, "TEA", 0, 0, 100);
		stocks.put(stock.getSymbol(), stock);
		// POP
		stock = new Stock(StockType.COMMON, "POP", 8, 0, 100);
		stocks.put(stock.getSymbol(), stock);
		// ALE
		stock = new Stock(StockType.COMMON, "ALE", 23, 0, 60);
		stocks.put(stock.getSymbol(), stock);
		// GIN
		stock = new Stock(StockType.PREFERRED, "GIN", 8, 2, 100);
		stocks.put(stock.getSymbol(), stock);
		// JOE
		stock = new Stock(StockType.COMMON, "JOE", 13, 0, 250);
		stocks.put(stock.getSymbol(), stock);

	}

	/**
	 * Generates 50 random sample trades over previous 30 minutes. Prices range
	 * between 0-50, quantities range between 1-5, buy or sell for a random
	 * stock
	 */
	public static void generateSampleTrades() {

		// Create array so we can get trades via integer index
		ArrayList<Stock> stocksList = new ArrayList<>();
		Iterator<Entry<String, Stock>> iter = ((Map<String, Stock>) stocks).entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, Stock> pair = iter.next();
			stocksList.add(pair.getValue());
		}

		TradeType[] types = { TradeType.BUY, TradeType.SELL };

		Date date;
		Trade trade = null;
		for (int i = 0; i < 50; i++) {
			// generate random date over last half hour
			date = new Date();
			date.setTime(date.getTime() - randInt(0, 1800000));
			trade = new Trade(stocksList.get(randInt(0, stocksList.size() - 1)), types[randInt(0, 1)], randInt(1, 5),
					randBigDecimal(0, 50));
			trade.setTimestamp(date);
			trades.add(trade);
		}

		// sort trades by date
		trades.sort(new Comparator<Trade>() {
			@Override
			public int compare(Trade t1, Trade t2) {
				return t1.getTimestamp().compareTo(t2.getTimestamp());
			}

		});
	}

	/**
	 * Main method, contains menu logic.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Super Simple Stocks!");
		System.out.println("====================");

		// load stocks specified in instructions
		loadSampleData();
		// loadSampleTrades();

		while (true) {
			// Render menu and capture choice
			int choice = menu();

			switch (choice) {

			// 1) Calculate the dividend yield
			case 1:
				menuCalcuateDividend();
				break;

			// 2) Calculate the P/E Ratio
			case 2:
				menuPERatio();
				break;

			// 3) Record a trade
			case 3:
				recordTrade();
				break;

			// 4) Calculate Stock Price for past 15 minutes
			case 4:
				menuCalculateStockPrice(15);
				break;

			// 5) Calculate the GBCE All Share Index for all stocks
			case 5:
				menuCalculateGBCEAllShareIndex();
				break;

			// print all stocks
			case 6:
				Iterator<Entry<String, Stock>> it = stocks.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry<String, Stock> pair = (Entry<String, Stock>) it.next();
					System.out.println(pair.getValue());
				}
				break;

			// print all stocks
			case 7:
				for (Trade trade : trades) {
					System.out.println(trade);
				}
				break;

			// generate & print all trades
			case 8:
				generateSampleTrades();
				for (Trade t : trades) {
					System.out.println(t);
				}
				break;

			case 0:
				System.exit(0);

			default:
				System.out.println("Invalid selection.");
			}
		}

	}

	/**
	 * Renders the main menu and captures users selection
	 *
	 * @return Integer representing user choice
	 */
	public static int menu() {

		System.out.println();
		System.out.println("1) Calculate the dividend yield");
		System.out.println("2) Calculate the P/E Ratio");
		System.out.println("3) Record a trade");
		System.out.println("4) Calculate Stock Price for past 15 minutes");
		System.out.println("5) Calculate the GBCE All Share Index for all stocks");
		System.out.println("6) testing - print all stocks");
		System.out.println("7) testing - print all trades");
		System.out.println("8) testing - generate random trades & print");
		System.out.println("0) Exit");
		System.out.println();

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		try {
			return scanner.nextInt();
		} catch (Exception e) {
			logger.warning("Not a number.");
		}

		return -1;
	}

	/**
	 * Captures necessary variables and displays the dividend yield for a given
	 * stock
	 */
	public static void menuCalcuateDividend() {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);

		// capture symbol
		System.out.print("Enter stock symbol: ");
		String symbol = scanner.next().toUpperCase();
		// stock check
		Stock stock = stocks.get(symbol);
		if (stock == null) {
			System.out.println("Stock not found.");
			return;
		}

		// capture price
		System.out.print("Enter ticker price: ");
		BigDecimal price;
		try {
			price = scanner.nextBigDecimal();
			if (price.intValue() <= 0) {
				System.out.println("Price must be greater than 0.");
				return;
			}
			;
		} catch (InputMismatchException e) {
			logger.warning("Not a valid number.");
			return;
		}

		BigDecimal dividend = calculateDividendYield(stock, price);
		System.out.println(String.format("Dividend yield: %.2f", dividend));

	}

	/**
	 * Captures necessary variables and displays the calculated stock price for
	 * a given time offset
	 *
	 * @param offset
	 *            Time in minutes to calculate price against
	 */
	public static void menuCalculateStockPrice(int offset) {
		// Go no further if no trades have been recorded
		if (trades.size() == 0) {
			System.out.println("No trades recorded!");
			return;
		}

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);

		// capture symbol
		System.out.print("Enter stock symbol: ");
		String symbol = scanner.next().toUpperCase();
		// stock check
		Stock stock = stocks.get(symbol);
		if (stock == null) {
			System.out.println("Stock not found.");
			return;
		}

		Date date = new Date();
		date.setTime(date.getTime() - offset * 60 * 1000);
		BigDecimal stockPrice = calculateStockPrice(stock, date);
		System.out.println(String.format("Stock price for %s is £%.2f", stock.getSymbol(), stockPrice));
	}

	/**
	 * Displays the results of the GBCE All Share Index using the geometric mean
	 * of prices for all stocks
	 */
	public static void menuCalculateGBCEAllShareIndex() {
		// Go no further if no trades have been recorded
		if (trades.size() == 0) {
			System.out.println("No trades recorded!");
			return;
		}

		double gbceAllShareIndex = calculateGBCEAllShareIndex();
		System.out.println(String.format("GBCE All Share Index: %.2f", gbceAllShareIndex));
	}

	/**
	 * Captures necessary variables and displays the P/E ratio for a given stock
	 * and price
	 */
	public static void menuPERatio() {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);

		// capture symbol
		System.out.print("Enter stock symbol: ");
		String symbol = scanner.next().toUpperCase();
		// stock check
		Stock stock = stocks.get(symbol);
		if (stock == null) {
			System.out.println("Stock not found.");
			return;
		}

		// capture price
		System.out.print("Enter ticker price: ");
		BigDecimal price;
		try {
			price = scanner.nextBigDecimal();
			if (price.intValue() <= 0) {
				System.out.println("Price must be greater than 0.");
				return;
			}
		} catch (InputMismatchException e) {
			logger.warning("Not a valid number.");
			return;
		}
		BigDecimal peRatio = calculatePERatio(stock, price);
		System.out.println(String.format("P/E Ratio: %.2f", peRatio));
	}

	/**
	 * Helper method for generating a random BigDecimal
	 *
	 * @param start
	 * @param end
	 * @return Random BigDecimal
	 */
	public static BigDecimal randBigDecimal(int start, int end) {
		BigDecimal bd = new BigDecimal(start + (Math.random() * (end - start + 1)), MathContext.DECIMAL128);
		return bd;
	}

	/**
	 * Helper method for generating a random integer
	 *
	 * @param start
	 * @param end
	 * @return Random integer
	 */
	public static int randInt(int start, int end) {
		return start + (int) (Math.random() * (end - start + 1));
	}

	/**
	 * Captures necessary variables to record a trade against the system
	 */
	public static void recordTrade() {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		Stock stock;
		int quantity;
		BigDecimal price;
		TradeType tradeType;

		// Lookup stock
		try {
			System.out.print("Look up stock, enter symbol: ");
			String symbol = scanner.next().toUpperCase();
			stock = stocks.get(symbol);
			if (stock == null) {
				System.out.println("Stock not found.");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		// Capture quantity
		try {
			System.out.print("Enter quantity: ");
			quantity = scanner.nextInt();
			if (quantity <= 0) {
				System.out.println("Quantity must be greater than 0.");
				return;
			}
		} catch (InputMismatchException e) {
			logger.warning("Not a valid number.");
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		// Capture Buy/Sell
		try {
			System.out.print("[B]uy or [S]ell: ");
			char c = scanner.next("(b|s)").toUpperCase().charAt(0);
			tradeType = (c == 'b') ? TradeType.BUY : TradeType.SELL;
		} catch (InputMismatchException e) {
			logger.warning("Not a choice.");
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		// Capture price
		try {
			System.out.print("Enter price: ");
			price = scanner.nextBigDecimal();
			if (price.intValue() <= 0) {
				System.out.println("Price must be greater than 0.");
				return;
			}
		} catch (InputMismatchException e) {
			logger.warning("Not a valid number.");
			return;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

		Trade trade = new Trade(stock, tradeType, quantity, price);
		System.out.println(trade);
		trades.add(trade);
	}

}
