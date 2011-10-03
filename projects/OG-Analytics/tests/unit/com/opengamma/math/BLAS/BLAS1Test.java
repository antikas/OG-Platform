/**
 * Copyright (C) 2011 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.math.BLAS;

import static org.testng.AssertJUnit.assertTrue;

import java.util.Arrays;

import org.testng.annotations.Test;

import com.opengamma.math.matrix.DoubleMatrix1D;


/**
 * Tests the BLAS1 library
 */
public class BLAS1Test {
  // tests 1element short vector
  double[] x1 = {1};
  double[] y1 = {10};
  double[] x1_plus_y1={11};
  double[] alpha_times_x1_plus_y1={17};

  // hits loop unwind max
  double[] x16 = range(1,16);
  double[] y16 = range(10,160,10);
  double[] x16_plus_y16 = {11,22,33,44,55,66,77,88,99,110,121,132,143,154,165,176};
  double[] alpha_times_x16_plus_y16 = {17,34,51,68,85,102,119,136,153,170,187,204,221,238,255,272};

  // trips loop unwinds to hit clean up code
  double[] x37 = range(1,37);
  double[] y37 = range(10,370,10);
  double[] x37_plus_y37 = {11,22,33,44,55,66,77,88,99,110,121,132,143,154,165,176,187,198,209,220,231,242,253,264,275,286,297,308,319,330,341,352,363,374,385,396,407};
  double[] alpha_times_x37_plus_y37 = {17,34,51,68,85,102,119,136,153,170,187,204,221,238,255,272,289,306,323,340,357,374,391,408,425,442,459,476,493,510,527,544,561,578,595,612,629};

  // scalar
  double alpha = 7;

