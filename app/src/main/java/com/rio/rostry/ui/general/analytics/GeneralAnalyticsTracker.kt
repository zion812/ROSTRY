package com.rio.rostry.ui.general.analytics

import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

interface GeneralAnalyticsTracker {
    fun navTabClick(tabRoute: String)
    fun createEntryOpen()
    fun marketFilterApply(filterKey: String, filterValue: String?)
    fun offlineBannerSeen(context: String)
}

@Singleton
class GeneralAnalyticsTrackerImpl @Inject constructor() : GeneralAnalyticsTracker {
    override fun navTabClick(tabRoute: String) {
        Timber.tag(TAG).d("nav_tab_click route=%s", tabRoute)
    }

    override fun createEntryOpen() {
        Timber.tag(TAG).d("create_entry_open")
    }

    override fun marketFilterApply(filterKey: String, filterValue: String?) {
        Timber.tag(TAG).d("market_filter_apply key=%s value=%s", filterKey, filterValue)
    }

    override fun offlineBannerSeen(context: String) {
        Timber.tag(TAG).d("offline_banner_seen context=%s", context)
    }

    private companion object {
        private const val TAG = "GeneralAnalytics"
    }
}
