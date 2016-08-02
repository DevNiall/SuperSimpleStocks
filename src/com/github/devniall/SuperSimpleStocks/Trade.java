package com.github.devniall.SuperSimpleStocks;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class definition of a stock
 *
 * @author @DevNiall
 *
 */
public class Trade {

	private Date timestamp;
	Stock stock;
	TradeType type;
	int quantity;
	BigDecimal price;

	public Trade(Stock stock, TradeType type, int quantity, BigDecimal price) {
		this.timestamp = new Date();
		this.stock = stock;
		this.type = type;
		this.quantity = quantity;
		this.price = price;
	}

	public Trade(Stock stock, TradeType type, int quantity, int price) {
		this(stock, type, quantity, new BigDecimal(price));
	}

	public BigDecimal getPrice() {
		return price;
	}

	public int getQuantity() {
		return quantity;
	}

	public Stock getStock() {
		return stock;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public TradeType getType() {
		return type;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return String.format("[%s] %s x%d £%.2f (%s)", sdf.format(getTimestamp()), getType().toString(), getQuantity(),
				getPrice(), getStock());
	}

}
