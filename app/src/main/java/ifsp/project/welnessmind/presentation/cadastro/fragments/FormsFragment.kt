package ifsp.project.welnessmind.presentation.cadastro.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ifsp.project.welnessmind.R

class FormsFragment : Fragment() {

    companion object {
        fun newInstance() = FormsFragment()
    }

    private lateinit var viewModel: FormsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_forms, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(FormsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}