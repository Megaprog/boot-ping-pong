package pp.protocol;

public class JsonRequest {
    private String command;

    public JsonRequest() {
    }

    public JsonRequest(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JsonRequest that = (JsonRequest) o;

        if (!command.equals(that.command)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return command.hashCode();
    }

    @Override
    public String toString() {
        return "JsonRequest{" +
                "command='" + command + '\'' +
                '}';
    }
}
