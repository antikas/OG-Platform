/**
 * Copyright (C) 2009 - 2011 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.math.integration;

import org.apache.commons.lang.Validate;

import com.opengamma.math.function.Function1D;

/**
 * Gauss-Legendre quadrature approximates the value of integrals of the form
 * {@latex.ilb %preamble{\\usepackage{amsmath}}
 * \\begin{align*}
 * \\int_{-1}^{1} f(x) dx
 * \\end{align*}
 * }
 * The weights and abscissas are generated by {@link GaussLegendreWeightAndAbscissaFunction}.
 * <p>
 * The function to integrate is scaled in such a way as to allow any values for the limits of the integrals.
 */
public class GaussLegendreQuadratureIntegrator1D extends GaussianQuadratureIntegrator1D {
  private static final Double[] LIMITS = new Double[] {-1., 1.};
  private static final GaussLegendreWeightAndAbscissaFunction GENERATOR = new GaussLegendreWeightAndAbscissaFunction();

  /**
   * @param n The number of sample points to be used in the integration, not negative or zero
   */
  public GaussLegendreQuadratureIntegrator1D(final int n) {
    super(n, GENERATOR);
  }

  @Override
  public Double[] getLimits() {
    return LIMITS;
  }

  /**
   * {@inheritDoc}
   * To evaluate an integral over {@latex.inline $[a, b]$}, a change of interval must be performed:
   * {@latex.ilb %preamble{\\usepackage{amsmath}}
   * \\begin{align*}
   * \\int_a^b f(x)dx 
   * &= \\frac{b - a}{2}\\int_{-1}^1 f(\\frac{b - a}{2} x + \\frac{a + b}{2})dx\\\\
   * &\\approx \\frac{b - a}{2}\\sum_{i=1}^n w_i f(\\frac{b - a}{2} x + \\frac{a + b}{2})
   * \\end{align*}
   * }
   */
  @Override
  public Function1D<Double, Double> getIntegralFunction(final Function1D<Double, Double> function, final Double lower, final Double upper) {
    Validate.notNull(function, "function");
    Validate.notNull(lower, "lower");
    Validate.notNull(upper, "upper");
    final double m = (upper - lower) / 2;
    final double c = (upper + lower) / 2;
    return new Function1D<Double, Double>() {

      @Override
      public Double evaluate(final Double x) {
        return m * function.evaluate(m * x + c);
      }

    };
  }

}
