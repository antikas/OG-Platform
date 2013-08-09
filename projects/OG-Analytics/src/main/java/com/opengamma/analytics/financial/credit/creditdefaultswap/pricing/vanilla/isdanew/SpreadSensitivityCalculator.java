/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.credit.creditdefaultswap.pricing.vanilla.isdanew;

import java.util.Arrays;

import com.opengamma.analytics.financial.credit.PriceType;
import com.opengamma.analytics.financial.model.BumpType;
import com.opengamma.analytics.math.differentiation.FiniteDifferenceType;
import com.opengamma.util.ArgumentChecker;

/**
 * This calculates the sensitivity of the present value of a CDS to various (finite) shifts of the market spreads -
 * this is performed by a "bump and reprice" so is accurate for arbitrarily large shifts/bumps.<br> For small bumps (typically 
 * less than 1bps) it approximates the derivative $$\frac{\partial V}{\partial S}$$  where $V$ is the present value and $S$ is
 * either a single market spread or the entire market spread curve. However, it is better (in accuracy and speed) to use 
 * AnalyticSpreadSensitivityCalculator if this derivative is required.  
 */
public class SpreadSensitivityCalculator {

  // private static final ISDACompliantCreditCurveBuild BUILDER = new ISDACompliantCreditCurveBuild();
  private static final PointsUpFrontConverter PUF_CONVERTER = new PointsUpFrontConverter();
  private static final ISDACompliantCreditCurveBuilder BUILDER = new FastCreditCurveBuilder();
  private static final AnalyticCDSPricer PRICER = new AnalyticCDSPricer();

  //***************************************************************************************************************
  // parallel CS01 of a CDS from single market quote of that CDS
  //***************************************************************************************************************

  /**
   * The CS01 (or credit DV01)  of a CDS - the sensitivity of the PV to a finite increase of market spread (on NOT the CDS's 
   * coupon). If the CDS is quoted as points up-front, this is first converted to a quoted spread, and <b>this</b> is bumped 
   * @param cds analytic description of a CDS traded at a certain time - it is this CDS that we are calculation CDV01 for
   * @param quote The market quote for the CDS - these can be ParSpread, PointsUpFront or QuotedSpread
   * @param yieldCurve The yield (or discount) curve 
   * @param fracBumpAmount The fraction bump amount of the spread so a 1pb bump is 1e-4 
   * @return
   */
  public double parallelCS01(final CDSAnalytic cds, final CDSQuoteConvention quote, final ISDACompliantYieldCurve yieldCurve, final double fracBumpAmount) {
    if (quote instanceof QuotedSpread) {
      final QuotedSpread qSpread = (QuotedSpread) quote;
      return parallelCS01FromParSpreads(cds, qSpread.getCoupon(), yieldCurve, new CDSAnalytic[] {cds }, new double[] {qSpread.getQuotedSpread() }, fracBumpAmount, BumpType.ADDITIVE);
    } else if (quote instanceof PointsUpFront) {
      final PointsUpFront puf = (PointsUpFront) quote;
      return parallelCS01FromPUF(cds, puf.getCoupon(), yieldCurve, puf.getPointsUpFront(), fracBumpAmount);
    } else if (quote instanceof ParSpread) {
      return parallelCS01FromParSpreads(cds, quote.getCoupon(), yieldCurve, new CDSAnalytic[] {cds }, new double[] {quote.getCoupon() }, fracBumpAmount, BumpType.ADDITIVE);
    }
    throw new IllegalArgumentException("unknow type " + quote.getClass());
  }

  /**
   *The CS01 (or credit DV01) by a shift of the quoted (or flat) spread of the CDS <b>when the CDS is quoted as points up-front (PUF)</b>.<br>
   *This simply converts the PUF quote to a quoted (or flat) spread then calls parallelCS01FromQuotedSpread
   * @param cds  analytic description of a CDS traded at a certain time - it is this CDS that we are calculation CDV01 for
   * @param coupon  the of the traded CDS  (expressed as <b>fractions not basis points</b>)
   * @param yieldCurve The yield (or discount) curve 
   * @param puf points up-front (as a fraction) 
   * @param fracBumpAmount The fraction bump amount <b>of the quoted (or flat) spread</b>, so a 1pb bump is 1e-4 
   * @return  The credit DV01
   */
  public double parallelCS01FromPUF(final CDSAnalytic cds, final double coupon, final ISDACompliantYieldCurve yieldCurve, final double puf, final double fracBumpAmount) {
    final double bumpedQSpread = PUF_CONVERTER.pufToQuotedSpread(cds, coupon, yieldCurve, puf) + fracBumpAmount;
    final ISDACompliantCreditCurve bumpedCurve = BUILDER.calibrateCreditCurve(cds, bumpedQSpread, yieldCurve);
    final double bumpedPrice = PRICER.pv(cds, yieldCurve, bumpedCurve, coupon);
    return (bumpedPrice - puf) / fracBumpAmount;
  }

