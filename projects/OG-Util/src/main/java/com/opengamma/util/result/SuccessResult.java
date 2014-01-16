/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.util.result;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.joda.beans.Bean;
import org.joda.beans.BeanDefinition;
import org.joda.beans.ImmutableBean;
import org.joda.beans.ImmutableConstructor;
import org.joda.beans.JodaBeanUtils;
import org.joda.beans.MetaProperty;
import org.joda.beans.Property;
import org.joda.beans.PropertyDefinition;
import org.joda.beans.impl.direct.DirectFieldsBeanBuilder;
import org.joda.beans.impl.direct.DirectMetaBean;
import org.joda.beans.impl.direct.DirectMetaProperty;
import org.joda.beans.impl.direct.DirectMetaPropertyMap;

/**
 * {@link Result} indicating a successful invocation of a calculation.
 *
 * @param <T> the type of the underlying result
 */
@BeanDefinition
public final class SuccessResult<T> implements Result<T>, ImmutableBean {

  /**
   * The result of the calculation.
   */
  // Manual getter as the getValue method is defined on the Result interface - see PLAT-5458
  @PropertyDefinition(validate = "notNull", get = "manual")
  private final T _value;

  @ImmutableConstructor
  /* package */ SuccessResult(T value) {
    _value = value;
  }

  @Override
  public T getValue() {
    return _value;
  }

  @Override
  public String getFailureMessage() {
    throw new IllegalStateException("Unable to get an error message from a success result");
  }

  @Override
  public String toString() {
    return "SuccessResult{_value=" + _value + '}';
  }

  @Override
  public ResultStatus getStatus() {
    return SuccessStatus.SUCCESS;
  }

  @Override
  public boolean isValueAvailable() {
    return true;
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code SuccessResult}.
   * @return the meta-bean, not null
   */
  @SuppressWarnings("rawtypes")
  public static SuccessResult.Meta meta() {
    return SuccessResult.Meta.INSTANCE;
  }

  /**
   * The meta-bean for {@code SuccessResult}.
   * @param <R>  the bean's generic type
   * @param cls  the bean's generic type
   * @return the meta-bean, not null
   */
  @SuppressWarnings("unchecked")
  public static <R> SuccessResult.Meta<R> metaSuccessResult(Class<R> cls) {
    return SuccessResult.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(SuccessResult.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   * @param <T>  the type
   * @return the builder, not null
   */
  public static <T> SuccessResult.Builder<T> builder() {
    return new SuccessResult.Builder<T>();
  }

  @SuppressWarnings("unchecked")
  @Override
  public SuccessResult.Meta<T> metaBean() {
    return SuccessResult.Meta.INSTANCE;
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
   * Returns a builder that allows this bean to be mutated.
   * @return the mutable builder, not null
   */
  public Builder<T> toBuilder() {
    return new Builder<T>(this);
  }

  @Override
  public SuccessResult<T> clone() {
    return this;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      SuccessResult<?> other = (SuccessResult<?>) obj;
      return JodaBeanUtils.equal(getValue(), other.getValue());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getValue());
    return hash;
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code SuccessResult}.
   */
  public static final class Meta<T> extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    @SuppressWarnings("rawtypes")
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code value} property.
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    private final MetaProperty<T> _value = (DirectMetaProperty) DirectMetaProperty.ofImmutable(
        this, "value", SuccessResult.class, Object.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "value");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case 111972721:  // value
          return _value;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public SuccessResult.Builder<T> builder() {
      return new SuccessResult.Builder<T>();
    }

    @SuppressWarnings({"unchecked", "rawtypes" })
    @Override
    public Class<? extends SuccessResult<T>> beanType() {
      return (Class) SuccessResult.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code value} property.
     * @return the meta-property, not null
     */
    public MetaProperty<T> value() {
      return _value;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case 111972721:  // value
          return ((SuccessResult<?>) bean).getValue();
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
   * The bean-builder for {@code SuccessResult}.
   */
  public static final class Builder<T> extends DirectFieldsBeanBuilder<SuccessResult<T>> {

    private T _value;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(SuccessResult<T> beanToCopy) {
      this._value = beanToCopy.getValue();
    }

    //-----------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    @Override
    public Builder<T> set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case 111972721:  // value
          this._value = (T) newValue;
          break;
        default:
          throw new NoSuchElementException("Unknown property: " + propertyName);
      }
      return this;
    }

    @Override
    public Builder<T> set(MetaProperty<?> property, Object value) {
      super.set(property, value);
      return this;
    }

    @Override
    public Builder<T> setString(String propertyName, String value) {
      setString(meta().metaProperty(propertyName), value);
      return this;
    }

    @Override
    public Builder<T> setString(MetaProperty<?> property, String value) {
      super.set(property, value);
      return this;
    }

    @Override
    public Builder<T> setAll(Map<String, ? extends Object> propertyValueMap) {
      super.setAll(propertyValueMap);
      return this;
    }

    @Override
    public SuccessResult<T> build() {
      return new SuccessResult<T>(
          _value);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the {@code value} property in the builder.
     * @param value  the new value, not null
     * @return this, for chaining, not null
     */
    public Builder<T> value(T value) {
      JodaBeanUtils.notNull(value, "value");
      this._value = value;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(64);
      buf.append("SuccessResult.Builder{");
      buf.append("value").append('=').append(JodaBeanUtils.toString(_value));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
