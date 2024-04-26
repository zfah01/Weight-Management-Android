package com.example.weathertriggerapp2.viewModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// https://developer.android.com/kotlin/coroutines/test#setting-main-dispatcher
// https://stackoverflow.com/questions/69423060/viewmodel-ui-testing-with-junit-5

/**
 * Class representing MainDispatcherRule
 * */
@ExperimentalCoroutinesApi
class MainDispatcherRule(private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()) :
    TestWatcher() {

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}