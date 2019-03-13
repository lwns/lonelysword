package com.timper.lonelysword.dagger;

import androidx.fragment.app.Fragment;
import dagger.MembersInjector;
import dagger.android.DispatchingAndroidInjector;
import javax.annotation.Generated;
import javax.inject.Provider;

/**
 * User: tangpeng.yang
 * Date: 2019/3/12
 * Description:
 * FIXME
 */
@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class DaggerFragment_MembersInjector implements MembersInjector<DaggerFragment> {
  private final Provider<DispatchingAndroidInjector<Fragment>> childFragmentInjectorProvider;

  public DaggerFragment_MembersInjector(
    Provider<DispatchingAndroidInjector<Fragment>> childFragmentInjectorProvider) {
    assert childFragmentInjectorProvider != null;
    this.childFragmentInjectorProvider = childFragmentInjectorProvider;
  }

  public static MembersInjector<DaggerFragment> create(
    Provider<DispatchingAndroidInjector<Fragment>> childFragmentInjectorProvider) {
    return new DaggerFragment_MembersInjector(childFragmentInjectorProvider);
  }

  @Override
  public void injectMembers(DaggerFragment instance) {
    if (instance == null) {
      throw new NullPointerException("Cannot inject members into a null reference");
    }
    instance.childFragmentInjector = childFragmentInjectorProvider.get();
  }

  public static void injectChildFragmentInjector(
    DaggerFragment instance,
    Provider<DispatchingAndroidInjector<Fragment>> childFragmentInjectorProvider) {
    instance.childFragmentInjector = childFragmentInjectorProvider.get();
  }
}
