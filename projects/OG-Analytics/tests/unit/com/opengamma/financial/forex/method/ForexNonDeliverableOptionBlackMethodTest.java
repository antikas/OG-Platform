/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.forex.method;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import javax.time.calendar.Period;
import javax.time.calendar.ZonedDateTime;

import org.testng.annotations.Test;

import com.opengamma.financial.convention.businessday.BusinessDayConvention;
import com.opengamma.financial.convention.businessday.BusinessDayConventionFactory;
import com.opengamma.financial.convention.calendar.Calendar;
import com.opengamma.financial.convention.calendar.MondayToFridayCalendar;
import com.opengamma.financial.forex.calculator.CurrencyExposureBlackForexCalculator;
import com.opengamma.financial.forex.calculator.ForwardRateForexCalculator;
import com.opengamma.financial.forex.calculator.PresentValueBlackForexCalculator;
import com.opengamma.financial.forex.definition.ForexDefinition;
import com.opengamma.financial.forex.definition.ForexNonDeliverableForwardDefinition;
import com.opengamma.financial.forex.definition.ForexNonDeliverableOptionDefinition;
import com.opengamma.financial.forex.definition.ForexOptionVanillaDefinition;
import com.opengamma.financial.forex.derivative.ForexNonDeliverableOption;
import com.opengamma.financial.forex.derivative.ForexOptionVanilla;
import com.opengamma.financial.interestrate.InterestRateCurveSensitivity;
import com.opengamma.financial.interestrate.YieldCurveBundle;
import com.opengamma.financial.model.option.definition.SmileDeltaTermStructureDataBundle;
import com.opengamma.financial.model.option.definition.SmileDeltaTermStructureParameter;
import com.opengamma.financial.schedule.ScheduleCalculator;
import com.opengamma.util.money.Currency;
import com.opengamma.util.money.MultipleCurrencyAmount;
import com.opengamma.util.time.DateUtils;
import com.opengamma.util.time.TimeCalculator;
import com.opengamma.util.tuple.DoublesPair;
import com.opengamma.util.tuple.Pair;

/**
 * Tests related to the valuation of non-deliverable forward by discounting.
 */
public class ForexNonDeliverableOptionBlackMethodTest {

  private static final Calendar CALENDAR = new MondayToFridayCalendar("A");
  private static final BusinessDayConvention BUSINESS_DAY = BusinessDayConventionFactory.INSTANCE.getBusinessDayConvention("Modified Following");
  private static final int SETTLEMENT_DAYS = 2;

  private static final Currency KRW = Currency.of("KRW");
  private static final Currency USD = Currency.USD;
  private static final ZonedDateTime FIXING_DATE = DateUtils.getUTCDate(2012, 5, 2);
  private static final ZonedDateTime PAYMENT_DATE = DateUtils.getUTCDate(2012, 5, 4);
  private static final double NOMINAL_USD = 100000000; // 1m
  private static final double STRIKE = 1200.00;
  private static final ForexNonDeliverableForwardDefinition NDF_DEFINITION = new ForexNonDeliverableForwardDefinition(KRW, USD, NOMINAL_USD, STRIKE, FIXING_DATE, PAYMENT_DATE);
  private static final ForexDefinition FOREX_DEFINITION = new ForexDefinition(KRW, USD, PAYMENT_DATE, -NOMINAL_USD * STRIKE, 1.0 / STRIKE);

  private static final boolean IS_CALL = true;
  private static final boolean IS_LONG = true;
  private static final ForexNonDeliverableOptionDefinition NDO_DEFINITION = new ForexNonDeliverableOptionDefinition(NDF_DEFINITION, IS_CALL, IS_LONG);
  private static final ForexOptionVanillaDefinition FOREX_OPT_DEFINITION = new ForexOptionVanillaDefinition(FOREX_DEFINITION, FIXING_DATE, IS_CALL, IS_LONG);

  private static final ZonedDateTime REFERENCE_DATE = DateUtils.getUTCDate(2011, 11, 10);

  private static final YieldCurveBundle CURVES = TestsDataSetsForex.createCurvesForex();
  private static final String[] CURVE_NAMES = TestsDataSetsForex.curveNames();
  private static final double USD_KRW = 1111.11;
  private static final FXMatrix FX_MATRIX = new FXMatrix(USD, KRW, USD_KRW);

  private static final ForexNonDeliverableOption NDO = NDO_DEFINITION.toDerivative(REFERENCE_DATE, new String[] {CURVE_NAMES[3], CURVE_NAMES[1]});
  private static final ForexOptionVanilla FOREX_OPT = FOREX_OPT_DEFINITION.toDerivative(REFERENCE_DATE, new String[] {CURVE_NAMES[3], CURVE_NAMES[1]});

