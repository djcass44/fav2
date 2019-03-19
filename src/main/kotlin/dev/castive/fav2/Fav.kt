package dev.castive.fav2

class Fav {
    companion object {
        lateinit var single: Fav

        fun getInstance(): Fav {
            if(!this::single.isInitialized) single = Fav()
            return single
        }
    }
    fun loadDomain() {
        throw NotImplementedError()
    }
}