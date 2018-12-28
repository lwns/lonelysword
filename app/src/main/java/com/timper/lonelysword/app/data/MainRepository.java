package com.timper.lonelysword.app.data;

import com.timper.lonelysword.annotations.apt.UseCase;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * User: tangpeng.yang
 * Date: 31/05/2018
 * Description:
 * FIXME
 */
public interface MainRepository {
  @UseCase Flowable<String> getUser(String hellow);

  @UseCase Completable getUsers(String hellow);
}
