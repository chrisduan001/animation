package com.example.chris.animatorprac

import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.CompletableSubject
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    var animType = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val durationMs = 1000L


        btn_anim.setOnClickListener {
            btn_1.alpha = 0f
            btn_2.alpha = 0f
            btn_3.alpha = 0f
            btn_4.alpha = 0f


            val type = animType % 4
            when (type) {
                0 -> {
                    btn_1.fadeIn(durationMs)
                        .andThen(btn_2.fadeIn(durationMs))
                        .andThen(btn_3.fadeIn(durationMs))
                        .andThen(btn_4.fadeIn(durationMs))
                        .subscribe()
                }

                1 -> {
                    btn_1.fadeIn()
                        .mergeWith(btn_2.fadeIn())
                        .mergeWith(btn_3.fadeIn())
                        .mergeWith(btn_4.fadeIn())
                        .subscribe()
                }

                2 -> {
                    (btn_1.fadeIn().mergeWith(btn_2.fadeIn()))
                        .andThen(btn_3.fadeIn().mergeWith(btn_4.fadeIn()))
                        .subscribe()
                }

                3 -> {
                    val timeOb = Observable.interval(100, TimeUnit.MILLISECONDS)
                    val btnOb = Observable.just(btn_1, btn_2, btn_3, btn_4)

                    Observable.zip(timeOb, btnOb, BiFunction<Long, View, Any> { _, btn: View ->
                        btn.fadeIn().subscribe()
                    }).subscribe()
                }
            }

            animType++
        }

    }
}

fun View.fadeIn(duration: Long = 1000): Completable {
    val animationSub = CompletableSubject.create()
    return animationSub.doOnSubscribe {
        ViewCompat.animate(this)
            .setDuration(duration)
            .alpha(1f)
            .withEndAction { animationSub.onComplete() }
    }
}
