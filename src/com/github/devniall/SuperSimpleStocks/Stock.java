package com.github.devniall.SuperSimpleStocks;

import java.math.BigDecimal;

/**
 * Class definition of a stock.
 *
 * @author @DevNiall
 *
 */
public class Stock {

	private String symbol;
	private StockType type;
	private BigDecimal lastDividend;
	private BigDecimal fixedDividend;
	private BigDecimal parValue;

	public Stock(StockType type, String symbol, BigDecimal lastDividend, BigDecimal fixedDividend,
			BigDecimal parValue) {
		this.type = type;
		this.symbol = symbol;
		this.lastDividend = lastDividend;
		this.fixedDividend = fixedDividend;
		this.parValue = parValue;
	}

	public Stock(StockType type, String symbol, int lastDividend, int fixedDividend, int parValue) {
		this(type, symbol, new BigDecimal(lastDividend), new BigDecimal(fixedDividend), new BigDecimal(parValue));
	}

	public BigDecimal getFixedDividend() {
		return fixedDividend;
	}

	public BigDecimal getLastDividend() {
		return lastDividend;
	}

	public BigDecimal getParValue() {
		return parValue;
	}

	public String getSymbol() {
		return symbol;
	}

	public StockType getType() {
		return type;
	}

	public void setFixedDividend(BigDecimal fixedDividend) {
		this.fixedDividend = fixedDividend;
	}

	@Override
	public String toString() {
		switch (getType()) {
		case COMMON:
			return String.format("\"%s\" (%s, %s, %s)", getSymbol(), getType(), getLastDividend(), getParValue());

		case PREFERRED:
			return String.format("\"%s\" (%s, %s/%%%.2f, %s)", getSymbol(), getType(), getLastDividend(),
					getFixedDividend().doubleValue() * 100, getParValue());
		}

		return super.toString();
	}

}
