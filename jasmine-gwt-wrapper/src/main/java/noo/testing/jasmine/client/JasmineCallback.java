package noo.testing.jasmine.client;

/**
 * @author Tal Shani
 */
public abstract class JasmineCallback {

    private boolean asyncOverridden = true;
    private boolean syncOverridden = true;

    public void define(DoneCallback done) {
        asyncOverridden = false;
    }

    public void define() {
        syncOverridden = false;
    }

    final void callDefine(DoneCallback done) {
        define(done);
        if (asyncOverridden) {
            return;
        }
        define();
        if (syncOverridden) {
            done.execute();
            return;
        }
        throw new RuntimeException("Jasmin callback muse define a sync or async block");
    }
}
