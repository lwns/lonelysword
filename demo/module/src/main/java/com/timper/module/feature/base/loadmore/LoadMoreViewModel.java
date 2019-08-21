package com.timper.module.feature.base.loadmore;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableField;
import android.databinding.ObservableList;
import com.timper.bindingadapter.collection.collections.MergeObservableList;
import com.timper.bindingadapter.recyclerview.LayoutManagers;
import com.timper.lonelysword.base.AppViewModel;
import com.timper.view.EmptyLayout;
import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

/**
 * User:
 * Date:
 * Description:
 * FIXME
 */
public abstract class LoadMoreViewModel<T> extends AppViewModel {

  protected static final int START_PAGENUM = 1;//如果首页0或者1

  protected int pageSize = 10;

  protected int pageNum = START_PAGENUM;

  protected boolean loadMore = false;//控制是否添加loadmore item项

  protected boolean needLoadMore = needLoadMore();

  public ObservableField<EmptyLayout.Status> state = new ObservableField<>();

  public ObservableField<Boolean> refresh = new ObservableField<>(false);

  public LayoutManagers.LayoutManagerFactory layoutManager;

  public LoadViewModel loadViewModel = new LoadViewModel();

  private ObservableList<T> observableList = new ObservableArrayList<>();

  public List<T> list = new ArrayList();

  public final MergeObservableList<Object> datas = new MergeObservableList<>().insertList(observableList);

  protected void notifyData(boolean error) {
    refresh.set(false);
    if (pageNum == START_PAGENUM && datas.size() < pageSize) {
      if (loadMore) {
        datas.removeItem(loadViewModel);
      }
      loadMore = false;
      loadViewModel.setStatus(LoadViewModel.Status.NOMORE);
    } else if (datas.size() / getPageNum() < pageSize) {
      loadViewModel.setStatus(LoadViewModel.Status.LOADCOMPLATE);
    } else {
      if (!loadMore && needLoadMore) {
        datas.insertItem(loadViewModel);
        loadMore = true;
      }
      loadViewModel.setStatus(LoadViewModel.Status.LOADMORE);
    }

    if (error) {
      state.set(EmptyLayout.Status.EMPTY);
    } else if (datas.size() == 0) {
      state.set(EmptyLayout.Status.EMPTY);
    } else {
      state.set(EmptyLayout.Status.NONE);
    }
  }

  private int getPageNum() {
    return START_PAGENUM == 0 ? pageNum + 1 : pageNum;
  }

  @Override
  public void afterViews() {
    super.afterViews();
    refresh();
  }

  public void refresh() {
    refresh.set(true);
    pageNum = START_PAGENUM;

    if (refreshData() != null){
      refreshData().subscribe(new LoadMoreSubscriber(customSubscriber()));
    }

    //refreshData().subscribe(data -> {
    //  observableList.clear();
    //  observableList.addAll(data);
    //}, error -> notifyData(true), () -> notifyData(false));
  }

  public void loadMore() {
    if (needLoadMore && loadMore) {
      pageNum++;
      if (loadMoreData()!=null){
        loadMoreData().subscribe(new LoadMoreSubscriber(customSubscriber()));
      }
      //loadMoreData().subscribe(data -> {
      //  observableList.addAll(data);
      //}, error -> notifyData(true), () -> notifyData(false));
    }
  }

  public boolean needLoadMore() {
    return true;
  }

  public FlowableSubscriber<List<T>> customSubscriber() {
    return null;
  }

  public Flowable<List<T>> refreshData() {
    return null;
  }

  public Flowable<List<T>> loadMoreData() {
    return null;
  }

  class LoadMoreSubscriber implements FlowableSubscriber<List<T>> {

    FlowableSubscriber flowableSubscriber;

    public LoadMoreSubscriber(FlowableSubscriber flowableSubscriber) {
      this.flowableSubscriber = flowableSubscriber;
    }

    @Override
    public void onSubscribe(Subscription s) {
      if (flowableSubscriber != null) {
        flowableSubscriber.onSubscribe(s);
      }
    }

    @Override
    public void onNext(List<T> t) {
      if (flowableSubscriber != null) {
        flowableSubscriber.onNext(t);
      }
      if (refresh.get()) {
        observableList.clear();
      }
      observableList.addAll(t);
    }

    @Override
    public void onError(Throwable t) {
      if (flowableSubscriber != null) {
        flowableSubscriber.onError(t);
      }
      notifyData(true);
    }

    @Override
    public void onComplete() {
      if (flowableSubscriber != null) {
        flowableSubscriber.onComplete();
      }
      notifyData(false);
    }
  }
}

