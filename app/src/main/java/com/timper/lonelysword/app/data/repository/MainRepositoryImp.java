package com.timper.lonelysword.app.data.repository;

import com.timper.lonelysword.app.data.MainRepository;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import javax.inject.Inject;

/**
 * User: tangpeng.yang
 * Date: 04/06/2018
 * Description:
 * FIXME
 */
public class MainRepositoryImp implements MainRepository {
  @Inject public MainRepositoryImp() {
  }

  @Override public Flowable<String> getUser(String hellow) {
    return null;
  }

  @Override public Completable getUsers(String hellow) {
    return null;
  }
}
