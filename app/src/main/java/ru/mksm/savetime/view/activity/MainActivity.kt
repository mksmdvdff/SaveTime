package ru.mksm.savetime.view.activity

import android.content.Context
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.GravityCompat
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
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.SearchView
import android.support.v7.widget.SwitchCompat
import android.widget.Toast
import ru.mksm.savetime.interactors.OrdersInteractor
import ru.mksm.savetime.model.OrderType
import ru.mksm.savetime.util.Locator


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

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
        setContentView(R.layout.navigation_drawer)

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

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.setDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            Locator.dishesInteractor.currOrder.clear()
            OrderCreatingActivity.create(this)
        }

        with((findViewById(R.id.stop_switch) as SwitchCompat)) {
            setOnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    this@MainActivity.findViewById(R.id.stop_layout).setBackgroundResource(R.color.navigation_red_block)
                    with(this@MainActivity.findViewById(R.id.stop_text) as TextView) {
                        text = getString(R.string.switch_stoped)
                        setTextColor(resources.getColor(R.color.white))
                    }
                } else {
                    this@MainActivity.findViewById(R.id.stop_layout).setBackgroundResource(R.color.navigation_gray_block)
                    with(this@MainActivity.findViewById(R.id.stop_text) as TextView) {
                        text = getString(R.string.switch_stop)
                        setTextColor(resources.getColor(R.color.order_black_text))
                    }
                }
            }
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

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}
