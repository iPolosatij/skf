package space.digitallab.cskf

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import space.digitallab.cskf.ui.theme.CskfTheme
import kotlin.math.ceil
import kotlin.math.pow

class MainActivity : ComponentActivity() {

    private val _age: MutableLiveData<Event<String>> = MutableLiveData()
    private val age: LiveData<Event<String>>  get() = _age
    private val _height: MutableLiveData<Event<String>> = MutableLiveData()
    private val height: LiveData<Event<String>> get() = _height
    private val _width: MutableLiveData<Event<String>> = MutableLiveData()
    private val width: LiveData<Event<String>> get() = _width
    private val _kreatinine: MutableLiveData<Event<String>> = MutableLiveData()
    private val kreatinine: LiveData<Event<String>> get() = _kreatinine
    private val _women: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val women: LiveData<Event<Boolean>> get() = _women
    private val _black: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val black: LiveData<Event<Boolean>> get() = _black
    private val _result: MutableLiveData<Event<String>> = MutableLiveData()
    private val result: LiveData<Event<String>> get() = _result
    private val _clear: MutableLiveData<Event<Boolean>> = MutableLiveData()
    private val clear: LiveData<Event<Boolean>> get() = _clear

    private var ageF = ""
    private var heightF = ""
    private var widthF = ""
    private var kreatnineF = ""
    private var womenF = false
    private var blackF = false
    private val checkList = listOf("Женщина", "Тёмная кожа")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Calculator()
        }
    }

    @Composable
    fun Calculator() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp, 48.dp, 0.dp, 0.dp)
                .background(Color.White)
        ) {
            val ageRange = remember { mutableIntStateOf(0) }

            age.observe(this@MainActivity) {
                it.getFirstOrNull()?.let { ageChange -> ageF = ageChange
                    try { ageRange.intValue = ageChange.toInt() }catch (_: Exception){ } }
            }
            height.observe(this@MainActivity) {
                it.getFirstOrNull()?.let { ageChange -> heightF = ageChange } }
            width.observe(this@MainActivity) {
                it.getFirstOrNull()?.let { ageChange -> widthF = ageChange } }
            kreatinine.observe(this@MainActivity) {
                it.getFirstOrNull()?.let { ageChange -> kreatnineF= ageChange }
            }
            black.observe(this@MainActivity) {
                it.getFirstOrNull()?.let { isBlack -> blackF = isBlack }
            }
            women.observe(this@MainActivity) {
                it.getFirstOrNull()?.let { isWomen -> womenF = isWomen }
            }
            if(ageRange.intValue > 21) Row(modifier = Modifier
                .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround) {
                for (check in checkList){
                    Check(label = check)
                }
            }
            Field(field = FieldEnum.Age)
            if (ageRange.intValue <= 21) Field(field = FieldEnum.Height)
            if (ageRange.intValue > 21)  Field(field = FieldEnum.Width)
            Field(field = FieldEnum.Kreatinine)
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 32.dp, 0.dp, 32.dp),
                horizontalArrangement = Arrangement.SpaceAround){
                for (btn in BtnEnum.entries){
                    ImageBtn(btn = btn)
                }
            }
            Result()
        }
    }

    @Composable
    fun Check(label: String){
        val checked = remember {
            mutableStateOf(false)
        }
        clear.observe(this@MainActivity){
            checked.value = false
        }
        Row (modifier = Modifier
            .background(color = Color.White),
            verticalAlignment = Alignment.CenterVertically){
            Checkbox(
                checked = checked.value,
                onCheckedChange = {
                    when(label){
                        checkList[0] -> _women.value = Event(it)
                        checkList[1] -> _black.value = Event(it)
                    }
                    checked.value = it
                },
            )
            Text(text = label)
        }
    }

    @Composable
    fun Field(field: FieldEnum) {
        val fieldValue = remember { mutableStateOf("") }
        clear.observe(this@MainActivity){
            fieldValue.value = ""
        }
        Card(modifier = Modifier.padding(8.dp)) {
            TextField(
                value = fieldValue.value,
                onValueChange = {
                    when (field) {
                        FieldEnum.Kreatinine -> _kreatinine.value = Event(it)
                        FieldEnum.Age -> _age.value = Event(it)
                        FieldEnum.Width -> _width.value = Event(it)
                        FieldEnum.Height -> _height.value = Event(it)
                    }
                    fieldValue.value = it
                },
                label = {
                    Text(
                        text =
                        when (field) {
                            FieldEnum.Kreatinine -> "Креатинин"
                            FieldEnum.Age -> "Возраст"
                            FieldEnum.Width -> "Вес"
                            FieldEnum.Height -> "Рост"
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

    }

    @Composable
    fun ImageBtn(btn: BtnEnum){
        Image(
            painter = painterResource(
                id = when (btn) {
                    BtnEnum.Clear -> R.drawable.ic_clear
                    BtnEnum.Calculate -> R.drawable.ic_calculate
                }
            ),
            contentDescription = "",
            modifier = Modifier.clickable {
                when (btn) {
                    BtnEnum.Clear -> clear()
                    BtnEnum.Calculate -> calculate()
                }
            }
        )
    }

    @Composable
    fun  Result(){
        val resultVar = remember {
            mutableStateOf("")
        }
        result.observe(this){
            it.getFirstOrNull()?.let {res->
                resultVar.value = res
            }
        }
        Text(text = resultVar.value,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            fontSize = 22.sp,
            textAlign = TextAlign.Center)

    }

    private fun calculate(){

        hideKeyboard()

        var calcAge = 0.0
        var calcKreatinine = 0.0
        var calcWidth = 0.0
        var calcHeight = 0.0

        var ckd = 0.0
        var mdrd = 0.0
        var kg = 0.0

        val womenK = if (womenF) 0.85 else 1.0
        val blackK = if (blackF) 0.742 else 1.0
        val blackK1 = if (blackF) 1.18 else 1.0

        try {
            calcAge = ageF.toDouble()
            try {
                calcKreatinine = kreatnineF.toDouble()
                if (calcKreatinine < 5) calcKreatinine *= 88.4017
                val i1 = -0.329
                val i2 = -1.209
                val i3 = -0.411
                val i4 = calcKreatinine / 0.7 * 0.0113
                val i5 = calcKreatinine / 0.9 * 0.0113
                val i6 = i4.pow(i1)
                val i7 = i4.pow(i2)
                val i8 = i5.pow(i3)
                val i9 = i5.pow(i2)
                val i10 = 0.993.pow(calcAge)

                val m1 = -1.154
                val m2 = -0.203
                val m3 = calcKreatinine * 0.0113
                val m4 = m3.pow(m1)
                val m5 = calcAge.pow(m2)

                if(calcAge >  21){
                    try {
                        calcWidth = widthF.toDouble()

                        mdrd =
                            ceil(blackK * 186 * m4 * m5 * blackK1)
                        ckd =
                            ceil(if (womenF) if (calcKreatinine <= 62) 144 * i6 * i10 else 144 * i7 * i10
                            else if (calcKreatinine <= 80) 144 * i8 * i10 else 144 * i9 * i10)
                        kg =
                            ceil((88 * (140 - calcAge) * calcWidth / (72 * calcKreatinine)) * womenK)

                        val hpb = if (ckd >= 90) "норма или ХПБ C1 (при наличии других признаков"
                        else if (ckd in 60.0..89.0) "ХПБ C2 повеждение почек с лёгким снижением СКФ."
                        else if (ckd in 45.0..59.0) "ХПБ C3a повеждение почек с умеренным снижением СКФ."
                        else if (ckd in 30.0..44.0) "ХПБ C3b повеждение почек с существенным снижением СКФ."
                        else if (ckd in 15.0..29.0) "ХПБ C4 Резкое снижение СКФ."
                        else if (ckd < 15) "ХПБ C5 Терминальное снижение СКФ."
                        else "проверьте правильность ввода данных"

                        _result.value = Event("KG - $kg мл/мин \n MDRD -  $mdrd мл/мин  \n  CKD-EPI - $ckd мл/мин  \n  $hpb")

                    }catch (_: Exception){
                        Toast.makeText(this, "Укажите вес", Toast.LENGTH_LONG).show()
                    }
                }else{
                    try {
                        calcHeight = heightF.toDouble()
                        if (calcHeight < 3) calcHeight *= 3
                        _result.value = Event("Скорость клубочковой фильтрации расчитана по формуле Шварца - ${ceil(41.3*(calcHeight/calcKreatinine))} мл/мин")
                    }catch (_: Exception){
                        Toast.makeText(this, "Укажите рост", Toast.LENGTH_LONG).show()
                    }
                }
            }catch (_: Exception){
                Toast.makeText(this, "Укажите креатинин", Toast.LENGTH_LONG).show()
            }
        }catch (_: Exception){
            Toast.makeText(this, "Укажите возраст", Toast.LENGTH_LONG).show()
        }
    }

    private fun clear(){
        hideKeyboard()
        ageF = ""
        heightF = ""
        widthF = ""
        kreatnineF = ""
        _result.value = Event("")
        _clear.value = Event(true)
    }

    enum class FieldEnum { Kreatinine, Age, Width, Height }
    enum class BtnEnum { Clear, Calculate }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        CskfTheme {
            Calculator()
        }
    }

    private fun hideKeyboard() {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }
}