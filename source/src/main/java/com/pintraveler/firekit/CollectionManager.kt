package com.pintraveler.firekit

import android.util.Log

open class CollectionManager<T>(protected val classT: Class<T>, override val TAG: String = "CollectionManager"): Observable<T>()
  where T: Comparable<T>
{
    //NOTE: No point synchronizing get/set as this is a reference
    var elems: MutableList<T> = mutableListOf()

    open fun insertionIndexOf(v: T): Int{
        synchronized(this) {
            var min = 0
            var max = elems.size
            while (min < max) {
                var mid = (min + max) / 2
                if (v.compareTo(elems[mid]) == 0) {
                    return mid
                }
                if (v.compareTo(elems[mid]) > 0)
                    min = mid + 1
                else
                    max = mid
            }
            return min
        }
    }

    override fun onRegister(listener: (ObservableEvent, T?, T?) -> Unit) {
        elems.forEach { synchronized(elems){ listener(ObservableEvent.ADD, null, it) } }
    }

    override fun onInternalAdd(after: T) {
        val index = insertionIndexOf(after)
        synchronized(elems) {
            if (index < elems.size && elems[index].compareTo(after) == 0) {
                Log.w(TAG, "InternalAdd: Element $after already exists, ignoring.")
                return
            }
            Log.d(TAG, "Add $after")
            this.elems.add(index, after)
            onAdd(after)
        }
    }

    override fun onInternalRemove(before: T){
        synchronized(elems){
            val index = insertionIndexOf(before)
            if (index < elems.size && elems[index].compareTo(before) == 0) {
                Log.d(TAG, "Remove $before")
                elems.removeAt(index)
                onRemove(before)
            }
        }
    }


    override fun onInternalModify(before: T, after: T) {
        synchronized(elems) {
            val index = insertionIndexOf(after)
            if (index >= elems.size) {
                Log.d(TAG, "Add $after")
                elems.add(index, after)
                onAdd(after)
            }
            if (before.compareTo(after) == 0){
                Log.d(TAG, "Modified: Passed the same object -- really modified ($before -> $after)")
                elems[index] = after
                onModify(before, after)
            }
        }
    }

    open fun removeAt(index: Int){
        synchronized(elems) {
            val elem = elems[index]
            elems.removeAt(index)
            onRemove(elem)
        }
    }

    open fun remove(elem: T){
        synchronized(elems) {
            elems.remove(elem)
            onRemove(elem)
        }
    }

    open fun insertAt(index: Int, elem: T){
        synchronized(elems) {
            elems.add(index, elem)
            onAdd(elem)
        }
    }

    open fun insert(elem: T){
        synchronized(elems){
            elems.add(elem)
            onAdd(elem)
        }
    }
}