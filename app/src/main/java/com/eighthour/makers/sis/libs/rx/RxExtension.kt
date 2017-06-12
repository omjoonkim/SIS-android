package com.eighthour.makers.sis.libs.rx

import com.eighthour.makers.sis.libs.util.Quardruple
import io.reactivex.*
import io.reactivex.functions.Function4
import io.reactivex.subjects.Subject

/**
 * Created by Omjoon on 2017. 5. 29..
 */

enum class Parameter {
    CLICK, NULL, SUCCESS, EVENT
}

fun <T> Observable<T>.handleToError(action: Subject<Throwable>? = null): Observable<T> = doOnError { action?.onNext(it) }
fun <T> Observable<T>.neverError(): Observable<T> = onErrorResumeNext { _: Throwable -> Observable.empty() }
fun <T> Observable<T>.neverError(action: Subject<Throwable>? = null): Observable<T> = handleToError(action).neverError()

fun <T> Single<T>.handleToError(action: Subject<Throwable>?): Single<T> = doOnError { action?.onNext(it) }
fun <T> Single<T>.neverError(): Maybe<T> = toMaybe().neverError()
fun <T> Single<T>.neverError(action: Subject<Throwable>? = null): Maybe<T>? = handleToError(action).neverError()

fun <T> Maybe<T>.handleToError(action: Subject<Throwable>? = null): Maybe<T> = doOnError { action?.onNext(it) }
fun <T> Maybe<T>.neverError(): Maybe<T> = onErrorResumeNext(onErrorComplete())
fun <T> Maybe<T>.neverError(action: Subject<Throwable>? = null): Maybe<T>? = handleToError(action).neverError()

fun Completable.handleToError(action: Subject<Throwable>? = null): Completable = doOnError { action?.onNext(it) }
fun Completable.neverError(): Completable = onErrorResumeNext { it.printStackTrace();Completable.never() }
fun Completable.neverError(action: Subject<Throwable>? = null): Completable = handleToError(action).neverError()

fun <T> Flowable<T>.handleToError(action: Subject<Throwable>? = null): Flowable<T> = doOnError { action?.onNext(it) }
fun <T> Flowable<T>.neverError(): Flowable<T> = onErrorResumeNext { _: Throwable -> Flowable.empty() }
fun <T> Flowable<T>.neverError(action: Subject<Throwable>? = null): Flowable<T> = handleToError(action).neverError()


fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any> Observable<T1>.combineLatest(observable1: Observable<T2>
                                                                          , observable2: Observable<T3>
                                                                          , observable3: Observable<T4>): Observable<Quardruple<T1, T2, T3, T4>>
        = Observable.combineLatest(this, observable1, observable2, observable3, Function4 { p1, p2, p3, p4 -> Quardruple<T1, T2, T3, T4>(p1, p2, p3, p4) })
