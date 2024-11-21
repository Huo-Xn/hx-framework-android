package wendu.dsbridge.helper;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import androidx.annotation.Nullable;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import wendu.dsbridge.PermissionsCallback;
import wendu.dsbridge.PhotoResultCallback;
import wendu.dsbridge.tool.Base64FileTypeEnum;
import wendu.dsbridge.tool.Base64Util;
import wendu.dsbridge.tool.GlideEngine;
import wendu.dsbridge.tool.GsonUtil;
import wendu.dsbridge.tool.PermissionsManager;

/**
 *
 *  图片处理
 *
 * @author: admin
 * @date: 2023/4/19
 */
public class PhotoDsbridge {


    private Activity context;
    private PermissionsManager permissionsManager;
    private PhotoResultCallback resultCallback;
    private Handler handler;
    /**
     * 图片响应数据标识
     */
    private int photoResult;

    public PhotoDsbridge(Activity context) {
        this.context = context;
        permissionsManager = new PermissionsManager(context);
        handler = new Handler(Looper.getMainLooper());
    }

    public PhotoDsbridge(Activity context,PhotoResultCallback resultCallback) {
        this.context = context;
        permissionsManager = new PermissionsManager(context);
        handler = new Handler(Looper.getMainLooper());
        this.resultCallback = resultCallback;
    }

    public void setResultCallback(PhotoResultCallback resultCallback) {
        this.resultCallback = resultCallback;
    }

    /**
     * 打开相册
     *
     * @param obj
     */
    @JavascriptInterface
    public void openPhotoalbum(Object obj) {
        if (null != obj) {
            String type = obj.toString();
            if (!TextUtils.isEmpty(type)) {
                try {
                    int limit = Integer.parseInt(type);
                    if (limit < 1) {
                        limit = 0;
                    }
                    if (limit > 9) {
                        limit = 9;
                    }
                    photoResult = PictureConfig.CHOOSE_REQUEST;
                    openPhotoAlbumFunction(limit, photoResult );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 打开相册
     *
     * @param obj
     */
    @JavascriptInterface
    public void openPhoto(Object obj) {
        if (null != obj) {
            Map<String, String> map = GsonUtil.toMap(obj.toString());
            String limitStr = map.get("limit");
            String code = map.get("code");
            try {
                if (!TextUtils.isEmpty(limitStr)) {
                    int limit = Integer.parseInt(limitStr);
                    if (limit < 1) {
                        limit = 0;
                    }
                    if (limit > 9) {
                        limit = 9;
                    }
                    photoResult = Integer.parseInt(code);
                    openPhotoAlbumFunction(limit, photoResult);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * 打开相机
     *
     * @param obj
     */
    @JavascriptInterface
    public void openCamera(Object obj) {
        openCameraFunction();
    }



    /**
     * 打开相册
     * todo:还没有写权限
     *
     * @param limit
     */
    public void openPhotoAlbumFunction(int limit, int code) {
        permissionsManager.camera(new PermissionsCallback() {
            @Override
            public void granted() {
                photoResult = code;
                handler.post(() -> {
                    PictureSelector
                            .create(context)
                            .openGallery(PictureMimeType.ofImage())
                            .imageEngine(GlideEngine.createGlideEngine())
                            .selectionMode(PictureConfig.MULTIPLE)
                            .maxSelectNum(limit)//最大选择数量,默认9张
                            .isPreviewImage(true)
                            .isCamera(true)
                            .forResult(code);
                });
            }
        });


    }

    /**
     * 打开相机
     * todo:还没有写权限
     *
     * @param
     */
    public void openCameraFunction() {
        permissionsManager.camera(new PermissionsCallback() {
            @Override
            public void granted() {
                handler.post(() -> {
                    PictureSelector
                            .create(context)
                            .openCamera(PictureMimeType.ofImage())
                            .imageEngine(GlideEngine.createGlideEngine())
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                });
            }
        });
    }


    /**
     * 前端通过指定的方法，传递指定的 result的值来区分 回调时使用哪一个方法
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void customPhotoResult( int requestCode, int resultCode, @Nullable Intent data) {
        int yourPhotoResult = photoResult;
        /**
         * 处理相册，相机
         */
        if (requestCode == yourPhotoResult) {
            ArrayList<FileSelection> fileList = new ArrayList<>();
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    try {
                        List<LocalMedia> result = PictureSelector.obtainMultipleResult(data);
                        if (null != result && result.size() > 0) {
                            for (int i = 0; i < result.size(); i++) {
                                FileSelection fileSelection = photo2FileSelection(result.get(i));
                                fileList.add(fileSelection);
                            }
                            //todo:拿到了文件了
                            if (null != resultCallback) {
                                resultCallback.onPhotoResult(true,yourPhotoResult ,GsonUtil.toJson(fileList),"");
                            }
                        }
                    } catch (Exception e) {
                        if (null != resultCallback) {
                            resultCallback.onPhotoResult(false,yourPhotoResult ,GsonUtil.toJson(fileList),"获取文件信息失败");
                        }
                    } catch (Throwable e) {
                        if (null != resultCallback) {
                            resultCallback.onPhotoResult(false,yourPhotoResult ,GsonUtil.toJson(fileList),"获取文件失败，文件过大");
                        }
                    }
                }
            } else {
                //todo:空的 或者是点了取消
                if (null != resultCallback) {
                    resultCallback.onPhotoResult(true,yourPhotoResult ,null,"");
                }
            }
        }
    }



    /**
     * 对图片进行处理
     */
    public FileSelection photo2FileSelection(LocalMedia localMedia) {
        File file = new File(localMedia.getRealPath());
        String base64 = Base64Util.file2Base64(file);
        FileSelection fileSelection = new FileSelection(file.getAbsolutePath(), file.getName());
        String[] split = file.getAbsolutePath().split("\\.");
        String fileType = "";
        if (null != split && split.length > 1) {
            fileType = split[split.length - 1];
            String fileT = Base64FileTypeEnum.getFileType(fileType);
            fileSelection.setFileType(fileType);
            base64 = fileT + "," + base64;
        }
        fileSelection.setBase64(base64);
        fileSelection.setBase64(base64);
        fileSelection.setFilePath(file.getAbsolutePath());
        fileSelection.setName(file.getName());

        return fileSelection;
    }

}
