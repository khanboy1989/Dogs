package net.cocooncreations.dogs.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import net.cocooncreations.dogs.model.DogBreed
import net.cocooncreations.dogs.model.DogDataBase
import net.cocooncreations.dogs.model.DogsApiService
import net.cocooncreations.dogs.util.NotificationsHelper
import net.cocooncreations.dogs.util.SharedPreferencesHelper
import java.lang.NumberFormatException

class ListViewModel(application:Application):BaseViewModel(application) {

    private val dogsService = DogsApiService()
    private val disposable  = CompositeDisposable()
    private var prefHelper = SharedPreferencesHelper(getApplication())
    private var refreshTime = 5 * 60 * 1000 * 1000 * 1000L

    val dogs = MutableLiveData<List<DogBreed>>()
    val dogsLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh(){
        checkCacheDuration()
        val updateTime = prefHelper.getUpdateTime()
        if(updateTime!=null && updateTime!= 0L && System.nanoTime() - updateTime < refreshTime){
            fetchDogsFromDatabase()
        } else {
            fetchDogsFromRemote()
        }
    }

    private fun checkCacheDuration(){
        val cachePreference = prefHelper.getCacheDuration()
        try{
            val cachePreferenceInt = cachePreference?.toInt() ?: 5*60
            refreshTime = cachePreferenceInt.times(1000 * 1000 * 1000L)
        }catch (ex:NumberFormatException){
            ex.printStackTrace()
        }
    }

    fun refreshByPassCache(){
        fetchDogsFromRemote()
    }

    private fun fetchDogsFromDatabase(){
        loading.value = true
        launch {
            val dogs = DogDataBase(getApplication()).dogDao().getAllDogs()
            dogsRetrived(dogs)
            Toast.makeText(getApplication(), "Dogs Retrieved from database",Toast.LENGTH_LONG).show()
        }
    }

    private fun fetchDogsFromRemote(){
        loading.value = true
        disposable.add(
            dogsService.getDogs()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object:DisposableSingleObserver<List<DogBreed>>(){
                    override fun onSuccess(dogList: List<DogBreed>) {
                        storeDogsLocally(dogList)
                        Toast.makeText(getApplication(), "Dogs Retrieved from endpoint",Toast.LENGTH_LONG).show()
                        NotificationsHelper(getApplication()).createNotification()
                    }

                    override fun onError(e: Throwable) {
                        dogsLoadError.value = true
                        loading.value = false
                        e.printStackTrace()
                    }
                }))
    }

    private fun dogsRetrived(dogList:List<DogBreed>){
        dogs.value = dogList
        loading.value = false
        dogsLoadError.value  = false
    }

    private fun storeDogsLocally(list:List<DogBreed>){
        launch {
            val dao = DogDataBase(getApplication()).dogDao()
            dao.deleteAllDogs()
            val result = dao.insertAll(*list.toTypedArray())
            var i = 0
            while (i<list.size){
                list[i].uuid = result[i].toInt()
                ++i
            }
            dogsRetrived(list)
        }
        prefHelper.saveUpdateTime(System.nanoTime())
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}