const TurboDeviceInfo = require('./NativeTurboDeviceInfo').default

export function multiply(a: number, b: number): Promise<number> {
  return TurboDeviceInfo.multiply(a, b);
}
