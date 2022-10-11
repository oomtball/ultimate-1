package info.scce.addlib.gencodegenerator.gmodel;

public class EnableableParam {

    private String name;

    public EnableableParam(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return (name == null ? 0 : name.hashCode());
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof EnableableParam) {
            EnableableParam otherParam = (EnableableParam) other;
            return (name == otherParam.name || name.equals(otherParam.name));
        }
        return false;
    }
}
