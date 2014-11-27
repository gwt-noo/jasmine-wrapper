package noo.testing.jasmine.rebind;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.*;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import noo.testing.jasmine.client.DoneCallback;
import noo.testing.jasmine.client.Jasmine;
import noo.testing.jasmine.client.rebind.*;

import java.io.PrintWriter;

/**
 * @author Tal Shani
 */
public class JasmineGenerator extends Generator {
    @Override
    public String generate(TreeLogger logger, GeneratorContext context, String typeName) throws UnableToCompleteException {
        try {
            JClassType testRegistryType = context.getTypeOracle().getType(typeName);
            SourceWriter writer = createSourceWriter(logger, context, testRegistryType);
            if (writer != null) { // Otherwise the class was already created
                writer.println("@Override");
                writer.println("public void registerTests() {");
                writer.indent();
                for (Class rootTestClass : getRootTestClasses(testRegistryType)) {
                    writeJasmineDescribeCode(logger, context, rootTestClass, writer);
                }
                writer.outdent();
                writer.println("}");
                writer.commit(logger);
            }
            return getFullyQualifiedGeneratedClassName(testRegistryType);
        } catch (NotFoundException e) {
            logger.log(TreeLogger.Type.ERROR, "Error generating " + typeName, e);
            throw new UnableToCompleteException();
        }
    }


    private Class[] getRootTestClasses(JClassType type) {
        TestClasses testClasses = type.getAnnotation(TestClasses.class);
        return testClasses.value();
    }

    private void writeJasmineDescribeCode(TreeLogger logger, GeneratorContext context, Class testClass, SourceWriter writer) throws UnableToCompleteException {
        String testClassCanonicalName = testClass.getCanonicalName();
        TypeOracle typeOracle = context.getTypeOracle();
        JClassType testType = typeOracle.findType(testClassCanonicalName);
        Describe describeAn = testType.getAnnotation(Describe.class);
        if (describeAn == null) {
            logger.log(TreeLogger.Type.ERROR, "No describe annotation on class " + testClass.getCanonicalName());
            throw new UnableToCompleteException();
        }

        writer.println("noo.testing.jasmine.client.Jasmine.describe(\"%s\", new noo.testing.jasmine.client.DescribeCallback() {", describeAn.value());
        writer.indent();
        {
            writer.println("@Override");
            writer.println("protected void doDescribe() {");
            writer.indent();
            {
                writer.println("final %1$s testInstance = new %1$s();", testClassCanonicalName);
                for (JMethod method : testType.getMethods()) {
                    if (method.getAnnotation(BeforeAll.class) != null) {
                        writeMethodCall(logger, method, writer);
                    }
                }
                for (JMethod method : testType.getMethods()) {
                    if (method.getAnnotation(It.class) != null) writeJasmineItCode(logger, context, method, writer);
                    if (method.getAnnotation(BeforeEach.class) != null)
                        writeJasmineBeforeAfterEachCode(logger, method, writer, true);
                    if (method.getAnnotation(AfterEach.class) != null)
                        writeJasmineBeforeAfterEachCode(logger, method, writer, false);
                }
            }
            writer.outdent();
            writer.println("}");
        }
        writer.outdent();
        writer.println("});");
    }

    private void writeJasmineItCode(TreeLogger logger, GeneratorContext context, JMethod method, SourceWriter writer) {
        It itAnnotation = method.getAnnotation(It.class);
        if (itAnnotation == null) return;

        // make sure we have at most one argument and it's the done callback
        JParameter[] params = method.getParameters();
        if (params.length > 0 && !params[0].getType().getQualifiedBinaryName().equals(DoneCallback.class.getCanonicalName())) {
            logger.log(TreeLogger.Type.WARN, "Jasmine IT method can receive only 'DoneCallback' at : " + method.getEnclosingType().getName() + " :: " + method.getName());
            return;
        }

        boolean isAsync = params.length > 0;

        writer.println("noo.testing.jasmine.client.Jasmine.it(\"%s\", new noo.testing.jasmine.client.JasmineCallback() {", itAnnotation.value());
        writer.indent();
        {
            writer.println("@Override");
            writer.println("public void define(noo.testing.jasmine.client.DoneCallback done) {");
            writer.indent();
            {
                if (isAsync) {
                    writer.println("testInstance.%s(done);", method.getName());
                } else {
                    writer.println("testInstance.%s();", method.getName());
                    writer.println("done.execute();");
                }
            }
            writer.outdent();
            writer.println("}");
        }
        writer.outdent();
        writer.println("});");
    }

    private void writeJasmineBeforeAfterEachCode(TreeLogger logger, JMethod method, SourceWriter writer, boolean before) {
        // make sure we have at most one argument and it's the done callback
        JParameter[] params = method.getParameters();
        if (params.length > 0 && !params[0].getType().getQualifiedBinaryName().equals(DoneCallback.class.getCanonicalName())) {
            logger.log(TreeLogger.Type.WARN, "Jasmine method can receive only 'DoneCallback' at : " + method.getEnclosingType().getName() + " :: " + method.getName());
            return;
        }

        boolean isAsync = params.length > 0;

        writer.println("noo.testing.jasmine.client.Jasmine.%sEach(new noo.testing.jasmine.client.JasmineCallback() {", before ? "before" : "after");
        writer.indent();
        {
            writer.println("@Override");
            writer.println("public void define(noo.testing.jasmine.client.DoneCallback done) {");
            writer.indent();
            {
                if (isAsync) {
                    writer.println("testInstance.%s(done);", method.getName());
                } else {
                    writer.println("testInstance.%s();", method.getName());
                    writer.println("done.execute();");
                }
            }
            writer.outdent();
            writer.println("}");
        }
        writer.outdent();
        writer.println("});");
    }

    private void writeMethodCall(TreeLogger logger, JMethod method, SourceWriter writer) throws UnableToCompleteException {
        // make sure we have at most one argument and it's the done callback
        JParameter[] params = method.getParameters();
        if (params.length > 0) {
            logger.log(TreeLogger.Type.ERROR, "Jasmine method cannot receive arguments : " + method.getEnclosingType().getName() + " :: " + method.getName());
            throw new UnableToCompleteException();
        }

        writer.println("testInstance.%s();", method.getName());

    }

    private SourceWriter createSourceWriter(TreeLogger logger, GeneratorContext context, JClassType type) {
        String simpleName = getSimpleGeneratedClassName(type);
        String packageName = type.getPackage().getName();
        ClassSourceFileComposerFactory composer =
                new ClassSourceFileComposerFactory(packageName, simpleName);
        composer.addImplementedInterface(type.getName());

        composer.addImport(com.google.gwt.core.client.GWT.class.getCanonicalName());
        composer.addImport(Jasmine.class.getCanonicalName());

        PrintWriter printWriter = context.tryCreate(logger, packageName, simpleName);
        return (printWriter != null) ? composer.createSourceWriter(context, printWriter) : null;
    }

    private String getSimpleGeneratedClassName(JClassType dialectType) {
        return dialectType.getName().replace('.', '_') + "Impl";
    }

    private String getFullyQualifiedGeneratedClassName(JClassType eventBinderType) {
        return new StringBuilder()
                .append(eventBinderType.getPackage().getName())
                .append('.')
                .append(getSimpleGeneratedClassName(eventBinderType))
                .toString();
    }
}
