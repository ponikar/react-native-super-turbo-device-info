import NativeTurboDeviceInfo from "./NativeTurboDeviceInfo";


export function isEmulator(): Promise<boolean> {
  return NativeTurboDeviceInfo.isEmulator();
}

export function getIpAddress() :string {
  return NativeTurboDeviceInfo.getIpAddress();
}

export function getMacAddress() :string {
  return NativeTurboDeviceInfo.getMacAddress();
}