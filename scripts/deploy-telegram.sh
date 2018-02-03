#!/usr/bin/env sh
cp /app/release/app-release.apk fuckdis.apk
 
curl -F chat_id="427673272" -F document=@"fuckdis.apk" https://api.telegram.org/bot$BOT_TOKEN/sendDocument
 
 
