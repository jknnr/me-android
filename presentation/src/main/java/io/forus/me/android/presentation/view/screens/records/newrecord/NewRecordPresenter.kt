package io.forus.me.android.presentation.view.screens.records.newrecord

import com.ocrv.ekasui.mrm.ui.loadRefresh.LRPartialChange
import com.ocrv.ekasui.mrm.ui.loadRefresh.LRPresenter
import com.ocrv.ekasui.mrm.ui.loadRefresh.LRViewState
import com.ocrv.ekasui.mrm.ui.loadRefresh.PartialChange
import io.forus.me.android.domain.models.records.NewRecordRequest
import io.forus.me.android.domain.repository.records.RecordsRepository
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class NewRecordPresenter constructor(private val recordRepository: RecordsRepository) : LRPresenter<NewRecordRequest, NewRecordModel, NewRecordView>() {


    override fun initialModelSingle(): Single<NewRecordRequest> = Single.just(NewRecordRequest())
            //.delay(1, TimeUnit.SECONDS)
            .flatMap {  Single.just(it)  }


    override fun NewRecordModel.changeInitialModel(i: NewRecordRequest): NewRecordModel = copy(item = i)


    override fun bindIntents() {

        var observable = Observable.merge(

                loadRefreshPartialChanges(),
                intent { it.createRecord() }
                        .switchMap {
                            recordRepository.newRecord(it)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .map<PartialChange> {
                                        NewRecordPartialChanges.CreateRecordEnd(it)
                                    }
                                    .onErrorReturn {
                                        LRPartialChange.LoadingError(it)
                                    }
                                    .startWith(NewRecordPartialChanges.CreateRecordStart(it))
                        }
        );


        val initialViewState = LRViewState(
                false,
                null,
                false,
                false,
                null,
                false,
                NewRecordModel())

        subscribeViewState(
                observable.scan(initialViewState, this::stateReducer)
                        .observeOn(AndroidSchedulers.mainThread()),
                NewRecordView::render)

//        val observable = loadRefreshPartialChanges()
//        val initialViewState = LRViewState(false, null, false, false, null, MapModel("", "" ))
//        subscribeViewState(observable.scan(initialViewState, this::stateReducer).observeOn(AndroidSchedulers.mainThread()),MapView::render)
    }

    override fun stateReducer(viewState: LRViewState<NewRecordModel>, change: PartialChange): LRViewState<NewRecordModel> {

        if (change !is NewRecordPartialChanges) return super.stateReducer(viewState, change)

        return when (change) {
            is NewRecordPartialChanges.CreateRecordEnd -> viewState.copy(closeScreen = true, model = viewState.model.copy(sendingCreateRecord = false))
            is NewRecordPartialChanges.CreateRecordStart -> viewState.copy(model = viewState.model.copy(sendingCreateRecord = true))

        }

    }











}