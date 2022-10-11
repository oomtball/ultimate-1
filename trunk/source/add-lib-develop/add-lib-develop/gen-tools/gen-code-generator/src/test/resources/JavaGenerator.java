/* This file was generated with the ADD-Lib Gen Tools
 * http://add-lib.scce.info/ */

package info.scce.addlib.codegenerator;

import info.scce.addlib.dd.LabelledRegularDD;
import info.scce.addlib.dd.RegularDD;
import info.scce.addlib.traverser.PreorderTraverser;

import java.io.PrintStream;
import java.util.List;

public class JavaGenerator<D extends RegularDD<?, D>> extends CodeGenerator<D> {

    private String paramPackage;
    private String paramImport;
    private String paramClassName = "ADDLibDecisionService";
    private boolean enableableParamStaticMethods;

    public JavaGenerator<D> withPackage(String pac) {
        this.paramPackage = pac;
        return this;
    }

    public JavaGenerator<D> withImport(String imp) {
        this.paramImport = imp;
        return this;
    }

    public JavaGenerator<D> withClassName(String cla) {
        this.paramClassName = cla;
        return this;
    }

    public JavaGenerator<D> withStaticMethodsEnabled(boolean sta) {
        this.enableableParamStaticMethods = sta;
        return this;
    }

    public JavaGenerator<D> withStaticMethodsEnabled() {
        return withStaticMethodsEnabled(true);
    }

    public JavaGenerator<D> withStaticMethodsDisabled() {
        return withStaticMethodsEnabled(false);
    }

    @Override
    public void generate(PrintStream out, List<LabelledRegularDD<D>> roots) {
        out.println("/* This file was generated with the ADD-Lib");
        out.println(" * http://add-lib.scce.info/ */");
        if (this.paramPackage != null) {
            out.println("");
            out.print("package ");
            if (this.paramPackage != null)
                out.print(this.paramPackage);
            out.println(";");
        }
        if (this.paramImport != null) {
            out.println("");
            out.print("import ");
            if (this.paramImport != null)
                out.print(this.paramImport);
            out.println(";");
        }
        out.println("");
        out.print("public class ");
        if (this.paramClassName != null)
            out.print(this.paramClassName);
        out.println(" {");
        for (LabelledRegularDD<D> x0 : roots) {
            out.println("");
            out.print("    public ");
            if (this.enableableParamStaticMethods) {
                out.print("static ");
            }
            returnType(out);
            out.print(" ");
            out.print(x0.label());
            out.print("(");
            argType(out);
            out.println(" arg) {");
            out.print("        return eval");
            out.print(x0.dd().ptr());
            out.println("(arg);");
            out.println("    }");
        }
        for (D x1 : new PreorderTraverser<D>(unlabelledRoots(roots))) {
            if (!x1.isConstant()) {
                out.println("");
                out.print("    private ");
                if (this.enableableParamStaticMethods) {
                    out.print("static ");
                }
                returnType(out);
                out.print(" eval");
                out.print(x1.ptr());
                out.print("(");
                argType(out);
                out.println(" arg) {");
                out.print("        if (");
                varCondition(out, x1);
                out.println(")");
                out.print("           return eval");
                out.print(x1.t().ptr());
                out.println("(arg);");
                out.println("        else");
                out.print("            return eval");
                out.print(x1.e().ptr());
                out.println("(arg);");
                out.println("    }");
            }
        }
        for (D x2 : new PreorderTraverser<D>(unlabelledRoots(roots))) {
            if (x2.isConstant()) {
                out.println("");
                out.print("    private ");
                if (this.enableableParamStaticMethods) {
                    out.print("static ");
                }
                returnType(out);
                out.print(" eval");
                out.print(x2.ptr());
                out.print("(");
                argType(out);
                out.println(" arg) {");
                out.print("        return ");
                resultInstatiation(out, x2);
                out.println(";");
                out.println("    }");
            }
        }
        out.println("}");
    }

    protected void returnType(PrintStream out) {
        out.print("String");
    }

    protected void resultInstatiation(PrintStream out, D x2) {
        out.print("\"");
        out.print(x2.toString());
        out.print("\"");
    }

    protected void argType(PrintStream out) {
        out.print("Predicates");
    }

    protected void varCondition(PrintStream out, D x1) {
        out.print("arg.");
        out.print(x1.readName());
        out.print("()");
    }
}
