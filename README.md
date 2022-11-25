# player-with-qrcode
- player
    - 광고 데이터를 수신 후 각 광고를 렌더링하는 컴포넌트
- reader
    - 사용자가 QR 코드를 스캔했을 때 방문할 수 있는 페이지

- how to execute
    - 실행 할 폴더(app)로 이동 (ex. cd player)
    - 터미널에서 ./gradlew assembleRelease 실행하여 build
    - build 완료 후, cd app/build/outputs/apk/release/app-release-unsigned.apk 파일 확인
    - .apk 파일 android device에 deploy