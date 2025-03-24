#! /bin/sh
# This script is used to start/stop/restart the AWS IoT Device Client.
# It is installed in /etc/init.d/aws-iot-device-client by the aws-iot-device-client recipe.
# It is started automatically when the system boots up.
#
# The AWS IoT Device Client is installed in /sbin/aws-iot-device-client by the aws-iot-device-client recipe.
# The AWS IoT Device Client configuration file is installed in /etc/aws-iot-device-client/config.json by the aws-iot-device-client recipe.
# The AWS IoT Device Client log file is installed in /var/log/aws-iot-device-client/aws-iot-device-client.log by the aws-iot-device-client recipe.
# The AWS IoT Device Client SDK log file is installed in /var/log/aws-iot-device-client/sdk.log by the aws-iot-device-client recipe.
# The AWS IoT Device Client Device certificate is installed in /etc/aws-credentials/device.pem.crt after successful fleet provisioning.
# The AWS IoT Device Client Device private key is installed in /etc/aws-credentials/device.private.key after successful fleet provisioning.

PATH=/usr/local/sbin:/usr/local/bin:/sbin:/bin:/usr/sbin:/usr/bin:/usr/local/egear/
NAME="aws-iot-device-client"
DESC="AWS IoT Device Client"
DAEMON=/sbin/$NAME
PIDFILE=/var/run/$NAME.pid
STATFILE=/var/run/watchdog.$NAME
UIDFILE=/var/persist/gwappd/gw_device_id.cfg

. /etc/init.d/functions || exit 1

# Check if the AWS IoT Device Client is installed
test -x $DAEMON || exit 2

# Function that starts the daemon/service
do_start() {
    if [ ! -f $UIDFILE ]; then
        echo "Device UID is not set, cannot start $DESC."
        exit 3
    fi

    local pid UID
    pid=$(pidof $NAME)
    uid=$(hexdump -e '16/1 "%02x""\n"' ${UIDFILE})

    if [ -z "$pid" ]; then
        echo $(cat /proc/uptime | printf "%.0f" $(awk '{print $1}')) >${STATFILE}
        echo "Starting $DESC..."
        start-stop-daemon -S -b -m -p $PIDFILE -x $DAEMON -- --config-file /etc/aws-iot-device-client/config.json --thing-name rpi-$uid --fleet-provisioning-template-parameters "{\"UUID\":\"$uid\", \"HW\":\"rpi\"}"
    else
        echo "$DESC is already running ($pid)"
        exit 0
    fi
}

# Function that stops the daemon/service
do_stop() {
    if [ ! -f $PIDFILE ]; then
        echo "$DESC is not running" >&2
    else
        echo "Stopping $DESC..."
        start-stop-daemon -K -p $PIDFILE -x $DAEMON
        rm -f $PIDFILE
        echo "$DESC is stopped"
    fi
}

# Function that returns the status of the daemon/service
do_status() {
    if [ ! -f $PIDFILE ]; then
        echo "$DESC is not running" >&2
    else
        echo "$DESC is running ($(cat $PIDFILE))"
    fi
}

case "$1" in
start)
    do_start
    ;;
stop)
    do_stop
    ;;
restart)
    do_stop
    do_start
    ;;
status)
    do_status
    ;;
*)
    echo "Usage: $0 {start|stop|restart|status}" >&2
    exit 0
    ;;
esac
