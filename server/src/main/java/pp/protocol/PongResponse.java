package pp.protocol;

public class PongResponse {
    private long pong;

    public PongResponse() {
    }

    public PongResponse(long pong) {
        this.pong = pong;
    }

    public long getPong() {
        return pong;
    }

    public void setPong(long pong) {
        this.pong = pong;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PongResponse that = (PongResponse) o;

        if (pong != that.pong) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (pong ^ (pong >>> 32));
    }

    @Override
    public String toString() {
        return "PongResponse{" +
                "pong=" + pong +
                '}';
    }
}
