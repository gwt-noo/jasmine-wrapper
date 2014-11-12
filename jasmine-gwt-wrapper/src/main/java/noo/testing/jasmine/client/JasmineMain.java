package noo.testing.jasmine.client;

import com.google.gwt.core.client.EntryPoint;

/**
 * Just runs the tests described by other modules
 *
 * @author Tal Shani
 */
public class JasmineMain implements EntryPoint {
    @Override
    public void onModuleLoad() {
        runJasmineTests();
        registerPPs();
        JasminePrettyPrintRegistry.registerCallback();
    }

    private static void registerPPs() {
        JasminePrettyPrintRegistry.add(new JasminePrettyPrint() {
            @Override
            public String prittyPrint(Object value) {
                if(value instanceof Integer) {
                    return value.toString();
                }
                return null;
            }
        });
    }

    /**
     * We must run the jasmin test method only after all other modules entry points were started so all tests
     * have been registered
     */
    private static native final void runJasmineTests() /*-{
      $wnd.setTimeout($wnd.runJasmineTests, 10);
    }-*/;
}
