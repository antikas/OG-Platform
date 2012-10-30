/**
 * Copyright (C) 2012 - present by OpenGamma Inc. and the OpenGamma group of companies
 * 
 * Please see distribution for license.
 */
package com.opengamma.maths.highlevelapi.functions.DOGMAFunctions.DOGMASpecialFunctions;

import java.util.Map;

import com.google.common.collect.Maps;
import com.opengamma.maths.commonapi.exceptions.MathsExceptionNotImplemented;
import com.opengamma.maths.highlevelapi.datatypes.primitive.OGArray;
import com.opengamma.maths.highlevelapi.datatypes.primitive.OGMatrix;
import com.opengamma.maths.highlevelapi.datatypes.primitive.OGSparseMatrix;
import com.opengamma.maths.highlevelapi.functions.DOGMAFunctions.DOGMASpecialFunctions.erfc.ErfcAbstract;
import com.opengamma.maths.highlevelapi.functions.DOGMAFunctions.DOGMASpecialFunctions.erfc.ErfcOGMatrix;
import com.opengamma.maths.highlevelapi.functions.DOGMAFunctions.DOGMASpecialFunctions.erfc.ErfcOGSparseMatrix;

/**
 * Overloaded Erfc
 */
public class Erfc {
  /**
   * hashmapped function pointers
   */
  private static Map<Class<?>, ErfcAbstract<?>> s_functionPointers = Maps.newHashMap();
  static {
    s_functionPointers.put(OGMatrix.class, ErfcOGMatrix.getInstance());
    s_functionPointers.put(OGSparseMatrix.class, ErfcOGSparseMatrix.getInstance());
  }

  @SuppressWarnings("unchecked")
  public <T extends OGArray<? extends Number>> OGArray<? extends Number> erfc(T array1) {
    ErfcAbstract<T> use = (ErfcAbstract<T>) s_functionPointers.get(array1.getClass());
    if (use == null) {
      throw new MathsExceptionNotImplemented("Erf() on array class " + array1.getClass().toString() + " is not yet implemented");
    }
    return use.erfc(array1);
  }

}
