package noo.testing.jasmine.client;

/**
 * @author Tal Shani
 */
public class Jasmine {

    public static void describe(String text, DescribeCallback define) {
        defineFn("describe", text, define);
    }

    public static void xdescribe(String text, DescribeCallback define) {
        defineFn("xdescribe", text, define);
    }

    public static void describe(String text) {
        defineFn("describe", text, null);
    }

    public static void xdescribe(String text) {
        defineFn("xdescribe", text, null);
    }

    public static void it(String text, JasmineCallback define) {
        defineFn("it", text, define);
    }

    public static void xit(String text, JasmineCallback define) {
        defineFn("xit", text, define);
    }

    public static void it(String text) {
        defineFn("it", text, null);
    }

    public static void xit(String text) {
        defineFn("xit", text, null);
    }

    public static void beforeEach(JasmineCallback define) {
        defineFn("beforeEach", define);
    }

    public static void afterEach(JasmineCallback define) {
        defineFn("afterEach", define);
    }

    public static native ExpectationBuilder expect(Object o) /*-{
        return $wnd.expect(o);
    }-*/;

    /**
     * If you need to have an expectation regarding a function, this is the function to user.
     *
     * @param o
     * @return
     */
    public static native ExpectationBuilder expect(FunctionWrapper o) /*-{
        var fn = function () {
            o.@noo.testing.jasmine.client.FunctionWrapper::execute()()
        };
        return $wnd.expect(fn);
    }-*/;

    public static native void expectFail() /*-{
        return $wnd.expect(false).toBeTruthy();
    }-*/;

    public static native void expectSuccess() /*-{
        return $wnd.expect(true).toBeTruthy();
    }-*/;

    private static native void defineFn(String fn, String text, JasmineCallback define) /*-{
        if (!define) {
            $wnd[fn](text);
        } else {
            $wnd[fn](text, function (done) {
                $entry(function () {
                    var cmd = @noo.testing.jasmine.client.DoneCallback::wrapFn(Lcom/google/gwt/core/client/JavaScriptObject;)(done);
                    define.@noo.testing.jasmine.client.JasmineCallback::callDefine(Lnoo/testing/jasmine/client/DoneCallback;)(cmd)
                })()
            })
        }
    }-*/;

    private static native void defineFn(String fn, JasmineCallback define) /*-{
        $wnd[fn](function (done) {
            $entry(function () {
                var cmd = @noo.testing.jasmine.client.DoneCallback::wrapFn(Lcom/google/gwt/core/client/JavaScriptObject;)(done);
                define.@noo.testing.jasmine.client.JasmineCallback::callDefine(Lnoo/testing/jasmine/client/DoneCallback;)(cmd)
            })()
        })
    }-*/;

    public static native int getDefaultTimeoutInterval() /*-{
        return $wnd.jasmine.DEFAULT_TIMEOUT_INTERVAL;
    }-*/;

    public static native void setDefaultTimeoutInterval(int interval) /*-{
        $wnd.jasmine.DEFAULT_TIMEOUT_INTERVAL = interval;
    }-*/;
}
