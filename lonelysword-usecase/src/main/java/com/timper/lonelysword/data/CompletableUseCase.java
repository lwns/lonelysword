package com.timper.lonelysword.data;

import com.timper.lonelysword.data.executor.PostExecutionThread;
import com.timper.lonelysword.data.executor.ThreadExecutor;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * User: tangpeng.yang
 * Date: 19/09/2018
 * Description:
 * FIXME
 */
public abstract class CompletableUseCase<Params> {

    protected final ThreadExecutor threadExecutor;
    protected final PostExecutionThread postExecutionThread;

    public CompletableUseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        this.threadExecutor = threadExecutor;
        this.postExecutionThread = postExecutionThread;
    }

    /**
     * Builds an {@link Completable} which will be used when executing the current {@link CompletableUseCase}.
     *
     * @param params usecase params
     * @return data return observable
     */
    protected abstract Completable buildUseCaseObservable(Params params);

    /**
     * Executes the current use case.
     *
     * @param params Parameters (Optional) used to build/execute this use case.
     * @return Completable
     */
    public Completable execute(Params params) {
        return this.buildUseCaseObservable(params)
                .subscribeOn(Schedulers.from(threadExecutor))
                .observeOn(postExecutionThread.getScheduler());
    }
}
