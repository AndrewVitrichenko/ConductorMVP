package com.jshvarts.conductormvp.editnote

import com.jshvarts.conductormvp.mvp.BasePresenter
import com.jshvarts.notedomain.model.Note
import com.jshvarts.notedomain.usecases.EditNoteUseCase
import com.jshvarts.notedomain.usecases.NoteDetailUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class EditNotePresenter @Inject constructor(private val noteDetailUseCase: NoteDetailUseCase, private val editNoteUseCase: EditNoteUseCase) : BasePresenter<EditNoteView>(), EditNoteContract.Presenter {

    override fun loadNote(id: Long) {
        disposables.add(noteDetailUseCase.findNoteById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ view?.onLoadNoteSuccess(it) }, { view?.onNoteLookupError(it) }))
    }

    override fun editNote(id: Long, noteText: String) {
        disposables.add(editNoteUseCase.edit(Note(id, noteText))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ view?.onEditNoteSuccess() }, this::onEditNoteError))
    }

    private fun onEditNoteError(throwable: Throwable) {
        when(throwable) {
            is IllegalArgumentException -> view?.onNoteValidationFailed(throwable)
            else -> view?.onEditNoteError(throwable)
        }
    }
}