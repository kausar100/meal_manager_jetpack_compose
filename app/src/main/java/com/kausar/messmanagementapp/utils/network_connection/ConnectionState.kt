package com.kausar.messmanagementapp.utils.network_connection

sealed class ConnectionState{
    object Available: ConnectionState()
    object Unavailable: ConnectionState()
}
