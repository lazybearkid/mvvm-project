package com.example.mvvmapplication.ui.main.blog

import android.os.Bundle
import android.view.*
import androidx.navigation.fragment.findNavController
import com.example.mvvmapplication.R

class ViewBlogFragment: BaseBlogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_view_blog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        //check if user is author of the blog
        val isAuthorOfBlog = true
        if(isAuthorOfBlog){
            inflater.inflate(R.menu.edit_view_menu, menu)

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val isAuthorOfBlog = true
        if(isAuthorOfBlog){
            when(item.itemId){
                R.id.edit -> {
                    navToUpdateBlogFragment()
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun navToUpdateBlogFragment(){
        findNavController().navigate(R.id.action_viewBlogFragment_to_updateBlogFragment)
    }
}