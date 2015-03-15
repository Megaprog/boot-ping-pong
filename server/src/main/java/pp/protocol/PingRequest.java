package pp.protocol;

public class PingRequest extends JsonRequest {
    public static final String COMMAND = "PING";

    private String userId;

    public PingRequest() {
        super(COMMAND);
    }

    public PingRequest(String userId) {
        this();
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PingRequest that = (PingRequest) o;

        if (!userId.equals(that.userId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + userId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PingRequest{" +
                "type='" + getCommand() + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
