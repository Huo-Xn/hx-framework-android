package wendu.dsbridge;

/**
 * @author: admin
 * @date: 2023/4/13
 */
public interface ResultCallback {


    /**
     * 成功返回
     *
     * @param suc
     * @param code
     * @param data
     */
    void results(boolean suc, int code,String data);

    /**
     * 发生了错误
     */
    void error(String err);


}
