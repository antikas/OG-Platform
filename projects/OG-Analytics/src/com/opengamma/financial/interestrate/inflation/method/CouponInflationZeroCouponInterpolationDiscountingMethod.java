/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.interestrate.inflation.method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;

import com.opengamma.financial.interestrate.InstrumentDerivative;
import com.opengamma.financial.interestrate.inflation.derivatives.CouponInflationZeroCouponInterpolation;
import com.opengamma.financial.interestrate.market.MarketBundle;
import com.opengamma.financial.interestrate.market.PresentValueCurveSensitivityMarket;
import com.opengamma.financial.interestrate.method.PricingMarketMethod;
import com.opengamma.util.money.MultipleCurrencyAmount;
import com.opengamma.util.tuple.DoublesPair;

/**
 * Pricing method for inflation zero-coupon. The price is computed by index estimation and discounting.
 */
public final class CouponInflationZeroCouponInterpolationDiscountingMethod implements PricingMarketMethod {

  /*
   * The unique instance of the method.
   */
  private static final CouponInflationZeroCouponInterpolationDiscountingMethod INSTANCE = new CouponInflationZeroCouponInterpolationDiscountingMethod();

  /*
   * Gets the method unique instance.
   */
  public static CouponInflationZeroCouponInterpolationDiscountingMethod getInstance() {
    return INSTANCE;
  }

  /**
   * Private constructor.
   */
  private CouponInflationZeroCouponInterpolationDiscountingMethod() {
  }

  /**
   * Computes the present value of the zero-coupon coupon with reference index at start of the month.
   * @param coupon The zero-coupon payment.
   * @param market The market bundle.
   * @return The present value.
   */
  public MultipleCurrencyAmount presentValue(CouponInflationZeroCouponInterpolation coupon, MarketBundle market) {
    Validate.notNull(coupon, "Coupon");
    Validate.notNull(market, "Market");
    double estimatedIndex = coupon.estimatedIndex(market);
    double discountFactor = market.getDiscountFactor(coupon.getCurrency(), coupon.getPaymentTime());
    double pv = (estimatedIndex / coupon.getIndexStartValue() - (coupon.payNotional() ? 0.0 : 1.0)) * discountFactor * coupon.getNotional();
    return MultipleCurrencyAmount.of(coupon.getCurrency(), pv);
  }

  @Override
  public MultipleCurrencyAmount presentValue(InstrumentDerivative instrument, MarketBundle market) {
    Validate.isTrue(instrument instanceof CouponInflationZeroCouponInterpolation, "Zero-coupon inflation with start of month reference date.");
    return presentValue((CouponInflationZeroCouponInterpolation) instrument, market);
  }

  /**
   * Compute the present value sensitivity to rates of a Inflation coupon.
   * @param coupon The coupon.
   * @param market The market curves.
   * @return The present value sensitivity.
   */
  public PresentValueCurveSensitivityMarket presentValueCurveSensitivity(final CouponInflationZeroCouponInterpolation coupon, final MarketBundle market) {
    Validate.notNull(coupon, "Coupon");
    Validate.notNull(market, "Market");
    double estimatedIndexMonth0 = market.getPriceIndex(coupon.getPriceIndex(), coupon.getReferenceEndTime()[0]);
    double estimatedIndexMonth1 = market.getPriceIndex(coupon.getPriceIndex(), coupon.getReferenceEndTime()[1]);
    double estimatedIndex = coupon.getWeight() * estimatedIndexMonth0 + (1 - coupon.getWeight()) * estimatedIndexMonth1;
    double discountFactor = market.getDiscountFactor(coupon.getCurrency(), coupon.getPaymentTime());
    // Backward sweep
    final double pvBar = 1.0;
    double discountFactorBar = (estimatedIndex / coupon.getIndexStartValue() - (coupon.payNotional() ? 0.0 : 1.0)) * coupon.getNotional() * pvBar;
    double estimatedIndexBar = 1.0 / coupon.getIndexStartValue() * discountFactor * coupon.getNotional() * pvBar;
    double estimatedIndexMonth1Bar = (1 - coupon.getWeight()) * estimatedIndexBar;
    double estimatedIndexMonth0Bar = coupon.getWeight() * estimatedIndexBar;
    final Map<String, List<DoublesPair>> resultMapDisc = new HashMap<String, List<DoublesPair>>();
    final List<DoublesPair> listDiscounting = new ArrayList<DoublesPair>();
    listDiscounting.add(new DoublesPair(coupon.getPaymentTime(), -coupon.getPaymentTime() * discountFactor * discountFactorBar));
    resultMapDisc.put(market.getCurve(coupon.getCurrency()).getCurve().getName(), listDiscounting);
    final Map<String, List<DoublesPair>> resultMapPrice = new HashMap<String, List<DoublesPair>>();
    final List<DoublesPair> listPrice = new ArrayList<DoublesPair>();
    listPrice.add(new DoublesPair(coupon.getReferenceEndTime()[0], estimatedIndexMonth0Bar));
    listPrice.add(new DoublesPair(coupon.getReferenceEndTime()[1], estimatedIndexMonth1Bar));
    resultMapPrice.put(market.getCurve(coupon.getPriceIndex()).getCurve().getName(), listPrice);
    final PresentValueCurveSensitivityMarket result = new PresentValueCurveSensitivityMarket(resultMapDisc, resultMapPrice);
    return result;
  }

}
