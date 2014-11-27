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
        writeJasmineDescribeCode(logger, testType, writer, 0, null);
    }

    private void writeJasmineDescribeCode(TreeLogger logger, JClassType testType, SourceWriter writer, int depth, String instantiation) throws UnableToCompleteException {
        Describe describeAn = testType.getAnnotation(Describe.class);
        String className = testType.getQualifiedSourceName();

        if (describeAn == null) {
            logger.log(TreeLogger.Type.ERROR, "No describe annotation on class " + className);
            throw new UnableToCompleteException();
        }

        String instanceName = "testInstance" + depth;
        // no instantiation so assume we can create the class
        if (instantiation == null) {
            instantiation = "new " + testType.getQualifiedSourceName() + "()";
        }

        writer.println("noo.testing.jasmine.client.Jasmine.describe(\"%s\", new noo.testing.jasmine.client.DescribeCallback() {", describeAn.value());
        writer.indent();
        {
            writer.println("@Override");
            writer.println("protected void doDescribe() {");
            writer.indent();
            {
                writer.println("final %1$s %2$s = %3$s;", className, instanceName, instantiation);
                for (JMethod method : testType.getMethods()) {
                    if (method.getAnnotation(BeforeAll.class) != null) {
                        writeMethodCall(logger, method, writer, instanceName);
                    }
                }
                for (JMethod method : testType.getMethods()) {
                    if (method.getAnnotation(It.class) != null) writeJasmineItCode(logger, method, writer, instanceName);
                    else if (method.getAnnotation(BeforeEach.class) != null)
                        writeJasmineBeforeAfterEachCode(logger, method, writer, instanceName, true);
                    else if (method.getAnnotation(AfterEach.class) != null)
                        writeJasmineBeforeAfterEachCode(logger, method, writer, instanceName, false);
                    else if (method.getParameters().length == 0 && method.getReturnType() != null) {
                        // we might have a sub-describe class
                        JClassType cls = method.getReturnType().isClass();
                        if (cls != null && cls.getAnnotation(Describe.class) != null) {
                            String subInstantiation = String.format("%s.%s()", instanceName, method.getName());
                            writeJasmineDescribeCode(logger, cls, writer, depth + 1, subInstantiation);
                        }
                    }
                }
            }
            writer.outdent();
            writer.println("}");
        }
        writer.outdent();
        writer.println("});");
    }

    private void writeJasmineItCode(TreeLogger logger, JMethod method, SourceWriter writer, String instanceName) {
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
                    writer.println("%s.%s(done);", instanceName, method.getName());
                } else {
                    writer.println("%s.%s();", instanceName, method.getName());
                    writer.println("done.execute();");
                }
            }
            writer.outdent();
            writer.println("}");
        }
        writer.outdent();
        writer.println("});");
    }

    private void writeJasmineBeforeAfterEachCode(TreeLogger logger, JMethod method, SourceWriter writer, String instanceName, boolean before) {
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
                    writer.println("%s.%s(done);", instanceName, method.getName());
                } else {
                    writer.println("%s.%s();", instanceName, method.getName());
                    writer.println("done.execute();");
                }
            }
            writer.outdent();
            writer.println("}");
        }
        writer.outdent();
        writer.println("});");
    }

    private void writeMethodCall(TreeLogger logger, JMethod method, SourceWriter writer, String instanceName) throws UnableToCompleteException {
        // make sure we have at most one argument and it's the done callback
        JParameter[] params = method.getParameters();
        if (params.length > 0) {
            logger.log(TreeLogger.Type.ERROR, "Jasmine method cannot receive arguments : " + method.getEnclosingType().getName() + " :: " + method.getName());
            throw new UnableToCompleteException();
        }

        writer.println("%s.%s();", instanceName, method.getName());

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
