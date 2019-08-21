package com.timper.module.feature.base.loadmore;

import android.databinding.ViewDataBinding;
import com.timper.bindingadapter.action.Command;
import com.timper.bindingadapter.action.ParamCommand;
import com.timper.bindingadapter.collection.itembindings.OnItemBindClass;
import com.timper.lonelysword.base.AppFragment;
import com.timper.module.R;
import com.timper.module.BR;
import com.timper.view.EmptyLayout;

public abstract class LoadMoreFragment<V extends LoadMoreViewModel, T extends ViewDataBinding> extends AppFragment<V, T> {

  public int[] colorScheme = new int[] {R.color.module_colorPrimary};

  public Command onRefresh = new Command(() -> {
    viewModel.refresh();
  });

  public ParamCommand<Integer> onLoadMore = new ParamCommand(data -> {
    viewModel.loadMore();
  });

  public Command onRetry = new Command(() -> {
    viewModel.refresh();
  });

  public OnItemBindClass itemBinding = onItemBindClass().map(LoadViewModel.class, BR.viewModel, R.layout.item_loadmore);

  public void setEmptyInfo(String info){
    EmptyLayout emptyLayout = this.view.findViewById(R.id.empty_layout);
    emptyLayout.setInfo(info);
  }

  public void setEmptyTop(int height){
    EmptyLayout emptyLayout = this.view.findViewById(R.id.empty_layout);
    emptyLayout.setOffHeight(height);
  }

  abstract public OnItemBindClass onItemBindClass();
}
