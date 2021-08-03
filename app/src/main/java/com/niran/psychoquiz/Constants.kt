package com.niran.psychoquiz

const val DISPLAY_ALL_WORDS_LIST = '0'

enum class LoadingState(var message: String = "") { LOADING, SUCCESS, ERROR }

enum class AnyTypes {
    BOOLEAN,
    INT,
    STRING,
    DOUBLE
}