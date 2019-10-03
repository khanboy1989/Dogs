package net.cocooncreations.dogs.view


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list.*

import net.cocooncreations.dogs.R
import net.cocooncreations.dogs.viewmodel.ListViewModel

class ListFragment : Fragment() {


    private lateinit var viewModel:ListViewModel
    private val dogListAdapter = DogListAdapter(arrayListOf())
    private val TAG = ListFragment::class.java.simpleName
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        viewModel = ViewModelProviders.of(this).get(ListViewModel::class.java)
        viewModel.refresh()

        dogsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dogListAdapter
        }

        refreshLayout.setOnRefreshListener {
            dogsList.visibility = View.GONE
            listError.visibility = View.GONE
            loadingView.visibility = View.VISIBLE
            viewModel.refreshByPassCache()
            refreshLayout.isRefreshing = false
        }
        observeViewModel()

    }



    fun observeViewModel(){
        viewModel.dogs.observe(this, Observer {dogs->
            dogs?.let{
                dogsList.visibility = View.VISIBLE
                dogListAdapter.updateDogsList(it)
            }
        })

        viewModel.dogsLoadError.observe(this, Observer {
           isError->
            isError?.let {
                when(it){
                   true -> listError.visibility = View.VISIBLE
                   false -> listError.visibility = View.GONE
                }
            }
        })

        viewModel.loading.observe(this, Observer {
            isLoading->
            isLoading.let {
                when(it){
                    true->  {
                        loadingView.visibility = View.VISIBLE
                        listError.visibility = View.GONE
                        dogsList.visibility = View.GONE
                    }
                    false -> {loadingView.visibility = View.GONE }
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.list_menu,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.actionSettings->{
                view?.let{
                    Navigation.findNavController(it).navigate(ListFragmentDirections.actionSettings())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
