package net.cocooncreations.dogs.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_dog.view.*
import net.cocooncreations.dogs.R
import net.cocooncreations.dogs.databinding.ItemDogBinding
import net.cocooncreations.dogs.model.DogBreed
import net.cocooncreations.dogs.util.getProgressDrawable
import net.cocooncreations.dogs.util.loadImage

class DogListAdapter(val dogsList: ArrayList<DogBreed>) : RecyclerView.Adapter<DogListAdapter.DogViewHolder>() {

    fun updateDogsList(newDogsList: List<DogBreed>) {
        dogsList.clear()
        dogsList.addAll(newDogsList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<ItemDogBinding>(inflater, R.layout.item_dog, parent, false)
        return DogViewHolder(view)
    }

    override fun getItemCount(): Int = dogsList.size


    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        holder.bindData(dogsList[position])
    }


    class DogViewHolder(var view: ItemDogBinding) : RecyclerView.ViewHolder(view.root), DogClickListener {

        fun bindData(item: DogBreed) {
            view.dog = item
            view.listener = this
        }

        override fun onDogClicked(v: View) {
            val uuid = v.dogId.text.toString().toInt()
            val action = ListFragmentDirections.actionDetailFragment()
            action.dogUuid = uuid
            Navigation.findNavController(v).navigate(action)
        }
    }

}