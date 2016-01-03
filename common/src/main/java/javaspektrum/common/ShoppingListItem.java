package javaspektrum.common;

public class ShoppingListItem {

    private long id;
    private String name;
    private boolean bought;

    public ShoppingListItem() {

    }

    public ShoppingListItem(String initName, boolean bought) {
        this.name = initName;
        this.bought = bought;
    }

    public ShoppingListItem(long id, String initName, boolean bought) {
        this(initName, bought);
        this.id = id;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ShoppingListItem that = (ShoppingListItem) o;

        if (id != that.id) return false;
        if (bought != that.bought) return false;
        return !(name != null ? !name.equals(that.name) : that.name != null);
    }

    @Override
    public int hashCode() {
        long result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (bought ? 1 : 0);
        return (int)result;
    }
}
