package ru.skillbranch.skillarticles.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.databinding.ActivityRootBinding
import ru.skillbranch.skillarticles.databinding.LayoutBottombarBinding
import ru.skillbranch.skillarticles.databinding.LayoutSubmenuBinding
import ru.skillbranch.skillarticles.extensions.data.dpToIntPx
import ru.skillbranch.skillarticles.viewmodels.ArticleState
import ru.skillbranch.skillarticles.viewmodels.ArticleViewModel
import ru.skillbranch.skillarticles.viewmodels.Notify
import ru.skillbranch.skillarticles.viewmodels.ViewModelFactory

class RootActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding
    private lateinit var btmBarBinding: LayoutBottombarBinding
    private lateinit var subMenuBinding: LayoutSubmenuBinding
    private lateinit var viewModel: ArticleViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootBinding.inflate(layoutInflater)
        btmBarBinding = LayoutBottombarBinding.bind(binding.root)
        subMenuBinding = LayoutSubmenuBinding.bind(binding.root)
        setContentView(binding.root)
        setupToolbar()
        setUpBottomBar()
        setUpSubMenu()

        val vmFactory = ViewModelFactory("0")
        viewModel = ViewModelProvider(this, vmFactory)[ArticleViewModel::class.java]
        viewModel.observeState(this) {
            renderUi(it)
        }
        viewModel.observeNotifications(this) {
            renderNotification(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu?.findItem(R.id.action_search)
        val mSearchView = searchItem?.actionView as SearchView
        mSearchView.apply {

            val currentState = viewModel.getAppSettings().value

            val isSearch = currentState?.isSearch ?: false
            val currentQuery = currentState?.querySearch ?: ""

            isSubmitButtonEnabled = true
            queryHint = "Search"

            if (isSearch) {
                searchItem.expandActionView()
                onActionViewExpanded()
                setQuery(currentQuery, false)
            }

            searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    viewModel.handleSearchMode(true)
                    onActionViewExpanded()
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    viewModel.handleSearchMode(false)
                    onActionViewCollapsed()
                    return true
                }

            })

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.handleSearch(query)
                    setQuery(null, false)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.handleSearch(newText)
                    return true
                }
            })
        }

        return super.onCreateOptionsMenu(menu)
    }

    private fun renderNotification(notify: Notify) {
        val snackbar =
            Snackbar.make(binding.coordinatorContainer, notify.message, Snackbar.LENGTH_LONG)
                .setAnchorView(binding.bottombar)

        when (notify) {
            is Notify.TextMessage -> { /*nothing*/
            }
            is Notify.ActionMessage -> {
                snackbar.setActionTextColor(getColor(R.color.color_accent_dark))
                snackbar.setAction(notify.actionLabel) {
                    notify.actionHandler.invoke()
                }
            }
            is Notify.ErrorMessage -> {
                with(snackbar) {
                    setBackgroundTint(getColor(R.color.design_default_color_error))
                    setTextColor(getColor(android.R.color.white))
                    setActionTextColor(getColor(android.R.color.white))
                    setAction(notify.errLabel) {
                        notify.errAction?.invoke()
                    }
                }
            }
        }

        snackbar.show()
    }

    private fun setUpSubMenu() {
        subMenuBinding.apply {
            btnTextUp.setOnClickListener {
                viewModel.handleUpText()
            }
            btnTextDown.setOnClickListener {
                viewModel.handleDownText()
            }
            switchMode.setOnClickListener {
                viewModel.handleNightMode()
            }
        }
    }

    private fun setUpBottomBar() {
        btmBarBinding.apply {
            btnLike.setOnClickListener {
                viewModel.handleLike()
            }
            btnBookmark.setOnClickListener {
                viewModel.handleBookmark()
            }
            btnShare.setOnClickListener {
                viewModel.handleShare()
            }
            btnSettings.setOnClickListener {
                viewModel.handleToggleMenu()
            }
        }
    }

    private fun renderUi(data: ArticleState) {
        btmBarBinding.apply {
            btnSettings.isChecked = data.isShowMenu
            if (data.isShowMenu) binding.submenu.open() else binding.submenu.close()
            btnLike.isChecked = data.isLike
            btnBookmark.isChecked = data.isBookmark
        }

        subMenuBinding.apply {
            switchMode.isChecked = data.isDarkMode
            delegate.localNightMode =
                if (data.isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            if (data.isBigText) {
                binding.tvTextContent.textSize = 18f
                btnTextUp.isChecked = true
                btnTextDown.isChecked = false
            } else {
                binding.tvTextContent.textSize = 14f
                btnTextUp.isChecked = false
                btnTextDown.isChecked = true
            }
        }

        binding.apply {
            tvTextContent.text =
                if (data.isLoadingContent) "loading" else data.content.first() as String
            toolbar.title = data.title ?: "loading"
            toolbar.subtitle = data.category ?: "loading"
            if (data.category != null) toolbar.logo =
                AppCompatResources.getDrawable(this@RootActivity, data.categoryIcon as Int)
        }
    }

    private fun setupToolbar() {
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val logo = if (toolbar.childCount > 2) toolbar.getChildAt(2) as ImageView else null
        logo?.scaleType = ImageView.ScaleType.CENTER_CROP
        val lp = logo?.layoutParams as? Toolbar.LayoutParams
        lp?.let {
            it.width = this.dpToIntPx(40)
            it.height = this.dpToIntPx(40)
            it.marginEnd = this.dpToIntPx(16)
            logo.layoutParams = it
        }
    }
}