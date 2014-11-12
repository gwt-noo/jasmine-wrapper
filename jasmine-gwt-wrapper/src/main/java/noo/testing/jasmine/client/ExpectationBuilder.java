package noo.testing.jasmine.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Tal Shani
 */
public final class ExpectationBuilder extends JavaScriptObject {
    protected ExpectationBuilder() {}

    public native void toBe(Object o) /*-{
      this.toBe(o);
    }-*/;

    public native ExpectationBuilder not() /*-{
      return this.not;
    }-*/;

    public native void toEqual(Object o) /*-{
      this.toEqual(o);
    }-*/;

}
