package jasmine.example.client;

import com.google.gwt.core.client.GWT;
import noo.testing.jasmine.client.DescribeCallback;
import com.google.gwt.core.client.EntryPoint;

import static noo.testing.jasmine.client.Jasmine.describe;
import static noo.testing.jasmine.client.Jasmine.expect;
import static noo.testing.jasmine.client.Jasmine.it;

/**
 * @author Tal Shani
 */
public class JasmineExample implements EntryPoint {
    public void onModuleLoad() {
        ((TestRegistry)GWT.create(TestRegistry.class)).registerTests();

        describe("some test", new DescribeCallback() {
            @Override
            protected void doDescribe() {
                it("should do this thing", new DescribeCallback() {
                    @Override
                    protected void doDescribe() {
                        expect(1).toBe(1);
                    }
                });
            }
        });
        describe("some test 2", new DescribeCallback() {
            @Override
            protected void doDescribe() {
                it("should do this thing2", new DescribeCallback() {
                    @Override
                    protected void doDescribe() {
                        expect(2).toBe(2);
                    }
                });
            }
        });
    }

}
