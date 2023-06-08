SUMMARY = "Bootstrap certificate and key required for AWS Fleet Provisioning"
SRC_URI = "file://AmazonRootCA1.pem \
           file://bootstrap-certificate.pem.crt \
           file://bootstrap-private.pem.key"
LICENSE = "CLOSED"

do_install() {
    install -m 0700 -d ${D}${datadir}/aws-credentials
    install -m 0644 ${WORKDIR}/AmazonRootCA1.pem ${D}${datadir}/aws-credentials/
    install -m 0644 ${WORKDIR}/bootstrap-certificate.pem.crt ${D}${datadir}/aws-credentials/
    install -m 0600 ${WORKDIR}/bootstrap-private.pem.key ${D}${datadir}/aws-credentials/
}

FILES_${PN} += "${datadir}/aws-credentials/AmazonRootCA1.pem"
FILES_${PN} += "${datadir}/aws-credentials/bootstrap-certificate.pem.crt"
FILES_${PN} += "${datadir}/aws-credentials/bootstrap-private.pem.key"