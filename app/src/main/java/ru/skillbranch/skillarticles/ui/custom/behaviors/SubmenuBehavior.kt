package ru.skillbranch.skillarticles.ui.custom.behaviors

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.marginEnd
import ru.skillbranch.skillarticles.ui.custom.ArticleSubmenu
import ru.skillbranch.skillarticles.ui.custom.Bottombar

/**
 * Created by Oleksiy Pasmarnov on 26.11.21
 */
class SubmenuBehavior: CoordinatorLayout.Behavior<ArticleSubmenu>()  {
    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: ArticleSubmenu,
        dependency: View
    ): Boolean {
        return dependency is Bottombar
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: ArticleSubmenu,
        dependency: View
    ): Boolean {
        if (dependency is Bottombar && dependency.translationY >= 0) {
            animate(child, dependency)
            return true
        } else {
            return false
        }
    }

    private fun animate(child: ArticleSubmenu, dependency: Bottombar) {
        val fraction = dependency.translationY / dependency.minimumHeight
        child.translationX = (child.width + child.marginEnd) * fraction
    }
}