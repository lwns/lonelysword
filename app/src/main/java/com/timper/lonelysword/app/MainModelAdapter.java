package com.timper.lonelysword.app;

import android.arch.lifecycle.MutableLiveData;
import com.timper.lonelysword.app.data.remote.service.GetUserUseCase;
import com.timper.lonelysword.base.ModelAdapter;
import io.reactivex.observers.DisposableObserver;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * User: tangpeng.yang
 * Date: 29/05/2018
 * Description:
 * FIXME
 */

public class MainModelAdapter extends ModelAdapter {

  GetUserUseCase userUseCase;

  MutableLiveData<List<String>> users = new MutableLiveData<>();

  @Inject public MainModelAdapter(GetUserUseCase userUseCase) {
    this.userUseCase = userUseCase;
  }

  public void getData(){
    //userUseCase.execute(new DisposableObserver<List<String>>() {
    //  @Override public void onNext(List<String> datas) {
    //    //List<String> data = users.getValue() == null ? new ArrayList<>() : users.getValue();
    //    //if (!loadMore) data.clear();
    //    //if (undoneModelListModel.getList() != null) {
    //    //  data.addAll(undoneModelListModel.getList());
    //    //}
    //    //unDoneData.setValue(data);
    //  }
    //
    //  @Override public void onError(Throwable e) {
    //    //super.onError(e);
    //    //unDoneRefresh.setValue(ResultState.ERROR);
    //  }
    //
    //  @Override public void onComplete() {
    //    //unDoneRefresh.setValue(ResultState.COMPLETE);
    //  }
    //}, "");
  }
}
