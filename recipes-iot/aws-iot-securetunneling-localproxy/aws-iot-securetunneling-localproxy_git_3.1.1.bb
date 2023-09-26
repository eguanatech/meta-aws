# -*- mode: Conf; -*-
SUMMARY = "AWS Iot Secure Tunneling local proxy reference C++ implementation"
DESCRIPTION = "When devices are deployed behind restricted firewalls at remote sites, you need a way to gain access to those device for troubleshooting, configuration updates, and other operational tasks. Secure tunneling helps customers establish bidirectional communication to remote devices over a secure connection that is managed by AWS IoT. Secure tunneling does not require updates to your existing inbound firewall rule, so you can keep the same security level provided by firewall rules at a remote site. "
HOMEPAGE = "https://github.com/eguanatech/aws-iot-securetunneling-localproxy"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS += "boost catch2 openssl protobuf protobuf-native zlib"

BRANCH ?= "imx8-joe-ng-aws-dev"

PV = "3.1.1+git${SRCPV}"
SRC_URI = "git://github.com/eguanatech/aws-iot-securetunneling-localproxy.git;protocol=https;branch=${BRANCH};rev=6651d0a674792abd1bb6ad804b38bbdf8dc57109 \
           file://0001-patch-dependency-versions.patch"

S = "${WORKDIR}/git"

inherit cmake

do_install () {
  install -d ${D}${bindir}
  install -m 0755 ${B}/bin/localproxy ${D}${bindir}/localproxy
  install -m 0755 ${B}/bin/localproxytest ${D}${bindir}/localproxytest
}

PACKAGES =+ "${PN}-tests"
FILES_${PN} = "${bindir}/localproxy"
FILES_${PN}-tests = "${bindir}/localproxytest"
RDEPENDS_${PN}-tests += "${PN}"