  /**
   * The CS01 (or credit DV01) by a shift of the market spread of the CDS (the coupon is unchanged). This finds two flat
   *  credit/hazard curves from  the CDS with its original spread and with the spread bumped. The traded CDS is then priced
   * off both curves, using its coupon, and the difference (divided by the bump size) is the credit DV01 
   * @param cds analytic description of a CDS traded at a certain time - it is this CDS that we are calculation CDV01 for
   * @param coupon the of the traded CDS  (expressed as <b>fractions not basis points</b>)
   * @param yieldCurve  The yield (or discount) curve 
   * @param marketSpread the market spread of the reference CDS (in this case it is irrelevant whether this is par or quoted spread)
   * @param fracBumpAmount The fraction bump amount, so a 1pb bump is 1e-4 
   * @param bumpType ADDITIVE or MULTIPLICATIVE
   * @return The credit DV01
   */
  public double parallelCS01FromSpread(final CDSAnalytic cds, final double coupon, final ISDACompliantYieldCurve yieldCurve, final double marketSpread, final double fracBumpAmount,
      final BumpType bumpType) {
    return parallelCS01FromParSpreads(cds, coupon, yieldCurve, new CDSAnalytic[] {cds }, new double[] {marketSpread }, fracBumpAmount, bumpType);
  }

  //***************************************************************************************************************
  // parallel CS01 of CDS from single market quote of (potentially) different CDS 
  //***************************************************************************************************************

  /**
   * The CS01 (or credit DV01) by a shift of the quoted (or flat) spread of the reference CDS. This finds two flat credit/hazard curves from  
   * a reference CDS with its original quoted (or flat) spread and with the spread bumped. The traded CDS is then priced off both curves, using its coupon, 
   * and the difference (divided by the bump size) is the credit DV01 
   * @param cds analytic description of a CDS traded at a certain time - it is this CDS that we are calculation CDV01 for
   * @param coupon the coupon of the traded CDS  (expressed as <b>fractions not basis points</b>)
   * @param yieldCurve  The yield (or discount) curve 
   * @param referenceCDS the reference CDS use to find the flat credit/hazard curve (this is often the same as the traded CDS)
   * @param quotedSpread the quoted (or flat) spread of the reference CDS
   * @param fracBumpAmount The fraction bump amount, so a 1pb bump is 1e-4 
   * @param bumpType ADDITIVE or MULTIPLICATIVE
   * @return The credit DV01
   */
  public double parallelCS01FromQuotedSpread(final CDSAnalytic cds, final double coupon, final ISDACompliantYieldCurve yieldCurve, final CDSAnalytic referenceCDS, final double quotedSpread,
      final double fracBumpAmount, final BumpType bumpType) {
    ArgumentChecker.notNull(cds, "cds");
    ArgumentChecker.notNull(referenceCDS, "referanceCDS");
    ArgumentChecker.notNull(yieldCurve, "yieldCurve");
    ArgumentChecker.notNull(bumpType, "bumpType");
    ArgumentChecker.isTrue(Math.abs(fracBumpAmount) > 1e-10, "bump amount too small");
    return parallelCS01FromParSpreads(cds, coupon, yieldCurve, new CDSAnalytic[] {referenceCDS }, new double[] {quotedSpread }, fracBumpAmount, bumpType);
  }

  //***************************************************************************************************************
  // parallel CS01 of a CDS from a set of market quotes at pillar dates (e.g. 6M, 1Y, 3Y, 5Y, 10Y)  
  //***************************************************************************************************************

