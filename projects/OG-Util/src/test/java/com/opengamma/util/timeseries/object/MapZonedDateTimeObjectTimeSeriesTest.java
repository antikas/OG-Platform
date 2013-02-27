/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.util.timeseries.object;


import java.math.BigDecimal;
import java.util.List;

import org.testng.annotations.Test;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;

import com.opengamma.util.timeseries.ObjectTimeSeries;
import com.opengamma.util.timeseries.zoneddatetime.MapZonedDateTimeObjectTimeSeries;
import com.opengamma.util.timeseries.zoneddatetime.ZonedDateTimeObjectTimeSeries;

@Test(groups = "unit")
public class MapZonedDateTimeObjectTimeSeriesTest extends ZonedDateTimeObjectTimeSeriesTest {
  @Override
  public ZonedDateTimeObjectTimeSeries<BigDecimal> createEmptyTimeSeries() {
    return new MapZonedDateTimeObjectTimeSeries<BigDecimal>(ZoneOffset.UTC);
  }

  @Override
  public ZonedDateTimeObjectTimeSeries<BigDecimal> createTimeSeries(ZonedDateTime[] times, BigDecimal[] values) {
    return new MapZonedDateTimeObjectTimeSeries<BigDecimal>(ZoneOffset.UTC, times, values);
  }

  @Override
  public ZonedDateTimeObjectTimeSeries<BigDecimal> createTimeSeries(List<ZonedDateTime> times, List<BigDecimal> values) {
    return new MapZonedDateTimeObjectTimeSeries<BigDecimal>(ZoneOffset.UTC, times, values);
  }

  @Override
  public ObjectTimeSeries<ZonedDateTime, BigDecimal> createTimeSeries(ObjectTimeSeries<ZonedDateTime, BigDecimal> dts) {
    return new MapZonedDateTimeObjectTimeSeries<BigDecimal>(ZoneOffset.UTC, dts);
  }
}
