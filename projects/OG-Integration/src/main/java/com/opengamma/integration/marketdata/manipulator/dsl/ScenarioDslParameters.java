/**
 * Copyright (C) 2013 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.integration.marketdata.manipulator.dsl;

import java.io.StringReader;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.codehaus.groovy.control.CompilerConfiguration;
import org.fudgemsg.FudgeMsg;
import org.fudgemsg.MutableFudgeMsg;
import org.fudgemsg.mapping.FudgeDeserializer;
import org.fudgemsg.mapping.FudgeSerializer;
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

import com.opengamma.core.config.Config;
import com.opengamma.core.config.ConfigGroups;
import com.opengamma.engine.marketdata.manipulator.ScenarioParameters;

import groovy.lang.GroovyShell;
import groovy.lang.Script;

/**
 * Config object for storing parameters required to build a scenario.
 * This object stores a Groovy script which is executed to populate the parameter values.
 * The script should be of the form:
 * <pre>
 * aString = "FOO"
 * aList = [1, 2, 3]
 * aMap = [key1: "val1", key2: "val2"]
 * </pre>
 */
@Config(description = "Scenario DSL parameters", group = ConfigGroups.SCENARIOS)
@BeanDefinition
public final class ScenarioDslParameters implements ImmutableBean, ScenarioParameters {

  /** Field name for Fudge message */
  private static final String SCRIPT = "script";

  /**
   * The script that populates the parameters.
   */
  @PropertyDefinition(get = "private", validate = "notEmpty")
  private final String _script;

  //-------------------------------------------------------------------------
  /**
   * Obtains an instance of {@code ScenarioDslParameters}.
   * 
   * @param script  the script, not null
   * @return the parameters script, not null
   */
  public static ScenarioDslParameters of(String script) {
    return new ScenarioDslParameters(script);
  }

  //-------------------------------------------------------------------------
  /**
   * Gets the parameters.
   * 
   * @return the parameters, keyed by name
   */
  @Override
  @SuppressWarnings("unchecked")
  public Map<String, Object> getParameters() {
    CompilerConfiguration config = new CompilerConfiguration();
    config.setScriptBaseClass(SimulationScript.class.getName());
    GroovyShell shell = new GroovyShell(config);
    Script script = shell.parse(new StringReader(_script));
    script.run();
    return script.getBinding().getVariables();
  }

  //-------------------------------------------------------------------------
  public MutableFudgeMsg toFudgeMsg(final FudgeSerializer serializer) {
    MutableFudgeMsg msg = serializer.newMessage();
    serializer.addToMessage(msg, SCRIPT, null, _script);
    return msg;
  }

  public static ScenarioDslParameters fromFudgeMsg(final FudgeDeserializer deserializer, final FudgeMsg msg) {
    String script = deserializer.fieldValueToObject(String.class, msg.getByName(SCRIPT));
    return new ScenarioDslParameters(script);
  }

  //------------------------- AUTOGENERATED START -------------------------
  ///CLOVER:OFF
  /**
   * The meta-bean for {@code ScenarioDslParameters}.
   * @return the meta-bean, not null
   */
  public static ScenarioDslParameters.Meta meta() {
    return ScenarioDslParameters.Meta.INSTANCE;
  }

  static {
    JodaBeanUtils.registerMetaBean(ScenarioDslParameters.Meta.INSTANCE);
  }

  /**
   * Returns a builder used to create an instance of the bean.
   * @return the builder, not null
   */
  public static ScenarioDslParameters.Builder builder() {
    return new ScenarioDslParameters.Builder();
  }

  private ScenarioDslParameters(
      String script) {
    JodaBeanUtils.notEmpty(script, "script");
    this._script = script;
  }