  /**
   * The CS01 (or credit DV01) by a parallel shift of the market spreads (CDS spread curve). This takes an extraneous yield curve,
   *  a set of reference CDSs (marketCDSs) and their market quotes and bootstraps a credit (hazard) curve - 
   * the target CDS is then priced with this credit curve. This is then repeated with the market spreads bumped in parallel by 
   * some amount. The result is the difference (bumped minus base price) is divided by the bump amount.<br>
   * This can take quotes as ParSpread, PointsUpFront or QuotedSpread (or some mix).  For par-spreads, these are bumped and a 
   * new credit curve built; for quoted-spreads, there are bumped and a new curve build be first converting to PUF; and finally 
   * for PUF, these are converted to quoted spreads, bumped and converted back to build the credit curve. 
   * @param cds analytic description of a CDS traded at a certain time - it is this CDS that we are calculation CDV01 for
   * @param cdsCoupon the coupon of the traded CDS  (expressed as <b>fractions not basis points</b>)
   * @param yieldCurve The yield (or discount) curve 
   * @param marketCDSs The market CDSs - these are the reference instruments used to build the credit curve 
   * @param quotes The quotes for the market CDSs - these can be ParSpread, PointsUpFront or QuotedSpread (or any mixture of these)
   * @param fracBumpAmount The fraction bump amount, so a 1pb bump is 1e-4
   * @return  The credit DV01
   */
  public double parallelCS01FromPillarQuotes(final CDSAnalytic cds, final double cdsCoupon, final ISDACompliantYieldCurve yieldCurve, final CDSAnalytic[] marketCDSs,
      final CDSQuoteConvention[] quotes, final double fracBumpAmount) {
    ArgumentChecker.notNull(cds, "cds");
    ArgumentChecker.noNulls(marketCDSs, "curvePoints");
    ArgumentChecker.notNull(yieldCurve, "yieldCurve");
    ArgumentChecker.noNulls(quotes, "quotes");
    ArgumentChecker.isTrue(Math.abs(fracBumpAmount) > 1e-10, "bump amount too small");
    final int n = marketCDSs.length;
    ArgumentChecker.isTrue(n == quotes.length, "speads length does not match curvePoints");

    final ISDACompliantCreditCurve baseCurve = BUILDER.calibrateCreditCurve(marketCDSs, quotes, yieldCurve);
    final double basePrice = PRICER.pv(cds, yieldCurve, baseCurve, cdsCoupon);

    final CDSQuoteConvention[] bumpedQuotes = bumpQuotes(marketCDSs, quotes, yieldCurve, fracBumpAmount);
    final ISDACompliantCreditCurve bumpedCurve = BUILDER.calibrateCreditCurve(marketCDSs, bumpedQuotes, yieldCurve);
    final double bumpedPrice = PRICER.pv(cds, yieldCurve, bumpedCurve, cdsCoupon);

    return (bumpedPrice - basePrice) / fracBumpAmount;
  }

  /**
   * The CS01 (or credit DV01) by a parallel shift of the market par spreads (CDS par spread curve). This takes an extraneous yield curve, a set of reference CDSs 
   * (marketCDSs) and their par-spreads (expressed as <b>fractions not basis points</b>) and bootstraps a credit (hazard) curve - 
   * the target CDS is then priced with this credit curve. This is then repeated with the market spreads bumped in parallel by 
   * some amount. The result is the difference (bumped minus base price) is divided by the bump amount.<br>
   * For small bumps (<1e-4) this approximates $$\frac{\partial V}{\partial S}$$<br>
   * Credit DV01 is (often) defined as -( V(S + 1bp) - V(s)) - to achieve this use fracBumpAmount = 1e-4 and bumpType ADDITIVE
   * @param cds analytic description of a CDS traded at a certain time 
   * @param cdsFracSpread The <b>fraction</b> spread of the CDS
   * @param yieldCurve The yield (or discount) curve  
   * @param marketCDSs The market CDSs - these are the reference instruments used to build the credit curve 
   * @param parSpreads The <b>fractional</b> spreads of the market CDSs 
   * @param fracBumpAmount The fraction bump amount, so a 1pb bump is 1e-4 
   * @param bumpType ADDITIVE or MULTIPLICATIVE
   * @return The credit DV01
   */
  public double parallelCS01FromParSpreads(final CDSAnalytic cds, final double cdsFracSpread, final ISDACompliantYieldCurve yieldCurve, final CDSAnalytic[] marketCDSs, final double[] parSpreads,
      final double fracBumpAmount, final BumpType bumpType) {
    ArgumentChecker.notNull(cds, "cds");
    ArgumentChecker.noNulls(marketCDSs, "curvePoints");
    ArgumentChecker.notEmpty(parSpreads, "spreads");
    ArgumentChecker.notNull(yieldCurve, "yieldCurve");
    ArgumentChecker.notNull(bumpType, "bumpType");
    ArgumentChecker.isTrue(Math.abs(fracBumpAmount) > 1e-10, "bump amount too small");
    final int n = marketCDSs.length;
    ArgumentChecker.isTrue(n == parSpreads.length, "speads length does not match curvePoints");
    final double[] bumpedSpreads = makeBumpedSpreads(parSpreads, fracBumpAmount, bumpType);
    final double diff = fdCreditDV01(cds, cdsFracSpread, marketCDSs, bumpedSpreads, parSpreads, yieldCurve, PriceType.DIRTY);
    return diff / fracBumpAmount;
  }

  //***************************************************************************************************************
  // bucked CS01 - the sensitivity of the CDS's PV to the market spreads used to build the credit curve - these are
  // the pillar dates (e.g. 6M, 1Y, 3Y, 5Y, 10Y) 
  //***************************************************************************************************************

