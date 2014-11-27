package noo.testing.jasmine.client.rebind;

import noo.testing.jasmine.client.ExpectationBuilder;
import noo.testing.jasmine.client.FunctionWrapper;
import noo.testing.jasmine.client.Jasmine;

/**
 * A helper base class with calls to static jasmine functions
 *
 * @author Tal Shani
 */
public abstract class JasmineTestClass {

    public ExpectationBuilder expect(Object object) {
        return Jasmine.expect(object);
    }

    public ExpectationBuilder expect(FunctionWrapper object) {
        return Jasmine.expect(object);
    }

}
