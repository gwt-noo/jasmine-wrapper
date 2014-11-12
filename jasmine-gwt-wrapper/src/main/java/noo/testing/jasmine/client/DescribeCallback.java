package noo.testing.jasmine.client;

/**
 * @author Tal Shani
 */
public abstract class DescribeCallback extends JasmineCallback {

    @Override
    public final void define(DoneCallback done) {
        super.define(done);
    }

    @Override
    public final void define() {
        doDescribe();
    }

    protected abstract void doDescribe();

}
