package com.eighthour.makers.sis.libs.util

import java.io.Serializable

/**
 * Created by Omjoon on 2017. 5. 29..
 */

data class Quardruple<out A, out B, out C, out D>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D
) : Serializable {

    /**
     * Returns string representation of the [Quardruple] including its [first], [second] and [third],[fourth] values.
     */
    override fun toString(): String = "($first, $second, $third, $fourth)"
}