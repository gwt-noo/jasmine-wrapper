package noo.testing.jasmine.client;

import com.google.gwt.core.client.JavaScriptObject;

/**
* @author Tal Shani
*/
public final class DoneCallback {

    private final JavaScriptObject fn;

    private DoneCallback(JavaScriptObject fn) {this.fn = fn;}

    public native void execute() /*-{
      var fn = this.@noo.testing.jasmine.client.DoneCallback::fn;
      if(fn) fn();
    }-*/;


    static DoneCallback wrapFn(JavaScriptObject doneFn) {
        return new DoneCallback(doneFn);
    }
}
