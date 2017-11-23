package com.playtomic

import com.stripe.Stripe
import com.stripe.model.Charge
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import javax.sound.sampled.AudioSystem


fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Stripe key required")
        return
    }

    val purchaseBuzzer = Buzzer(fileName = "bell_2.wav")
    val refundBuzzer = Buzzer(fileName = "emergency.wav")
    val stripeKey = args[0]
    println("Connecting to Stripe using key: $stripeKey")
    Stripe.apiKey = stripeKey

    var lastCharge = Charge.list(mapOf("limit" to 1)).data.first()
    println("OK [${Date()}] - Last charge: ${lastCharge.id} made on ${lastCharge.formattedDate()}")
    purchaseBuzzer.play()
    Thread.sleep(300)
    purchaseBuzzer.play()
    Thread.sleep(300)
    purchaseBuzzer.play()
    Thread.sleep(300)

    while (true) {
        Thread.sleep(45000)
        try {
            val newCharges = Charge.list(mapOf("ending_before" to lastCharge.id))
            for (charge in newCharges.data.reversed()) {
                if (charge.status == "succeeded") {
                    if (charge.refunded) {
                        println("OK [${Date()}] - Refunded charge: ${charge.id} made on ${charge.formattedDate()}")
                        refundBuzzer.play()
                    } else {
                        println("OK [${Date()}] - New charge: ${charge.id} made on ${charge.formattedDate()}")
                        purchaseBuzzer.play()
                    }
                    Thread.sleep(4000)
                }
                lastCharge = charge
            }
        } catch (t: Throwable) {
            println("ERR [${Date()}] - Error connecting to Stripe: ${t.localizedMessage}")
            t.printStackTrace()
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

