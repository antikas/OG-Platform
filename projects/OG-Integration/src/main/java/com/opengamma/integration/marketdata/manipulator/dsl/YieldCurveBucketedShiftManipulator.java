/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.integration.marketdata.manipulator.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

import com.google.common.collect.ImmutableList;
import com.opengamma.analytics.ShiftType;
import com.opengamma.analytics.financial.model.interestrate.curve.YieldCurve;
import com.opengamma.analytics.financial.model.interestrate.curve.YieldCurveUtils;
import com.opengamma.engine.marketdata.manipulator.function.StructureManipulator;
import com.opengamma.engine.value.ValueSpecification;
import com.opengamma.util.tuple.DoublesPair;

/**
 * A {@link StructureManipulator} which performs a list of bucketed shifts on a {@link YieldCurve}.
 */
@BeanDefinition
public final class YieldCurveBucketedShiftManipulator implements ImmutableBean, StructureManipulator<YieldCurve> {

  /**
   * 
   */
  private static final long serialVersionUID = 4722638152564212872L;

  /**
   * Shift type
   */
  @PropertyDefinition
  private final GroovyAliasable _bucketedShiftType;

  
  /**
   * Shifts to apply
   */
  @PropertyDefinition
  private final ImmutableList<YieldCurveBucketedShift> _shifts;
  
  /**
   * Creates a new YieldCurveBucketedShifts object
   * @param bucketedShiftType bucketed shift type
   * @param shifts the list of shifts
   * @return a new YieldCurveBucketedShifts object
   */
  public static YieldCurveBucketedShiftManipulator create(/*GroovyAliasable bucketedShiftType, */
                                                          ImmutableList<YieldCurveBucketedShift> shifts) {
    return new YieldCurveBucketedShiftManipulator(/*bucketedShiftType, */null, shifts);
  }
  
  
  @Override
  public YieldCurve execute(YieldCurve structure, ValueSpecification valueSpecification) {
    final List<DoublesPair> buckets = new ArrayList<>();
    final List<Double> shifts = new ArrayList<>();
    ShiftType shiftType = null;
    for (YieldCurveBucketedShift bucketedShift : _shifts) {
      buckets.add(DoublesPair.of(bucketedShift.getStartYears(), bucketedShift.getEndYears()));
      shifts.add(bucketedShift.getShift());      
      if (shiftType == null) {
        shiftType = bucketedShift.getCurveShiftType().toAnalyticsType();
      }
    }
    return YieldCurveUtils.withBucketedShifts(structure, buckets, shifts, shiftType);
  }

