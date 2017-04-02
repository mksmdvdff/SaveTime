package ru.mksm.savetime.view.activity

import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.*
import android.widget.TextView
import ru.mksm.savetime.R
import ru.mksm.savetime.model.Order
import ru.mksm.savetime.util.ActivityCompanion
import android.support.v4.widget.SearchViewCompat.setOnQueryTextListener
import android.support.v4.view.MenuItemCompat
import android.support.v7.widget.SearchView
import android.widget.Toast
import ru.mksm.savetime.interactors.OrdersInteractor
import ru.mksm.savetime.model.OrderType


class MainActivity : AppCompatActivity() {

    val mSearchListener : SearchView.OnQueryTextListener = object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            Toast.makeText(this@MainActivity, "ага", Toast.LENGTH_LONG).show()
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            Toast.makeText(this@MainActivity, "ага", Toast.LENGTH_LONG).show()
            return true
        }
    }

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var mViewPager: ViewPager? = null
    companion object : ActivityCompanion(MainActivity::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        setTitle(R.string.order_activity_label)
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container) as ViewPager
        mViewPager!!.adapter = mSectionsPagerAdapter

        val tabLayout = findViewById(R.id.tabs) as TabLayout
        tabLayout.setupWithViewPager(mViewPager)




        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            MenuOrderActivity.create(this)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = MenuInflater(this)
        inflater.inflate(R.menu.search_menu, menu)
        val searchItem = menu!!.findItem(R.id.action_search)
        searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS
                + MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
        val searchView = MenuItemCompat.getActionView(searchItem) as SearchView
        searchView.setOnQueryTextListener(mSearchListener)
        return true
    }

    /**
     * A [FragmentPagerAdapter] that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int) = PlaceholderFragment.newInstance(TabType.values()[position])

        override fun getCount() = TabType.values().size;

        override fun getPageTitle(position: Int): CharSequence? =
            applicationContext.getString(TabType.values()[position].titleId)
    }

    enum class TabType(val titleId : Int, vararg val orderTypes: OrderType) {
        New(R.string.orders_tab_new, OrderType.Payed, OrderType.Cooking, OrderType.Cooked),
        Done(R.string.orders_tab_done, OrderType.Done),
        NotPayed(R.string.orders_tab_not_payed, OrderType.NotPayed);
    }
}