  /**
   * The bucked CS01 (or credit DV01) by a shift of each the market spread in turn. This takes an extraneous yield curve,
   *  a set of reference CDSs (marketCDSs) and their market quotes and bootstraps a credit (hazard) curve - 
   * the target CDS is then priced with this credit curve. This is then repeated with each market spreads bumped in turn by 
   * some amount. The result is the array of differences (bumped minus base price) is divided by the bump amount.<br>
   * This can take quotes as ParSpread, PointsUpFront or QuotedSpread (or some mix).  For par-spreads, these are bumped and a 
   * new credit curve built; for quoted-spreads, there are bumped and a new curve build be first converting to PUF; and finally 
   * for PUF, these are converted to quoted spreads, bumped and converted back to build the credit curve. 
   * @param cds analytic description of a CDS traded at a certain time - it is this CDS that we are calculation CDV01 for
   * @param cdsCoupon the coupon of the traded CDS  (expressed as <b>fractions not basis points</b>)
   * @param yieldCurve The yield (or discount) curve 
   * @param marketCDSs The market CDSs - these are the reference instruments used to build the credit curve 
   * @param quotes The quotes for the market CDSs - these can be ParSpread, PointsUpFront or QuotedSpread (or any mixture of these)
   * @param fracBumpAmount The fraction bump amount, so a 1pb bump is 1e-4
   * @return  The bucketed credit DV01
   */
  public double[] bucketedCS01FromPillarQuotes(final CDSAnalytic cds, final double cdsCoupon, final ISDACompliantYieldCurve yieldCurve, final CDSAnalytic[] marketCDSs,
      final CDSQuoteConvention[] quotes, final double fracBumpAmount) {

    ArgumentChecker.notNull(cds, "cds");
    ArgumentChecker.noNulls(marketCDSs, "curvePoints");
    ArgumentChecker.noNulls(quotes, "quotes");
    ArgumentChecker.notNull(yieldCurve, "yieldCurve");
    ArgumentChecker.isTrue(Math.abs(fracBumpAmount) > 1e-10, "bump amount too small");
    final int n = marketCDSs.length;
    ArgumentChecker.isTrue(n == quotes.length, "speads length does not match curvePoints");

    final ISDACompliantCreditCurve baseCurve = BUILDER.calibrateCreditCurve(marketCDSs, quotes, yieldCurve);
    final double basePrice = PRICER.pv(cds, yieldCurve, baseCurve, cdsCoupon);
    final double[] res = new double[n];
    for (int i = 0; i < n; i++) {
      final CDSQuoteConvention[] bumpedQuotes = bumpQuoteAtIndex(marketCDSs, quotes, yieldCurve, fracBumpAmount, i);
      final ISDACompliantCreditCurve bumpedCurve = BUILDER.calibrateCreditCurve(marketCDSs, bumpedQuotes, yieldCurve);
      final double price = PRICER.pv(cds, yieldCurve, bumpedCurve, cdsCoupon);
      res[i] = (price - basePrice) / fracBumpAmount;
    }
    return res;
  }

  /**
   * The bucked CS01 (or credit DV01) by shifting each  market par-spread in turn. This takes an extraneous yield curve, a set of reference CDSs 
   * (marketCDSs) and their par-spreads (expressed as <b>fractions not basis points</b>) and bootstraps a credit (hazard) curve - 
   * the target CDS is then priced with this credit curve. This is then repeated with each market spreads bumped in turn. 
   * The result is the vector of differences (bumped minus base price) divided by the bump amount.<br>
   * For small bumps (<1e-4) this approximates $$\frac{\partial V}{\partial S_i}$$ where $$S_i$$ is the spread of the $$1^{th}$$
   * market CDS<br>
   * @param cds analytic description of a CDS traded at a certain time 
   * @param cdsCoupon The <b>fraction</b> spread of the CDS
   * @param yieldCurve The yield (or discount) curve  
   * @param marketCDSs The market CDSs - these are the reference instruments used to build the credit curve 
   * @param marketParSpreads The <b>fractional</b> par-spreads of the market CDSs 
   * @param fracBumpAmount The fraction bump amount, so a 1pb bump is 1e-4 
   * @param bumpType ADDITIVE or MULTIPLICATIVE
   * @return The credit DV01
   * @return
   */
  public double[] bucketedCS01FromParSpreads(final CDSAnalytic cds, final double cdsCoupon, final ISDACompliantYieldCurve yieldCurve, final CDSAnalytic[] marketCDSs, final double[] marketParSpreads,
      final double fracBumpAmount, final BumpType bumpType) {
    ArgumentChecker.notNull(cds, "cds");
    ArgumentChecker.noNulls(marketCDSs, "curvePoints");
    ArgumentChecker.notEmpty(marketParSpreads, "spreads");
    ArgumentChecker.notNull(yieldCurve, "yieldCurve");
    ArgumentChecker.notNull(bumpType, "bumpType");
    ArgumentChecker.isTrue(Math.abs(fracBumpAmount) > 1e-10, "bump amount too small");
    final int n = marketCDSs.length;
    ArgumentChecker.isTrue(n == marketParSpreads.length, "speads length does not match curvePoints");
    final PriceType priceType = PriceType.DIRTY;

    final ISDACompliantCreditCurve baseCurve = BUILDER.calibrateCreditCurve(marketCDSs, marketParSpreads, yieldCurve);
    final double basePrice = PRICER.pv(cds, yieldCurve, baseCurve, cdsCoupon, priceType);

    final double[] res = new double[n];
    for (int i = 0; i < n; i++) {
      final double[] temp = makeBumpedSpreads(marketParSpreads, fracBumpAmount, bumpType, i);
      final ISDACompliantCreditCurve bumpedCurve = BUILDER.calibrateCreditCurve(marketCDSs, temp, yieldCurve);
      final double price = PRICER.pv(cds, yieldCurve, bumpedCurve, cdsCoupon, priceType);
      res[i] = (price - basePrice) / fracBumpAmount;
    }

    return res;
  }

