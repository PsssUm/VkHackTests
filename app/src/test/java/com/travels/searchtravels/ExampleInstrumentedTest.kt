package com.travels.searchtravels

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.api.services.vision.v1.model.LatLng
import com.preview.planner.Define
import com.preview.planner.prefs.AppPreferences
import com.travels.searchtravels.activity.DetailsActivity
import com.travels.searchtravels.api.AviasalesApi
import com.travels.searchtravels.api.AviasalesApi.OnTicketPriceListener
import com.travels.searchtravels.api.OnVisionApiListener
import com.travels.searchtravels.api.VisionApi
import com.travels.searchtravels.utils.Constants
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL



/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

@RunWith(AndroidJUnit4::class)
@Config(maxSdk = Build.VERSION_CODES.P)
class ExampleInstrumentedTest {
    //в googleToken вставьте свой токен, который получите после авторизации в приложении
    val googleToken = "ya29.a0AfH6SMCJZE_wYEYTJYH2xTlopTGD1JHXdaZBXSgKBf1ccdymFE_EjngyhYeEsrfFyrxBcyLRk7cE3NehT1j32W4O8o02DxKeOlM5BKJsLeghQP1Jh-s_xhOH2UMAPoIfMaHPWQKzKXt8UBM4yGus1fedNHHJ5uOtQimJ"

    //тест проверяет запрос с ценами на билеты
    @Test
    fun testPriceRequest() {
        val cities = arrayOf<String>("Москва","Екатеринбург","Мурманск","Самара")
        for (cityName in cities) {
            AviasalesApi.getTicketPrice(cityName, object : OnTicketPriceListener {
                override fun onSuccess(
                    price: Int,
                    countryName: String,
                    code: String
                ) {
                    Assert.assertNotNull("TicketPrice is Null",price)

                }

                override fun onError(error: String) {
                    Assert.fail("Серверный запрос Travelpayouts не работает. ");
                }
            })
        }

    }

    //тест проверяет правильность определения категорий у фотографий
    @Test
    fun imagesCategoryTest() {
        Define.isTestMode = true
        val photos = arrayOf<Array<String>>(
            arrayOf("https://klike.net/uploads/posts/2019-06/1559370578_1.jpg", "mountain"),
            arrayOf("https://img1.goodfon.ru/original/2048x1365/b/3f/assiniboine-provincial-park-2914.jpg", "mountain"),
            arrayOf("https://blog.apltravel.ua/wp-content/uploads/2018/05/Jamajka-ostrov.jpg", "beach"),
            arrayOf("https://krot.info/uploads/posts/2020-01/1580232261_1-p-foni-s-zakatami-na-more-1.jpg", "sea"),
            arrayOf("https://img2.goodfon.ru/original/7420x5064/4/c7/elki-sneg-zima-les-oblaka.jpg", "snow"),
            arrayOf("https://www.blackpantera.ru/upload/iblock/fd0/Sonnik-okean.jpg", "ocean"),
            arrayOf("https://img3.goodfon.ru/original/1920x1408/8/c9/kanada-vankuver-noch-zdaniya.jpg", "other")
        )
        for (photo in photos) {
            val bitmap = getImage(photo[0])
            VisionApi.findLocation(
                bitmap,
                googleToken,
                object : OnVisionApiListener {
                    override fun onSuccess(latLng: LatLng?) {
                        System.out.println("onSuccess");
                    }

                    override fun onErrorPlace(category: String) {
                        System.out.println("onErrorPlace category = " + category);
                        Assert.assertEquals(category, photo[1])

                    }

                    override fun onError() {
                        System.out.println("onError");
                        Assert.fail("Неверная категория, ожидаемый ответ: ${photo[1]}")
                    }
                })
        }
    }
    //тест проверяет правильность получения геоточки для фотографии
    @Test
    fun imagesLocationTest() {
        Define.isTestMode = true
        val photos = arrayOf<String>("https://tripmydream.cc/travelhub/travel/blocks/20/5779/block_205779.jpg",
            "https://globusmira.ru/wp-content/uploads/2019/07/s1200-1-1.jpg",
            "https://funart.pro/uploads/posts/2019-11/1573817042_petropavlovskaja-krepost-rossija-12.jpg",

            "https://cdn.fishki.net/upload/post/201511/22/1747071/9491952.jpg"
        )
        for (photo in photos) {
            val bitmap = getImage(photo)
            VisionApi.findLocation(
                bitmap,
                googleToken,
                object : OnVisionApiListener {
                    override fun onSuccess(latLng: LatLng?) {
                        System.out.println("onSuccess");
                    }

                    override fun onErrorPlace(category: String) {
                        System.out.println("category = " + category);
                        Assert.fail("Нет данных о местоположении")
                    }

                    override fun onError() {
                        System.out.println("onError ");
                        Assert.fail("Нет данных о местоположении")
                    }
                })
        }
    }
    private fun getImage(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            null
        }
    }
}