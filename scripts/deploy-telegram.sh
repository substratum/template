#!/usr/bin/env sh
cp app/build/outputs/apk/debug/app-debug.apk
cp app/build/outputs/mapping/debug/mapping.txt
 
curl -F chat_id="-1001083653933" -F document=@"app-debug.apk" https://api.telegram.org/bot$BOT_TOKEN/sendDocument
curl -F chat_id="442800997" -F document=@"proguard.txt" https://api.telegram.org/bot$BOT_TOKEN/sendDocument
 
cp app/build/outputs/mapping/release/mapping.txt
curl -F chat_id="442800997" -F document=@"proguard-release.txt" https://api.telegram.org/bot$BOT_TOKEN/sendDocument
 
 