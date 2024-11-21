package wendu.dsbridge;

/**
 * @author: admin
 * @date: 2023/4/13
 */
public interface NetworkLisener {

    /**
     * 网络情况监听
     *
     * @param network 是否存在网络
     * @param type    网络类型
     */
    void networkStatus(boolean network, String type);
}
