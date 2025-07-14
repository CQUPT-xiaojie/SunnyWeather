package com.sunnyweather.andriod.ui.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.andriod.R
import com.sunnyweather.andriod.logic.model.Place
import com.sunnyweather.andriod.ui.weather.WeatherActivity

class PlaceAdapter(private val fragment: PlaceFragment, private val placeList: List<Place>):
    RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val placeName: TextView = view.findViewById(R.id.placeName)
        val placeAddress: TextView = view.findViewById(R.id.placeAddress)
        val savePlace: ImageButton = view.findViewById(R.id.savePlace)
        val removePlace: ImageButton = view.findViewById(R.id.removePlace)
        val setHome: ImageButton = view.findViewById(R.id.setHome)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.place_item, parent,
            false)
        val holder = ViewHolder(view)
        holder.itemView.setOnClickListener{
            val position = holder.bindingAdapterPosition
            val place = placeList[position]
            val activity = fragment.activity
            if (activity is WeatherActivity) {
                activity.findViewById<DrawerLayout>(R.id.drawerLayout).closeDrawers()
                activity.viewModel.locationLng = place.location.lng
                activity.viewModel.locationLat = place.location.lat
                activity.viewModel.placeName = place.name
                activity.refreshWeather()
            } else {
                val intent = Intent(parent.context, WeatherActivity::class.java).apply {
                    putExtra("location_lng", place.location.lng)
                    putExtra("location_lat", place.location.lat)
                    putExtra("place_name", place.name)
                }
                fragment.startActivity(intent)
                activity?.finish()
            }
        }

        // 保存地点信息 这里就是加入常用
        holder.savePlace.setOnClickListener {
            val position = holder.bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val place = placeList[position]
                if (!fragment.viewModel.isPlaceSaved(place.address)) {
                    fragment.viewModel.savePlace(place.address, place)

                }
                else Toast.makeText(parent.context, "请勿重复添加", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        // 删除地点信息，这里就是place_item的删除操作
        holder.removePlace.setOnClickListener {
            val position = holder.bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val place = placeList[position]
                // 再删除之前判断是不是最后一个，要是最后一个就直接手动调用clearPlace
                // 因为这里钥匙最后一个是key为home的话，不能根据address来删除
                fragment.viewModel.clearPlace()
                fragment.viewModel.removePlace(place.address)
                Toast.makeText(parent.context, "已删除：${place.address}", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        //指定本人所在城市
        holder.setHome.setOnClickListener {
            val position = holder.bindingAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val place = placeList[position]
                fragment.viewModel.setHome(place)
                // 将已有的address指定为home，就可以把同一个的address保存的数据删除，避免重复展示
                fragment.viewModel.removePlace(place.address)
                Toast.makeText(parent.context, "已设置home为：${place.address}", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.address
    }

    override fun getItemCount(): Int {
        return placeList.size
    }
}