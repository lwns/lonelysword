package com.timper.lonelysword.data;

import com.timper.lonelysword.data.executor.PostExecutionThread;
import com.timper.lonelysword.data.executor.ThreadExecutor;
import io.reactivex.disposables.CompositeDisposable;

/**
 * User: tangpeng.yang
 * Date: 2019-08-06
 * Description:
 * FIXME
 */
public abstract class UseCase<T, Params> {

    protected final ThreadExecutor threadExecutor;
    protected final PostExecutionThread postExecutionThread;
    protected final CompositeDisposable disposables;

    public UseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        this.threadExecutor = threadExecutor;
        this.postExecutionThread = postExecutionThread;
        this.disposables = new CompositeDisposable();
    }

    /**
     * Builds an which will be used when executing the current.
     *
     * @param params usecase params
     * @return data return observable
     */
    protected abstract T buildUseCaseObservable(Params params);

    /**
     * Executes the current use case.
     *
     * @param params Parameters (Optional) used to build/execute this use case.
     * @return Flowable
     */
    public T execute(Params params) {
        return this.buildUseCaseObservable(params);
    }
}
