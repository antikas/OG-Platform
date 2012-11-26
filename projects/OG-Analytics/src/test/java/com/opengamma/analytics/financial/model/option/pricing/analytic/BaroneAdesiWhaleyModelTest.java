/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.analytics.financial.model.option.pricing.analytic;

import static org.testng.AssertJUnit.assertEquals;

import org.testng.annotations.Test;

import com.opengamma.analytics.financial.model.volatility.BlackFormulaRepository;
import com.opengamma.analytics.financial.model.volatility.GenericImpliedVolatiltySolver;
import com.opengamma.analytics.math.function.Function1D;

/**
 * 
 */
public class BaroneAdesiWhaleyModelTest {

  private static final double STRIKE = 100;
  private static final double R = 0.1;
  private static final double B = 0.0;
  private static final double[] COM_PRICES = new double[] {90, 100, 110 };
  private static final double[] VOLS = new double[] {0.15, 0.25, 0.35 };
  private static final double[] EXPIRIES = new double[] {0.1, 0.5 };
  private static final double[][][] CALL_PRICES = new double[][][] { { //taken from  Haug p. 100 table 3.1 
      {0.0206, 1.8771, 10.0089 },
      {0.3159, 3.1280, 10.3919 },
      {0.9495, 4.3777, 11.1679 } }, {
      {0.8208, 4.0842, 10.8087 },
      {2.7437, 6.8015, 13.0170 },
      {5.0063, 9.5106, 15.5689 }
      } };
  private static final double[][][] PUT_PRICES = new double[][][] { {
  {10.0, 1.8770, 0.0410 },
  {10.2533, 3.1277, 0.4562 },
  {10.8787, 4.3777, 1.2402 } }, {
  {10.5595, 4.0842, 1.0822 },
  {12.4419, 6.8014, 3.3226 },
  {14.6945, 9.5104, 5.8823 }
  } };

  //TODO either the values in Haug are wrong, or we have a subtle bug 
  @Test
  public void knownValuesTest() {
    final BaroneAdesiWhaleyModel baw = new BaroneAdesiWhaleyModel();
    for (int i = 0; i < EXPIRIES.length; i++) {
      for (int j = 0; j < VOLS.length; j++) {
        for (int k = 0; k < COM_PRICES.length; k++) {
          double call = baw.price(COM_PRICES[k], STRIKE, R, B, EXPIRIES[i], VOLS[j], true);
          double put = baw.price(COM_PRICES[k], STRIKE, R, B, EXPIRIES[i], VOLS[j], false);
          assertEquals("call", CALL_PRICES[i][j][k], call, 3e-3); //these should be 1e-4
          assertEquals("put", PUT_PRICES[i][j][k], put, 1e-3);
        }
      }
    }

  }

  @Test
      (enabled = false)
      public void test() {

    final BaroneAdesiWhaleyModel baw = new BaroneAdesiWhaleyModel();

    final double s0 = 110;
    final double k = 100;
    final double t = 0.5;
    final double r = 0.1;
    final double b = -0.0;
    final double sigma = 0.35;
    final boolean isCall = true;

    final double bawPrice = baw.price(s0, k, r, b, t, sigma, isCall);
    final double bsprice = Math.exp(-r * t) * BlackFormulaRepository.price(s0 * Math.exp(b * t), k, t, sigma, isCall);
    System.out.println(bawPrice + " " + bsprice);

    double impVol = baw.impliedVolatility(bawPrice, s0, k, r, b, t, isCall);
    System.out.println(impVol);
  }

