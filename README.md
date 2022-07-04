# react-native-turbo-device-info

JSI Turbo module support for `react-native-device-info`.

## Installation

> NOTE: Before you get started, this library will work only on react-native new architecture. Make sure you are using `react-native 0.68` version at least. This is a pre-release version your project might be crashed in production. 

```sh
npm install react-native-turbo-device-info
```

## ios
1. In your podfile make sure you have enabled hermes and fabric flag.
```ruby 
  flags[:hermes_enabled] = true
  flags[:fabric_enabled] = true
```

2. Pod install with `RCT_NEW_ARCH_ENABLED` flag

```sh
RCT_NEW_ARCH_ENABLED=1 bundle exec pod install
```

## Android 

1. In android make sure you have hermes enabled. Follow [this step](https://reactnative.dev/docs/hermes) to enable hermes. 

2. Enable new Architecture in `gradle.properites` file

```gradle
newArchEnabled=true
```

3. clean and rebuild your project.


## Usage

Curretly This library has only 2 working methods as an experiment.

```js
import { getIpAddress, getMacAddress } from "react-native-turbo-device-info";

// Don't need to use Promise based function since we are using JSI turbo modules this call can be syncronised.

const ipAddress = getIpAddress();

const getMacAddress = getMacAddress();
```

## Contributing

If you are having any idea that can help this library to be more useful, you can make PR.

If you are facing any problems while installing this library, feel free to create an issue.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
