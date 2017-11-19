package com.playtomic

import com.stripe.Stripe
import com.stripe.model.Charge
import java.net.URL
import java.text.SimpleDateFormat
import javax.sound.sampled.AudioSystem


fun main(args : Array<String>) {
    if (args.isEmpty()) {
        println("Stripe key required")
        return
    }

    val alarm = Buzzer(fileName = "bell_2.wav")
    val stripeKey = args[0]
    println("Connecting to Stripe using key: $stripeKey")
    Stripe.apiKey = stripeKey

    var lastCharge = Charge.list(mapOf("limit" to 1)).data.first()
    println("Last charge: ${lastCharge.id} \n\tmade on ${lastCharge.formattedDate()}")
    alarm.play()

    while (true) {
        Thread.sleep(60000)
        val newCharges = Charge.list(mapOf("ending_before" to lastCharge.id))
        for (charge in newCharges.data.reversed()) {
            println("New charge: ${lastCharge.id} \n\tmade on ${lastCharge.formattedDate()}")
            alarm.play()
            Thread.sleep(2000)
            lastCharge = charge
        }
    }
}

class Buzzer(fileName: String) {
    private val resourceURL: URL = this.javaClass.classLoader.getResource(fileName)

    fun play() {
        val audioInput = AudioSystem.getAudioInputStream(resourceURL)
        val clip = AudioSystem.getClip()
        clip.open(audioInput)
        clip.start()
    }
}

fun Charge.formattedDate(): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
    return dateFormat.format(this.created * 1000)
}

