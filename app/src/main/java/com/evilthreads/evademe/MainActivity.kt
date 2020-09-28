package com.evilthreads.evademe

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.evilthreads.evade.evade

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        evade {
            Log.d("EVADE", "I LOVE YOU");
        }.onEscape{
            Toast.makeText(this, "We evaded with networking", Toast.LENGTH_LONG).show()
        }.onSuccess {
            Toast.makeText(this, "We executed the paylod with networking", Toast.LENGTH_LONG).show()
        }
        evade(requiresNetwork = false) {
            Log.d("EVADE", "I LOVE YOU");
        }.onEscape{
            Toast.makeText(this, "We evaded without networking", Toast.LENGTH_LONG).show()
        }.onSuccess {
            Toast.makeText(this, "We executed the payload without networking", Toast.LENGTH_LONG).show()
        }
    }
}