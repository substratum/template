#!/usr/bin/env sh

cp app/build/outputs/apk/debug/app-debug.apk TelegramX-Mods-$MAJOR_MINOR.$TRAVIS_BUILD_NUMBER.apk
cp app/build/outputs/mapping/debug/mapping.txt proguard-$MAJOR_MINOR.$TRAVIS_BUILD_NUMBER.txt
 
curl -F chat_id="-1001220326034" -F document=@"TelegramX-Mods-$MAJOR_MINOR.$TRAVIS_BUILD_NUMBER.apk" https://api.telegram.org/bot$BOT_TOKEN/sendDocument
 
cp app/build/outputs/mapping/release/mapping.txt proguard-$MAJOR_MINOR.${TRAVIS_BUILD_NUMBER}_plah.txt
curl -F chat_id="-1001220326034" -F document=@"proguard-$MAJOR_MINOR.${TRAVIS_BUILD_NUMBER}_plah.txt" https://api.telegram.org/bot$BOT_TOKEN/sendDocument

 