package kr.co.sbproject.pincode.sample

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    /**
     * 키패드를 관리하기위한 클래스로 selset가 기본값 true를 가지며 false일 경우 클릭이벤트를 주지 않는다
     *
     * @param value     키패드에 나타낼 값
     * @param select    해당 키배프 클릭 여부
     */
    class PinNumber(var value: String, var select: Boolean = true)

    /**
     * 현재까지 작상된 pincode 위치를 나타내는 내는 변수
     */
    private var pinPosition = 0

    /**
     * 상단 pin code 를 나타내기위한 TextView 배열
     */
    private var pinList: List<TextView>? = null

    /**
     * 번호 패드 배열
     *
     * 추후 번호의 위치 변경이 필요할 경우 리스트를 섞어서 변형가능
     */
    private var numberList = listOf(
        PinNumber("1"),
        PinNumber("2"),
        PinNumber("3"),
        PinNumber("4"),
        PinNumber("5"),
        PinNumber("6"),
        PinNumber("7"),
        PinNumber("8"),
        PinNumber("9"),
        PinNumber("", false),
        PinNumber("0"),
        PinNumber("삭제")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // pincode TextView 배열 추가
        pinList = listOf(
            tv_pin_1.findViewById(R.id.tv_pin),
            tv_pin_2.findViewById(R.id.tv_pin),
            tv_pin_3.findViewById(R.id.tv_pin),
            tv_pin_4.findViewById(R.id.tv_pin),
            tv_pin_5.findViewById(R.id.tv_pin),
            tv_pin_6.findViewById(R.id.tv_pin)
        )

        // RecyclerView에 PinNumberAdapter를 적용
        rv_keypad.adapter = PinNumberAdapter(
            this@MainActivity,
            numberList,
            View.OnClickListener {
                var position = it.tag as Int

                when (position) {
                    9 -> {
                    }
                    11 -> {
                        setPinText(--pinPosition, "")
                    }
                    10 -> {
                        setPinText(pinPosition, "0")
                    }
                    else -> {
                        setPinText(pinPosition, "${++position}")
                    }
                }

            })

        //RecyclerView의 layoutManager를 GridLayoutManager로 지정하여 gridview 형태로 사용 하도록함 spanCount로 한 행에 표현할 데이터 숫자를 지정
        rv_keypad.layoutManager = GridLayoutManager(this@MainActivity, 3)
    }

    /**
     * 키패드의 클릭 이벤트를 통해 상단의 pincode TextView에 데이터를 출력
     *
     * @param position  데이터를 출력할 TextView 위치
     * @param value     출력할 데이터
     */
    private fun setPinText(position: Int, value: String) {
        if (position < 0) {
            pinPosition = 0
        } else {
            pinList?.let {
                if (it.size > position) {
                    it[position].text = value
                    if (value != "") {
                        pinPosition++
                    }
                    if (it.size <= pinPosition) {//모든 pincode가 입력됨
                        checkPinCode()
                    }
                }
            }
        }
    }

    /**
     * pincode가 모두 입력되면 호출 함
     */
    private fun checkPinCode() {
        var pin = StringBuffer()
        pinList?.let { list ->
            for (view in list) {
                if (view.text != "") {
                    pin.append(view.text)
                }
            }
        }
        Log.i("sgim", "pin $pin")
    }

    /**
     * 키패드를 표현하기 위한 RecyclerView Adapter로 기본적으로 ViewHolder 패턴을 가지고 있음
     *
     * @param context
     * @param list      RecyclerView에 표현할 데이터 배열
     * @param listener  클릭 이벤트 리스너
     */
    private class PinNumberAdapter(
        var context: Context,
        var list: List<PinNumber>,
        var listener: View.OnClickListener
    ) :
        RecyclerView.Adapter<PinNumberAdapter.NumberViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberViewHolder {
            var view =
                LayoutInflater.from(context).inflate(R.layout.view_number, parent, false)

            // parent에 post하지 않고 바로 measuredHeight를 하면 해당 사이즈가 0으로 호출됨 post를 통해 뷰가 생성된 후 에 실행하여 해당 사이즈를 받아오도록 함
            parent.post {
                val height = parent.measuredHeight / 4
                view.minimumHeight = height
            }
            return NumberViewHolder(
                view
            )
        }

        override fun getItemCount(): Int = list.size

        override fun onBindViewHolder(holder: NumberViewHolder, position: Int) {
            list[position].run {
                holder.tvNumber.text = value
                // 해당 뷰에 position을 tag값으로 할당하여 해당 뷰가 클릭될때 tag값을 확인 어떤 데이터가 클릭되었는지 판단하기 위해 사용
                holder.viewNumber.tag = position
                // select 여부를 통해 클릭 이벤트를 할당 함
                if (select) {
                    holder.viewNumber.setOnClickListener(listener)
                }
            }
        }

        class NumberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var viewNumber: FrameLayout = itemView.findViewById(R.id.view_number)
            var tvNumber: TextView = itemView.findViewById(R.id.tv_number)
        }
    }
}
