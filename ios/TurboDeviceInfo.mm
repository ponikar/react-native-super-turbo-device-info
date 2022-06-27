//
//  MyDeviceInfo.m
//  DeviceTurbo
//
//  Created by Darshan Ponikar on 26/06/22.
//

#include "TurboDeviceInfo.h"
#import <Foundation/Foundation.h>
#include <ifaddrs.h>
#include <arpa/inet.h>
#import <mach/mach.h>
#import <mach-o/arch.h>
#import <CoreLocation/CoreLocation.h>
#import <React/RCTUtils.h>
#import <DeviceCheck/DeviceCheck.h>
#import "EnvironmentUtil.h"

using namespace facebook;


@interface TurboDeviceInfo: NSObject<NativeTurboDeviceInfoSpec>
@end

@implementation TurboDeviceInfo

RCT_EXPORT_MODULE()

- (NSString *)getIpAddress {
  NSString *address = @"0.0.0.0";
     struct ifaddrs *interfaces = NULL;
     struct ifaddrs *temp_addr = NULL;
     int success = 0;
     // retrieve the current interfaces - returns 0 on success
     success = getifaddrs(&interfaces);
     if (success == 0) {
         // Loop through linked list of interfaces
         temp_addr = interfaces;
         while(temp_addr != NULL) {
             sa_family_t addr_family = temp_addr->ifa_addr->sa_family;
             // Check for IPv4 or IPv6-only interfaces
             if(addr_family == AF_INET || addr_family == AF_INET6) {
                 NSString* ifname = [NSString stringWithUTF8String:temp_addr->ifa_name];
                     if(
                         // Check if interface is en0 which is the wifi connection the iPhone
                         // and the ethernet connection on the Apple TV
                         [ifname isEqualToString:@"en0"] ||
                         // Check if interface is en1 which is the wifi connection on the Apple TV
                         [ifname isEqualToString:@"en1"]
                     ) {
                         const struct sockaddr_in *addr = (const struct sockaddr_in*)temp_addr->ifa_addr;
                         socklen_t addr_len = addr_family == AF_INET ? INET_ADDRSTRLEN : INET6_ADDRSTRLEN;
                         char addr_buffer[addr_len];
                         // We use inet_ntop because it also supports getting an address from
                         // interfaces that are IPv6-only
                         const char *netname = inet_ntop(addr_family, &addr->sin_addr, addr_buffer, addr_len);

                          // Get NSString from C String
                         address = [NSString stringWithUTF8String:netname];
                     }
             }
             temp_addr = temp_addr->ifa_next;
         }
     }
     // Free memory
     freeifaddrs(interfaces);
     return address;
}

- (NSString *)getMacAddress {
   if ([[UIDevice currentDevice] respondsToSelector:@selector(identifierForVendor)]) {
         return [[[UIDevice currentDevice] identifierForVendor] UUIDString];
     }
  return @"";
}

- (void)isEmulator:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
  resolve(@YES);
}

- (std::shared_ptr<react::TurboModule>)getTurboModule:(const react::ObjCTurboModule::InitParams &)params {
  return std::make_shared<react::NativeTurboDeviceInfoSpecJSI>(params);
}

@end