  BLAS1 blas1 = new BLAS1();

/** test the sanity checker */
@Test(expectedExceptions = AssertionError.class)
public void testDAXPYsanityCheckerArg1() {
  double[] tmp1 = null;
  double[] tmp2 = {1};
  blas1.daxpy(tmp1, tmp2);
}

@Test(expectedExceptions = AssertionError.class)
public void testDAXPYsanityCheckerArg2() {
  double[] tmp1 = {1};
  double[] tmp2 = null;
  blas1.daxpy(tmp1, tmp2);
}

@Test(expectedExceptions = AssertionError.class)
public void testDAXPYsanityCheckerLengths() {
  double[] tmp1 = {1,2};
  double[] tmp2 = {3,4,5};
  blas1.daxpy(tmp1, tmp2);
}

/** Stateless y:=x+y */
// Test DAXPY double[] double[] interface

@Test
public void testDAXPY_ans_eq_x1_plus_y1() {
  double[] tmp = blas1.daxpy(x1, y1);
  assertTrue(Arrays.equals(x1_plus_y1, tmp));
}

@Test
public void testDAXPY_ans_eq_x16_plus_y16() {
  double[] tmp = blas1.daxpy(x16, y16);
  assertTrue(Arrays.equals(x16_plus_y16, tmp));
}

@Test
public void testDAXPY_ans_eq_x37_plus_y37() {
  double[] tmp = blas1.daxpy(x37, y37);
  assertTrue(Arrays.equals(x37_plus_y37, tmp));
}

//Test DAXPY double[] DoubleMatrix1D interface
@Test
public void testDAXPY_ans_eq_x1_plus_D1D_y1() {
  double[] tmp = blas1.daxpy(x1, new DoubleMatrix1D(y1));
  assertTrue(Arrays.equals(x1_plus_y1, tmp));
}

@Test
public void testDAXPY_ans_eq_x16_plus_D1D_y16() {
  double[] tmp = blas1.daxpy(x16, new DoubleMatrix1D(y16));
  assertTrue(Arrays.equals(x16_plus_y16, tmp));
}

@Test
public void testDAXPY_ans_eq_x37_plus_D1D_y37() {
  double[] tmp = blas1.daxpy(x37, new DoubleMatrix1D(y37));
  assertTrue(Arrays.equals(x37_plus_y37, tmp));
}


//Test DAXPY DoubleMatrix1D double[] interface
@Test
public void testDAXPY_ans_eq_D1D_x1_plus_y1() {
  double[] tmp = blas1.daxpy(new DoubleMatrix1D(x1), y1);
  assertTrue(Arrays.equals(x1_plus_y1, tmp));
}

@Test
public void testDAXPY_ans_eq_D1D_x16_plus_y16() {
  double[] tmp = blas1.daxpy(new DoubleMatrix1D(x16), y16);
  assertTrue(Arrays.equals(x16_plus_y16, tmp));
}

@Test
public void testDAXPY_ans_eq_D1D_x37_plus_y37() {
  double[] tmp = blas1.daxpy(new DoubleMatrix1D(x37), y37);
  assertTrue(Arrays.equals(x37_plus_y37, tmp));
}

//Test DAXPY DoubleMatrix1D DoubleMatrix1D interface
@Test
public void testDAXPY_ans_eq_D1D_x1_plus_D1D_y1() {
  double[] tmp = blas1.daxpy(new DoubleMatrix1D(x1), new DoubleMatrix1D(y1));
  assertTrue(Arrays.equals(x1_plus_y1, tmp));
}

@Test
public void testDAXPY_ans_eq_D1D_x16_plus_D1D_y16() {
  double[] tmp = blas1.daxpy(new DoubleMatrix1D(x16), new DoubleMatrix1D(y16));
  assertTrue(Arrays.equals(x16_plus_y16, tmp));
}

@Test
public void testDAXPY_ans_eq_D1D_x37_plus_D1D_y37() {
  double[] tmp = blas1.daxpy(new DoubleMatrix1D(x37), new DoubleMatrix1D(y37));
  assertTrue(Arrays.equals(x37_plus_y37, tmp));
}


/** y:=alpha*x + y */
//Test DAXPY double[] double[] interface
@Test
public void testDAXPY_ans_eq_alpha_x1_plus_y1() {
  double[] tmp = blas1.daxpy(alpha, x1, y1);
  assertTrue(Arrays.equals(alpha_times_x1_plus_y1, tmp));
}

@Test
public void testDAXPY_ans_eq_alpha_x16_plus_y16() {
  double[] tmp = blas1.daxpy(alpha, x16, y16);
  assertTrue(Arrays.equals(alpha_times_x16_plus_y16, tmp));
}

@Test
public void testDAXPY_ans_eq_alpha_x37_plus_y37() {
  double[] tmp = blas1.daxpy(alpha, x37, y37);
  assertTrue(Arrays.equals(alpha_times_x37_plus_y37, tmp));
}

//Test DAXPY double[] DoubleMatrix1D interface
@Test
public void testDAXPY_ans_eq_alpha_x1_plus_D1D_y1() {
  double[] tmp = blas1.daxpy(alpha, x1, new DoubleMatrix1D(y1));
  assertTrue(Arrays.equals(alpha_times_x1_plus_y1, tmp));
}

@Test
public void testDAXPY_ans_eq_alpha_x16_plus_D1D_y16() {
  double[] tmp = blas1.daxpy(alpha, x16, new DoubleMatrix1D(y16));
  assertTrue(Arrays.equals(alpha_times_x16_plus_y16, tmp));
}

@Test
public void testDAXPY_ans_eq_alpha_x37_plus_D1D_y37() {
  double[] tmp = blas1.daxpy(alpha, x37, new DoubleMatrix1D(y37));
  assertTrue(Arrays.equals(alpha_times_x37_plus_y37, tmp));
}


//Test DAXPY DoubleMatrix1D double[] interface
@Test
public void testDAXPY_ans_eq_alpha_D1D_x1_plus_y1() {
  double[] tmp = blas1.daxpy(alpha, new DoubleMatrix1D(x1), y1);
  assertTrue(Arrays.equals(alpha_times_x1_plus_y1, tmp));
}

@Test
public void testDAXPY_ans_eq_alpha_D1D_x16_plus_y16() {
  double[] tmp = blas1.daxpy(alpha, new DoubleMatrix1D(x16), y16);
  assertTrue(Arrays.equals(alpha_times_x16_plus_y16, tmp));
}

@Test
public void testDAXPY_ans_eq_alpha_D1D_x37_plus_y37() {
  double[] tmp = blas1.daxpy(alpha, new DoubleMatrix1D(x37), y37);
  assertTrue(Arrays.equals(alpha_times_x37_plus_y37, tmp));
}

//Test DAXPY DoubleMatrix1D DoubleMatrix1D interface
@Test
public void testDAXPY_ans_eq_alpha_D1D_x1_plus_D1D_y1() {
  double[] tmp = blas1.daxpy(alpha, new DoubleMatrix1D(x1), new DoubleMatrix1D(y1));
  assertTrue(Arrays.equals(alpha_times_x1_plus_y1, tmp));
}

@Test
public void testDAXPY_ans_eq_alpha_D1D_x16_plus_D1D_y16() {
  double[] tmp = blas1.daxpy(alpha, new DoubleMatrix1D(x16), new DoubleMatrix1D(y16));
  assertTrue(Arrays.equals(alpha_times_x16_plus_y16, tmp));
}

@Test
public void testDAXPY_ans_eq_alpha_D1D_x37_plus_D1D_y37() {
  double[] tmp = blas1.daxpy(alpha, new DoubleMatrix1D(x37), new DoubleMatrix1D(y37));
  assertTrue(Arrays.equals(alpha_times_x37_plus_y37, tmp));
}

//test fall through if(alpha==0)
@Test
public void testDAXPY_ans_eq_alphaZERO_x1_plus_y1() {
  double[] tmp = blas1.daxpy(0, x1, y1);
  assertTrue(Arrays.equals(y1, tmp));
}

@Test
public void testDAXPY_ans_eq_alphaZERO_x16_plus_y16() {
  double[] tmp = blas1.daxpy(0, x16, y16);
  assertTrue(Arrays.equals(y16, tmp));
}

@Test
public void testDAXPY_ans_eq_alphaZERO_x37_plus_y37() {
  double[] tmp = blas1.daxpy(0, x37, y37);
  assertTrue(Arrays.equals(y37, tmp));
}

/** Test the in places */
/** Statefull: y:=x+y */
//Test DAXPY double[] double[] interface
@Test
public void testDAXPY_y1_eq_x1_plus_y1() {
double[] tmp = new double[y1.length];
System.arraycopy(y1, 0, tmp, 0, y1.length);
blas1.daxpyInplace(x1, tmp);
assertTrue(Arrays.equals(x1_plus_y1, tmp));
}

@Test
public void testDAXPY_y16_eq_x16_plus_y16() {
double[] tmp = new double[y16.length];
System.arraycopy(y16, 0, tmp, 0, y16.length);
blas1.daxpyInplace(x16, tmp);
assertTrue(Arrays.equals(x16_plus_y16, tmp));
}

@Test
public void testDAXPY_y37_eq_x37_plus_y37() {
double[] tmp = new double[y37.length];
System.arraycopy(y37, 0, tmp, 0, y37.length);
blas1.daxpyInplace(x37, tmp);
assertTrue(Arrays.equals(x37_plus_y37, tmp));
}

//Test DAXPY DoubleMatrix1D double[] interface
@Test
public void testDAXPY_D1D_y1_eq_x1_plus_D1D_y1() {
DoubleMatrix1D tmp = new DoubleMatrix1D(y1);
blas1.daxpyInplace(x1, tmp);
assertTrue(Arrays.equals(x1_plus_y1, tmp.getData()));
}

@Test
public void testDAXPY_D1D_y16_eq_x16_plus_D1D_y16() {
DoubleMatrix1D tmp = new DoubleMatrix1D(y16);
blas1.daxpyInplace(x16, tmp);
assertTrue(Arrays.equals(x16_plus_y16, tmp.getData()));
}

@Test
public void testDAXPY_D1D_y37_eq_x37_plus_D1D_y37() {
DoubleMatrix1D tmp = new DoubleMatrix1D(y37);
blas1.daxpyInplace(x37, tmp);
assertTrue(Arrays.equals(x37_plus_y37, tmp.getData()));
}

//Test DAXPY double[] DoubleMatrix1D interface
@Test
public void testDAXPY_y1_eq_D1D_x1_plus_y1() {
double[] tmp = new double[y1.length];
System.arraycopy(y1, 0, tmp, 0, y1.length);
blas1.daxpyInplace(new DoubleMatrix1D(x1), tmp);
assertTrue(Arrays.equals(x1_plus_y1, tmp));
}

@Test
public void testDAXPY_y16_eq_D1D_x16_plus_y16() {
double[] tmp = new double[y16.length];
System.arraycopy(y16, 0, tmp, 0, y16.length);
blas1.daxpyInplace(new DoubleMatrix1D(x16), tmp);
assertTrue(Arrays.equals(x16_plus_y16, tmp));
}

@Test
public void testDAXPY_y37_eq_D1D_x37_plus_y37() {
double[] tmp = new double[y37.length];
System.arraycopy(y37, 0, tmp, 0, y37.length);
blas1.daxpyInplace(new DoubleMatrix1D(x37), tmp);
assertTrue(Arrays.equals(x37_plus_y37, tmp));
}


//Test DAXPY DoubleMatrix1D DoubleMatrix1D interface
@Test
public void testDAXPY_D1D_y1_eq_D1D_x1_plus_D1D_y1() {
DoubleMatrix1D tmp = new DoubleMatrix1D(y1);
DoubleMatrix1D tmp2 = new DoubleMatrix1D(x1);
blas1.daxpyInplace(tmp2, tmp);
assertTrue(Arrays.equals(x1_plus_y1, tmp.getData()));
}

@Test
public void testDAXPY_D1D_y16_eq_D1D_x16_plus_D1D_y16() {
DoubleMatrix1D tmp = new DoubleMatrix1D(y16);
DoubleMatrix1D tmp2 = new DoubleMatrix1D(x16);
blas1.daxpyInplace(tmp2, tmp);
assertTrue(Arrays.equals(x16_plus_y16, tmp.getData()));
}

@Test
public void testDAXPY_D1D_y37_eq_D1D_x37_plus_D1D_y37() {
DoubleMatrix1D tmp = new DoubleMatrix1D(y37);
DoubleMatrix1D tmp2 = new DoubleMatrix1D(x37);
blas1.daxpyInplace(tmp2, tmp);
assertTrue(Arrays.equals(x37_plus_y37, tmp.getData()));
}


/** Statefull y:=alpha*x+y */
//Test DAXPY double[] double[] interface
@Test
public void testDAXPY_y1_eq_alphaZERO_times_x1_plus_y1() {
  double[] tmp = new double[y1.length];
  System.arraycopy(y1, 0, tmp, 0, y1.length);
  blas1.daxpyInplace(0, x1, tmp);
  assertTrue(Arrays.equals(y1, tmp));
}

@Test
public void testDAXPY_y16_eq_alphaZERO_times_x16_plus_y16() {
  double[] tmp = new double[y16.length];
  System.arraycopy(y16, 0, tmp, 0, y16.length);
  blas1.daxpyInplace(0, x16, tmp);
  assertTrue(Arrays.equals(y16, tmp));
}

@Test
public void testDAXPY_y37_eq_alphaZERO_times_x37_plus_y37() {
  double[] tmp = new double[y37.length];
  System.arraycopy(y37, 0, tmp, 0, y37.length);
  blas1.daxpyInplace(0, x37, tmp);
  assertTrue(Arrays.equals(y37, tmp));
}

//Test DAXPY DoubleMatrix1D double[] interface
@Test
public void testDAXPY_D1D_y1_eq_alpha_times_x1_plus_D1D_y1() {
  DoubleMatrix1D tmp = new DoubleMatrix1D(y1);
  blas1.daxpyInplace(alpha, x1, tmp);
  assertTrue(Arrays.equals(alpha_times_x1_plus_y1, tmp.getData()));
}

@Test
public void testDAXPY_D1D_y16_eq_alpha_times_x16_plus_D1D_y16() {
  DoubleMatrix1D tmp = new DoubleMatrix1D(y16);
  blas1.daxpyInplace(alpha, x16, tmp);
  assertTrue(Arrays.equals(alpha_times_x16_plus_y16, tmp.getData()));
}

@Test
public void testDAXPY_D1D_y37_eq_alpha_times_x37_plus_D1D_y37() {
  DoubleMatrix1D tmp = new DoubleMatrix1D(y37);
  blas1.daxpyInplace(alpha, x37, tmp);
  assertTrue(Arrays.equals(alpha_times_x37_plus_y37, tmp.getData()));
}

//Test DAXPY double[] DoubleMatrix1D interface
@Test
public void testDAXPY_y1_eq_alpha_times_D1D_x1_plus_y1() {
  double[] tmp = new double[y1.length];
  System.arraycopy(y1, 0, tmp, 0, y1.length);
  blas1.daxpyInplace(alpha, new DoubleMatrix1D(x1), tmp);
  assertTrue(Arrays.equals(alpha_times_x1_plus_y1, tmp));
}

@Test
public void testDAXPY_y16_eq_alpha_times_D1D_x16_plus_y16() {
  double[] tmp = new double[y16.length];
  System.arraycopy(y16, 0, tmp, 0, y16.length);
  blas1.daxpyInplace(alpha, new DoubleMatrix1D(x16), tmp);
  assertTrue(Arrays.equals(alpha_times_x16_plus_y16, tmp));
}

@Test
public void testDAXPY_y37_eq_alpha_times_D1D_x37_plus_y37() {
  double[] tmp = new double[y37.length];
  System.arraycopy(y37, 0, tmp, 0, y37.length);
  blas1.daxpyInplace(alpha, new DoubleMatrix1D(x37), tmp);
  assertTrue(Arrays.equals(alpha_times_x37_plus_y37, tmp));
}


//Test DAXPY DoubleMatrix1D DoubleMatrix1D interface
@Test
public void testDAXPY_D1D_y1_eq_alpha_times_D1D_x1_plus_D1D_y1() {
  DoubleMatrix1D tmp = new DoubleMatrix1D(y1);
  DoubleMatrix1D tmp2 = new DoubleMatrix1D(x1);
  blas1.daxpyInplace(alpha, tmp2, tmp);
  assertTrue(Arrays.equals(alpha_times_x1_plus_y1, tmp.getData()));
}

@Test
public void testDAXPY_D1D_y16_eq_alpha_times_D1D_x16_plus_D1D_y16() {
  DoubleMatrix1D tmp = new DoubleMatrix1D(y16);
  DoubleMatrix1D tmp2 = new DoubleMatrix1D(x16);
  blas1.daxpyInplace(alpha, tmp2, tmp);
  assertTrue(Arrays.equals(alpha_times_x16_plus_y16, tmp.getData()));
}

@Test
public void testDAXPY_D1D_y37_eq_alpha_times_D1D_x37_plus_D1D_y37() {
  DoubleMatrix1D tmp = new DoubleMatrix1D(y37);
  DoubleMatrix1D tmp2 = new DoubleMatrix1D(x37);
  blas1.daxpyInplace(alpha, tmp2, tmp);
  assertTrue(Arrays.equals(alpha_times_x37_plus_y37, tmp.getData()));
}

//test fall through if(alpha==0)
@Test
public void testDAXPY_y1_eq_alpha_times_x1_plus_y1() {
  double[] tmp = new double[y1.length];
  System.arraycopy(y1, 0, tmp, 0, y1.length);
  blas1.daxpyInplace(alpha, x1, tmp);
  assertTrue(Arrays.equals(alpha_times_x1_plus_y1, tmp));
}

@Test
public void testDAXPY_y16_eq_alpha_times_x16_plus_y16() {
  double[] tmp = new double[y16.length];
  System.arraycopy(y16, 0, tmp, 0, y16.length);
  blas1.daxpyInplace(alpha, x16, tmp);
  assertTrue(Arrays.equals(alpha_times_x16_plus_y16, tmp));
}

@Test
public void testDAXPY_y37_eq_alpha_times_x37_plus_y37() {
  double[] tmp = new double[y37.length];
  System.arraycopy(y37, 0, tmp, 0, y37.length);
  blas1.daxpyInplace(alpha, x37, tmp);
  assertTrue(Arrays.equals(alpha_times_x37_plus_y37, tmp));
}









/** helper functions to generate number ranges */
  private double[] range(int low, int high) {
    assert(high>=low);
    final int lim = (high - low) > 0 ? (high - low) + 1 : 1;
    double [] tmp = new double[lim];
    for ( int i = 0; i < lim; i++) {
      tmp[i] = low + i;
    }
    return tmp;
  }

  private double[] range(int low, int high, int step) {
    assert(high>=low);
    assert(step>0);
    final int t = (high - low) > 0 ? (high - low)  + step : 1;
    final int lim = t/step;
    double [] tmp = new double[lim];
    for ( int i = 0; i < lim; i++) {
      tmp[i] = low + i*step;
    }
    return tmp;
  }


}
