package com.example.caresses_pepper.utlis

object StickerUtils {

    data class Sticker (var stickerId: String, var attachedObject: String)
    var stickers = arrayListOf(
        Sticker("C09A634D9870AEDC","pinkshoe"),
        Sticker("B15BC4B081D2A7E0","lemonchair"),
        Sticker("C15DE122BF2973DF","icedog"),
        Sticker("71417EE260BBA6F3","candydoor")

    )


}