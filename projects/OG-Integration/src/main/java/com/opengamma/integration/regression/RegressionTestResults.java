/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.integration.regression;

import java.util.ArrayList;
import java.util.Collection;

import org.joda.beans.BeanDefinition;
import org.joda.beans.DerivedProperty;
import org.joda.beans.ImmutableBean;
import org.joda.beans.ImmutableConstructor;
import org.joda.beans.PropertyDefinition;

import com.google.common.collect.ImmutableList;
import com.opengamma.util.ArgumentChecker;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import org.joda.beans.Bean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.impl.BasicImmutableBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

/**
 *
 */
@BeanDefinition
public final class RegressionTestResults implements ImmutableBean {

  @PropertyDefinition(validate = "notNull")
  private final String _baseVersion;

  @PropertyDefinition(validate = "notNull")
  private final String _testVersion;

  @PropertyDefinition(validate = "notNull")
  private final List<CalculationDifference> _differences;

  private final TestStatus _status;

  @ImmutableConstructor
  public RegressionTestResults(String baseVersion, String testVersion, Collection<CalculationDifference> differences) {
    ArgumentChecker.notEmpty(baseVersion, "baseVersion");
    ArgumentChecker.notEmpty(testVersion, "testVersion");
    ArgumentChecker.notEmpty(differences, "results");
    _baseVersion = baseVersion;
    _testVersion = testVersion;
    _differences = ImmutableList.copyOf(differences);
    TestStatus status = TestStatus.PASS;
    for (CalculationDifference result : differences) {
      status = status.combine(result.getStatus());
      if (!baseVersion.equals(result.getBaseVersion()) || !testVersion.equals(result.getTestVersion())) {
        throw new IllegalArgumentException("All results must have the same base and test versions");
      }
    }
    _status = status;
  }