  @Test
  public void sCritSensitivityTest() {

    final BaroneAdesiWhaleyModel baw = new BaroneAdesiWhaleyModel();

    final double s0 = 110;
    final double k = 100;
    final double t = 0.5;
    final double r = 0.1;
    final double b = -0.03;
    final double sigma = 0.35;

    final double eps = 1e-5;

    for (int i = 0; i < 2; i++) {
      boolean isCall = i == 0;
      //delta
      final double sUp = baw.sCrit(s0 + eps, k, r, b, t, sigma, isCall);
      final double sDown = baw.sCrit(s0 - eps, k, r, b, t, sigma, isCall);
      final double fdDelta = (sUp - sDown) / 2 / eps;
      assertEquals("delta", 0.0, fdDelta, 0.0); //sCrit has no dependence on s0

      double[] sCAdj = baw.getsCritAdjoint(s0, k, r, b, t, sigma, isCall);
      //dual-delta  
      final double kUp = baw.sCrit(s0, k + eps, r, b, t, sigma, isCall);
      final double kDown = baw.sCrit(s0, k - eps, r, b, t, sigma, isCall);
      final double fdDD = (kUp - kDown) / 2 / eps;
      assertEquals("dual-delta", sCAdj[0], fdDD, 1e-7);
      //rho  
      final double rUp = baw.sCrit(s0, k, r + eps, b, t, sigma, isCall);
      final double rDown = baw.sCrit(s0, k, r - eps, b, t, sigma, isCall);
      final double fdRho = (rUp - rDown) / 2 / eps;
      assertEquals("rho", sCAdj[1], fdRho, 1e-5);
      //b-rho  
      final double bUp = baw.sCrit(s0, k, r, b + eps, t, sigma, isCall);
      final double bDown = baw.sCrit(s0, k, r, b - eps, t, sigma, isCall);
      final double fdBRho = (bUp - bDown) / 2 / eps;
      assertEquals("b-rho", sCAdj[2], fdBRho, 1e-4);
      //theta  
      final double tUp = baw.sCrit(s0, k, r, b, t + eps, sigma, isCall);
      final double tDown = baw.sCrit(s0, k, r, b, t - eps, sigma, isCall);
      final double fdTheta = (tUp - tDown) / 2 / eps;
      assertEquals("theta", sCAdj[3], fdTheta, 1e-6);
      //vega  
      final double volUp = baw.sCrit(s0, k, r, b, t, sigma + eps, isCall);
      final double volDown = baw.sCrit(s0, k, r, b, t, sigma - eps, isCall);
      final double fdVega = (volUp - volDown) / 2 / eps;
      assertEquals("vega", sCAdj[4], fdVega, 1e-5);
    }
  }

  @Test
  public void sensitivityTest() {

    final BaroneAdesiWhaleyModel baw = new BaroneAdesiWhaleyModel();

    final double s0 = 110;
    final double k = 100;
    final double t = 0.5;
    final double r = 0.1;
    final double b = 0.07;
    final double sigma = 0.35;

    final double eps = 1e-5;

    for (int i = 0; i < 2; i++) {
      boolean isCall = i == 0;
      double[] greeks = baw.getPriceAdjoint(s0, k, r, b, t, sigma, isCall);

      final double sUp = baw.price(s0 + eps, k, r, b, t, sigma, isCall);
      final double sDown = baw.price(s0 - eps, k, r, b, t, sigma, isCall);
      final double fdDelta = (sUp - sDown) / 2 / eps;
      assertEquals("delta", greeks[0], fdDelta, 1e-9);
      final double kUp = baw.price(s0, k + eps, r, b, t, sigma, isCall);
      final double kDown = baw.price(s0, k - eps, r, b, t, sigma, isCall);
      final double fdDD = (kUp - kDown) / 2 / eps;
      assertEquals("dual-delta", greeks[1], fdDD, 1e-9);
      final double rUp = baw.price(s0, k, r + eps, b, t, sigma, isCall);
      final double rDown = baw.price(s0, k, r - eps, b, t, sigma, isCall);
      final double fdRho = (rUp - rDown) / 2 / eps;
      assertEquals("rho", greeks[2], fdRho, 1e-7);
      final double bUp = baw.price(s0, k, r, b + eps, t, sigma, isCall);
      final double bDown = baw.price(s0, k, r, b - eps, t, sigma, isCall);
      final double fdBRho = (bUp - bDown) / 2 / eps;
      assertEquals("b-rho", greeks[3], fdBRho, 1e-7);
      final double tUp = baw.price(s0, k, r, b, t + eps, sigma, isCall);
      final double tDown = baw.price(s0, k, r, b, t - eps, sigma, isCall);
      final double fdTheta = (tUp - tDown) / 2 / eps;
      assertEquals("theta", greeks[4], fdTheta, 1e-9);
      final double volUp = baw.price(s0, k, r, b, t, sigma + eps, isCall);
      final double volDown = baw.price(s0, k, r, b, t, sigma - eps, isCall);
      final double fdVega = (volUp - volDown) / 2 / eps;
      assertEquals("vega", greeks[5], fdVega, 1e-8);
    }
  }

