package com.example.ferreira.diaristas

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_arc_list.*
import client.yalantis.com.foldingtabbar.FoldingTabBar
import org.jetbrains.annotations.NotNull









class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_arc_list)

        recyclerView.adapter = DummyAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)

        val tabBar = findViewById<View>(R.id.folding_tab_bar) as FoldingTabBar

        tabBar.onMainButtonClickListener(object : FoldingTabBar.OnMainButtonClickedListener {
            override fun onMainButtonClicked() {

            }
        })

    }
}

private operator fun FoldingTabBar.OnMainButtonClickedListener?.invoke(onMainButtonClickedListener: FoldingTabBar.OnMainButtonClickedListener) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
