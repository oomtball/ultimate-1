package info.scce.addlib.gencodegenerator.checks;

import info.scce.addlib.gencodegenerator.parser.ast.TextListRoot;

import java.util.ArrayList;
import java.util.List;

public abstract class Check {

    private List<String> errors = new ArrayList<String>();

    public List<String> getErrors() {
        return errors;
    }

    protected void addError(String msg) {
        getErrors().add(msg);
    }

    public abstract void check(TextListRoot textListRoot);
}
