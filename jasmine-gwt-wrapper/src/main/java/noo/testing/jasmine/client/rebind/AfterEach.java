package noo.testing.jasmine.client.rebind;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Tal Shani
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface AfterEach {
}
