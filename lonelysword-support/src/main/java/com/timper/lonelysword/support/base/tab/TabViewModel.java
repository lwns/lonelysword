package com.timper.lonelysword.support.base.tab;

import android.databinding.ObservableField;
import com.timper.lonelysword.ActivityScope;
import com.timper.lonelysword.base.AppViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * User:
 * Date:
 * Description:
 * FIXME
 */
@ActivityScope
public class TabViewModel extends AppViewModel {

  public final ObservableField<Integer> selectPosition = new ObservableField<>(0);

  public final List<String> titles = new ArrayList<>();

  public final ObservableField<Integer> pageLimit = new ObservableField<>(0);

}