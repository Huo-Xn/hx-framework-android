package wendu.dsbridge;

/**
 * @author: admin
 * @date: 2022/7/26
 *
 * 主题风格样式
 * 可以查看说明文档查看对应的效果呈现
 *
 */
public interface ThemeStyle {

    /**
     * 默认效果
     */
    int DEFAULT = 0;

    /**
     * 视频模式，顶部状态栏和底部导航栏全部都存在，但是都会允许内容通过
     */
    int VEDIO = 4;

    /**
     * 底部导航栏存在。顶部状态栏 存在，但是会允许内容透过。
     */
    int TRANSLUCENTSTATUS = 3;


}
