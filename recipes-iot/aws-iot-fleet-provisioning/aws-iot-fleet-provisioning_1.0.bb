SUMMARY = "Bootstrap Certificate And Key"
SRC_URI = "file://AmazonRootCA1.pem \
           file://bootstrap.cert.pem \
           file://bootstrap.private.key"
LICENSE = "CLOSED"

do_install() {
    install -d -m 0700 ${D}${sysconfdir}/aws-credentials
    install -m 0644 ${WORKDIR}/AmazonRootCA1.pem ${D}${sysconfdir}/aws-credentials/
    install -m 0644 ${WORKDIR}/bootstrap.cert.pem ${D}${sysconfdir}/aws-credentials/
    install -m 0600 ${WORKDIR}/bootstrap.private.key ${D}${sysconfdir}/aws-credentials/
}

FILES_${PN} += "${sysconfdir}/aws-credentials/AmazonRootCA1 \
                ${sysconfdir}/aws-credentials/bootstrap.cert.pem \
                ${sysconfdir}/aws-credentials/bootstrap.private.key"