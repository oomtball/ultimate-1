package info.scce.addlib.gencodegenerator.gmodel;

import info.scce.addlib.gencodegenerator.parser.ast.TextList;

import java.util.List;

public class InjectableMethod {

    private String name;
    private List<TypedVar> paramList;
    private TextList body;

    public InjectableMethod(String name, List<TypedVar> paramList, TextList body) {
        this.name = name;
        this.paramList = paramList;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public List<TypedVar> getParamList() {
        return paramList;
    }

    public TextList getBody() {
        return body;
    }

    @Override
    public int hashCode() {
        return (name == null ? 0 : name.hashCode()) + (paramList == null ? 0 : paramList.hashCode());
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof InjectableMethod) {
            InjectableMethod otherInjectableMethod = (InjectableMethod) other;
            return (name == otherInjectableMethod.name || name.equals(otherInjectableMethod.name))
                    && (paramList == otherInjectableMethod.paramList
                    || paramList.equals(otherInjectableMethod.paramList));
        }
        return false;
    }
}
