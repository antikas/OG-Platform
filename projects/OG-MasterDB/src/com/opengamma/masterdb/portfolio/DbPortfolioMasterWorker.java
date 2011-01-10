/**
 * Copyright (C) 2009 - Present by OpenGamma Inc.
 *
 * Please see distribution for license.
 */
package com.opengamma.masterdb.portfolio;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.time.TimeSource;

import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import com.opengamma.id.UniqueIdentifier;
import com.opengamma.master.portfolio.ManageablePortfolioNode;
import com.opengamma.master.portfolio.PortfolioDocument;
import com.opengamma.master.portfolio.PortfolioHistoryRequest;
import com.opengamma.master.portfolio.PortfolioHistoryResult;
import com.opengamma.master.portfolio.PortfolioMaster;
import com.opengamma.master.portfolio.PortfolioSearchRequest;
import com.opengamma.master.portfolio.PortfolioSearchResult;
import com.opengamma.util.db.DbHelper;

/**
 * Base worker class for the portfolio master.
 * <p>
 * This is designed to allow each portfolio master method to be implemented by a
 * different class and easily replaced by an alternative.
 * <p>
 * The API of this class follows {@link PortfolioMaster}.
 * Each of the methods should be implemented as per the documentation on the master.
 * The parameters to the methods will be pre-checked for nulls before the worker is called,
 * including any internal required values in request or document objects.
 * <p>
 * This base implementation throws {@code UnsupportedOperationException} from each method.
 * As a result, subclasses only need to implement those methods they want to.
 */
public class DbPortfolioMasterWorker {

  /**
   * The main master.
   */
  private DbPortfolioMaster _master;

  /**
   * Creates an instance.
   */
  protected DbPortfolioMasterWorker() {
  }

  /**
   * Initializes the instance.
   * @param master  the portfolio master, not null
   */
  protected void init(final DbPortfolioMaster master) {
    _master = master;
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the parent master.
   * @return the parent, not null
   */
  protected DbPortfolioMaster getMaster() {
    return _master;
  }

  /**
   * Gets the database template.
   * @return the database template, not null if correctly initialized
   */
  protected SimpleJdbcTemplate getJdbcTemplate() {
    return _master.getDbSource().getJdbcTemplate();
  }

  /**
   * Gets the transaction template.
   * @return the transaction template, not null if correctly initialized
   */
  protected TransactionTemplate getTransactionTemplate() {
    return _master.getDbSource().getTransactionTemplate();
  }

  /**
   * Gets the database helper.
   * @return the helper, not null if correctly initialized
   */
  protected DbHelper getDbHelper() {
    return _master.getDbSource().getDialect();
  }

  /**
   * Gets the time-source that determines the current time.
   * @return the time-source, not null
   */
  protected TimeSource getTimeSource() {
    return _master.getTimeSource();
  }

  /**
   * Gets the scheme in use for UniqueIdentifier.
   * @return the scheme, not null
   */
  protected String getIdentifierScheme() {
    return _master.getIdentifierScheme();
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the next database id.
   * @return the next database id
   */
  protected long nextId() {
    return getJdbcTemplate().queryForLong(getDbHelper().sqlNextSequenceValueSelect("prt_master_seq"));
  }
  
  /**
   * Gets the next database id.
   * @param sequenceName  the name of the sequence to query, not null
   * @return the next database id
   */
  protected long nextId(String sequenceName) {
    return getJdbcTemplate().queryForLong(getDbHelper().sqlNextSequenceValueSelect(sequenceName));
  }

  /**
   * Extracts the row id.
   * @param id  the identifier to extract from, not null
   * @return the extracted row id
   */
  protected long extractRowId(final UniqueIdentifier id) {
    try {
      return Long.parseLong(id.getValue()) + Long.parseLong(id.getVersion());
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException("UniqueIdentifier is not from this portfolio master: " + id, ex);
    }
  }

  /**
   * Extracts the oid.
   * @param id  the identifier to extract from, not null
   * @return the extracted oid
   */
  protected long extractOid(final UniqueIdentifier id) {
    try {
      return Long.parseLong(id.getValue());
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException("UniqueIdentifier is not from this portfolio master: " + id, ex);
    }
  }

  //-------------------------------------------------------------------------
  /**
   * Creates a unique identifier.
   * @param oid  the portfolio object identifier
   * @param rowId  the node unique row identifier, null if object identifier
   * @return the unique identifier, not null
   */
  protected UniqueIdentifier createUniqueIdentifier(final long oid, final long rowId) {
    return UniqueIdentifier.of(getIdentifierScheme(), Long.toString(oid), Long.toString(rowId - oid));
  }

  //-------------------------------------------------------------------------
  /**
   * Extracts a BigDecimal handling DB annoyances.
   * @param rs  the result set, not null
   * @param columnName  the column name, not null
   * @return the extracted value, may be null
   * @throws SQLException 
   */
  protected BigDecimal extractBigDecimal(final ResultSet rs, final String columnName) throws SQLException {
    BigDecimal value = rs.getBigDecimal(columnName);
    if (value == null) {
      return null;
    }
    BigDecimal stripped = value.stripTrailingZeros();  // Derby, and maybe others, add trailing zeroes
    if (stripped.scale() < 0) {
      return stripped.setScale(0);
    }
    return stripped;
  }

  //-------------------------------------------------------------------------
  protected PortfolioSearchResult search(PortfolioSearchRequest request) {
    throw new UnsupportedOperationException();
  }

  protected PortfolioDocument get(UniqueIdentifier uid) {
    throw new UnsupportedOperationException();
  }

  protected PortfolioDocument add(PortfolioDocument document) {
    throw new UnsupportedOperationException();
  }

  protected PortfolioDocument update(PortfolioDocument document) {
    throw new UnsupportedOperationException();
  }

  protected void remove(UniqueIdentifier uid) {
    throw new UnsupportedOperationException();
  }

  protected PortfolioHistoryResult history(PortfolioHistoryRequest request) {
    throw new UnsupportedOperationException();
  }

  protected PortfolioDocument correct(PortfolioDocument document) {
    throw new UnsupportedOperationException();
  }

  protected ManageablePortfolioNode getNode(UniqueIdentifier uid) {
    throw new UnsupportedOperationException();
  }

  //-------------------------------------------------------------------------
  /**
   * Returns a string summary of this worker.
   * @return the string summary, not null
   */
  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + getIdentifierScheme() + "]";
  }

}
