package wendu.dsbridge.helper;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import wendu.dsbridge.base.BaseFm;
import wendu.dsbridge.fragment.WebViewFragment;

/**
 * @description:
 * @author: ash
 * @date : 2022/4/1 15:30
 * @email : ash_945@126.com
 */
public class FragmentViewModel extends AndroidViewModel {


    /**
     * 根据流程实例id，获取已完成的流程节点信息
     */
    public MutableLiveData<WebViewFragment> fragmentModel;

    public MutableLiveData<WebViewFragment> getFragmentModel() {
        return fragmentModel;
    }

    public FragmentViewModel(@NonNull Application application) {
        super(application);
        fragmentModel = new MutableLiveData<>();
    }


}
