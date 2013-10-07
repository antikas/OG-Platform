/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.core.marketdatasnapshot.impl;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.fudgemsg.FudgeField;
import org.fudgemsg.FudgeMsg;
import org.fudgemsg.MutableFudgeMsg;
import org.fudgemsg.mapping.FudgeDeserializer;
import org.fudgemsg.mapping.FudgeSerializer;
import org.joda.beans.Bean;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.ImmutableConstructor;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.BasicImmutableBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;
import org.threeten.bp.Instant;

import com.opengamma.core.marketdatasnapshot.YieldCurveSnapshot;
import com.opengamma.util.ArgumentChecker;

/**
 * Mutable snapshot of yield curve data.
 */
@BeanDefinition
public final class ManageableYieldCurveSnapshot implements ImmutableBean, YieldCurveSnapshot {

  /**
   * The valuation instant.
   */
  @PropertyDefinition
  private final Instant _valuationTime;
  /**
   * The values.
   */
  @PropertyDefinition
  private final ManageableUnstructuredMarketDataSnapshot _values;


  /**
   * Creates an instance.
   *
   * @param valuationTime  the valuationTime, not null
   * @param values  the values, not null
   */
  @ImmutableConstructor
  private  ManageableYieldCurveSnapshot(final Instant valuationTime, final ManageableUnstructuredMarketDataSnapshot values) {
    super();
    ArgumentChecker.notNull(valuationTime, "valuationTime");
    ArgumentChecker.notNull(values, "values");
    _valuationTime = valuationTime;
    _values = values;
  }
  
  /**
   * Creates an instance.
   *
   * @param valuationTime  the valuationTime, not null
   * @param values  the values, not null
   * @return the yield curve snapshot
   */
  public static ManageableYieldCurveSnapshot of(final Instant valuationTime, final ManageableUnstructuredMarketDataSnapshot values) {
    return new ManageableYieldCurveSnapshot(valuationTime, values);
  }

  public org.fudgemsg.FudgeMsg toFudgeMsg(final FudgeSerializer serializer) {
    final MutableFudgeMsg ret = serializer.newMessage();
    // TODO: this should not be adding its own class header; the caller should add it based on application knowledge about the receiving end
    FudgeSerializer.addClassHeader(ret, ManageableYieldCurveSnapshot.class);
    serializer.addToMessage(ret, "values", null, _values);
    serializer.addToMessage(ret, "valuationTime", null, _valuationTime);
    return ret;
  }

