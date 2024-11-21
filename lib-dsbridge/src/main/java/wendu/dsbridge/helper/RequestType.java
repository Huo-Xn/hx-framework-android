package wendu.dsbridge.helper;

/**
 * @author: admin
 * @date: 2023/4/13
 */
public enum RequestType {

    /**
     * Post 请求，type code为 1
     */
    GET(0), POST(1);

    private int type;

    RequestType(int priority) {
        this.type = priority;
    }

    public int getType() {
        return type;
    }

    

}
