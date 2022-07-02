import {TurboModule, TurboModuleRegistry} from 'react-native';

interface Spec extends TurboModule {
  getIpAddress(): string;

  getIpAddressAsync(): Promise<string>;

  isEmulatorAsync(): Promise<boolean>;

  getMacAddress(): string;
}

export default TurboModuleRegistry.getEnforcing<Spec>('TurboDeviceInfo');