  @Override
  public Class<YieldCurve> getExpectedType() {
    return YieldCurve.class;
  }

  
  
  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code YieldCurveBucketedShiftManipulator}.
   * @return the meta-bean, not null
   */
  public static YieldCurveBucketedShiftManipulator.Meta meta() {
    return YieldCurveBucketedShiftManipulator.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(YieldCurveBucketedShiftManipulator.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static YieldCurveBucketedShiftManipulator.Builder builder() {
    return new YieldCurveBucketedShiftManipulator.Builder();
  }

  private YieldCurveBucketedShiftManipulator(
      GroovyAliasable bucketedShiftType,
      List<YieldCurveBucketedShift> shifts) {
    this._bucketedShiftType = bucketedShiftType;
    this._shifts = (shifts != null ? ImmutableList.copyOf(shifts) : null);
  }

  @Override
  public YieldCurveBucketedShiftManipulator.Meta metaBean() {
    return YieldCurveBucketedShiftManipulator.Meta.INSTANCE;
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
   * Gets shift type
   * @return the value of the property
   */
  public GroovyAliasable getBucketedShiftType() {
    return _bucketedShiftType;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets shifts to apply
   * @return the value of the property
   */
  public ImmutableList<YieldCurveBucketedShift> getShifts() {
    return _shifts;
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
  public YieldCurveBucketedShiftManipulator clone() {
    return this;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      YieldCurveBucketedShiftManipulator other = (YieldCurveBucketedShiftManipulator) obj;
      return JodaBeanUtils.equal(getBucketedShiftType(), other.getBucketedShiftType()) &&
          JodaBeanUtils.equal(getShifts(), other.getShifts());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getBucketedShiftType());
    hash += hash * 31 + JodaBeanUtils.hashCode(getShifts());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("YieldCurveBucketedShiftManipulator{");
    buf.append("bucketedShiftType").append('=').append(getBucketedShiftType()).append(',').append(' ');
    buf.append("shifts").append('=').append(JodaBeanUtils.toString(getShifts()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code YieldCurveBucketedShiftManipulator}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code bucketedShiftType} property.
     */
    private final MetaProperty<GroovyAliasable> _bucketedShiftType = DirectMetaProperty.ofImmutable(
        this, "bucketedShiftType", YieldCurveBucketedShiftManipulator.class, GroovyAliasable.class);
    /**
     * The meta-property for the {@code shifts} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<ImmutableList<YieldCurveBucketedShift>> _shifts = DirectMetaProperty.ofImmutable(
        this, "shifts", YieldCurveBucketedShiftManipulator.class, (Class) ImmutableList.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "bucketedShiftType",
        "shifts");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -1911851821:  // bucketedShiftType
          return _bucketedShiftType;
        case -903338959:  // shifts
          return _shifts;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public YieldCurveBucketedShiftManipulator.Builder builder() {
      return new YieldCurveBucketedShiftManipulator.Builder();
    }

    @Override
    public Class<? extends YieldCurveBucketedShiftManipulator> beanType() {
      return YieldCurveBucketedShiftManipulator.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code bucketedShiftType} property.
     * @return the meta-property, not null
     */
    public MetaProperty<GroovyAliasable> bucketedShiftType() {
      return _bucketedShiftType;
    }

    /**
     * The meta-property for the {@code shifts} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ImmutableList<YieldCurveBucketedShift>> shifts() {
      return _shifts;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -1911851821:  // bucketedShiftType
          return ((YieldCurveBucketedShiftManipulator) bean).getBucketedShiftType();
        case -903338959:  // shifts
          return ((YieldCurveBucketedShiftManipulator) bean).getShifts();
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
   * The bean-builder for {@code YieldCurveBucketedShiftManipulator}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<YieldCurveBucketedShiftManipulator> {

    private GroovyAliasable _bucketedShiftType;
    private List<YieldCurveBucketedShift> _shifts;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(YieldCurveBucketedShiftManipulator beanToCopy) {
      this._bucketedShiftType = beanToCopy.getBucketedShiftType();
      this._shifts = (beanToCopy.getShifts() != null ? new ArrayList<YieldCurveBucketedShift>(beanToCopy.getShifts()) : null);
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case -1911851821:  // bucketedShiftType
          this._bucketedShiftType = (GroovyAliasable) newValue;
          break;
        case -903338959:  // shifts
          this._shifts = (List<YieldCurveBucketedShift>) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public Builder set(MetaProperty<?> property, Object value) {
      super.set(property, value);
      return this;
    }

    @Override
    public Builder setString(String propertyName, String value) {
      setString(meta().metaProperty(propertyName), value);
      return this;
    }

    @Override
    public Builder setString(MetaProperty<?> property, String value) {
      super.set(property, value);
      return this;
    }

    @Override
    public Builder setAll(Map<String, ? extends Object> propertyValueMap) {
      super.setAll(propertyValueMap);
      return this;
    }

    @Override
    public YieldCurveBucketedShiftManipulator build() {
      return new YieldCurveBucketedShiftManipulator(
          _bucketedShiftType,
          _shifts);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the {@code bucketedShiftType} property in the builder.
     * @param bucketedShiftType  the new value
     * @return this, for chaining, not null
     */
    public Builder bucketedShiftType(GroovyAliasable bucketedShiftType) {
      this._bucketedShiftType = bucketedShiftType;
      return this;
    }

    /**
     * Sets the {@code shifts} property in the builder.
     * @param shifts  the new value
     * @return this, for chaining, not null
     */
    public Builder shifts(List<YieldCurveBucketedShift> shifts) {
      this._shifts = shifts;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(96);
      buf.append("YieldCurveBucketedShiftManipulator.Builder{");
      buf.append("bucketedShiftType").append('=').append(JodaBeanUtils.toString(_bucketedShiftType)).append(',').append(' ');
      buf.append("shifts").append('=').append(JodaBeanUtils.toString(_shifts));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
