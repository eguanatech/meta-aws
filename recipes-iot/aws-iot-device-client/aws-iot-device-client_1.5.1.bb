# -*- mode: Conf; -*-
SUMMARY = "AWS IoT Device Client"
DESCRIPTION = "The AWS IoT Device Client is free, open-source, modular software written in C++ that you can compile and install on your Embedded Linux based IoT devices to access AWS IoT Core, AWS IoT Device Management, and AWS IoT Device Defender features by default."
HOMEPAGE = "https://github.com/eguanatech/aws-iot-device-client"
LICENSE = "Apache-2.0"
PROVIDES = "aws/aws-iot-device-client"
PACKAGES:${PN} += "aws-crt-cpp"
PREFERRED_RPROVIDER:${PN} += "aws/crt-cpp"

LIC_FILES_CHKSUM = "file://LICENSE;md5=3eb31626add6ada64ff9ac772bd3c653"

BRANCH ?= "imx6-eguana-aws-dev"

SRC_URI = "git://github.com/eguanatech/aws-iot-device-client.git;protocol=https;branch=${BRANCH};rev=2443eed5174ae5566d748f0c8ab0c0cc547d94df \
           file://01-missing-thread-includes.patch \
           file://02-missing-thread-includes.patch \
           file://aws-iot-device-client.json \
           file://aws-iot-device-client \
"

S= "${WORKDIR}/git"
DEPENDS = "openssl aws-iot-device-sdk-cpp-v2 googletest"
RDEPENDS:${PN} = "openssl aws-iot-device-sdk-cpp-v2"

inherit cmake

do_configure:append() {
}

do_install() {
  install -d ${D}${base_sbindir}
  install -d ${D}${sysconfdir}
  install -d ${D}${systemd_user_unitdir}
  install -d ${D}${sysconfdir}/init.d
  install -d ${D}${sysconfdir}/rc5.d

  install -m 0755 ${WORKDIR}/build/aws-iot-device-client \
                  ${D}${base_sbindir}/aws-iot-device-client
  install -m 0644 ${S}/setup/aws-iot-device-client.service \
                  ${D}${systemd_user_unitdir}/aws-iot-device-client.service
  install -m 0644 ${WORKDIR}/aws-iot-device-client.json \
                  ${D}${sysconfdir}/aws-iot-device-client.json
  install -m 0755 ${WORKDIR}/aws-iot-device-client \
                  ${D}${sysconfdir}/init.d/aws-iot-device-client
  
  sed -i -e "s,/sbin/aws-iot-device-client,/sbin/aws-iot-device-client --config /etc/aws-iot-device-client.json,g" \
    ${D}${systemd_user_unitdir}/aws-iot-device-client.service

  cd ${D}${sysconfdir}/rc5.d
  ln -sf ../init.d/aws-iot-device-client ./S99aws-iot-device-client
}

AWSIOTDC_EXCL_JOBS ?= "ON"
AWSIOTDC_EXCL_DD ?= "ON"
AWSIOTDC_EXCL_ST ?= "OFF"
AWSIOTDC_EXCL_FP ?= "OFF"

OECMAKE_BUILDPATH += "${WORKDIR}/build"
OECMAKE_SOURCEPATH += "${S}"
EXTRA_OECMAKE += "-DBUILD_SDK=OFF"
EXTRA_OECMAKE += "-DBUILD_TEST_DEPS=OFF"
EXTRA_OECMAKE += "-DBUILD_TESTING=OFF"
EXTRA_OECMAKE += "-DCMAKE_BUILD_TYPE=Release"
EXTRA_OECMAKE += "-DCMAKE_VERBOSE_MAKEFILE=ON"
EXTRA_OECMAKE += "-DCMAKE_CXX_FLAGS_RELEASE=-s"
EXTRA_OECMAKE += "-DEXCLUDE_JOBS=${AWSIOTDC_EXCL_JOBS}"
EXTRA_OECMAKE += "-DEXCLUDE_DD=${AWSIOTDC_EXCL_DD}"
EXTRA_OECMAKE += "-DEXCLUDE_ST=${AWSIOTDC_EXCL_ST}"
EXTRA_OECMAKE += "-DEXCLUDE_FP=${AWSIOTDC_EXCL_FP}"

FILES:${PN} += "${base_sbindir}/sbin/aws-iot-device-client"
FILES:${PN} += "${systemd_user_unitdir}/aws-iot-device-client.service"
FILES:${PN} += "${sysconfdir}/aws-iot-device-client.json"
FILES:${PN} += "${sysconfdir}/init.d/aws-iot-device-client"

INSANE_SKIP:${PN}:append = "already-stripped"

inherit systemd
SYSTEMD_AUTO_ENABLE = "enable"
SYSTEMD_SERVICE:${PN} = "aws-iot-device-client.service"