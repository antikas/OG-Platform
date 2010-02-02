/**
 * Copyright (C) 2009 - 2010 by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.util.test;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.opengamma.util.ArgumentChecker;

/**
 * 
 *
 * @author pietari
 */
@RunWith(Parameterized.class)
abstract public class DBTest {
  
  private final String _databaseType;
  private final DBTool _dbtool;
  
  protected DBTest(String databaseType) {
    ArgumentChecker.checkNotNull(databaseType, "Database type");
    _databaseType = databaseType;
    
    String dbHost = TestProperties.getDbHost(_databaseType);
    String user = TestProperties.getDbUsername(_databaseType);
    String password = TestProperties.getDbPassword(_databaseType);
    
    _dbtool = new DBTool(dbHost, user, password);
  }
  
  @Parameters
  public static Collection<Object[]> getDatabaseTypes() {
    String databaseType = System.getProperty("test.database.type");
    if (databaseType == null) {
      databaseType = "derby"; // If you run from Eclipse, use Derby only
    }
    
    ArrayList<Object[]> returnValue = new ArrayList<Object[]>();
    for (String db : TestProperties.getDatabaseTypes(databaseType)) {
      returnValue.add(new Object[] { db });      
    }
    return returnValue;
  }
  
  @Before
  public void setUp() throws Exception {
    _dbtool.initialise(); // avoids locking issues with Derby
    _dbtool.clearTestTables();
  }

  public DBTool getDbTool() {
    return _dbtool;
  }

}
