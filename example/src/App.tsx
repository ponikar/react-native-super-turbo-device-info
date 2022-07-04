import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import {
  getIpAddress,
  getMacAddress,
  getTotalDiskCapacity,
  isEmulator,
  getFreeDiskStorage,
  getCarrierSync,
  isBatteryCharging,
  getBatteryLevel,
  isAirplaneMode,
  isHeadphonesConnected,
} from 'react-native-turbo-device-info';

export default function App() {
  const [macAddress] = React.useState<string>(getIpAddress());
  const [ipAddress] = React.useState<string>(getMacAddress());

  React.useEffect(() => {
    console.log('isEmulator: ', isEmulator());
    console.log('getTotalDiskCapacity: ', getTotalDiskCapacity());
    console.log('getFreeDiskStorage: ', getFreeDiskStorage());
    console.log('getCarrierSync: ', getCarrierSync());
    console.log('isBatteryCharging: ', isBatteryCharging());
    console.log('getBatteryLevel: ', getBatteryLevel());
    console.log('isAirplaneMode: ', isAirplaneMode());
    console.log('isHeadphonesConnected: ', isHeadphonesConnected());
  }, []);

  return (
    <View style={styles.container}>
      <Text>Mac Address: {macAddress}</Text>
      <Text>Ip Address: {ipAddress}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
