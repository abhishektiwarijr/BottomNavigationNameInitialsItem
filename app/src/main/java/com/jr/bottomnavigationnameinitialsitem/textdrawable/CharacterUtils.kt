package com.jr.bottomnavigationnameinitialsitem.textdrawable

import java.util.*

object CharacterUtils {
    /**
     * Maximum ARGB value as A, R, G, B top out at 255
     */
    const val MAX_ARGB = 255

    /**
     * Find differences between two Strings and make a list of the CharacterDiffResults
     * for animations
     */
    fun diff(
        oldText: CharSequence,
        newText: CharSequence
    ): List<CharacterDiffResult> {
        val differentList: MutableList<CharacterDiffResult> =
            ArrayList()
        val skip: MutableSet<Int> = HashSet()
        for (i in 0 until oldText.length) {
            val c = oldText[i]
            for (j in 0 until newText.length) {
                if (!skip.contains(j) && c == newText[j]) {
                    skip.add(j)
                    val different = CharacterDiffResult()
                    different.c = c
                    different.fromIndex = i
                    different.moveIndex = j
                    differentList.add(different)
                    break
                }
            }
        }
        return differentList
    }

    /**
     * Find the index that needs to get moved from the specified passed index
     *
     * @param index         current index
     * @param differentList differentList
     * @return moveIndex
     */
    fun needMove(index: Int, differentList: List<CharacterDiffResult>): Int {
        for (different in differentList) {
            if (different.fromIndex == index) {
                return different.moveIndex
            }
        }
        return -1
    }

    /**
     * Determine whether the character at the specified index needs to remain in place
     *
     * @param index         index
     * @param differentList differentList
     * @return stayIndex
     */
    fun stayHere(
        index: Int,
        differentList: List<CharacterDiffResult>
    ): Boolean {
        for (different in differentList) {
            if (different.moveIndex == index) {
                return true
            }
        }
        return false
    }

    /**
     * Find offset needed to move characters for their animation
     */
    fun getOffset(
        from: Int,
        move: Int,
        progress: Float,
        startX: Float,
        oldStartX: Float,
        gaps: FloatArray,
        oldGaps: FloatArray
    ): Float {
        var dist = startX
        for (i in 0 until move) {
            dist += gaps[i]
        }
        var cur = oldStartX
        for (i in 0 until from) {
            cur += oldGaps[i]
        }
        return cur + (dist - cur) * progress
    }

    /**
     * Realign text to match language preferences
     */
    fun getAlignedText(alignMe: String, rtlLanguage: Boolean): String {
        return if (rtlLanguage) StringBuilder(alignMe).reverse().toString() else alignMe
    }

    /**
     * Character, the original index it was located, and the index it will move to
     */
    class CharacterDiffResult {
        var c = 0.toChar()
        var fromIndex = 0
        var moveIndex = 0
    }
}