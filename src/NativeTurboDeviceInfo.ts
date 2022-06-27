import {TurboModule, TurboModuleRegistry} from 'react-native';

interface Spec extends TurboModule {
  getIpAddress(): string;

  isEmulator(): Promise<boolean>;

  getMacAddress(): string;
}

export default TurboModuleRegistry.getEnforcing<Spec>('TurboDeviceInfo');
