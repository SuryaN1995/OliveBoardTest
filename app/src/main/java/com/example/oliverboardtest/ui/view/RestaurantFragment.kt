package com.example.oliverboardtest.ui.view

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.oliverboardtest.utils.AppConstants
import com.example.oliverboardtest.R
import com.example.oliverboardtest.api.helper.AppApiHelper
import com.example.oliverboardtest.api.model.Photos
import com.example.oliverboardtest.api.model.Restaurant
import com.example.oliverboardtest.databinding.FragmentRestaurantBinding
import com.example.oliverboardtest.ui.contract.RestaurantContract
import com.example.oliverboardtest.ui.presenter.RestaurantPresenter

open class RestaurantFragment : Fragment(), View.OnClickListener, RestaurantContract.View {

    override var presenter: RestaurantContract.Presenter? = null

    private var binding: FragmentRestaurantBinding? = null

    private var resId = 0

    private var photos = arrayListOf<Photos>()

    private var deeplink: String? = null

    private var adapter: RestaurantAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (binding == null) {
            binding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_restaurant, container, false)
        }
        presenter = RestaurantPresenter(context?.let {
            AppApiHelper(
                it
            )
        }, this)
        binding?.clickHandler = this
        resId = arguments?.getInt(AppConstants.RESTAURANT_ID) ?: 0
        presenter?.restaurant(resId)
        iniRv()
        return binding?.root
    }

    private fun iniRv() {
        adapter = RestaurantAdapter(photos)
        binding?.rvPhotos?.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = this@RestaurantFragment.adapter
            /**
             * To avoid multiple item click of the recycler view
             */
            isMotionEventSplittingEnabled = false
        }
    }

    override fun showLoading(isLoading: Boolean) {
        binding?.isLoading = isLoading
    }

    override fun showData(response: Restaurant) {
        binding?.isError = false
        photos.clear()
        response.photos?.let {
            if (it.isNotEmpty())
                photos.addAll(it)
            else {
                binding?.tvPhoto?.visibility = View.GONE
                binding?.rvPhotos?.visibility = View.GONE
            }
        }
        context?.let {
            binding?.image?.let { it1 ->
                Glide.with(it)
                    .asDrawable()
                    .load(response.featured_image)
                    .error(R.drawable.ic_placeholder)
                    .placeholder(R.drawable.ic_placeholder)
                    .priority(Priority.HIGH)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .transform(CenterCrop())
                    .into(it1)
            }
        }
        deeplink = response.deeplink
        binding?.name?.apply {
            text = response.name
            /**
             * To enable marquee the textView has to be set isSelected to true
             */
            isSelected = true
        }
        val rating = response.user_rating?.aggregate_rating
        binding?.ratingText?.text = rating
        binding?.ratings?.rating = rating?.toFloat() ?: 0f
        binding?.location?.apply {
            text = response.location?.address
            /**
             * To enable marquee the textView has to be set isSelected to true
             */
            isSelected = true
        }
        adapter?.notifyDataSetChanged()
    }

    override fun showError(error: String?) {
        binding?.isError = true
        binding?.tvError?.text = error
    }

    override fun noInternetError() {
        binding?.isError = true
        binding?.tvError?.text = getString(R.string.network_error_msg)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.tv_launch -> {
                launchZomatoApp()
            }
            R.id.tv_error_btn -> {
                presenter?.restaurant(resId)
            }
        }
    }

    private fun launchZomatoApp() {
        if (appInstalledOrNot() && deeplink?.isNotEmpty() == true) {
            openZomatoApp()
        }
    }

    private fun openZomatoApp() {
        val pm = context?.packageManager
        try {
            val intent = pm?.getLaunchIntentForPackage(AppConstants.ZOMATO_PACKAGE)
            intent?.addCategory(Intent.CATEGORY_LAUNCHER)
            intent?.data = Uri.parse(deeplink)
            context?.startActivity(intent)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

    }

    private fun appInstalledOrNot(): Boolean {
        val pm = context?.packageManager
        try {
            pm?.getPackageInfo(AppConstants.ZOMATO_PACKAGE, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return false
    }
}