  /**
   * The bucked CS01 (or credit DV01) by bumping each quoted (or flat) spread in turn. This takes an extraneous yield curve,
   *  a set of reference CDSs (marketCDSs) and their quoted (or flat) spreads (expressed as <b>fractions not basis points</b>) and
   *  bootstraps a credit (hazard) curve - 
   * the target CDS is then priced with this credit curve. This is then repeated with each market spreads bumped in turn. 
   * The result is the vector of differences (bumped minus base price) divided by the bump amount.<br>
   * For small bumps (<1e-4) this approximates $$\frac{\partial V}{\partial S_i}$$ for a flat curve where $$S_i$$ 
   * is the spread of the $$1^{th}$$ market CDS
   * @param cds analytic description of a CDS traded at a certain time 
   * @param dealSpread The <b>fraction</b> spread of the CDS
   * @param yieldCurve The yield (or discount) curve  
   * @param marketCDSs The market CDSs - these are the reference instruments used to build the credit curve 
   * @param quotedSpreads The <b>fractional</b> spreads of the market CDSs 
   * @param fracBumpAmount The fraction bump amount, so a 1pb bump is 1e-4 
   * @param bumpType ADDITIVE or MULTIPLICATIVE
   * @return The bucked CS01 for a single CDS 
   */
  public double[] bucketedCS01FromQuotedSpreads(final CDSAnalytic cds, final double dealSpread, final ISDACompliantYieldCurve yieldCurve, final CDSAnalytic[] marketCDSs, final double[] quotedSpreads,
      final double fracBumpAmount, final BumpType bumpType) {
    ArgumentChecker.notNull(cds, "cds");
    ArgumentChecker.noNulls(marketCDSs, "curvePoints");
    ArgumentChecker.notEmpty(quotedSpreads, "spreads");
    ArgumentChecker.notNull(yieldCurve, "yieldCurve");
    ArgumentChecker.notNull(bumpType, "bumpType");
    ArgumentChecker.isTrue(Math.abs(fracBumpAmount) > 1e-10, "bump amount too small");
    final int n = marketCDSs.length;
    ArgumentChecker.isTrue(n == quotedSpreads.length, "speads length does not match curvePoints");
    final PriceType priceType = PriceType.DIRTY;
    final double[] premiums = new double[n];
    Arrays.fill(premiums, dealSpread); // assume the premiums of all CDS are equal

    final double[] puf = PUF_CONVERTER.quotedSpreadsToPUF(marketCDSs, premiums, yieldCurve, quotedSpreads);
    final ISDACompliantCreditCurve baseCurve = BUILDER.calibrateCreditCurve(marketCDSs, premiums, yieldCurve, puf);
    final double basePrice = PRICER.pv(cds, yieldCurve, baseCurve, dealSpread, priceType);

    final double[] bumpedPUF = new double[n];
    final double[] res = new double[n];
    for (int i = 0; i < n; i++) {
      System.arraycopy(puf, 0, bumpedPUF, 0, n);
      final double bumpedSpread = bumpedSpread(quotedSpreads[i], fracBumpAmount, bumpType);
      bumpedPUF[i] = PUF_CONVERTER.quotedSpreadToPUF(marketCDSs[i], premiums[i], yieldCurve, bumpedSpread);
      // TODO a lot of unnecessary recalibration here
      final ISDACompliantCreditCurve bumpedCurve = BUILDER.calibrateCreditCurve(marketCDSs, premiums, yieldCurve, bumpedPUF);
      final double price = PRICER.pv(cds, yieldCurve, bumpedCurve, dealSpread, priceType);
      res[i] = (price - basePrice) / fracBumpAmount;
    }
    return res;
  }