  private static final ForexNonDeliverableOptionBlackMethod METHOD_NDO = ForexNonDeliverableOptionBlackMethod.getInstance();
  private static final ForexOptionVanillaBlackMethod METHOD_FXO = ForexOptionVanillaBlackMethod.getInstance();
  private static final ForexNonDeliverableForwardDiscountingMethod METHOD_NDF = ForexNonDeliverableForwardDiscountingMethod.getInstance();
  private static final PresentValueBlackForexCalculator PVC_BLACK = PresentValueBlackForexCalculator.getInstance();
  private static final CurrencyExposureBlackForexCalculator CE_BLACK = CurrencyExposureBlackForexCalculator.getInstance();

  // Smile data
  private static final Period[] EXPIRY_PERIOD = new Period[] {Period.ofMonths(3), Period.ofMonths(6), Period.ofYears(1), Period.ofYears(2), Period.ofYears(5)};
  private static final int NB_EXP = EXPIRY_PERIOD.length;
  private static final ZonedDateTime REFERENCE_SPOT = ScheduleCalculator.getAdjustedDate(REFERENCE_DATE, SETTLEMENT_DAYS, CALENDAR);
  private static final ZonedDateTime[] PAY_DATE = new ZonedDateTime[NB_EXP];
  private static final ZonedDateTime[] EXPIRY_DATE = new ZonedDateTime[NB_EXP];
  private static final double[] TIME_TO_EXPIRY = new double[NB_EXP + 1];
  static {
    TIME_TO_EXPIRY[0] = 0.0;
    for (int loopexp = 0; loopexp < NB_EXP; loopexp++) {
      PAY_DATE[loopexp] = ScheduleCalculator.getAdjustedDate(REFERENCE_SPOT, EXPIRY_PERIOD[loopexp], BUSINESS_DAY, CALENDAR);
      EXPIRY_DATE[loopexp] = ScheduleCalculator.getAdjustedDate(PAY_DATE[loopexp], -SETTLEMENT_DAYS, CALENDAR);
      TIME_TO_EXPIRY[loopexp + 1] = TimeCalculator.getTimeBetween(REFERENCE_DATE, EXPIRY_DATE[loopexp]);
    }
  }
  private static final double[] ATM = {0.175, 0.185, 0.18, 0.17, 0.16, 0.16};
  private static final double[] DELTA = new double[] {0.10, 0.25};
  private static final double[][] RISK_REVERSAL = new double[][] { {-0.010, -0.0050}, {-0.011, -0.0060}, {-0.012, -0.0070}, {-0.013, -0.0080}, {-0.014, -0.0090}, {-0.014, -0.0090}};
  private static final double[][] STRANGLE = new double[][] { {0.0300, 0.0100}, {0.0310, 0.0110}, {0.0320, 0.0120}, {0.0330, 0.0130}, {0.0340, 0.0140}, {0.0340, 0.0140}};
  private static final SmileDeltaTermStructureParameter SMILE_TERM = new SmileDeltaTermStructureParameter(TIME_TO_EXPIRY, DELTA, ATM, RISK_REVERSAL, STRANGLE);
  private static final SmileDeltaTermStructureDataBundle SMILE_BUNDLE = new SmileDeltaTermStructureDataBundle(CURVES, FX_MATRIX, SMILE_TERM, Pair.of(USD, KRW));

  @Test
  /**
   * Tests the present value of NDO by comparison with vanilla European options.
   */
  public void presentValue() {
    MultipleCurrencyAmount pvNDO = METHOD_NDO.presentValue(NDO, SMILE_BUNDLE);
    MultipleCurrencyAmount pvFXO = METHOD_FXO.presentValue(FOREX_OPT, SMILE_BUNDLE);
    assertEquals("Forex non-deliverable option: present value", pvFXO, pvNDO);
  }

  @Test
  /**
   * Check the coherence of the present value of NDO in the method and in the calculator.
   */
  public void presentValueMethodVsCalculator() {
    MultipleCurrencyAmount pvMethod = METHOD_NDO.presentValue(NDO, SMILE_BUNDLE);
    MultipleCurrencyAmount pvCalculator = PVC_BLACK.visit(NDO, SMILE_BUNDLE);
    assertEquals("Forex non-deliverable option: present value", pvMethod, pvCalculator);
  }

