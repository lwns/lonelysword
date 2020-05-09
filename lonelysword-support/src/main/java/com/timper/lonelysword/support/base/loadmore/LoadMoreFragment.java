package com.timper.lonelysword.support.base.loadmore;

import androidx.databinding.ViewDataBinding;
import com.timper.bindingadapter.action.Command;
import com.timper.bindingadapter.action.ParamCommand;
import com.timper.bindingadapter.collection.itembindings.OnItemBindClass;
import com.timper.lonelysword.support.R;
import com.timper.lonelysword.support.BR;
import com.timper.lonelysword.support.base.BaseFragment;

public abstract class LoadMoreFragment<V extends LoadMoreViewModel, T extends ViewDataBinding> extends BaseFragment<V, T> {

  public int[] colorScheme = new int[] {R.color.lonelysword_support_transparent};

  public Command onRefresh = new Command(() -> {
    viewModel.refresh();
  });

  public ParamCommand<Integer> onLoadMore = new ParamCommand(data -> {
    viewModel.loadMore();
  });

  public Command onRetry = new Command(() -> {
    viewModel.refresh();
  });

  public OnItemBindClass itemBinding = onItemBindClass().map(LoadViewModel.class, BR.viewModel, R.layout.lonelysword_support_item_loadmore);


  abstract public OnItemBindClass onItemBindClass();
}
