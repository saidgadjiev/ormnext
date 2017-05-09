package field;

/**
 * Created by said on 26.02.17.
 */
public enum DataType {
    STRING("varchar(255)"),
    INTEGER("INTEGER"),
    LONG("INTEGER"),
    UNKNOWN("");

    private final String type;

    DataType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
