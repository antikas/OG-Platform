/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.livedata.normalization;

import static org.junit.Assert.assertEquals;

import org.fudgemsg.FudgeContext;
import org.fudgemsg.FudgeFieldContainer;
import org.fudgemsg.MutableFudgeFieldContainer;
import org.junit.Test;

/**
 * 
 *
 * @author pietari
 */
public class StandardRulesTest {
  
  @Test
  public void noNormalization() {
    NormalizationRuleSet ruleSet = StandardRules.getNoNormalization();
    
    MutableFudgeFieldContainer msg = FudgeContext.GLOBAL_DEFAULT.newMessage();
    msg.add("Foo", "1");
    msg.add("Bar", 2.0);
    msg.add("Baz", 500);
    
    FudgeFieldContainer normalizedMsg = ruleSet.getNormalizedMessage(msg);
    assertEquals(msg, normalizedMsg);
  }
  
  @Test
  public void openGamma() {
    NormalizationRuleSet ruleSet = StandardRules.getNoNormalization();
    
    MutableFudgeFieldContainer msg = FudgeContext.GLOBAL_DEFAULT.newMessage();
    msg.add("Foo", "1");
    msg.add("Bar", 2.0);
    msg.add("Baz", 500);
    
    FudgeFieldContainer normalizedMsg = ruleSet.getNormalizedMessage(msg);
    assertEquals(msg, normalizedMsg);
  }

}
