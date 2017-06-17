package clause.element;

/**
 * Created by said on 17.06.17.
 */
public class StringLiteral extends Literal<String> {
    private String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
