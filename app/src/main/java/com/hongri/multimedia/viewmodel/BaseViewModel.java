package com.hongri.multimedia.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import org.jetbrains.annotations.NotNull;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @author cpc$
 * @version 1.0
 * @description: TODO
 * @date $ $
 */
public class BaseViewModel extends AndroidViewModel {
    private CompositeDisposable compositeDisposable;

    /**
     * 描述：构造函数接收一个非空的Application对象作为参数，并将其传递给父类构造函数。
     * 这样ViewModel就可以在整个应用生命周期内保持存活，并且可以在多个Activity或Fragment之间共享数据。
     */
    public BaseViewModel(@NonNull @NotNull Application application) {
        super(application);
    }

    /***
     * 这个方法用于执行一个RxJava的Observable流。它接受三个参数：一个待订阅的Observable对象、
     * 一个接收到数据时调用的Consumer（即onNext回调）以及一个当发生错误时调用的Consumer（即onError回调）。
     * 通过.subscribeOn(Schedulers.io())将Observable的执行切换到IO线程池，
     * 然后通过.observeOn(AndroidSchedulers.mainThread())将结果的处理切换回主线程，以确保可以安全地更新UI。最后，返回一个Disposable对象，可用于取消订阅。
     *
     */

    protected <T> Disposable execute(Observable<T> observable, Consumer<? super T> onNext, Consumer<? super Throwable> onError) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError);
    }

    /**
     * 这个方法用于添加一个已创建的Disposable到CompositeDisposable容器中。CompositeDisposable是一个集合类，
     * 它可以方便地管理和统一取消多个Disposable对象。如果compositeDisposable尚未初始化，则先创建一个新的实例。
     */
    protected void add(Disposable disposable) {
        if (this.compositeDisposable == null) {
            this.compositeDisposable = new CompositeDisposable();
        }
        this.compositeDisposable.add(disposable);
    }
//    protected <T> Disposable execute(Observable<T> observable, io.reactivex.rxjava3.functions.Consumer<? super T> onNext, Consumer<? super T>,io.reactivex.rxjava3.functions.Consumer<? super Throwable> onError, Action onComplete){
//        return observable.subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(onNext,onError,onComplete);
//
//    }

    /**
     * onCleared()是AndroidViewModel的一个回调方法，在ViewModel不再被使用并即将销毁时调用。在这里，我们首先调用父类的onCleared()方法，
     * 然后检查compositeDisposable是否非空并且未被dispose。如果是这种情况，就调用clear()方法来取消所有包含的Disposable对象，释放它们占用的资源。
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        if (this.compositeDisposable != null && !compositeDisposable.isDisposed()) {
            this.compositeDisposable.clear();
        }
    }
}