  @Override
  public ScenarioDslParameters.Meta metaBean() {
    return ScenarioDslParameters.Meta.INSTANCE;
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
   * Gets the script that populates the parameters.
   * @return the value of the property, not empty
   */
  private String getScript() {
    return _script;
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
  public ScenarioDslParameters clone() {
    return this;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj != null && obj.getClass() == this.getClass()) {
      ScenarioDslParameters other = (ScenarioDslParameters) obj;
      return JodaBeanUtils.equal(getScript(), other.getScript());
    }
    return false;
  }

  @Override
  public int hashCode() {
    int hash = getClass().hashCode();
    hash += hash * 31 + JodaBeanUtils.hashCode(getScript());
    return hash;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(64);
    buf.append("ScenarioDslParameters{");
    buf.append("script").append('=').append(JodaBeanUtils.toString(getScript()));
    buf.append('}');
    return buf.toString();
  }

  //-----------------------------------------------------------------------
  /**
   * The meta-bean for {@code ScenarioDslParameters}.
   */
  public static final class Meta extends DirectMetaBean {
    /**
     * The singleton instance of the meta-bean.
     */
    static final Meta INSTANCE = new Meta();

    /**
     * The meta-property for the {@code script} property.
     */
    private final MetaProperty<String> _script = DirectMetaProperty.ofImmutable(
        this, "script", ScenarioDslParameters.class, String.class);
    /**
     * The meta-properties.
     */
    private final Map<String, MetaProperty<?>> _metaPropertyMap$ = new DirectMetaPropertyMap(
        this, null,
        "script");

    /**
     * Restricted constructor.
     */
    private Meta() {
    }

    @Override
    protected MetaProperty<?> metaPropertyGet(String propertyName) {
      switch (propertyName.hashCode()) {
        case -907685685:  // script
          return _script;
      }
      return super.metaPropertyGet(propertyName);
    }

    @Override
    public ScenarioDslParameters.Builder builder() {
      return new ScenarioDslParameters.Builder();
    }

    @Override
    public Class<? extends ScenarioDslParameters> beanType() {
      return ScenarioDslParameters.class;
    }

    @Override
    public Map<String, MetaProperty<?>> metaPropertyMap() {
      return _metaPropertyMap$;
    }

    //-----------------------------------------------------------------------
    /**
     * The meta-property for the {@code script} property.
     * @return the meta-property, not null
     */
    public MetaProperty<String> script() {
      return _script;
    }

    //-----------------------------------------------------------------------
    @Override
    protected Object propertyGet(Bean bean, String propertyName, boolean quiet) {
      switch (propertyName.hashCode()) {
        case -907685685:  // script
          return ((ScenarioDslParameters) bean).getScript();
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
   * The bean-builder for {@code ScenarioDslParameters}.
   */
  public static final class Builder extends DirectFieldsBeanBuilder<ScenarioDslParameters> {

    private String _script;

    /**
     * Restricted constructor.
     */
    private Builder() {
    }

    /**
     * Restricted copy constructor.
     * @param beanToCopy  the bean to copy from, not null
     */
    private Builder(ScenarioDslParameters beanToCopy) {
      this._script = beanToCopy.getScript();
    }

    //-----------------------------------------------------------------------
    @Override
    public Builder set(String propertyName, Object newValue) {
      switch (propertyName.hashCode()) {
        case -907685685:  // script
          this._script = (String) newValue;
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
    public ScenarioDslParameters build() {
      return new ScenarioDslParameters(
          _script);
    }

    //-----------------------------------------------------------------------
    /**
     * Sets the {@code script} property in the builder.
     * @param script  the new value, not empty
     * @return this, for chaining, not null
     */
    public Builder script(String script) {
      JodaBeanUtils.notEmpty(script, "script");
      this._script = script;
      return this;
    }

    //-----------------------------------------------------------------------
    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder(64);
      buf.append("ScenarioDslParameters.Builder{");
      buf.append("script").append('=').append(JodaBeanUtils.toString(_script));
      buf.append('}');
      return buf.toString();
    }

  }

  ///CLOVER:ON
  //-------------------------- AUTOGENERATED END --------------------------
}