  /**
   * The bucked CS01 (or credit DV01) on a set of CDSS by bumping each quoted (or flat) spread in turn. This takes an extraneous yield curve,
   *  a set of reference CDSs (marketCDSs) and their quoted (or flat) spreads (expressed as <b>fractions not basis points</b>) and
   *  bootstraps a credit (hazard) curve - 
   * the target CDS is then priced with this credit curve. This is then repeated with each market spreads bumped in turn. 
   * The result is the vector of differences (bumped minus base price) divided by the bump amount.<br>
   * For small bumps (<1e-4) this approximates $$\frac{\partial V}{\partial S_i}$$ for a flat curve where $$S_i$$ 
   * @param cds a set of analytic description of  CDSs traded at a certain times 
   * @param dealSpread The <b>fraction</b> spread of the CDS
   * @param yieldCurve The yield (or discount) curve  
   * @param marketCDSs The market CDSs - these are the reference instruments used to build the credit curve 
   * @param quotedSpreads The <b>fractional</b> spreads of the market CDSs 
   * @param fracBumpAmount The fraction bump amount, so a 1pb bump is 1e-4 
   * @param bumpType ADDITIVE or MULTIPLICATIVE
   * @return The bucked CS01 for a set of  CDSs
   */
  public double[][] bucketedCS01FromQuotedSpreads(final CDSAnalytic[] cds, final double dealSpread, final ISDACompliantYieldCurve yieldCurve, final CDSAnalytic[] marketCDSs,
      final double[] quotedSpreads, final double fracBumpAmount, final BumpType bumpType) {
    ArgumentChecker.noNulls(cds, "cds");
    ArgumentChecker.noNulls(marketCDSs, "curvePoints");
    ArgumentChecker.notEmpty(quotedSpreads, "spreads");
    ArgumentChecker.notNull(yieldCurve, "yieldCurve");
    ArgumentChecker.notNull(bumpType, "bumpType");
    ArgumentChecker.isTrue(Math.abs(fracBumpAmount) > 1e-10, "bump amount too small");
    final int nMarketCDSs = marketCDSs.length;
    ArgumentChecker.isTrue(nMarketCDSs == quotedSpreads.length, "speads length does not match curvePoints");
    final PriceType priceType = PriceType.DIRTY;
    final double[] premiums = new double[nMarketCDSs];
    Arrays.fill(premiums, dealSpread); // assume the premiums of all CDS are equal

    final int nTradeCDSs = cds.length;

    final double[] puf = PUF_CONVERTER.quotedSpreadsToPUF(marketCDSs, premiums, yieldCurve, quotedSpreads);
    //TODO not needed
    final ISDACompliantCreditCurve baseCurve = BUILDER.calibrateCreditCurve(marketCDSs, premiums, yieldCurve, puf);
    final double[] basePrices = new double[nTradeCDSs];
    for (int j = 0; j < nTradeCDSs; j++) {
      basePrices[j] = PRICER.pv(cds[j], yieldCurve, baseCurve, dealSpread, priceType);
    }

    final double[] bumpedPUF = new double[nMarketCDSs];
    final double[][] res = new double[nTradeCDSs][nMarketCDSs];

    for (int i = 0; i < nMarketCDSs; i++) { //Outer loop is over bumps
      System.arraycopy(puf, 0, bumpedPUF, 0, nMarketCDSs);
      final double bumpedSpread = bumpedSpread(quotedSpreads[i], fracBumpAmount, bumpType);
      bumpedPUF[i] = PUF_CONVERTER.quotedSpreadToPUF(marketCDSs[i], premiums[i], yieldCurve, bumpedSpread);
      // TODO a lot of unnecessary recalibration here
      final ISDACompliantCreditCurve bumpedCurve = BUILDER.calibrateCreditCurve(marketCDSs, premiums, yieldCurve, bumpedPUF);
      for (int j = 0; j < nTradeCDSs; j++) {
        final double price = PRICER.pv(cds[j], yieldCurve, bumpedCurve, dealSpread, priceType);
        res[j][i] = (price - basePrices[j]) / fracBumpAmount;
      }
    }
    return res;
  }