  public static ManageableYieldCurveSnapshot fromFudgeMsg(final FudgeDeserializer deserializer, final FudgeMsg msg) {
    ManageableUnstructuredMarketDataSnapshot values = null;
    FudgeField field = msg.getByName("values");
    if (field != null) {
      values = deserializer.fieldValueToObject(ManageableUnstructuredMarketDataSnapshot.class, field);
      
    }
    Instant valuationTime = null;
    field = msg.getByName("valuationTime");
    if (field != null) {
      valuationTime = deserializer.fieldValueToObject(Instant.class, field);
    }
    ManageableYieldCurveSnapshot result = null;
    if (valuationTime != null && values != null) {
      result = ManageableYieldCurveSnapshot.of(valuationTime, values);
    }
    return result;
  }
  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code ManageableYieldCurveSnapshot}.
   * @return the meta-bean, not null
   */
  public static ManageableYieldCurveSnapshot.Meta meta() {
    return ManageableYieldCurveSnapshot.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(ManageableYieldCurveSnapshot.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   *
   * @return the builder, not null
   */
  public static ManageableYieldCurveSnapshot.Builder builder() {
    return new ManageableYieldCurveSnapshot.Builder();
  }

  @Override
  public ManageableYieldCurveSnapshot.Meta metaBean() {
    return ManageableYieldCurveSnapshot.Meta.INSTANCE;
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
   * Gets the valuation instant.
   * @return the value of the property
   */
  public Instant getValuationTime() {
    return _valuationTime;
  }

  //-----------------------------------------------------------------------
  /**
   * Gets the values.
   * @return the value of the property
   */
  public ManageableUnstructuredMarketDataSnapshot getValues() {
    return _values;
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
  public ManageableYieldCurveSnapshot clone() {
    return this;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      ManageableYieldCurveSnapshot other = (ManageableYieldCurveSnapshot) obj;
      return JodaBeanUtils.equal(getValuationTime(), other.getValuationTime()) &&
          JodaBeanUtils.equal(getValues(), other.getValues());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getValuationTime());
    hash += hash * 31 + JodaBeanUtils.hashCode(getValues());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(96);
    buf.append("ManageableYieldCurveSnapshot{");
    buf.append("valuationTime").append('=').append(getValuationTime()).append(',').append(' ');
    buf.append("values").append('=').append(getValues());
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code ManageableYieldCurveSnapshot}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code valuationTime} property.
     */
    private final MetaProperty<Instant> _valuationTime = DirectMetaProperty.ofImmutable(
        this, "valuationTime", ManageableYieldCurveSnapshot.class, Instant.class);
    /**
     * The meta-property for the {@code values} property.
     */
    private final MetaProperty<ManageableUnstructuredMarketDataSnapshot> _values = DirectMetaProperty.ofImmutable(
        this, "values", ManageableYieldCurveSnapshot.class, ManageableUnstructuredMarketDataSnapshot.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "valuationTime",
        "values");

    /**
     * Restricted constructor.
     */
    protected Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 113591406:  // valuationTime
          return _valuationTime;
        case -823812830:  // values
          return _values;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public ManageableYieldCurveSnapshot.Builder builder() {
      return new ManageableYieldCurveSnapshot.Builder();
    }

    @Override
    public Class<? extends ManageableYieldCurveSnapshot> beanType() {
      return ManageableYieldCurveSnapshot.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code valuationTime} property.
     * @return the meta-property, not null
     */
    public MetaProperty<Instant> valuationTime() {
      return _valuationTime;
    }

    /**
     * The meta-property for the {@code values} property.
     * @return the meta-property, not null
     */
    public MetaProperty<ManageableUnstructuredMarketDataSnapshot> values() {
      return _values;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 113591406:  // valuationTime
          return ((ManageableYieldCurveSnapshot) bean).getValuationTime();
        case -823812830:  // values
          return ((ManageableYieldCurveSnapshot) bean).getValues();
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
   * The bean-builder for {@code ManageableYieldCurveSnapshot}.
   */
  public static final class Builder extends BasicImmutableBeanBuilder<ManageableYieldCurveSnapshot> {

    private Instant _valuationTime;
    private ManageableUnstructuredMarketDataSnapshot _values;

    /**
     * Restricted constructor.
     */
    private Builder() {
      super(ManageableYieldCurveSnapshot.Meta.INSTANCE);
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(ManageableYieldCurveSnapshot beanToCopy) {
      super(ManageableYieldCurveSnapshot.Meta.INSTANCE);
      this._valuationTime = beanToCopy.getValuationTime();
      this._values = beanToCopy.getValues();
    }

    //-----------------------------------------------------------------------
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 113591406:  // valuationTime
          this._valuationTime = (Instant) newValue;
          break;
        case -823812830:  // values
          this._values = (ManageableUnstructuredMarketDataSnapshot) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public ManageableYieldCurveSnapshot build() {
      return new ManageableYieldCurveSnapshot(
          _valuationTime,
          _values);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the {@code valuationTime} property in the builder.
     * @param valuationTime  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder valuationTime(Instant valuationTime) {
      this._valuationTime = valuationTime;
      return this;
    }

    /**
     * Sets the {@code values} property in the builder.
     * @param values  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder values(ManageableUnstructuredMarketDataSnapshot values) {
      this._values = values;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(96);
      buf.append("ManageableYieldCurveSnapshot.Builder{");
      buf.append("valuationTime").append('=').append(_valuationTime).append(',').append(' ');
      buf.append("values").append('=').append(_values);
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
