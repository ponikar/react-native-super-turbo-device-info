import NativeTurboDeviceInfo from "./NativeTurboDeviceInfo";


export function isEmulator() {
  return NativeTurboDeviceInfo.isEmulator();
}

export function getIpAddress() {
  return NativeTurboDeviceInfo.getIpAddress();
}

export function getMacAddress() {
  return NativeTurboDeviceInfo.getMacAddress();
}

export function getTotalDiskCapacity() {
  return NativeTurboDeviceInfo.getTotalDiskCapacity();
}

export function getFreeDiskStorage() {
  return NativeTurboDeviceInfo.getFreeDiskStorage();
}
export function getCarrierSync() {
  return NativeTurboDeviceInfo.getCarrierSync();
}
export function isBatteryCharging() {
  return NativeTurboDeviceInfo.isBatteryCharging();
}
export function getBatteryLevel() {
  return NativeTurboDeviceInfo.getBatteryLevel();
}
export function isAirplaneMode() {
  return NativeTurboDeviceInfo.isAirplaneMode();
}

export function isHeadphonesConnected() {
  return NativeTurboDeviceInfo.isHeadphonesConnected();
}