  /**
   * The difference in PV between two market spread 
   * @param cds analytic description of a CDS traded at a certain time 
   * @param cdsFracSpread The <b>fraction</b> spread of the CDS
   * @param priceType Clean or dirty price
   * @param yieldCurve The yield (or discount) curve  
   * @param marketCDSs The market CDSs - these are the reference instruments used to build the credit curve 
   * @param marketFracSpreads The <b>fractional</b> spreads of the market CDSs 
   * @param fracDeltaSpreads Non-negative shifts 
   * @param fdType
   * @return
   */
  public double finateDifferenceSpreadSensitivity(final CDSAnalytic cds, final double cdsFracSpread, final PriceType priceType, final ISDACompliantYieldCurve yieldCurve,
      final CDSAnalytic[] marketCDSs, final double[] marketFracSpreads, final double[] fracDeltaSpreads, final FiniteDifferenceType fdType) {
    ArgumentChecker.notNull(cds, "cds");
    ArgumentChecker.noNulls(marketCDSs, "curvePoints");
    ArgumentChecker.notEmpty(marketFracSpreads, "spreads");
    ArgumentChecker.notEmpty(fracDeltaSpreads, "deltaSpreads");
    ArgumentChecker.notNull(yieldCurve, "yieldCurve");
    ArgumentChecker.notNull(priceType, "priceType");

    final int n = marketCDSs.length;
    ArgumentChecker.isTrue(n == marketFracSpreads.length, "speads length does not match curvePoints");
    ArgumentChecker.isTrue(n == fracDeltaSpreads.length, "deltaSpreads length does not match curvePoints");
    for (int i = 0; i < n; i++) {
      ArgumentChecker.isTrue(marketFracSpreads[i] > 0, "spreads must be positive");
      ArgumentChecker.isTrue(fracDeltaSpreads[i] >= 0, "deltaSpreads must none negative");
      ArgumentChecker.isTrue(fdType == FiniteDifferenceType.FORWARD || fracDeltaSpreads[i] < marketFracSpreads[i], "deltaSpread must be less spread, unless forward difference is used");
    }

    switch (fdType) {
      case CENTRAL:
        return fdCentral(cds, cdsFracSpread, marketCDSs, marketFracSpreads, fracDeltaSpreads, yieldCurve, priceType);
      case FORWARD:
        return fdForward(cds, cdsFracSpread, marketCDSs, marketFracSpreads, fracDeltaSpreads, yieldCurve, priceType);
      case BACKWARD:
        return fdBackwards(cds, cdsFracSpread, marketCDSs, marketFracSpreads, fracDeltaSpreads, yieldCurve, priceType);
      default:
        throw new IllegalArgumentException("unknown type " + fdType);
    }
  }

  private double fdCreditDV01(final CDSAnalytic pricingCDS, final double cdsSpread, final CDSAnalytic[] curvePoints, final double[] spreadsUp, final double[] spreadsDown,
      final ISDACompliantYieldCurve yieldCurve, final PriceType priceType) {

    final ISDACompliantCreditCurve curveUp = BUILDER.calibrateCreditCurve(curvePoints, spreadsUp, yieldCurve);
    final ISDACompliantCreditCurve curveDown = BUILDER.calibrateCreditCurve(curvePoints, spreadsDown, yieldCurve);
    final double up = PRICER.pv(pricingCDS, yieldCurve, curveUp, cdsSpread, priceType);
    final double down = PRICER.pv(pricingCDS, yieldCurve, curveDown, cdsSpread, priceType);
    return up - down;
  }

  private double fdCentral(final CDSAnalytic pricingCDS, final double cdsSpread, final CDSAnalytic[] curvePoints, final double[] spreads, final double[] deltaSpreads,
      final ISDACompliantYieldCurve yieldCurve, final PriceType priceType) {
    final int n = curvePoints.length;
    final double[] spreadUp = new double[n];
    final double[] spreadDown = new double[n];
    for (int i = 0; i < n; i++) {
      spreadUp[i] = spreads[i] + deltaSpreads[i];
      spreadDown[i] = spreads[i] - deltaSpreads[i];
    }
    final ISDACompliantCreditCurve curveUp = BUILDER.calibrateCreditCurve(curvePoints, spreadUp, yieldCurve);
    final ISDACompliantCreditCurve curveDown = BUILDER.calibrateCreditCurve(curvePoints, spreadDown, yieldCurve);
    final double up = PRICER.pv(pricingCDS, yieldCurve, curveUp, cdsSpread, priceType);
    final double down = PRICER.pv(pricingCDS, yieldCurve, curveDown, cdsSpread, priceType);

    return up - down;
  }

  private double fdForward(final CDSAnalytic pricingCDS, final double cdsSpread, final CDSAnalytic[] curvePoints, final double[] spreads, final double[] deltaSpreads,
      final ISDACompliantYieldCurve yieldCurve, final PriceType priceType) {
    final int n = curvePoints.length;
    final double[] spreadUp = new double[n];
    for (int i = 0; i < n; i++) {
      spreadUp[i] = spreads[i] + deltaSpreads[i];
    }
    final ISDACompliantCreditCurve curveUp = BUILDER.calibrateCreditCurve(curvePoints, spreadUp, yieldCurve);
    final ISDACompliantCreditCurve curveMid = BUILDER.calibrateCreditCurve(curvePoints, spreads, yieldCurve);
    final double up = PRICER.pv(pricingCDS, yieldCurve, curveUp, cdsSpread, priceType);
    final double mid = PRICER.pv(pricingCDS, yieldCurve, curveMid, cdsSpread, priceType);

    return up - mid;
  }

