package Utils;

public class Packet {
    private byte[] message;
    private int noMessage;
    private int state;

    public Packet(byte[] message, int noMessage, int state) {
        this.message = message;
        this.noMessage = noMessage;
        this.state = state;
    }

    public byte[] getMessage() {
        return message;
    }

    public int getNoMessage() {
        return noMessage;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }
}
