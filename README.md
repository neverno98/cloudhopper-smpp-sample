cloudhopper 를 이용한 Smpp sample
==================================================

> cloudhopper 가 업데이트 되고 있지 않지만, 대안할만한 라이브러리가 적으므로 샘플을 만들어 본다.

java 기반으로 만들어져 있던 부분을 특기할 부분을 제외하고 kotlin 을 이용해서 재작성을 하며 kotlin 으로 작성했을 때 특성들을 공부하기 위한 것이다.


개발환경 
------------

- 코틀린(Kotlin) 버전 : 1.1.1
- com.cloudhoppe 버전 : 5.0.9

주요 모듈
------------

- SmppChGateway : smpp 연결을 맡아서 처리하는 부분
- SmppChHanlder : cloudhopper 의 내부에서 발생하는 event 를 커스터 마이징하기 위한 모듈 - mo/dr 등에 대해서 처리하고, 연결 끊어짐등을 관리한다
- SmppChJob : enquireLink 를 관리하여 분당 한번씩 확인을 요청하여 끊어졌을 경우 재접속을 진행한다.
- SmppChDto : 접속 관련된 정보 관리
- syniverse / openmarket / mblox / iconectiv / simulator 등 특정부분에 관련된 모듈, 다만 접속 정보등은 계약때 얻은 것을 넣어야 한다.

