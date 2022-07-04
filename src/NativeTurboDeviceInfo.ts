import {TurboModule, TurboModuleRegistry} from 'react-native';

interface Spec extends TurboModule {
  getIpAddress(): string;
  getMacAddress(): string;
  isEmulator(): boolean;
  getTotalDiskCapacity(): number;

  getFreeDiskStorage(): number;

  getCarrierSync(): string;


  isBatteryCharging(): boolean;

  getBatteryLevel(): number;

  isAirplaneMode(): boolean;

  isHeadphonesConnected(): boolean;
}

export default TurboModuleRegistry.getEnforcing<Spec>('TurboDeviceInfo');
