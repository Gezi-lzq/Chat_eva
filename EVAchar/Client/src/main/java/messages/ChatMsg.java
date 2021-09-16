package messages;

import java.io.Serializable;

public class ChatMsg implements Serializable {
    private int type;//0-文本 1-图片
    private String msg_content;
    private String from_ID;
    private String to_ID;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsg_content() {
        return msg_content;
    }

    public void setMsg_content(String msg_content) {
        this.msg_content = msg_content;
    }

    public String getFrom_ID() {
        return from_ID;
    }

    public void setFrom_ID(String from_ID) {
        this.from_ID = from_ID;
    }

    public String getTo_ID() {
        return to_ID;
    }

    public void setTo_ID(String to_ID) {
        this.to_ID = to_ID;
    }
}
