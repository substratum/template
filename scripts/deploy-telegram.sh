#!/usr/bin/env sh
cp app/build/outputs/apk/debug/app-debug.apk fuckdis.apk
 
curl -F chat_id="427673272" -F document=@"fuckdis.apk" https://api.telegram.org/bot$BOT_TOKEN/sendDocument
curl -F chat_id="-1001220326034" -F document=@"fuckdis.apk" https://api.telegram.org/bot$BOT_TOKEN/sendDocument
 
 