  @Test
  /**
   * Tests the currency exposure against the present value.
   */
  public void currencyExposureVsPresentValue() {
    MultipleCurrencyAmount pv = METHOD_NDO.presentValue(NDO, SMILE_BUNDLE);
    MultipleCurrencyAmount ce = METHOD_NDO.currencyExposure(NDO, SMILE_BUNDLE);
    assertEquals("Forex vanilla option: currency exposure vs present value", ce.getAmount(USD) + ce.getAmount(KRW) / USD_KRW, pv.getAmount(USD), 1E-2);
  }

  @Test
  /**
   * Check the coherence of the currency exposure of NDO in the method and in the calculator.
   */
  public void currencyExposureMethodVsCalculator() {
    MultipleCurrencyAmount ceMethod = METHOD_NDO.currencyExposure(NDO, SMILE_BUNDLE);
    MultipleCurrencyAmount ceCalculator = CE_BLACK.visit(NDO, SMILE_BUNDLE);
    assertEquals("Forex non-deliverable option: currency exposure", ceMethod, ceCalculator);
  }

  @Test
  /**
   * Tests the present value curve sensitivity of NDO by comparison with vanilla European options.
   */
  public void presentValueCurveSensitivity() {
    double tolerance = 1.0E-2;
    MultipleCurrencyInterestRateCurveSensitivity pvcsNDO = METHOD_NDO.presentValueCurveSensitivity(NDO, SMILE_BUNDLE);
    MultipleCurrencyInterestRateCurveSensitivity pvcsFXO = METHOD_FXO.presentValueCurveSensitivity(FOREX_OPT, SMILE_BUNDLE);
    assertTrue("Forex non-deliverable option: present value curve sensitivity", InterestRateCurveSensitivity.compare(pvcsFXO.getSensitivity(USD), pvcsNDO.getSensitivity(USD), tolerance));
  }

  @Test
  /**
   * Tests the forward rate of NDO.
   */
  public void forwardForexRate() {
    double fwd = METHOD_NDO.forwardForexRate(NDO, SMILE_BUNDLE);
    double fwdExpected = METHOD_NDF.forwardForexRate(NDO.getUnderlyingNDF(), SMILE_BUNDLE);
    assertEquals("Forex non-deliverable option: forward rate", fwdExpected, fwd, 1.0E-10);
  }

  @Test
  /**
   * Tests the forward Forex rate through the method and through the calculator.
   */
  public void forwardRateMethodVsCalculator() {
    double fwdMethod = METHOD_NDO.forwardForexRate(NDO, SMILE_BUNDLE);
    ForwardRateForexCalculator FWDC = ForwardRateForexCalculator.getInstance();
    double fwdCalculator = FWDC.visit(NDO, SMILE_BUNDLE);
    assertEquals("Forex: forward rate", fwdMethod, fwdCalculator, 1.0E-10);
  }

  @Test
  /**
   * Tests the present value curve sensitivity of NDO by comparison with vanilla European options.
   */
  public void presentValueVolatilitySensitivity() {
    double tolerance = 1.0E-2;
    PresentValueForexBlackVolatilitySensitivity pvvsNDO = METHOD_NDO.presentValueVolatilitySensitivity(NDO, SMILE_BUNDLE);
    PresentValueForexBlackVolatilitySensitivity pvvsFXO = METHOD_FXO.presentValueVolatilitySensitivity(FOREX_OPT, SMILE_BUNDLE);
    final DoublesPair point = DoublesPair.of(NDO.getExpiryTime(), NDO.getStrike());
    assertEquals("Forex non-deliverable option: present value curve sensitivity", pvvsFXO.getVega().getMap().get(point), pvvsNDO.getVega().getMap().get(point), tolerance);
  }

  @Test
  /**
   * Tests the present value curve sensitivity of NDO by comparison with vanilla European options.
   */
  public void presentValueVolatilityNodeSensitivity() {
    double tolerance = 1.0E-2;
    PresentValueVolatilityNodeSensitivityDataBundle nsNDO = METHOD_NDO.presentValueVolatilityNodeSensitivity(NDO, SMILE_BUNDLE);
    PresentValueVolatilityNodeSensitivityDataBundle nsFXO = METHOD_FXO.presentValueVolatilityNodeSensitivity(FOREX_OPT, SMILE_BUNDLE);
    for (int loopexp = 0; loopexp < NB_EXP; loopexp++) {
      for (int loopstrike = 0; loopstrike < nsNDO.getDelta().getNumberOfElements(); loopstrike++) {
        assertEquals("Forex non-deliverable option: vega node", nsFXO.getVega().getEntry(loopexp, loopstrike), nsNDO.getVega().getEntry(loopexp, loopstrike), tolerance);
      }
    }
  }

}
