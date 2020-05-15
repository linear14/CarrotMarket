package com.dongldh.carrotmarket.dialog

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.*
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dongldh.carrotmarket.App
import com.dongldh.carrotmarket.R
import com.dongldh.carrotmarket.SignActivity
import com.dongldh.carrotmarket.database.FROM_CHANGE_LOCATION_TO_SETTING_LOCATION
import com.dongldh.carrotmarket.setting.SettingLocationActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_change_location.view.*
import kotlinx.android.synthetic.main.item_change_location.view.*

class ChangeLocationDialog: DialogFragment() {
    val list = mutableListOf<String>()

    // ? 왜 onStart() 처럼 메서드 구현하니 다이얼로그 위치가 내가 원하는대로 된거지?.. 뭐지?? ㅋㅋㅋㅋ
    // setBackgrondDrawableResource 메서드에 투명한 값을 집어넣어 다이얼로그를 깔끔하게 만들어준다. (실제로 외부 영역에 어두운 백그라운드가 존재한다)
    override fun onStart() {
        super.onStart()
        val window = dialog?.window
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    /**
     * 우와 초록색 글자 신기하다 ㅎㅎ!!
     *
     * 다이얼로그 커스텀 시 설정 가능한 몇 가지를 다루어 보았는데,
     * 1. 커스텀 다이얼로그의 위치를 수정하는 작업
     * 2. 커스텀 다이얼로그에 발생하는 상단의 알 수 없는 여백을 없애는 작업
     * 3. 투명한 다이얼로그 만드는 방법
     * 4. 다이얼로그 외부 화면을 회색이 아닌 투명하게 만드는 방법
     * 추가 5. Toolbar의 높이를 코드상으로 찾는 방법
     *
     * 간단한 코드를 작성했다.
     * 후에 아래의 코드에 달린 주석을 읽으면 이해할 수 있을 것이라고.. 내 자신을 믿어본다.
     *
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // 커스텀 다이얼로그 열면, 위에(top) 알 수 없는 여백이 생기는데, 이를 없애주는 코드는 아래와 같다.
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)

        // 다이얼로그 외부 영역 투명하게 만들기
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        // Toolbar (ActionBar)의 높이를 찾아주는 방법
        val attribute = activity?.theme?.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))!!
        val actionBarHeight = attribute.getDimension(0, 0.0F).toInt()
        attribute.recycle()

        // 커스텀 다이얼로그의 생성 위치를 결정해주는 작업
        val layoutParams = dialog?.window?.attributes!!
        layoutParams.gravity = Gravity.TOP or Gravity.START
        layoutParams.x = 16
        layoutParams.y = actionBarHeight - 24

        val view = inflater.inflate(R.layout.dialog_change_location, container, false)
        isCancelable = true

        // MainActivity에서 location 정보를 받아온뒤, list에 뿌려줌.
        val location = arguments?.getStringArrayList("location")!!
        list.add(location[0])
        if(location.size == 2) list.add(location[1])
        list.add("내 동네 설정")

        view.change_location_recycler.layoutManager = LinearLayoutManager(activity)
        view.change_location_recycler.adapter = ChangeLocationAdapter(list)

        return view
    }

    class ChangeLocationViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val location = view.location_text
    }

    inner class ChangeLocationAdapter(list: MutableList<String>): RecyclerView.Adapter<ChangeLocationViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChangeLocationViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            return ChangeLocationViewHolder(layoutInflater.inflate(R.layout.item_change_location, parent, false))
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ChangeLocationViewHolder, position: Int) {
            val item = list[position]
            holder.location.text = item

            holder.location.setOnClickListener {
                if(holder.location.text == "내 동네 설정") {
                    val intent = Intent(activity, SettingLocationActivity::class.java)
                    startActivityForResult(intent, FROM_CHANGE_LOCATION_TO_SETTING_LOCATION)
                    dismiss()
                } else {
                    activity?.findViewById<TextView>(R.id.title_text)?.text = holder.location.text.toString()
                    App.preference.nowSelected = position

                    // snackBar를 main_fragment를 대상뷰로 띄우면 bottomNavigation에서 나타나는 현상이 있었음
                    // 이를 아래와 같이 .apply{ anchorView } 로 바텀네비게이션뷰를 설정하면 snackBar가 그 위에 뜨게 됨!
                    val bottomNavView: BottomNavigationView = activity?.findViewById(R.id.bottom_navigation)!!
                    val snackBar = Snackbar.make(activity?.findViewById(R.id.main_content)!!,
                        "현재 동네가 '${holder.location.text}'(으)로 변경되었습니다.", Snackbar.LENGTH_SHORT).apply {
                        anchorView = bottomNavView
                    }
                    snackBar.setTextColor(Color.WHITE)
                    snackBar.show()
                    dismiss()
                }
            }
        }

    }

    // 다이얼로그 크기 설정해주는 방법
    override fun onResume() {
        super.onResume()
        val windowManager = this.context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        val params = dialog?.window?.attributes
        val deviceWidth = size.x
        params?.width = (deviceWidth * 0.4).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }
}