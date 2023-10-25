package com.example.componentsui.stories.page

interface PageState<out T> {

    val screens: List<T>?

    /*    object Idle : PageState<Any> {
            override val list: List<Any>? = null
        }

        object Loading : PageState
        data class Success<T>(val items: List<T>) : PageState
        data class Error(val e: Throwable) : PageState*/
}

/*
data class StoriesPage<Config, Screen>(
    val config: Config,
    val screens: List<Screen>?
)

enum class Type {
    IDLE,
    LOADING,
    SUCCESS,
    ERROR
}*/