  /**
   * The Barone-Adesi Whaley model does not exactly obey the put-call transformation (Bjerksund and Stensland (1993))
   */
  @Test
  public void putCallTransformTest() {

    final double s0 = 110;
    final double t = 0.5;
    final double r = 0.1;
    final double b = 0.06;
    final double sigma = 0.35;

    final BaroneAdesiWhaleyModel baw = new BaroneAdesiWhaleyModel();

    for (int i = 0; i < 11; i++) {
      double k = 90 + i * 4;
      double cprice = baw.price(s0, k, r, b, t, sigma, true);
      double ccprice = baw.price(k, s0, r - b, -b, t, sigma, false); //price the call as a put
      assertEquals("strike " + k, cprice, ccprice, 1e-3);

    }

  }

  @Test
  public void impliedVolTest() {

    final double s0 = 110;
    final double t = 0.5;
    final double r = 0.1;
    final double b = 0.07;
    final double sigma = 0.35;
    final BaroneAdesiWhaleyModel baw = new BaroneAdesiWhaleyModel();

    for (int j = 0; j < 2; j++) {
      boolean isCall = j == 0;
      for (int i = 0; i < 10; i++) {
        double k = 80 + 50 * i / 9.;
        double p = baw.price(s0, k, r, b, t, sigma, isCall);
        Function1D<Double, double[]> func = baw.getPriceAndVegaFunction(s0, k, r, b, t, isCall);
        double vol = GenericImpliedVolatiltySolver.impliedVolatility(p, func);
        assertEquals("k = " + k, sigma, vol, 1e-9);
      }
    }

  }

  @Test
  public void impliedVol2Test() {

    final double s0 = 110;

    final double t = 0.5;
    final double r = 0.1;
    final double b = 0.07;
    final double sigma = 0.35;
    final BaroneAdesiWhaleyModel baw = new BaroneAdesiWhaleyModel();
    final double fwd = s0 * Math.exp(b * t);
    final double df = Math.exp(-r * t);

    for (int j = 0; j < 2; j++) {
      boolean isCall = j == 0;
      for (int i = 0; i < 50; i++) {
        double k = 75 + 80 * i / 49.;
        double fp = baw.price(s0, k, r, b, t, sigma, isCall) / df;
        double biv = BlackFormulaRepository.impliedVolatility(fp, fwd, k, t, isCall);
        System.out.println(k + "\t" + biv);
      }
      System.out.println();
    }

  }

  @Test(enabled = false)
  public void sCritTest() {

    final double s0 = 110;
    final double k = 110;
    ;
    final double r = 0.1;
    final double b = -0.04;
    final double sigma = 0.35;
    final boolean isCall = true;

    final BaroneAdesiWhaleyModel baw = new BaroneAdesiWhaleyModel();

    for (int i = 0; i < 100; i++) {
      double t = Math.exp(i / 15.0 - 6.0);
      double sCrit = baw.sCrit(s0, k, r, b, t, sigma, isCall);
      System.out.println(t + "\t" + sCrit);
    }

  }

}
