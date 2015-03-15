package pp.model;

import org.jmmo.sc.annotation.Key;

public class User {
    @Key
    private String id;
    private long pings;

    public User() {
    }

    public User(String id) {
        this.id = id;
    }

    public User(String id, long pings) {
        this.id = id;
        this.pings = pings;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getPings() {
        return pings;
    }

    public void setPings(long pings) {
        this.pings = pings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (pings != user.pings) return false;
        if (!id.equals(user.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (int) (pings ^ (pings >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", pings=" + pings +
                '}';
    }
}
