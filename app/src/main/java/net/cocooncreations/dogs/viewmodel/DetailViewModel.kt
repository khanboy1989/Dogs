package net.cocooncreations.dogs.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.launch
import net.cocooncreations.dogs.model.DogBreed
import net.cocooncreations.dogs.model.DogDataBase

class DetailViewModel(application: Application) : BaseViewModel(application) {

    val dogLiveData = MutableLiveData<DogBreed>()
    fun fetch(uuid: Int) {
        launch {
            val dog = DogDataBase(getApplication()).dogDao().getDog(uuid)
            dogLiveData.value = dog
        }
    }
}