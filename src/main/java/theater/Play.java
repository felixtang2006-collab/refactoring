package theater;

/**
 * Represents a play with a name and a type.
 *
 * @nonnull name
 * @nonnull type
 */
public class Play {

    private String name;
    private String type;

    public Play(String name, String type) {

        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
