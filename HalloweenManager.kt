/**
 * 할로윈 이벤트 당시 사용했던 1회용 코드입니다.
 * 특정 액티비티에서 랜덤한 위치에 lottie 호박 애니메이션을 나타내고, 클릭 시 이벤트를 발생시킵니다. 
 */

package com.apposter.watchmaker.event

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Guideline
import com.airbnb.lottie.LottieAnimationView
import com.apposter.watchlib.retrofit.api.APIConsts
import com.apposter.watchlib.utils.systems.SystemUtil
import com.apposter.watchmaker.R
import com.apposter.watchmaker.activities.WebViewActivity
import com.apposter.watchmaker.controllers.retrofit.MrTimeAPIController
import com.apposter.watchmaker.utils.PreferenceUtil
import com.google.android.material.snackbar.Snackbar
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

/**
 * 할로윈 이벤트 관리 클래스
 * TODO 이벤트 후 제거
 */
class HalloweenManager {
    // 이벤트 UTC 일정 한국 시간 기준 25일 00:00 ~ 31일 23:59
    private val eventStartDateTime: String
        get() = if (APIConsts.isTestMode()) "2019-10-13T15:00:01.000Z" else "2019-10-24T15:00:01.000Z"
    private val eventEndDateTime: String
        get() = if (APIConsts.isTestMode()) "2019-10-20T14:59:59.000Z" else "2019-10-31T14:59:59.000Z"

    // 이벤트 상품 워치 아이디
    val rewardWatchSellId: String
        get() = if (APIConsts.isTestMode()) "0zSg2UMZ1b" else "j1yUSCtL18"

    /**
     * 해당 액티비티의 컨스트레인트 레이아웃에 호박 세팅
     */
    fun setPumpkin(activity: AppCompatActivity, rootLayout: ConstraintLayout, num: Int) {

        // 이벤트 기간 여부
        val currentDateTime = DateTime.now(DateTimeZone.UTC)
        val isEventTime = DateTime(eventStartDateTime) < currentDateTime &&
                currentDateTime < DateTime(eventEndDateTime)

        // 로그인 여부
        val isSignIn = PreferenceUtil.instance(activity.applicationContext).account != null

        // 이미 찾은 호박인지 여부
        val isFoundAlready = HalloweenPreferenceUtil.instance(activity).checkPumpkin(num)

        // 위 조건들 일치할 때 동작
        if (isEventTime && isSignIn && !isFoundAlready) {

            // 로티 애니메이션 뷰
            val lottie = LottieAnimationView(activity).apply {
                id = View.generateViewId()
                layoutParams = ConstraintLayout.LayoutParams(SystemUtil.getPixelByDP(context, LOTTIE_SIZE), SystemUtil.getPixelByDP(context, LOTTIE_SIZE))
                enableMergePathsForKitKatAndAbove(true)
                setAnimation("halloween/pum${num}_jump.json")
                repeatCount = ValueAnimator.INFINITE
                bringToFront()
            }
            rootLayout.addView(lottie)

            // 가이드라인 가로 랜덤 : 0.3 ~ 0.9 (툴바에 가려지는 경우가 있어 0.3부터 시작)
            val guidelineHorizontal = Guideline(activity).apply {
                id = View.generateViewId()
                layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                setGuidelinePercent(Math.random().toFloat() * 0.6f + 0.3f)
            }
            rootLayout.addView(guidelineHorizontal)

            // 가이드라인 세로 랜덤 : 0.1 ~ 0.9
            val guidelineVertical = Guideline(activity).apply {
                id = View.generateViewId()
                layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
                setGuidelinePercent(Math.random().toFloat() * 0.8f + 0.1f)
            }
            rootLayout.addView(guidelineVertical)

            // 자식 뷰들 중 id 부여되지 않았으면 부여하기 : constraint set clone 예외 방지용
            for (i in 0 until rootLayout.childCount) {
                val view = rootLayout.getChildAt(i)
                if (view.id == -1) {
                    view.id = View.generateViewId()
                }
            }

            // 위치들 지정
            ConstraintSet().run {
                clone(rootLayout)
                create(guidelineHorizontal.id, ConstraintSet.HORIZONTAL_GUIDELINE)
                create(guidelineVertical.id, ConstraintSet.VERTICAL_GUIDELINE)
                connect(lottie.id, ConstraintSet.START, guidelineVertical.id, ConstraintSet.START)
                connect(lottie.id, ConstraintSet.END, guidelineVertical.id, ConstraintSet.END)
                connect(lottie.id, ConstraintSet.TOP, guidelineHorizontal.id, ConstraintSet.TOP)
                connect(lottie.id, ConstraintSet.BOTTOM, guidelineHorizontal.id, ConstraintSet.BOTTOM)
                applyTo(rootLayout)
            }

            lottie.run {
                // 플레이
                playAnimation()

                // 클릭 리스너
                setOnClickListener {
                    setAnimation("halloween/pum${num}_cut.json")
                    repeatCount = 0
                    playAnimation()
                    setOnClickListener(null)
                    addAnimatorListener(object: Animator.AnimatorListener{
                        override fun onAnimationRepeat(animation: Animator?) {
                        }

                        override fun onAnimationEnd(animation: Animator?) {
                            visibility = View.GONE
                        }

                        override fun onAnimationCancel(animation: Animator?) {
                        }

                        override fun onAnimationStart(animation: Animator?) {
                        }
                    })

                    // 호박 찾았다고 프리퍼런스에 체크
                    HalloweenPreferenceUtil.instance(activity).findPumpkin(num)

                    // 7개 다 모았는지 프리퍼런스 체크
                    if (HalloweenPreferenceUtil.instance(activity).checkAllPumpkin()) {
                        // 보상 수령
                        MrTimeAPIController.instance.requestPurchasePremiumWatch(rewardWatchSellId).subscribe({}){}
                        // 이벤트 성공 다이얼로그
                        HalloweenSuccessDialogFragment().show(activity.supportFragmentManager.beginTransaction(), "")
                    } else {
                        // 7개가 아닐 시 스낵바
                        Snackbar.make(rootLayout, R.string.halloween_event_snackbar_find_pumpkin, Snackbar.LENGTH_LONG).apply {
                            setAction(R.string.halloween_event_snackbar_btn) {
                                // 이벤트 페이지 이동
                                val dialog = HalloweenWebViewDialogFragment()
                                dialog.show(activity.supportFragmentManager.beginTransaction(), "")
                            }
                        }.show()
                    }
                }
            }
        }
    }

    private object Holder {
        val INSTANCE = HalloweenManager()
    }

    companion object {
        val instance: HalloweenManager by lazy { Holder.INSTANCE }

        // 호박 애니메이션 사이즈
        private const val LOTTIE_SIZE = 128f
    }
}
