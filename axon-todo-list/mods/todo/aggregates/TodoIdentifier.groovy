package aggregates

import org.axonframework.common.Assert
import org.axonframework.domain.IdentifierFactory
/**
 * The Identifier for the todoItems
 *
 * @author Jettro Coenradie
 */
class TodoIdentifier implements Serializable {
    String identifier

    TodoIdentifier() {
        this.identifier = IdentifierFactory.getInstance().generateIdentifier();
    }

    TodoIdentifier(String identifier) {
        Assert.notNull(identifier, "Identifier may not be null");
        this.identifier = identifier
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        TodoIdentifier that = (TodoIdentifier) o

        if (identifier != that.identifier) return false

        return true
    }

    int hashCode() {
        return (identifier != null ? identifier.hashCode() : 0)
    }

    public String asString() {
        return identifier;
    }

    @Override
    public String toString() {
        return "TodoIdentifier{" +
                "identifier='" + identifier + '\'' +
                '}';
    }
}