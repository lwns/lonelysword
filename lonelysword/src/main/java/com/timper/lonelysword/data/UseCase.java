/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.timper.lonelysword.data;

import com.timper.lonelysword.data.executor.PostExecutionThread;
import com.timper.lonelysword.data.executor.ThreadExecutor;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * User: tangpeng.yang
 * Date: 13/03/2018
 * Description: Abstract class for a Use Case (Interactor in terms of Clean Architecture).
 * This interface represents a execution unit for different use cases (this means any use case
 * in the application should implement this contract).
 *
 * By convention each UseCase implementation will return the result using a {@link DisposableObserver}
 * that will execute its job in a background thread and will post the result in the UI thread.
 * FIXME
 */
public abstract class UseCase<T, Params> {

  private final ThreadExecutor threadExecutor;
  private final PostExecutionThread postExecutionThread;
  private final CompositeDisposable disposables;

  public UseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
    this.threadExecutor = threadExecutor;
    this.postExecutionThread = postExecutionThread;
    this.disposables = new CompositeDisposable();
  }

  /**
   * Builds an {@link Observable} which will be used when executing the current {@link UseCase}.
   * @param params usecase params
   * @return data return observable
   */
  protected abstract Observable<T> buildUseCaseObservable(Params params);

  /**
   * Executes the current use case.
   *
   * @param observer {@link DisposableObserver} which will be listening to the observable build
   * @param params Parameters (Optional) used to build/execute this use case.
   */
  public void execute(DisposableObserver<T> observer, Params params) {
    //Preconditions.checkNotNull(observer);
    final Observable<T> observable = this.buildUseCaseObservable(params)
        .subscribeOn(Schedulers.from(threadExecutor))
        .observeOn(postExecutionThread.getScheduler());
    addDisposable(observable.subscribeWith(observer));
  }

  public Observable<T> execute(Params params) {
    return this.buildUseCaseObservable(params)
        .subscribeOn(Schedulers.from(threadExecutor))
        .observeOn(postExecutionThread.getScheduler());
  }

  /**
   * Dispose from current {@link CompositeDisposable}.
   */
  public void dispose() {
    if (!disposables.isDisposed()) {
      disposables.dispose();
    }
  }

  /**
   * Dispose from current {@link CompositeDisposable}.
   */
  private void addDisposable(Disposable disposable) {
    //Preconditions.checkNotNull(disposable);
    //Preconditions.checkNotNull(disposables);
    disposables.add(disposable);
  }
}
