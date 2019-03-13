package com.timper.lonelysword.dagger;


import androidx.fragment.app.Fragment;
import dagger.MembersInjector;
import dagger.android.DispatchingAndroidInjector;
import javax.annotation.Generated;
import javax.inject.Provider;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class DaggerAppCompatActivity_MembersInjector
  implements MembersInjector<DaggerAppCompatActivity> {
  private final Provider<DispatchingAndroidInjector<Fragment>> supportFragmentInjectorProvider;

  private final Provider<DispatchingAndroidInjector<android.app.Fragment>>
    frameworkFragmentInjectorProvider;

  public DaggerAppCompatActivity_MembersInjector(
    Provider<DispatchingAndroidInjector<Fragment>> supportFragmentInjectorProvider,
    Provider<DispatchingAndroidInjector<android.app.Fragment>>
      frameworkFragmentInjectorProvider) {
    assert supportFragmentInjectorProvider != null;
    this.supportFragmentInjectorProvider = supportFragmentInjectorProvider;
    assert frameworkFragmentInjectorProvider != null;
    this.frameworkFragmentInjectorProvider = frameworkFragmentInjectorProvider;
  }

  public static MembersInjector<DaggerAppCompatActivity> create(
    Provider<DispatchingAndroidInjector<Fragment>> supportFragmentInjectorProvider,
    Provider<DispatchingAndroidInjector<android.app.Fragment>>
      frameworkFragmentInjectorProvider) {
    return new DaggerAppCompatActivity_MembersInjector(
      supportFragmentInjectorProvider, frameworkFragmentInjectorProvider);
  }

  @Override
  public void injectMembers(DaggerAppCompatActivity instance) {
    if (instance == null) {
      throw new NullPointerException("Cannot inject members into a null reference");
    }
    instance.supportFragmentInjector = supportFragmentInjectorProvider.get();
    instance.frameworkFragmentInjector = frameworkFragmentInjectorProvider.get();
  }

  public static void injectSupportFragmentInjector(
    DaggerAppCompatActivity instance,
    Provider<DispatchingAndroidInjector<Fragment>> supportFragmentInjectorProvider) {
    instance.supportFragmentInjector = supportFragmentInjectorProvider.get();
  }

  public static void injectFrameworkFragmentInjector(
    DaggerAppCompatActivity instance,
    Provider<DispatchingAndroidInjector<android.app.Fragment>>
      frameworkFragmentInjectorProvider) {
    instance.frameworkFragmentInjector = frameworkFragmentInjectorProvider.get();
  }
}
