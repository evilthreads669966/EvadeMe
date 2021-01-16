/*
Copyright 2020 Chris Basinger

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
import androidx.annotation.RequiresApi
import com.scottyab.rootbeer.RootBeer
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.UnknownHostException
import java.util.*
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
/**
 * @author Chris Basinger
 * @email evilthreads669966@gmail.com
 * @date 10/09/20
 *
 * [Context.Evade] is an asynchronous higher order function that takes a trailing lambda containg a block of code called your payload which is only executed if it is safe from behavioural analysis. Before the trailing lambda argument's block of code is executed there are numerous checks that happen before
 * deciding whether to execute your code. All of these checks are in place to make sure that the user of your application is not a developer, cyber security analyst, or network analyst.
 * The evasion algorithm for checking whether it is safe to execute your payload runs multiple evasion check methods returning either false or true.
 * [isEmulator], [isRooted], [hasAdbOverWifi], [isConnected], [hasVpn], [hasFirewall], [hasUsbDevices]
 * All checks must return false in order for your block of code inside the trailing lambda payload argument of [Context.evade] to execute.
 * [Context.evade] provides two callbacks: [onEscape] and [onSuccess] which are provided by [OnEvade.Escape] and [OnEvade.Success] Unfortunately for now you must call [onEscape] before
 * calling [onSuccess] which means that chaining these callbacks in that respective order is requirement.
 * You can bypass two evasion checks if you do not require networking by passing in false to the default named argument [requiresNetwork]. [requiresNetwork] has a default value of true. Passing in false for [requiresNetwork]
 * allows [Context.evade] to execute your trailing lambda payload without checking [hasFirewall] and [hasVpn].
 * No dangerous permissions are required by this KTX function, so no permission requests are required. The required non-dangerous permissions are [Manifest.permission.INTERNET],
 * [Manifest.permission.ACCESS_NETWORK_STATE], and [Manifest.permission.ACCESS_WIFI_STATE] will be merged into your app's Android.manifest file
 * when compiling.
 **/
@ExperimentalStdlibApi
inline suspend fun Context.evade(requiresNetwork: Boolean = true, crossinline payload: suspend () -> Unit): OnEvade.Escape{
    return withContext(Dispatchers.Default){
        val isEmulator = async { isEmulator }
        val isRooted = async { isRooted() }
        val hasAdbOverWifi = async { hasAdbOverWifi() }
        val isConnected = async { isConnected() }
        val hasUsbDevices = async { hasUsbDevices() }
        var hasFirewall: Deferred<Boolean>? = null
        var hasVpn: Deferred<Boolean>? = null
        if(requiresNetwork){
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.R)
                hasFirewall = async { hasFirewall() }
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                hasVpn = async { hasVPN() }
        }
        val evaded =  !(!isEmulator.await() && !isRooted.await() && !hasAdbOverWifi.await() && !isConnected.await() && !hasUsbDevices.await() && !(hasVpn?.await() ?: false) && !(hasFirewall?.await() ?: false))
        if(!evaded) payload()
        return@withContext OnEvade.Escape(evaded)
    }
}

class OnEvade{
    class Success(val result: Boolean): Result{
        suspend fun onSuccess(callback: suspend () -> Unit): Escape{
            if(!this.result)
                callback()
            return Escape(this.result)
        }
    }
    class Escape(val result: Boolean): Result{
        suspend fun onEscape(callback: suspend () -> Unit): Success{
            if(this.result)
                callback()
            return Success(this.result)
        }
    }
}

interface Result

/*Checks whether this phone is connected to a usb device such as a computer. I do not know whether this works but I believe it won't hurt to check*/
@RequiresApi(Build.VERSION_CODES.HONEYCOMB_MR1)
@PublishedApi
internal suspend fun Context.hasUsbDevices() = (this.getSystemService(Context.USB_SERVICE) as UsbManager).deviceList.isNotEmpty()

/*Checks whether the app is running on a fake device*/
@PublishedApi
internal val isEmulator  by lazy{
    (Build.DEVICE.contains("generic")
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
}

/*checks whether the device has a firewall or networking utilities app installed.*/
@PublishedApi
internal suspend fun Context.hasFirewall(): Boolean {
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
@PublishedApi
internal suspend fun Context.hasAdbOverWifi(): Boolean{
    var isOpen = false
    val mgr = this.getSystemService(Context.WIFI_SERVICE) as WifiManager
    if(!mgr.isWifiEnabled)
        return isOpen
    val wifiAddress = this.applicationContext.getWifiIpAddress(mgr)
    runCatching {
        SocketFactory.getDefault().createSocket(wifiAddress, 5555).close()
        isOpen = true
    }
    return isOpen
}

/*checks whether the network is running through a VPN*/
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
@PublishedApi
internal suspend fun Context.hasVPN(): Boolean{
    val mgr = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    mgr.allNetworks.forEach { network ->
        val capabilities = mgr.getNetworkCapabilities(network)
        if(capabilities!!.hasTransport(NetworkCapabilities.TRANSPORT_VPN))
            return true
    }
    return false
}

/*checks whether the device has super user powers SU*/
@PublishedApi
internal suspend fun Context.isRooted() = RootBeer(this).apply { setLogging(false) }.isRooted

/*checks whether there is a usb cord plugged into the phone*/
@PublishedApi
internal suspend fun Context.isConnected(): Boolean {
    val intent = this.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    val plugged = intent!!.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS
    return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB
}

private suspend fun Context.getWifiIpAddress(wifiManager: WifiManager): String?{
    val intRepresentation = wifiManager.dhcpInfo.ipAddress
    val addr = intToInetAddress(intRepresentation)
    return addr?.hostAddress
}

private suspend fun intToInetAddress(hostAddress: Int): InetAddress? {
    val addressBytes = byteArrayOf(
        (0xff and hostAddress).toByte(),
        (0xff and (hostAddress shr 8)).toByte(),
        (0xff and (hostAddress shr 16)).toByte(),
        (0xff and (hostAddress shr 24)).toByte()
    )
    return try {
        InetAddress.getByAddress(addressBytes)
    } catch (e: UnknownHostException) {
        throw AssertionError()
    }
}