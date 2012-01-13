/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.financial.forex.calculator;

import com.opengamma.financial.forex.derivative.ForexNonDeliverableOption;
import com.opengamma.financial.forex.derivative.ForexOptionDigital;
import com.opengamma.financial.forex.derivative.ForexOptionSingleBarrier;
import com.opengamma.financial.forex.derivative.ForexOptionVanilla;
import com.opengamma.financial.forex.method.ForexNonDeliverableOptionBlackMethod;
import com.opengamma.financial.forex.method.ForexOptionDigitalBlackMethod;
import com.opengamma.financial.forex.method.ForexOptionSingleBarrierBlackMethod;
import com.opengamma.financial.forex.method.ForexOptionVanillaBlackMethod;
import com.opengamma.financial.forex.method.PresentValueVolatilityNodeSensitivityDataBundle;
import com.opengamma.financial.interestrate.AbstractInstrumentDerivativeVisitor;
import com.opengamma.financial.model.option.definition.SmileDeltaTermStructureDataBundle;

/**
 * Calculator of the present value volatility sensitivity for Forex derivatives in the Black (Garman-Kohlhagen) world. The volatilities are given by delta-smile descriptions.
 */
public final class PresentValueForexVegaSensitivityCalculator extends AbstractInstrumentDerivativeVisitor<SmileDeltaTermStructureDataBundle, PresentValueVolatilityNodeSensitivityDataBundle> {

  /**
   * The unique instance of the calculator.
   */
  private static final PresentValueForexVegaSensitivityCalculator INSTANCE = new PresentValueForexVegaSensitivityCalculator();

  /**
   * Gets the calculator instance.
   * @return The calculator.
   */
  public static PresentValueForexVegaSensitivityCalculator getInstance() {
    return INSTANCE;
  }

  /**
   * Constructor.
   */
  private PresentValueForexVegaSensitivityCalculator() {
  }

  /**
   * The methods used by the different instruments.
   */
  private static final ForexOptionVanillaBlackMethod METHOD_FXOPTION = ForexOptionVanillaBlackMethod.getInstance();
  private static final ForexOptionSingleBarrierBlackMethod METHOD_FXOPTIONBARRIER = ForexOptionSingleBarrierBlackMethod.getInstance();
  private static final ForexNonDeliverableOptionBlackMethod METHOD_NDO = ForexNonDeliverableOptionBlackMethod.getInstance();
  private static final ForexOptionDigitalBlackMethod METHOD_FXOPTIONDIGITAL = ForexOptionDigitalBlackMethod.getInstance();

  @Override
  public PresentValueVolatilityNodeSensitivityDataBundle visitForexOptionVanilla(final ForexOptionVanilla option, final SmileDeltaTermStructureDataBundle data) {
    return METHOD_FXOPTION.presentValueVolatilityNodeSensitivity(option, data);
  }

  @Override
  public PresentValueVolatilityNodeSensitivityDataBundle visitForexOptionSingleBarrier(final ForexOptionSingleBarrier option, final SmileDeltaTermStructureDataBundle data) {
    return METHOD_FXOPTIONBARRIER.presentValueVolatilityNodeSensitivity(option, data);
  }

  @Override
  public PresentValueVolatilityNodeSensitivityDataBundle visitForexNonDeliverableOption(final ForexNonDeliverableOption option, final SmileDeltaTermStructureDataBundle data) {
    return METHOD_NDO.presentValueVolatilityNodeSensitivity(option, data);
  }

  @Override
  public PresentValueVolatilityNodeSensitivityDataBundle visitForexOptionDigital(final ForexOptionDigital option, final SmileDeltaTermStructureDataBundle data) {
    return METHOD_FXOPTIONDIGITAL.presentValueVolatilityNodeSensitivity(option, data);
  }

}