  @DerivedProperty
  public TestStatus getStatus() {
    return _status;
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code RegressionTestResults}.
   * @return the meta-bean, not null
   */
  public static RegressionTestResults.Meta meta() {
    return RegressionTestResults.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(RegressionTestResults.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   *
   * @return the builder, not null
   */
  public static RegressionTestResults.Builder builder() {
    return new RegressionTestResults.Builder();
  }

  @Override
  public RegressionTestResults.Meta metaBean() {
    return RegressionTestResults.Meta.INSTANCE;
  }

  @Override
  public <R> Property<R> property(String propertyName) {
    return metaBean().<R>metaProperty(propertyName).createProperty(this);
  }

  @Override
  public Set<String> propertyNames() {
    return metaBean().metaPropertyMap().keySet();
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the baseVersion.
   * @return the value of the property, not null
   */
  public String getBaseVersion() {
    return _baseVersion;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the testVersion.
   * @return the value of the property, not null
   */
  public String getTestVersion() {
    return _testVersion;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the differences.
   * @return the value of the property, not null
   */
  public List<CalculationDifference> getDifferences() {
    return _differences;
  }

  //-----------------------------------------------------------------------
  /**
   * Returns a builder that allows this bean to be mutated.
   * @return the mutable builder, not null
   */
  public Builder toBuilder() {
    return new Builder(this);
  }

  @Override
  public RegressionTestResults clone() {
    return this;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      RegressionTestResults other = (RegressionTestResults) obj;
      return JodaBeanUtils.equal(getBaseVersion(), other.getBaseVersion()) &&
          JodaBeanUtils.equal(getTestVersion(), other.getTestVersion()) &&
          JodaBeanUtils.equal(getDifferences(), other.getDifferences()) &&
          JodaBeanUtils.equal(getStatus(), other.getStatus());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getBaseVersion());
    hash += hash * 31 + JodaBeanUtils.hashCode(getTestVersion());
    hash += hash * 31 + JodaBeanUtils.hashCode(getDifferences());
    hash += hash * 31 + JodaBeanUtils.hashCode(getStatus());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(160);
    buf.append("RegressionTestResults{");
    buf.append("baseVersion").append('=').append(getBaseVersion()).append(',').append(' ');
    buf.append("testVersion").append('=').append(getTestVersion()).append(',').append(' ');
    buf.append("differences").append('=').append(getDifferences()).append(',').append(' ');
    buf.append("status").append('=').append(JodaBeanUtils.toString(getStatus()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code RegressionTestResults}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code baseVersion} property.
     */
    private final MetaProperty<String> _baseVersion = DirectMetaProperty.ofImmutable(
        this, "baseVersion", RegressionTestResults.class, String.class);
    /**
     * The meta-property for the {@code testVersion} property.
     */
    private final MetaProperty<String> _testVersion = DirectMetaProperty.ofImmutable(
        this, "testVersion", RegressionTestResults.class, String.class);
    /**
     * The meta-property for the {@code differences} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<List<CalculationDifference>> _differences = DirectMetaProperty.ofImmutable(
        this, "differences", RegressionTestResults.class, (Class) List.class);
    /**
     * The meta-property for the {@code status} property.
     */
    private final MetaProperty<TestStatus> _status = DirectMetaProperty.ofDerived(
        this, "status", RegressionTestResults.class, TestStatus.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "baseVersion",
        "testVersion",
        "differences",
        "status");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1641901881:  // baseVersion
          return _baseVersion;
        case -40990746:  // testVersion
          return _testVersion;
        case 2039608022:  // differences
          return _differences;
        case -892481550:  // status
          return _status;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public RegressionTestResults.Builder builder() {
      return new RegressionTestResults.Builder();
    }

    @Override
    public Class<? extends RegressionTestResults> beanType() {
      return RegressionTestResults.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code baseVersion} property.
     * @return the meta-property, not null
     */
    public MetaProperty<String> baseVersion() {
      return _baseVersion;
    }

    /**
     * The meta-property for the {@code testVersion} property.
     * @return the meta-property, not null
     */
    public MetaProperty<String> testVersion() {
      return _testVersion;
    }

    /**
     * The meta-property for the {@code differences} property.
     * @return the meta-property, not null
     */
    public MetaProperty<List<CalculationDifference>> differences() {
      return _differences;
    }

    /**
     * The meta-property for the {@code status} property.
     * @return the meta-property, not null
     */
    public MetaProperty<TestStatus> status() {
      return _status;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -1641901881:  // baseVersion
          return ((RegressionTestResults) bean).getBaseVersion();
        case -40990746:  // testVersion
          return ((RegressionTestResults) bean).getTestVersion();
        case 2039608022:  // differences
          return ((RegressionTestResults) bean).getDifferences();
        case -892481550:  // status
          return ((RegressionTestResults) bean).getStatus();
      }
      return super.propertyGet(bean, propertyName, quiet);
    }

    @Override
    protected void propertySet(Bean bean, String propertyName, Object newValue, boolean quiet) {
      metaProperty(propertyName);
      if (quiet) {
        return;
      }
      throw new UnsupportedOperationException("Property cannot be written: " + propertyName);
    }

  }

  //-----------------------------------------------------------------------
  /**
   * The bean-builder for {@code RegressionTestResults}.
   */
  public static final class Builder extends BasicImmutableBeanBuilder<RegressionTestResults> {

    private String _baseVersion;
    private String _testVersion;
    private List<CalculationDifference> _differences = new ArrayList<CalculationDifference>();

    /**
     * Restricted constructor.
     */
    private Builder() {
      super(RegressionTestResults.Meta.INSTANCE);
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(RegressionTestResults beanToCopy) {
      super(RegressionTestResults.Meta.INSTANCE);
      this._baseVersion = beanToCopy.getBaseVersion();
      this._testVersion = beanToCopy.getTestVersion();
      this._differences = new ArrayList<CalculationDifference>(beanToCopy.getDifferences());
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case -1641901881:  // baseVersion
          this._baseVersion = (String) newValue;
          break;
        case -40990746:  // testVersion
          this._testVersion = (String) newValue;
          break;
        case 2039608022:  // differences
          this._differences = (List<CalculationDifference>) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public RegressionTestResults build() {
      return new RegressionTestResults(
          _baseVersion,
          _testVersion,
          _differences);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the {@code baseVersion} property in the builder.
     * @param baseVersion  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder baseVersion(String baseVersion) {
      JodaBeanUtils.notNull(baseVersion, "baseVersion");
      this._baseVersion = baseVersion;
      return this;
    }

    /**
     * Sets the {@code testVersion} property in the builder.
     * @param testVersion  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder testVersion(String testVersion) {
      JodaBeanUtils.notNull(testVersion, "testVersion");
      this._testVersion = testVersion;
      return this;
    }

    /**
     * Sets the {@code differences} property in the builder.
     * @param differences  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder differences(List<CalculationDifference> differences) {
      JodaBeanUtils.notNull(differences, "differences");
      this._differences = differences;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(128);
      buf.append("RegressionTestResults.Builder{");
      buf.append("baseVersion").append('=').append(_baseVersion).append(',').append(' ');
      buf.append("testVersion").append('=').append(_testVersion).append(',').append(' ');
      buf.append("differences").append('=').append(_differences);
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
