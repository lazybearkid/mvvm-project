package com.example.mvvmapplication.util

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.mvvmapplication.R
import com.example.mvvmapplication.util.BottomNavController.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavController(
    val context: Context,
    @IdRes val containerId: Int,
    @IdRes val appStartDestinationId: Int,
    val graphChangedListener: OnNavigationGraphChanged?,
    val navGraphProvider: NavGraphProvider //this interface will set the navGraph to navController
) {
    private val TAG: String = "AppDebug"
    lateinit var activity: Activity
    lateinit var fragmentManager: FragmentManager
    lateinit var navItemChangedListener: OnNavigationItemChanged
    private val navigationBackStack = BackStack.of(appStartDestinationId)

    init {
        if (context is Activity) {
            activity = context
            fragmentManager = (activity as FragmentActivity).supportFragmentManager
        }
    }

    fun onNavigationItemSelected(itemId: Int = navigationBackStack.last()): Boolean {
        // replace the fragment representing a navigation item
        // tag = string version of Id
        val navHostFragment = fragmentManager.findFragmentByTag(itemId.toString())
            ?: NavHostFragment.create(navGraphProvider.getNavGraphId(itemId))

        fragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            .replace(containerId, navHostFragment, itemId.toString())
            .addToBackStack(null)
            .commit()

        //add to backstack
        navigationBackStack.moveLast(itemId)

        //update: highlight item selected
        navItemChangedListener.onItemChanged(itemId)

        //communicate with activity
        //purpose of this is to be able to cancel pending jobs if users leave the fragment while jobs are still being execute/not finished
        graphChangedListener?.onGraphChanged()

        return true
    }

    fun onBackPressed() {
        val childFragmentManager = fragmentManager.findFragmentById(containerId)!!
            .childFragmentManager
        when {
            // We should always try to go back on the child fragment manager stack before going to
            // the navigation stack. It's important to use the child fragment manager instead of the
            // NavController because if the user change tabs super fast commit of the
            // supportFragmentManager may mess up with the NavController child fragment manager back
            // stack
            childFragmentManager.popBackStackImmediate() -> {
                Log.e(TAG, "onBackPressed: containerid $containerId")
                Log.e(TAG, "onBackPressed: here ${childFragmentManager.primaryNavigationFragment}")
            }

            //fragment backstack is empty so try to back on the navigation stack
            navigationBackStack.size > 1 -> {
                //remove last item from backstack
                navigationBackStack.removeLast()
                //update container with new fragment
                //back to the last element of bottom bigger stack
                onNavigationItemSelected()
            }

            //if the stack has only one element and its not the navigation home we should
            // ensure that the application always leaves from startDestination
            navigationBackStack.last() != appStartDestinationId -> {
                navigationBackStack.removeLast()
                navigationBackStack.add(0, appStartDestinationId)
                onNavigationItemSelected()
            }
            else -> {
                activity.finish()
            }
        }
    }

    private class BackStack : ArrayList<Int>() {
        companion object {
            fun of(vararg elements: Int): BackStack { //params: a list of integer which contains the id of navHostFragments
                val b = BackStack()
                b.addAll(elements.toTypedArray())
                return b
            }
        }

        fun removeLast() = removeAt(size - 1)

        fun moveLast(item: Int) {
            remove(item)
            add(item)
        }
    }

    //For setting the checked icon on the bottom navigation
    interface OnNavigationItemChanged {
        fun onItemChanged(itemId: Int)
    }

    //Need a setter for this interface bc other interfaces will be implemented by MainActivity
    fun setOnNavigationItemChanged(listener: (itemId: Int) -> Unit) {
        this.navItemChangedListener = object : OnNavigationItemChanged {
            override fun onItemChanged(itemId: Int) {
                listener.invoke(itemId)
            }

        }
    }

    //get id of each graph
    //ex: R.navigation.nav_blog
    //ex: R.navigation.nav_create_blog
    //ex: R.navigation.nav_account
    interface NavGraphProvider {
        @NavigationRes
        fun getNavGraphId(itemId: Int): Int
    }

    //doing the same function as OnNavigationItemChanged interface but this interface is for working with activity while the other is for only internally
    //Example: Select a new item on the bottom nav
    //Ex: Home -> Account
    interface OnNavigationGraphChanged {
        fun onGraphChanged()
    }

    //execute when the item is clicked in within the same NavGraph
    interface OnNavigationReselectedListener {
        fun onReselectNavItem(navController: NavController, fragment: Fragment)
    }


}

fun BottomNavigationView.setupNavigation(
    bottomNavController: BottomNavController,
    onReselectListener: OnNavigationReselectedListener
) {
    setOnNavigationItemSelectedListener {
        bottomNavController.onNavigationItemSelected(it.itemId)
    }

    setOnNavigationItemReselectedListener {
        bottomNavController
            .fragmentManager
            .findFragmentById(bottomNavController.containerId)!!
            .childFragmentManager
            .fragments[0]?.let { fragment ->
            onReselectListener.onReselectNavItem(
                bottomNavController.activity.findNavController(bottomNavController.containerId),
                fragment
            )
        }
    }

    bottomNavController.setOnNavigationItemChanged { itemId ->
        menu.findItem(itemId).isChecked = true
    }
}