  private double fdBackwards(final CDSAnalytic pricingCDS, final double cdsSpread, final CDSAnalytic[] curvePoints, final double[] spreads, final double[] deltaSpreads,
      final ISDACompliantYieldCurve yieldCurve, final PriceType priceType) {
    final int n = curvePoints.length;
    final double[] spreadDown = new double[n];
    for (int i = 0; i < n; i++) {
      spreadDown[i] = spreads[i] - deltaSpreads[i];
    }
    final ISDACompliantCreditCurve curveMid = BUILDER.calibrateCreditCurve(curvePoints, spreads, yieldCurve);
    final ISDACompliantCreditCurve curveDown = BUILDER.calibrateCreditCurve(curvePoints, spreadDown, yieldCurve);
    final double mid = PRICER.pv(pricingCDS, yieldCurve, curveMid, cdsSpread, priceType);
    final double down = PRICER.pv(pricingCDS, yieldCurve, curveDown, cdsSpread, priceType);

    return mid - down;
  }

  private double bumpedSpread(final double spread, final double amount, final BumpType bumpType) {
    if (bumpType == BumpType.ADDITIVE) {
      return spread + amount;
    } else if (bumpType == BumpType.MULTIPLICATIVE) {
      return spread * (1 + amount);
    } else {
      throw new IllegalArgumentException("BumpType " + bumpType + " is not supported");
    }
  }

  private double[] makeBumpedSpreads(final double[] spreads, final double amount, final BumpType bumpType) {
    final int n = spreads.length;
    final double[] res = new double[n];

    if (bumpType == BumpType.ADDITIVE) {
      for (int i = 0; i < n; i++) {
        res[i] = spreads[i] + amount;
      }
    } else if (bumpType == BumpType.MULTIPLICATIVE) {
      final double a = 1 + amount;
      for (int i = 0; i < n; i++) {
        res[i] = spreads[i] * a;
      }
    } else {
      throw new IllegalArgumentException("BumpType " + bumpType + " is not supported");
    }
    return res;
  }

  private CDSQuoteConvention bumpQuote(final CDSAnalytic cds, final CDSQuoteConvention quote, final ISDACompliantYieldCurve yieldCurve, final double eps) {
    if (quote instanceof ParSpread) {
      return new ParSpread(quote.getCoupon() + eps);
    } else if (quote instanceof QuotedSpread) {
      final QuotedSpread qSpread = (QuotedSpread) quote;
      return new QuotedSpread(qSpread.getCoupon(), qSpread.getQuotedSpread() + eps);
    } else if (quote instanceof PointsUpFront) {
      final PointsUpFront puf = (PointsUpFront) quote;
      final double bumpedQSpread = PUF_CONVERTER.pufToQuotedSpread(cds, puf.getCoupon(), yieldCurve, puf.getPointsUpFront()) + eps;
      return new PointsUpFront(puf.getCoupon(), PUF_CONVERTER.quotedSpreadToPUF(cds, puf.getCoupon(), yieldCurve, bumpedQSpread));
    } else {
      throw new IllegalArgumentException("unknow type " + quote.getClass());
    }
  }

  private CDSQuoteConvention[] bumpQuotes(final CDSAnalytic[] cds, final CDSQuoteConvention[] quotes, final ISDACompliantYieldCurve yieldCurve, final double eps) {
    final int n = cds.length;
    final CDSQuoteConvention[] res = new CDSQuoteConvention[n];
    for (int i = 0; i < n; i++) {
      res[i] = bumpQuote(cds[i], quotes[i], yieldCurve, eps);
    }
    return res;
  }

  private CDSQuoteConvention[] bumpQuoteAtIndex(final CDSAnalytic[] cds, final CDSQuoteConvention[] quotes, final ISDACompliantYieldCurve yieldCurve, final double eps, final int index) {
    final int n = cds.length;
    final CDSQuoteConvention[] res = new CDSQuoteConvention[n];
    System.arraycopy(quotes, 0, res, 0, n);
    res[index] = bumpQuote(cds[index], quotes[index], yieldCurve, eps);
    return res;
  }

  private double[] makeBumpedSpreads(final double[] spreads, final double amount, final BumpType bumpType, final int index) {
    final int n = spreads.length;
    final double[] res = new double[n];
    System.arraycopy(spreads, 0, res, 0, n);

    switch (bumpType) {
      case ADDITIVE:
        res[index] += amount;
        break;
      case MULTIPLICATIVE:
        res[index] += res[index] * amount;
        break;
      default:
        throw new IllegalArgumentException("BumpType " + bumpType + " is not supported");
    }
    return res;
  }
}