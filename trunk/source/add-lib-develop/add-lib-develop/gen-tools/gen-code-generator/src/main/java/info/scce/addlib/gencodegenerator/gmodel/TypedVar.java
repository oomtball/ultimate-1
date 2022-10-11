package info.scce.addlib.gencodegenerator.gmodel;

public class TypedVar {

    private String type;
    private String name;

    public TypedVar(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return (type == null ? 0 : type.hashCode()) + (name == null ? 0 : name.hashCode());
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof TypedVar) {
            TypedVar otherTypedVariable = (TypedVar) other;
            return (type == otherTypedVariable.type || type.equals(otherTypedVariable.type))
                    && (name == otherTypedVariable.name || name.equals(otherTypedVariable.name));
        }
        return false;
    }
}
