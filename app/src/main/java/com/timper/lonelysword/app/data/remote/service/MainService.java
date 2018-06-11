package com.timper.lonelysword.app.data.remote.service;

import com.timper.lonelysword.annotations.apt.UseCase;
import io.reactivex.Observable;

/**
 * User: tangpeng.yang
 * Date: 04/06/2018
 * Description:
 * FIXME
 */
public interface MainService {

  Observable<String> getUser(String hellow);
}
