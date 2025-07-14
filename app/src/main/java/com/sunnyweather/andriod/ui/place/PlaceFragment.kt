package com.sunnyweather.andriod.ui.place

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.andriod.MainActivity
import com.sunnyweather.andriod.R
import com.sunnyweather.andriod.ui.weather.WeatherActivity

class PlaceFragment: Fragment() {
    val viewModel by lazy { ViewModelProvider(this).get(PlaceViewModel::class.java) }

    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_place, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super .onViewCreated(view, savedInstanceState)

        // 这里第一次是没有记录的 主界面会加载Fragment，但是一旦有place记录，就会出现无限调用的情况：
        // 这里有记录直接拉起WeatherActivity，但是WeatherActivity会静态布局加载PlaceFragment
        //PlaceFragment被创建了会重复拉起WeatherActivity
        // 所以这里先判断当前的activity，要是WeatherActivity就不继续执行下面的代码
//        if (activity is WeatherActivity) else {
//            if (viewModel.isPlaceSaved()) {
//                val place = viewModel.getSavedPlace()
//                val intent = Intent(context, WeatherActivity::class.java).apply {
//                    putExtra("location_lng", place.location.lng)
//                    putExtra("location_lat", place.location.lat)
//                    putExtra("place_name", place.name)
//                }
//                startActivity(intent)
//                activity?.finish()
//                return
//            }
//        }
        if(activity is MainActivity && viewModel.getHome() != "") {
            val place = viewModel.getSavedPlace(viewModel.getHome()!!)
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }

        // 常用已收藏的城市列表展示
        val frequentPlace = view.findViewById<RecyclerView>(R.id.frequentPlace)
        val frequentPlaceLayoutManager = LinearLayoutManager(activity)
        frequentPlace.layoutManager = frequentPlaceLayoutManager
        viewModel.refreshList()
        // PlaceFragment中
        val placeListAdapter = PlaceAdapter(this, viewModel.frequentPlaceListLiveData.value)
        frequentPlace.adapter = placeListAdapter

        val layoutManager = LinearLayoutManager(activity)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        adapter = PlaceAdapter(this, viewModel.placeList)
        recyclerView.adapter = adapter
        view.findViewById<EditText>(R.id.searchPlaceEdit).addTextChangedListener { editable ->
            val content = editable.toString()
            if (content.isNotEmpty()) {
                viewModel.searchPlaces(content)
            } else {
                recyclerView.visibility = View.GONE
                frequentPlace.visibility = View.VISIBLE
                view.findViewById<ImageView>(R.id.bgImageView).visibility = View.VISIBLE
                viewModel.placeList.clear()
                adapter.notifyDataSetChanged()
            }
        }
        viewModel.placeLiveData.observe(viewLifecycleOwner, { result ->
            val places = result.getOrNull()
            if (places != null) {
                recyclerView.visibility = View.VISIBLE
                frequentPlace.visibility = View.GONE
                view.findViewById<ImageView>(R.id.bgImageView).visibility = View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(places)
                adapter.notifyDataSetChanged()
            } else {
                Toast.makeText(activity, "未能查找到任何地点", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
    }
}













