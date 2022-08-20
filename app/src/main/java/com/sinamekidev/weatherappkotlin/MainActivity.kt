package com.sinamekidev.weatherappkotlin

import android.opengl.Visibility
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.sinamekidev.weatherappkotlin.databinding.ActivityMainBinding
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    val CITY:String = "ankara,tr"
    val API:String = "49ad26746ace08afddb16b3fd59b5565"
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        weatherTask().execute()
    }
    inner class weatherTask : AsyncTask<String,Void,String>(){
        override fun onPreExecute() {
            super.onPreExecute()
            binding.loader.visibility = View.VISIBLE
            binding.mainContainer.visibility = View.GONE
            binding.errorText.visibility = View.GONE
        }

        override fun doInBackground(vararg p0: String?): String? {
            var response:String?
            try{
                response = URL("https://api.openweathermap.org/data/2.5/weather?q=${CITY}&appid=${API}")
                    .readText(Charsets.UTF_8)
            }catch (e:Exception){
                response = null
                binding.errorText.visibility = View.VISIBLE
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try{
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val updateAt:Long = jsonObj.getLong("dt")
                val updateText = "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                    Date(updateAt * 1000)
                )
                val temp = (main.getString("temp").toDouble().toBigDecimal().toInt() - 273).toString()+"°C"
                val temp_min = "Min Temp: "+(main.getString("temp_min").toDouble().toBigDecimal().toInt() - 273).toString() + "°C"
                val temp_max = "Max Temp: "+(main.getString("temp_max").toDouble().toBigDecimal().toInt() - 273).toString() + "°C"
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")
                val sunrise = sys.getLong("sunrise")
                val sunset = sys.getLong("sunset")
                val windspeed = wind.getString("speed")
                val weatherDescyprt = weather.getString("description")
                val address = jsonObj.getString("name") + "," + sys.getString("country")
                binding.address.text = address
                binding.humidity.text = humidity
                binding.maxTemp.text = temp_max
                binding.tempMin.text = temp_min
                binding.pressure.text = pressure
                binding.sunrise.text = sunrise.toString()
                binding.sunset.text = sunset.toString()
                binding.wind.text = windspeed
                binding.updatedAt.text = updateText
                binding.status.text = weatherDescyprt.capitalize()
                binding.temp.text = temp
                binding.sunrise.text = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(1000* sunrise))
                binding.sunset.text = SimpleDateFormat("hh:mm a",Locale.ENGLISH).format(Date(1000 * sunset))
                binding.loader.visibility = View.GONE
                binding.errorText.visibility = View.GONE
                binding.mainContainer.visibility = View.VISIBLE

            }catch (e:Exception){
                binding.loader.visibility = View.GONE
                binding.mainContainer.visibility = View.GONE
                binding.errorText.visibility = View.VISIBLE
            }
        }
    }
}