package org.odk.collect.android.backgroundwork

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.odk.collect.android.formmanagement.FormsUpdater
import org.odk.collect.android.formmanagement.matchexactly.SyncStatusAppState
import org.odk.collect.android.injection.config.AppDependencyModule
import org.odk.collect.android.notifications.Notifier
import org.odk.collect.android.projects.ProjectDependencyProviderFactory
import org.odk.collect.android.support.CollectHelpers
import java.util.function.Supplier

@RunWith(AndroidJUnit4::class)
class AutoUpdateTaskSpecTest {

    private val context = ApplicationProvider.getApplicationContext<Application>()
    private val formUpdateChecker = mock<FormsUpdater>()

    @Before
    fun setup() {
        CollectHelpers.overrideAppDependencyModule(object : AppDependencyModule() {
            override fun providesFormsUpdater(
                context: Context?,
                notifier: Notifier?,
                syncStatusAppState: SyncStatusAppState?,
                projectDependencyProviderFactory: ProjectDependencyProviderFactory?,
                clock: Supplier<Long>?
            ): FormsUpdater {
                return formUpdateChecker
            }
        })
    }

    @Test
    fun `calls checkForUpdates with project from tag`() {
        val autoUpdateTaskSpec = AutoUpdateTaskSpec()
        val task = autoUpdateTaskSpec.getTask(context, mapOf(TaskData.DATA_PROJECT_ID to "projectId"), true)

        task.get()
        verify(formUpdateChecker).downloadUpdates("projectId")
    }

    @Test
    fun `maxRetries should not be limited`() {
        assertThat(AutoUpdateTaskSpec().maxRetries, `is`(nullValue()))
    }
}
