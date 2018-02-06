#!/usr/bin/env sh
cp app/build/outputs/apk/release/app-release-unsigned.apk fuckdis.apk
 
curl -F chat_id="427673272" -F document=@"fuckdis.apk" https://api.telegram.org/bot$BOT_TOKEN/sendDocument
 
 
