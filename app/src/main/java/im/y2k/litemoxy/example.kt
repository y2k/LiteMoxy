package im.y2k.litemoxy

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import im.y2k.litemoxy.LoginFragment.Events
import im.y2k.litemoxy.LoginFragment.Events.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

class LoginFragment : MvpFragment<Events>(), MvpView<Events> {

    override val presenter = LoginPresenter(LoginServices(), Router())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_login, container, false)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        login.setOnClickListener { presenter.onLoginAction("" + username.text, "" + password.text) }
    }

    override fun update(event: Events) = when (event) {
        ShowProgress -> progress.visibility = View.VISIBLE
        HideProgress -> progress.visibility = View.GONE
        is ShowErrorToast -> Toast.makeText(activity, event.text, Toast.LENGTH_LONG).show()
    }

    sealed class Events {
        object ShowProgress : Events()
        object HideProgress : Events()
        class ShowErrorToast(val text: String) : Events()
    }
}

class LoginPresenter(private val service: LoginServices, private val router: Router) : MvpPresenter<Events>() {

    init {
        view.update(HideProgress)
    }

    fun onLoginAction(username: String, password: String) = launch(UI) {
        view.update(ShowProgress)
        try {
            service.login(username, password)
            router.navigateToMain()
        } catch (e: Exception) {
            view.update(ShowErrorToast(errorToMessage(e)))
        }
        view.update(HideProgress)
    }

    private fun errorToMessage(e: Exception): String = e.message ?: ""
}

// Infrastructure

class LoginServices {
    suspend fun login(username: String, password: String) {
        delay(5000)
    }
}

class Router {
    fun navigateToMain() = Unit
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}