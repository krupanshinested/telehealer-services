package com.thealer.telehealer.common.Util.Array


import kotlin.collections.ArrayList

class ArrayListUtil<E,T>  {
    fun filterList(listCutom: ArrayList<E>, filter : ArrayListFilter<E>) : ArrayList<E> {
        val fiterList = listCutom.filter { filter.needToAddInFilter(it) }
        return ArrayList(fiterList);
    }

    fun mapList(listCutom: ArrayList<E>, mapInterface : ArrayListMap<E, T>) : ArrayList<T> {
        val modelMap = listCutom.map { mapInterface.transform(it) }
        return ArrayList(modelMap);
    }
}