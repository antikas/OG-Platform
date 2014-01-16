/**
 * Copyright (C) 2014 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.instrument.future;

import org.apache.commons.lang.ObjectUtils;
import org.threeten.bp.ZonedDateTime;

import com.opengamma.analytics.financial.interestrate.future.derivative.FuturesSecurity;
import com.opengamma.util.ArgumentChecker;

/**
 * Abstract class for transactions on generic futures.
 * @param <FS> The futures type of the underlying security.
 */
public abstract class FuturesTransactionDefinition<FS extends FuturesSecurityDefinition<? extends FuturesSecurity>> {

  /**
   * Underlying future security. Not null;
   */
  private final FS _underlyingFuture;
  /**
   * Quantity of future. Can be positive or negative.
   */
  private final int _quantity;
  /**
   * Transaction date. Not null.
   */
  private final ZonedDateTime _tradeDate;
  /**
   * Transaction price. The price is in relative number and not in percent. A standard price will be 0.985 and not 98.5.
   */
  private final double _tradePrice;

  /**
   * Constructor.
   * @param underlyingFuture The underlying futures security.
   * @param quantity The quantity of the transaction.
   * @param tradeDate The transaction date.
   * @param tradePrice The transaction price (in the convention of the futures).
   */
  public FuturesTransactionDefinition(final FS underlyingFuture, int quantity, ZonedDateTime tradeDate, double tradePrice) {
    super();
    ArgumentChecker.notNull(underlyingFuture, "Underlying futures");
    ArgumentChecker.notNull(tradeDate, "Trade date");
    _underlyingFuture = underlyingFuture;
    _quantity = quantity;
    _tradeDate = tradeDate;
    _tradePrice = tradePrice;
  }

  /**
   * Returns the underlying futures security.
   * @return The security.
   */
  public FS getUnderlyingFuture() {
    return _underlyingFuture;
  }

  /**
   * Returns the transaction quantity.
   * @return The quantity.
   */
  public int getQuantity() {
    return _quantity;
  }

  /**
   * Returns the transaction date.
   * @return The date.
   */
  public ZonedDateTime getTradeDate() {
    return _tradeDate;
  }

  /**
   * Returns the transaction price (in the convention of the futures).
   * @return The price.
   */
  public double getTradePrice() {
    return _tradePrice;
  }

  @Override
  public String toString() {
    final String result = "Quantity: " + _quantity + " of " + _underlyingFuture.toString();
    return result;
  }

  //  @Override
  //  public FuturesTransaction<?> toDerivative(final ZonedDateTime date) {
  //    throw new UnsupportedOperationException("The method toDerivative of FutureTransactionDefinition does not support the one argument method (without margin price data).");
  //  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + _quantity;
    result = prime * result + _tradeDate.hashCode();
    long temp;
    temp = Double.doubleToLongBits(_tradePrice);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + _underlyingFuture.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    FuturesTransactionDefinition<?> other = (FuturesTransactionDefinition<?>) obj;
    if (_quantity != other._quantity) {
      return false;
    }
    if (!ObjectUtils.equals(_tradeDate, other._tradeDate)) {
      return false;
    }
    if (Double.doubleToLongBits(_tradePrice) != Double.doubleToLongBits(other._tradePrice)) {
      return false;
    }
    if (!ObjectUtils.equals(_underlyingFuture, other._underlyingFuture)) {
      return false;
    }
    return true;
  }

}
