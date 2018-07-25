package im.y2k.litemoxy

import android.support.v4.app.Fragment

abstract class MvpPresenter<T : Any> {

    val view: MvpView<T> = BufferView()

    fun attach(view: MvpView<T>) = (this.view as BufferView).attach(view)
    fun detach() = (view as BufferView).detach()

    private class BufferView<T : Any> : MvpView<T> {

        private val buffer = ArrayList<T>()
        private var view: MvpView<T>? = null

        override fun update(event: T) {
            buffer.removeAll { it::class == event::class }
            buffer.add(event)
            view?.update(event)
        }

        fun attach(view: MvpView<T>) {
            buffer.forEach(view::update)
            this.view = view
        }

        fun detach() {
            view = null
        }
    }
}

interface MvpView<T> {
    fun update(event: T)
}

abstract class MvpFragment<T : Any> : Fragment(), MvpView<T> {

    abstract val presenter: MvpPresenter<T>

    override fun onResume() {
        super.onResume()
        retainInstance = true
        presenter.attach(this)
    }

    override fun onPause() {
        super.onPause()
        presenter.detach()
    }
}