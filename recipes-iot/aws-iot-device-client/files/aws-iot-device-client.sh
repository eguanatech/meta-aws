#!/bin/sh
#
# Start/Stop the aws-iot-device-client service
NAME="aws-iot-device-client"
DESC="AWS IoT Device Client"
DAEMON=/sbin/$NAME
PIDFILE=/var/run/$NAME.pid
STATFILE=/var/run/watchdog.$NAME
UIDFILE=/var/persist/gwappd/gw_device_id.cfg

. /etc/init.d/functions || exit 1

# Exit if the package is not installed.
test -x $DAEMON || exit 2

# Function that starts the daemon/service.
do_start() {
    if [ ! -f $UIDFILE ]; then
        echo "Device UID is not set, cannot start ${DESC}"
        exit 3
    fi

    local status pid uid

    status=0
    pid=$(pidofproc $NAME) || status=$?
    uid=$(hexdump -e '16/1 "%02x""\n"' ${UIDFILE})

    case $status in
    0)
        echo "$DESC already running ($pid)."
        exit 0
        ;;
    *)
        echo $(cat /proc/uptime | printf "%.0f" $(awk '{print $1}')) >${STATFILE}
        echo "Starting $DESC ..."
        start-stop-daemon -S -b -m -p $PIDFILE -n $NAME -a $DAEMON -- --config-file /etc/aws-iot-device-client.json --thing-name 6ul-$uid --fleet-provisioning-template-parameters "{\"UUID\":\"$uid\"}"
        ;;
    esac
}

# Function that stops the daemon/service.
do_stop() {
    local pid status

    status=0
    pid=$(pidofproc $NAME) || status=$?

    case $status in
    0)
        echo -n "Stopping $DESC ..."
        # Exit when fail to stop, the kill would complain when fail
        kill -s 15 $pid >/dev/null && rm -f $PIDFILE &&
            echo "Stopped $DESC ($pid)." || exit $?
        ;;
    *)
        echo "$DESC is not running; none killed." >&2
        ;;
    esac

    return $status
}

# Function that shows the daemon/service status.
status_of_proc() {
    local pid status

    status=0
    pid=$(pidofproc $NAME) || status=$?

    case $status in
    0)
        echo "$DESC is running ($pid)."
        exit 0
        ;;
    *)
        echo "$DESC is not running." >&2
        exit $status
        ;;
    esac
}

case "$1" in
start)
    do_start
    ;;
stop)
    do_stop || exit $?
    ;;
status)
    status_of_proc
    ;;
restart)
    # Always start the service regardless the status of do_stop.
    do_stop
    do_start
    ;;
*)
    echo "Usage: $0 {start|stop|status|restart}" >&2
    exit 0
    ;;
esac
