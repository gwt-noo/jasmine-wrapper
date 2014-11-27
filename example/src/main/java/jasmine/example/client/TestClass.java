package jasmine.example.client;

import noo.testing.jasmine.client.rebind.*;

/**
 * @author Tal Shani
 */
@Describe("What are we describing")
public class TestClass extends JasmineTestClass {


    @BeforeAll
    public void init() {

    }

    @BeforeEach
    public void beforeEach1() {

    }
    @BeforeEach
    public void beforeEach2() {

    }


    @It("should be ea")
    public void aTest() {

    }

    public AnotherTest subTest() {
        return new AnotherTest();
    }

    @Describe("Inner describe")
    public class AnotherTest {

        @It("should be inside the outer describe")
        public void inner() {

        }
    }

}
