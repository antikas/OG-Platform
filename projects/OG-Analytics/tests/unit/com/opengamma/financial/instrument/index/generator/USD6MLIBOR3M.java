/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.financial.instrument.index.generator;

import javax.time.calendar.Period;

import com.opengamma.financial.convention.calendar.Calendar;
import com.opengamma.financial.convention.daycount.DayCountFactory;
import com.opengamma.financial.instrument.index.GeneratorSwap;
import com.opengamma.financial.instrument.index.iborindex.USDLIBOR3M;

/**
 * Swap generator for the USD semi-annual 30/360 vs Libor 3M.
 */
public class USD6MLIBOR3M extends GeneratorSwap {

  /**
   * Constructor.
   * @param calendar A USD calendar.
   */
  public USD6MLIBOR3M(Calendar calendar) {
    super(Period.ofMonths(6), DayCountFactory.INSTANCE.getDayCount("30/360"), new USDLIBOR3M(calendar));
  }

}
