package noo.testing.jasmine.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Tal Shani
 */
public final class ExpectationBuilder extends JavaScriptObject {
    protected ExpectationBuilder() {
    }

    public native void toBe(Object o) /*-{
        this.toBe(o);
    }-*/;

    public native ExpectationBuilder not() /*-{
        return this.not;
    }-*/;

    public native void toEqual(Object o) /*-{
        this.toEqual(o);
    }-*/;

    public native void toMatch(String regex) /*-{
        this.toMatch(regex);
    }-*/;

    public native void toBeDefined() /*-{
        this.toBeDefined();
    }-*/;

    public native void toBeUndefined() /*-{
        this.toBeUndefined();
    }-*/;

    public native void toBeNull() /*-{
        this.toBeNull();
    }-*/;

    public native void toBeTruthy() /*-{
        this.toBeTruthy();
    }-*/;

    public native void toBeFalsy() /*-{
        this.toBeFalsy();
    }-*/;

    public native void toContain(Object o) /*-{
        this.toContain(o);
    }-*/;

    public native void toBeLessThan(double o) /*-{
        this.toBeLessThan(o);
    }-*/;

    public native void toThrow() /*-{
        this.toThrow();
    }-*/;

}
