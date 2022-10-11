package info.scce.addlib.gencodegenerator.gmodel;

public class Param {

    private String name;
    private String defaultValue;

    public Param(String name, String defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public int hashCode() {
        return (name == null ? 0 : name.hashCode()) + (defaultValue == null ? 0 : defaultValue.hashCode());
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Param) {
            Param otherParam = (Param) other;
            return (name == otherParam.name || name.equals(otherParam.name))
                    && (defaultValue == otherParam.defaultValue || defaultValue.equals(otherParam.defaultValue));
        }
        return false;
    }
}
