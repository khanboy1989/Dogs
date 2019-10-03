package net.cocooncreations.dogs.model

import androidx.lifecycle.MutableLiveData
import io.reactivex.Single
import retrofit2.http.GET

interface DogsApi {

    @GET("DevTides/DogsApi/master/dogs.json")
    fun getDogs(): Single<List<DogBreed>>

}