package noo.testing.jasmine.client;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tal Shani
 */
public class JasminePrettyPrintRegistry {
    private static List<JasminePrettyPrint> functions = new ArrayList<JasminePrettyPrint>();

    public static int size() {
        return functions.size();
    }

    public static boolean add(JasminePrettyPrint jasminePrettyPrint) {
        return functions.add(jasminePrettyPrint);
    }

    static native void registerCallback() /*-{
      $wnd.jasmine.pp = (function (pp) {
        return function (value) {
          var ret = $entry(function () {
            return @noo.testing.jasmine.client.JasminePrettyPrintRegistry::prettyPrint(Ljava/lang/Object;)(value);
          })();
          if (typeof ret == 'string') {
            return ret;
          }
          return pp(value);
        };
      })($wnd.jasmine.pp);
    }-*/;

    /**
     * Starting from the last registered pretty printer, we try to find a match
     *
     * @param value
     * @return
     */
    static String prettyPrint(Object value) {
        int l = functions.size();
        while (l-- > 0) {
            String val = functions.get(l).prittyPrint(value);
            if (val != null) return val;
        }
        return null;
    }
}
