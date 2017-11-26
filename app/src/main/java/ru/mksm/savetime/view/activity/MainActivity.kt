package ru.mksm.savetime.view.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.support.v7.widget.SwitchCompat
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import ru.mksm.savetime.R
import ru.mksm.savetime.model.OrderStage
import ru.mksm.savetime.util.ActivityCompanion
import ru.mksm.savetime.util.Locator


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var internetDisposable : Disposable? = null

    val mSearchListener: SearchView.OnQueryTextListener = object : SearchView.OnQueryTextListener {
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

    companion object : ActivityCompanion(MainActivity::class.java) {
        fun createWithoutHistory(context: Activity,
                                 withoutAnimation: Boolean = false,
                                 withNewOrder: Boolean = false) {
            var intent = getIntent(context)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            if (withNewOrder) {
                intent.putExtra(NEW_ORDER, true)
            }
            context.startActivity(intent)
            if (withoutAnimation) {
                context.overridePendingTransition(0, 0)
            }
        }

        var NEW_ORDER = "new_order"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_drawer)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        setTitle(R.string.order_activity_label)
        if (intent.getBooleanExtra(NEW_ORDER, false) && savedInstanceState == null) {
            Snackbar.make(toolbar, "Заказ создан", Snackbar.LENGTH_SHORT).show()
        }
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
        setPhoneAndName(navigationView)
        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            Locator.dishesInteractor.currOrder.clear()
            OrderCreatingActivity.create(this)
        }
        var offline = toolbar.findViewById(R.id.offline)
        internetDisposable = Locator.internetInteractor.observeInternet()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    if (!it) {
                        offline.visibility = View.VISIBLE
                    } else {
                        offline.visibility = View.GONE
                    }
                }

        with((findViewById(R.id.stop_switch) as SwitchCompat)) {
            setOnCheckedChangeListener { compoundButton, b ->
                setSwitchState(b);
                Locator.stopInteractor.stopped = b
//
            }
            isChecked = Locator.stopInteractor.stopped
            setSwitchState(Locator.stopInteractor.stopped)
        }

        with((findViewById(R.id.service_switch) as SwitchCompat)) {
            setOnCheckedChangeListener { compoundButton, b ->
                if (b) {
                    Locator.ordersInteractor.startScheduler()
                } else {
                    Locator.ordersInteractor.stopScheduler()
                }
            }
            isChecked = Locator.ordersInteractor.IsServiceStarted()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (internetDisposable != null) {
            internetDisposable!!.dispose()
        }
    }

    private fun setSwitchState(value: Boolean) {
        if (value) {
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

    private fun setPhoneAndName(navigationView: NavigationView) {
        var view = navigationView.getHeaderView(0)
        (view.findViewById(R.id.nd_name) as TextView).text = Locator.accountInteractor.name
        (view.findViewById(R.id.nd_phone) as TextView).text = Locator.accountInteractor.number
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        val inflater = MenuInflater(this)
//        inflater.inflate(R.menu.search_menu, menu)
//        val searchItem = menu!!.findItem(R.id.action_search)
//        searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS
//                + MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW)
//        val searchView = MenuItemCompat.getActionView(searchItem) as ArrayAdapterSearchView
//        searchView.setAdapter(ArrayAdapter(this, android.R.id.li))
//        searchView.setOnItemClickListener(object : AdapterView.OnItemClickListener {
//            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//
//            }
//
//        })
//        searchView.setOnQueryTextListener(mSearchListener)
//        return true
//    }

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

    enum class TabType(val titleId: Int, vararg val orderStages: OrderStage) {
        New(R.string.orders_tab_new, OrderStage.Payed, OrderStage.Cooking, OrderStage.Cooked),
        Done(R.string.orders_tab_done, OrderStage.Done),
        NotPayed(R.string.orders_tab_not_payed, OrderStage.NotPayed);
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

        if (id == R.id.logout) {
            Locator.accountInteractor.logout()
            Locator.dishesInteractor.repo.clear()
            Locator.ordersInteractor.repo.clear()
            Locator.ordersInteractor.stopScheduler()
            Locator.internetInteractor.cancelNotif()
            RegistrationActivity.createWithoutHistory(this)
        }

        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}
