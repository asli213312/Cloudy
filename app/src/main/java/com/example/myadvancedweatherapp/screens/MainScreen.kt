package com.example.myadvancedweatherapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myadvancedweatherapp.R
import com.example.myadvancedweatherapp.data.WeatherModel
import com.example.myadvancedweatherapp.ui.theme.BlueLight
import com.example.myadvancedweatherapp.ui.theme.WhiteBold
//import com.example.myadvancedweatherapp.ui.theme.BlueLight
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

@Composable
fun MainCard(currentDay: MutableState<WeatherModel>) {
    Column(
        modifier = Modifier.padding(5.dp),
    ) {
        Card(
            modifier = Modifier.background(BlueLight), elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            ), shape = RoundedCornerShape(10.dp), colors = CardDefaults.cardColors(
                containerColor = BlueLight
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            modifier = Modifier.padding(top = 8.dp, start = 8.dp),
                            text = currentDay.value.time,
                            style = TextStyle(fontSize = 15.sp),
                            color = Color.White
                        )
                        AsyncImage(
                            model = "http:${currentDay.value.icon}",
                            contentDescription = "im2",
                            modifier = Modifier
                                .size(35.dp)
                                .padding(start = 3.dp, end = 8.dp)
                        )
                    }
                    Text(
                        text = currentDay.value.city, style = TextStyle(fontSize = 24.sp), color = Color.White
                    )
                    Text(
                        text =
                        if (currentDay.value.currentTemp.isNotEmpty())
                            currentDay.value.currentTemp.toFloat().toInt().toString() + "°C"
                        else "${currentDay.value.maxTemp.toFloat().toInt()}" +
                                "°C/${currentDay.value.minTemp.toFloat().toInt()}°C",
                        style = TextStyle(fontSize = 65.sp),
                        color = Color.White
                    )
                    Text(
                        text = currentDay.value.condition, style = TextStyle(fontSize = 16.sp), color = Color.White
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = {

                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_search),
                                contentDescription = "img3",
                                tint = Color.White
                            )
                        }
                        Text(
                            text = "${currentDay.value.maxTemp.toFloat().toInt()}" +
                                    "°C/${currentDay.value.minTemp.toFloat().toInt()}°C",
                            style = TextStyle(fontSize = 16.sp),
                            color = Color.White
                        )
                        IconButton(onClick = {

                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_sync),
                                contentDescription = "img4",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun TabLayout(daysList: MutableState<List<WeatherModel>>,
              currentDay: MutableState<WeatherModel>)
{
    val tabList = listOf<String>("HOURS", "DAYS")
    val pagerState = rememberPagerState()
    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .padding(start = 5.dp, end = 5.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(BlueLight)
    ) {
        TabRow(
            selectedTabIndex = tabIndex,
            contentColor = Color.White,
            backgroundColor = BlueLight,
            indicator = { position ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(position[tabIndex]),
                height = 2.dp,
                color = Color.White
            )}
        ) {
            tabList.forEachIndexed { index, text ->
                Tab(selected = false,
                    modifier = Modifier.background(BlueLight),
                    selectedContentColor = WhiteBold,
                    unselectedContentColor = Color.White,
                    onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }, text = {
                    Text(text = text)
                })
            }
        }
    }

    HorizontalPager(
        count = tabList.size,
        state = pagerState,
    ) { index ->

        val list = when(index) {
            0 -> getWeatherByHours(currentDay.value.hours)
            1 -> daysList.value
            else -> daysList.value
        }
        MainList(list, currentDay)
    }
}

private fun getWeatherByHours(hours: String): List<WeatherModel> {
    if (hours.isEmpty()) return listOf()

    val hoursArray = JSONArray(hours)
    val listHours = ArrayList<WeatherModel>()

    for (i in 0 until hoursArray.length()) {
        val item = hoursArray[i] as JSONObject
        listHours.add(
            WeatherModel(
                "",
                item.getString("time"),
                item.getString("temp_c").toFloat().toInt().toString() + "°C",
                item.getJSONObject("condition").getString("text"),
                item.getJSONObject("condition").getString("icon"),
                "",
                "",
                ""
            )
        )
    }

    return listHours
}