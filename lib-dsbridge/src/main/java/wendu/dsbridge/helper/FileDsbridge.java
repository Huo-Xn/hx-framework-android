package wendu.dsbridge.helper;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import androidx.annotation.Nullable;

import java.io.File;

import wendu.dsbridge.PriorityBackPressedListener;
import wendu.dsbridge.FileResultCallback;
import wendu.dsbridge.tool.Base64FileTypeEnum;
import wendu.dsbridge.tool.Base64Util;
import wendu.dsbridge.tool.GetFilePathFromUri;
import wendu.dsbridge.tool.GsonUtil;
import wendu.dsbridge.tool.PermissionsManager;
import wendu.dsbridge.view.FilesCheckWindow;

/**
 * 文件处理
 *
 * @author: admin
 * @date: 2023/4/19
 */
public class FileDsbridge implements PriorityBackPressedListener {
    public static final int FILE_RESULT_CODE = 12003;

    private Activity context;
    private PermissionsManager permissionsManager;
    private FileResultCallback resultCallback;
    private FilesCheckWindow mFilesCheckWindow;
    private Handler handler;


    /**
     * 文件响应数据标识
     */
    private int fileResultCode;


    public FileDsbridge(Activity context) {
        this.context = context;
        permissionsManager = new PermissionsManager(context);
        handler = new Handler(Looper.getMainLooper());
        mFilesCheckWindow = new FilesCheckWindow(context);
    }

    public FileDsbridge(Activity context, FileResultCallback resultCallback) {
        this.context = context;
        permissionsManager = new PermissionsManager(context);
        handler = new Handler(Looper.getMainLooper());
        this.resultCallback = resultCallback;
        mFilesCheckWindow = new FilesCheckWindow(context);
    }

    public void setResultCallback(FileResultCallback resultCallback) {
        this.resultCallback = resultCallback;
    }

    /**
     * 打开文件管理器
     *
     * @param obj
     */
    @JavascriptInterface
    public void chooseFile(Object obj) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        if (null != obj) {
            String b = obj.toString();
            if (TextUtils.isEmpty(b)) {
                fileResultCode=FILE_RESULT_CODE;
                context.startActivityForResult(intent, FILE_RESULT_CODE);
            } else {
                try {
                    fileResultCode = Integer.parseInt(b);
                    context.startActivityForResult(intent, fileResultCode);
                } catch (Exception e) {
                    fileResultCode=FILE_RESULT_CODE;
                    context.startActivityForResult(intent, FILE_RESULT_CODE);
                }
            }
        } else {
            fileResultCode=FILE_RESULT_CODE;
            context.startActivityForResult(intent, FILE_RESULT_CODE);
        }

    }


    public void customFileResult(Activity activity, int requestCode, int resultCode, @Nullable Intent data) {
        int yourPhotoResult = fileResultCode;
        /**
         * 自定义 resultcode 文件获取
         */
        if (resultCode == RESULT_OK) {
            if (requestCode == yourPhotoResult) {
                if (null != data) {
                    Uri uri = data.getData();
                    if (null != uri) {
                        try {
                            String fileAbsolutePath = GetFilePathFromUri.getFileAbsolutePath(activity, uri);
                            File file = new File(fileAbsolutePath);
                            if (null != file) {
                                String json = fileProcessing(file, fileAbsolutePath);
                                if (null != resultCallback) {
                                    resultCallback.onFileResult(true, yourPhotoResult, json, "");
                                }
                            }
                        } catch (Exception e) {
                            if (null != resultCallback) {
                                resultCallback.onFileResult(false, yourPhotoResult, "", "获取文件信息失败");
                            }
                        } catch (Throwable e) {
                            if (null != resultCallback) {
                                resultCallback.onFileResult(false, yourPhotoResult, "", "获取文件信息失败,文件过大");
                            }
                        }
                    } else {
                        //进入到这里，说明他进行了多选
                        ClipData clipData = data.getClipData();
                        if (null != clipData) {
                            mFilesCheckWindow.setData(activity, data, new FilesCheckWindow.ItemClick() {
                                @Override
                                public void click(String[] data) {
                                    try {
                                        String json = photo2FileSelection(data);
                                        if (null != resultCallback) {
                                            resultCallback.onFileResult(true, yourPhotoResult, json, "");
                                        }
                                    } catch (Exception e) {
                                        if (null != resultCallback) {
                                            resultCallback.onFileResult(false, yourPhotoResult, "", "获取文件信息失败");
                                        }
                                    } catch (Throwable e) {
                                        if (null != resultCallback) {
                                            resultCallback.onFileResult(false, yourPhotoResult, "", "获取文件信息失败,文件过大");
                                        }
                                    }
                                }
                            });
                            mFilesCheckWindow.show();
                        } else {
                            if (null != resultCallback) {
                                resultCallback.onFileResult(false, yourPhotoResult, "", "获取文件信息失败");
                            }
                        }
                    }
                } else {
                    if (null != resultCallback) {
                        resultCallback.onFileResult(false, yourPhotoResult, "", "获取文件信息失败");
                    }
                }
            }
        }
    }


    /**
     * 对文件进行处理
     */
    public String fileProcessing(File file, String fileAbsolutePath) {
        String base64 = Base64Util.file2Base64(file);
        String[] split = fileAbsolutePath.split("\\.");
        String fileType = "";
        if (null != split && split.length > 1) {
            fileType = split[split.length - 1];
            String fileT = Base64FileTypeEnum.getFileType(fileType);
            base64 = fileT + "," + base64;
        }
        String json = GsonUtil.mapToJson(
                new String[]{"data", base64},
                new String[]{"fileAbsolutePath", fileAbsolutePath},
                new String[]{"fileName", file.getName()},
                new String[]{"fileType", fileType}
        );
        return json;
    }

    /**
     * 对图片进行处理
     */
    public String photo2FileSelection(String[] data) {
        File file = new File(data[0]);
        String base64 = Base64Util.file2Base64(file);
        String fileT = Base64FileTypeEnum.getFileType(data[2]);
        base64 = fileT + "," + base64;
        String json = GsonUtil.mapToJson(
                new String[]{"data", base64},
                new String[]{"fileAbsolutePath", data[0]},
                new String[]{"fileName", file.getName()},
                new String[]{"fileType", data[2]}
        );
        return json;
    }


    @Override
    public boolean onPriorityBackPressed() {
        if (mFilesCheckWindow.isShowing()) {
            mFilesCheckWindow.dismiss();
            return true;
        } else {
            return false;
        }
    }



}
