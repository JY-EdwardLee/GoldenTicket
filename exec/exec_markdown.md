# exec

1. [Gitlab 소스 클론 이후 빌드 및 배포할 수 있도록 정리한 문서](#1-gitlab-소스-클론-이후-빌드-및-배포할-수-있도록-정리한-문서)
    - 1.1. [사용한 JVM, 웹서버, WAS 제품 등의 종류와 설정 값, 버전(IDE 포함) 기재](#사용한-jvm-web서버-was-제품-등의-종류와-설정-값-버전ide-포함-기재)
    - 1.2. [빌드 시 사용되는 환경 변수 등의 내용 상세 기재](#빌드-시-사용되는-환경-변수-등의-내용-상세-기재)
    - 1.3. [배포 시 특이사항 기재](#배포-시-특이사항-기재)
    - 1.4. [DB 접속 정보 등 프로젝트(ERD)에 활용되는 주요 계정 및 프로퍼티가 정의된 파일 목록](#db-접속-정보-등-프로젝트erd에-활용되는-주요-계정-및-프로퍼티가-정의된-파일-목록)
2. [프로젝트에서 사용하는 외부 서비스 정보를 정리한 문서](#2-프로젝트에서-사용하는-외부-서비스-정보를-정리한-문서)
3. [DB 덤프 파일 최신본](#3-db-덤프-파일-최신본)
4. [시연 시나리오](#4-시연-시나리오)
---

## 1. Gitlab 소스 클론 이후 빌드 및 배포할 수 있도록 정리한 문서

### 사용한 JVM, 웹서버, WAS 제품 등의 종류와 설정 값, 버전(IDE 포함) 기재

1. 백엔드
    1. 개발 환경
        - IntelliJ IDEA(2025.2)
    2. 빌드 도구
        - Gradle(8.7)
    3. JVM
        - Java(17)
    4. 프레임워크
        - Spring Boot
            - Spring Boot 버전 : 3.2.5
            - 주요 사용 모듈
                - spring-boot-starter-web : Spring MVC를 사용하여 RESTful API를 구축
                - spring-boot-starter-security : 인증 및 인가 처리를 위해 Spring Security를 사용
                - spring-boot-starter-oauth2-client : Kakao, Naver 등 OAuth 2.0 기반 소셜 로그인을 구현
                - spring-boot-starter-data-redis : Redis와의 연동을 지원
                - spring-boot-starter-mail : 이메일 발송 기능을 위해 사용
                - spring-boot-starter-websocke t: 실시간 양방향 통신을 위해 WebSocket을 사용
    5. 웹 서버 / WAS
        - 내장 Tomcat : spring-boot-starter-web 의존성에 기본적으로 포함된 내장 Tomcat을 WAS로 사용
            - 서버 포트 : 8080
    6. 데이터베이스
        - PostgreSQL
        - MyBatis
            - Mapper XML 경로 : mybatis.mapper-locations=classpath:/mapper/**/*.xml
        - Redis
    7. 외부 서비스 및 라이브러리
        - 인증
            - JWT
            - OAuth 2.0: Kakao, Naver 로그인을 위해 Spring Security의 OAuth 2.0 클라이언트 기능을 사용
        - 파일 저장소
            - AWS S3 : 파일 업로드 및 관리에 Amazon S3 사용
        - 크롤링
            - 야구 경기 관련 데이터를 수집
        - API 문서화
            - Springdoc : API 명세를 자동으로 생성, /swagger-ui.html 경로에서 확인
        - QR 코드
            - Zxing : QR 코드를 생성
        - SMS 발송
            - Nurigo : SMS 메시지를 발송하는 기능
        - 결제
            - KakaoPay : 카카오페이 결제 기능
        - 기타
            - Lombok : @Data, @Getter 등의 어노테이션을 통해 반복적인 코드를 줄이기 위해 사용
            - Jackson : JSON 데이터 처리를 위해 사용
            - Jakarta Mail : spring-boot-starter-mail과 함께 메일 전송
2. 프론트엔드
    1. 개발 환경
        - VS Code
    2. 빌드 도구
        - Node.js(v24)
        - Vite(7.0.4)
    3. 프레임워크
        - Vue
            - Vue 버전 : 3.5.17
    4. 외부 서비스 및 라이브러리
        - Pinia : Vue의 공식 상태 관리 라이브러리
        - Axios : 백엔드 API 서버와 HTTP 통신을 하기 위한 라이브러리
        - ESLint / Prettier : 코드 스타일 유지, 잠재적 오류를 찾아주는 린팅 및 포맷팅 도구

### 빌드 시 사용되는 환경 변수 등의 내용 상세 기재

- 중앙 관리 및 주입
    - S13P11A109\.gitlab-ci.yml
    - 정의된 변수들은 GitLab의 Settings > CI/CD > Variables에 키-값 형태로 저장, 배포 시 EC2 서버에 환경변수로 전달
1. 데이터베이스 (PostgreSQL)
    - SPRING_DATASOURCE_URL
        - 설정 : spring.datasource.url
        - 설명 : PostgreSQL 데이터베이스의 연결 주소를 설정
    - SPRING_DATASOURCE_USERNAME
        - 설정 : spring.datasource.username
        - 설명 : 데이터베이스에 접속하기 위한 사용자 계정 이름을 설정
    - SPRING_DATASOURCE_PASSWORD
        - 설정 : spring.datasource.password
        - 설명 : 데이터베이스 사용자 계정의 비밀번호를 설정
2. Redis
    - SPRING_EC2_URL
        - 설정 : spring.data.redis.host
        - 설명 : Redis 서버의 호스트 주소를 설정
    - SPRING_REDIS_PASSWORD
        - 설정 : spring.data.redis.password
        - 설명 : Redis 서버 접속에 필요한 비밀번호를 설정
3. 인증 (JWT & OAuth 2.0)
    - JWT_SECRET
        - 설정 : jwt.secret
        - 설명 : JWT 생성 및 검증할 때 사용
    - KAKAO_REST_API_KEY
        - 설정 : kakao.rest.api.key, spring.security.oauth2.client.registration.kakao.client-id
        - 설명 : 카카오 로그인 및 기타 카카오 API 사용을 위한 REST API 키
    - KAKAO_CLIENT_SECRET
        - 설정 : spring.security.oauth2.client.registration.kakao.client-secret
        - 설명 : 카카오 로그인 인증 시 필요한 클라이언트 시크릿 값
    - NAVER_CLIENT_ID
        - 설정 : [naver.client.id](http://naver.client.id/), spring.security.oauth2.client.registration.naver.client-id
        - 설명 : 네이버 로그인을 위한 클라이언트 ID
    - NAVER_CLIENT_SECRET
        - 설정 : naver.client.secret, spring.security.oauth2.client.registration.naver.client-secret
        - 설명 : 네이버 로그인 인증 시 필요한 클라이언트 시크릿 값
4. 외부 서비스 API 키 및 설정
    - KAKAOPAY_API_KEY
        - 설정 : kakaopay.api-key
        - 설명 : 카카오페이 결제 서비스를 이용하기 위한 Admin 키
    - CLOUD_AWS_CREDENTIALS_ACCESS_KEY
        - 설정 : cloud.aws.credentials.access-key
        - 설명 : AWS S3 버킷에 접근하기 위한 IAM 사용자의 액세스 키
    - CLOUD_AWS_CREDENTIALS_SECRET_KEY
        - 설정 : cloud.aws.credentials.secret-key
        - 설명 : AWS S3 버킷 접근용 IAM 사용자의 시크릿 액세스 키
    - SMS_API_KEY
        - 설정 : SMS_API_KEY
        - 설명 : SMS 발송 서비스를 이용하기 위한 API 키
    - SMS_API_SECRET_KEY
        - 설정 : SMS_API_SECRET_KEY
        - 설명 : SMS 발송 서비스의 API 시크릿 키
    - MAIL_USERNAME
        - 설정 : spring.mail.username
        - 설명 : SMTP를 통해 이메일을 발송할 Gmail 계정
    - MAIL_PASSWORD
        - 설정 : spring.mail.password
        - 설명 : Gmail 계정의 앱 비밀번호
    - CHATBOT_API_URL
        - 설정 : CHATBOT_API_URL
        - 설명 : 연동할 챗봇 서비스의 API 엔드포인트 URL
    - CHATBOT_SECRET_KEY
        - 설정 : CHATBOT_SECRET_KEY
        - 설명 : 챗봇 서비스 API를 인증하기 위한 시크릿 키
5. 애플리케이션 URL
    - FE_BASE_URL
        - 설정 : FE_BASE_URL
        - 설명 : 프론트엔드 애플리케이션의 기본 URL 주소
    - BE_BASE_URL
        - 설정 : BE_BASE_URL
        - 설명 : 백엔드 애플리케이션의 기본 URL 주소

### 배포 시 특이사항 기재

- 종합
    - 실행 순서 : master 브랜치에 변경 사항이 푸시되면 파이프라인이 실행
        1. `build`  : frontend_build Job과 build 잡이 동시에 실행
        2. `deploy`  : build 스테이지의 두 잡이 모두 성공하면 deploy 잡이 실행
    - 배포 서버 : 모든 결과물은 GitLab CI/CD 변수인 $SPRING_EC2_URL에 해당하는 EC2 인스턴스에 배포
    - 인증 방식 : GitLab CI/CD 변수로 등록된 $SSH_PRIVATE_KEY를 사용하여 EC2 서버에 SSH로 접속
1. 프론트엔드 배포 상세
    - 빌드 `frontend_build`
        - node : 24 도커 이미지 환경에서 실행
        - Ticket-FrontEnd 디렉토리로 이동하여 npm install로 의존성을 설치하고 npm run build로 빌드
        - 특이사항 : 빌드 스크립트 중간에 rollup 관련 문제를 해결하기 위한 npm uninstall/install 과정이 포함
        - 빌드 결과물인 dist 폴더는 artifacts로 지정되어 deploy에서 사용할 수 있도록 1시간 동안 보관
    - 배포 `deploy`
        - deploy 잡의 스크립트 중 scp -r -i ~/.ssh/id_rsa Ticket-FrontEnd/dist/* $EC2_USER@$EC2_HOST:/var/www/html/ 명령어가 실행
        - 빌드된 dist 폴더 안의 모든 파일(*)을 EC2 서버의 /var/www/html/ 디렉토리로 복사
        - 프론트엔드 배포는 Nginx나 Apache 같은 웹서버가 `/var/www/html/`를 루트 디렉토리로 하여 정적 파일을 서빙하는 방식으로 이루어짐
2. 백엔드 배포 상세
    - 빌드 `build`
        - gradle : 7-jdk17 이미지 환경에서 실행되므로, Java 17을 사용
        - Ticket_BackEnd 디렉토리에서 ./gradlew clean build -x test 명령어로 테스트를 제외하고 빌드를 수행
        - 생성된 Ticket_BackEnd-0.0.1-SNAPSHOT.jar 파일의 이름을 goldenticketBE.jar로 변경하여 artifacts로 저장
    - 배포 `deploy`
        - scp 명령어로 goldenticketBE.jar 파일을 EC2 서버의 /home/ubuntu/app 경로로 전송
        - ssh 명령어로 EC2에 접속하여 /home/ubuntu/app/deploy.sh 셸 스크립트를 실행
        - deploy.sh를 실행할 때, GitLab CI/CD에 등록된 환경 변수들($SPRING_DATASOURCE_URL, $JWT_SECRET 등)을 EC2 서버의 환경변수로 주입한 뒤 스크립트를 실행
        - EC2 서버에 미리 준비된 `deploy.sh` 스크립트를 통해 실행. 기존에 실행 중이던 Java 프로세스를 종료하고, 새로 업로드된 JAR 파일을 환경변수와 함께 실행
3. 챗봇 배포 상세
    - 빌드 : 별도의 빌드 스테이지가 없음. 소스 코드 전체가 EC2 서버로 전송된 후 서버 내에서 직접 도커 이미지를 빌드
    - 배포 `deploy`
        - scp 명령어로 Ticket-Chatbot 폴더 전체를 EC2 서버의 /home/ubuntu/ 경로로 복사
        - ssh로 EC2에 접속하여 다음 작업들을 순차적으로 수행
            - docker-compose down : 기존에 실행 중이던 챗봇 컨테이너를 중지 및 삭제
            - docker image prune -f : 사용하지 않는 도커 이미지를 정리
            - echo ... > .env : GitLab CI/CD 변수($OPENAI_API_KEY 등)를 사용하여 Ticket-Chatbot 폴더 내에 .env 파일을 생성
            - docker-compose up --build -d: docker-compose.yml 파일을 사용하여 이미지를 새로 빌드하고 백그라운드에서 컨테이너를 실행
        - Docker Compose를 통해 EC2 서버에서 직접 빌드되고 배포
    1. 기타
        - Redis와 PostgreSQL은 EC2 서버에서 Docker Compose를 통해 배포
        - `docker-compose.yml` 파일 내에 Redis, PostgreSQL 서비스 정의
        - EC2 서버 재시작 시에도 자동으로 컨테이너가 재실행되도록 설정
        - 배포 시 특이사항
        - GitLab CI/CD 파이프라인에서는 Redis/PostgreSQL을 따로 빌드·배포하지 않음
        - EC2 서버 내에서 이미 실행 중인 Docker Compose 서비스를 그대로 활용
        - 데이터 유지 필요로 인해 DB 컨테이너의 볼륨(`docker volume`)은 별도로 마운트하여 컨테이너 재생성 시에도 데이터가 보존되도록 구성

### DB 접속 정보 등 프로젝트(ERD)에 활용되는 주요 계정 및 프로퍼티가 정의된 파일 목록

- 주요 프로퍼티 목록
    - Backend
        - SPRING_EC2_URL : 백엔드 서버 주소
        - SPRING_DATASOURCE_URL: 데이터베이스 JDBC URL
        - SPRING_DATASOURCE_USERNAME : 데이터베이스 사용자 이름
        - SPRING_DATASOURCE_PASSWORD : 데이터베이스 비밀번호
        - SPRING_REDIS_PASSWORD : Redis 비밀번호
        - JWT_SECRET : JWT 생성/검증을 위한 시크릿 키
        - KAKAO_REST_API_KEY : 카카오 로그인 REST API 키
        - NAVER_CLIENT_ID : 네이버 로그인 클라이언트 ID
        - NAVER_CLIENT_SECRET : 네이버 로그인 클라이언트 시크릿
        - CLOUD_AWS_CREDENTIALS_ACCESS_KEY : AWS S3 접근 키
        - CLOUD_AWS_CREDENTIALS_SECRET_KEY : AWS S3 시크릿 키
        - SMS_API_KEY, SMS_API_SECRET_KEY : SMS 발송 서비스 API 키
        - KAKAOPAY_API_KEY : 카카오페이 API 키
        - FE_BASE_URL : 프론트엔드 기본 URL
        - BE_BASE_URL : 백엔드 기본 URL
        - MAIL_USERNAME, MAIL_PASSWORD : 이메일 발송용 계정 정보
    - Chatbot
        - OPENAI_API_KEY: OpenAI API 키
        - PINECONE_API_KEY: Pinecone API 키
        - PINECONE_ENVIRONMENT: Pinecone 환경 이름
        - UPSTAGE_API_KEY: Upstage API 키

---

## 2. 프로젝트에서 사용하는 외부 서비스 정보를 정리한 문서

| 서비스명 | 용도 | 가입 경로 | 주요 설정 | 제한/비용 |
| --- | --- | --- | --- | --- |
| **AWS S3** | 이미지 파일 업로드 및 정적 자원 저장 | [AWS Console](https://aws.amazon.com/console/) | Bucket: `team-109-lions`
Region: `ap-northeast-2` | 저장/트래픽 과금, 무료 티어 5GB |
| **AWS EC2** | 서버 인스턴스 운영 | [AWS Console](https://aws.amazon.com/console/) | 인스턴스 유형: t3.mediumOS: Ubuntu 22.04 | 사용 시간/트래픽 과금 |
| **Certbot** | SSL 인증서 발급/갱신 | [Certbot](https://certbot.eff.org/) | 도메인: `골든티켓.홈페이지.한국`
웹 서버: Nginx | 무료, 90일마다 갱신 필요 |
| **DNS** | 도메인 연결 관리 | 도메인 제공사 | 도메인: `골든티켓.홈페이지.한국`
A 레코드: EC2 서버 IP
CNAME 등 | 도메인 비용 별도 |
| **GPT (OpenAI API)** | AI 기반 챗봇 | [OpenAI Platform](https://platform.openai.com/) | 모델: `gpt-4o` 사용 | 토큰 사용량 
10만 크레딧 제한 |
| **Gmail SMTP** | 단체 관람 신청 메일 전송  | [Google Cloud Console](https://console.cloud.google.com/) | Port: 587(SSL) | 하루 500건 제한 (무료 Gmail) |
| **Solapi** | 문자(SMS/LMS) 발송 | [Solapi](https://solapi.com/) | 발신번호: `010942677242`엔드포인트: `/messages/v4/send` | 발송 건당 과금 |
| **네이버 OAuth** | 네이버 계정 로그인 | [네이버 개발자 센터](https://developers.naver.com/) | Redirect URI: `https://www.xn--bb0b44m5yw9qd.xn--hu5b25b77nvwc.xn--3e0b707e/api/users/auth/kakao/callback` | 무료, 일부 API는 검수 필요 |
| **카카오 OAuth** | 카카오 계정 로그인 | [카카오 개발자 센터](https://developers.kakao.com/) | Redirect URI: `https://www.xn--bb0b44m5yw9qd.xn--hu5b25b77nvwc.xn--3e0b707e/api/users/auth/kakao/callback` | 무료, 검수 전에는 제한 |
| **카카오페이** | 결제 처리 | [카카오페이 개발자 센터](https://developers.kakaopay.com/) | 결제 준비 → 요청 → 승인 API 연동
`kakaopay.host=https://kapi.kakao.comkakaopay.ready-url=/v1/payment/readykakaopay.approve-url=open-api.kakaopay.com/payment/approvekakaopay.success-url=/payment/kakao/success`
 | 무료, 검수 전에는 제한  |

---

## 3. DB 덤프 파일 최신본

[backup.zip](backup.zip)

### **1. PostgreSQL**

- 방식 : pgAdmin GUI → Backup 기능 사용
- 백업 범위 : 스키마(테이블 구조, 제약조건 등) + 데이터(INSERT 구문 형태)
- 결과물 : postgresql.dump
- 특징 : SQL 실행문/전용 포맷이라, 다른 PostgreSQL 환경에서 그대로 복원 가능
    - pgAdmin → Restore or pg_restore CLI 사용

### 2. **Redis**

- 방식 : Docker 컨테이너 내부 /data/dump.rdb 파일을 docker cp → scp 으로 추출
- 백업 범위 : Redis 메모리에 존재하는 모든 Key-Value 쌍 + TTL (스냅샷)
- 결과물 : redis.rdb
- 특징 : Redis 재시작 시 이 파일을 읽어, 해당 시점 메모리 상태 그대로 복원

---

## 4. 시연 시나리오

- 시연 전제 사항 :
    - 카카오를 통한 소셜 로그인 상태
    - 기본 테마 - 삼성라이온즈, 변경 테마 - 롯데 자이언츠

### 응모하기

1. 응모하기 페이지 이동
    - 화면 : 메인페이지
    - 동작 : 히어로 배너 [응모하기] 클릭
2. 응모할 팀 선택
    - 화면 : 응모하기1 - 팀 선택
    - 동작 : [응모할 팀](배너) 선택 클릭 → [다음] 버튼 클릭
3. 응모할 날짜 선택
    - 화면 : 응모하기2 - 날짜선택
    - 동작 : [날짜] 선택 → [응모하기] 버튼 클릭 → 알럿창 [확인] 클릭
4. 응모 완료
    - 화면 : 응모하기3 - 응모 완료
    - 동작 : [확인] 클릭 → 나의 응모 페이지 이동

### 양도하기

1. 양도하기 페이지 이동
    - 화면 : 메인페이지
    - 동작 : 히어로 배너 [양도하기] 클릭
2. 플랫폼 선택
    - 화면 : 양도하기1 - 플랫폼 선택
    - 동작 : [티켓링크](연동할 플랫폼) 클릭
3. 양도할 티켓  선택
    - 화면 : 양도하기2 - 티켓 선택
    - 동작 : [양도할 티켓] 호버 → [양도하기] 클릭
4. 양도하기
    - 화면 : 양도하기3- 양도하기
    - 동작 : [네] 클릭 → 양도 완료창 [확인] 클릭

### 양도완료

1. 나의 티켓 이동
    - 화면 : 네비바
    - 동작 : [마이페이지] 클릭 → 좌측 네비바 [나의 티켓] 이동
2. 티켓 조회
    - 화면 : 마이페이지 - 나의 티켓
    - 동작 : [조회할 티켓] 선택 → 티켓 상세보기 → QR코드 조회
3. 홈화면 이동
    - 화면 : 네비바
    - 동작 : [로고] 클릭 → 홈화면 이동

### 테마 설정

1. 마이페이지 이동
    - 화면 : 네비바
    - 동작 : [마이페이지] 클릭
2. 테마 선택
    - 화면 : 마이페이지 - 내 정보
    - 동작 : [관심 야구팀] 드롭다운 메뉴 중 [팀] 선택
3. 적용된 페이지 조회
    - 화면 : 메인페이지, 마이페이지, 응모하기, 게시판
    - 동작 :
        - 마이페이지 테마적용 조회 → [로고] 클릭 → 메인페이지 이동
        - 메인페이지 테마적용 조회 → [응모하기] 클릭 → 응모하기 페이지 이동
        - 테마적용 조회

### 단체관람 기능

1. 단체관람 페이지 이동
    - 화면 : 게시판 - 공지사항
    - 동작 : [단체 관람 게시판] 클릭 → 선호 구단 설정 상태 조회
2. 단체 관람 신청하기
    - 화면 : 게시판 - 단체 관람 게시판
    - 동작 : 신청할 경기 선택 → [관람 신청] 클릭 → 신청 완료
3. 구단 측 메일 발송 조회
    - 화면 : 메일 이미지
    - 동작 : 구단측으로 단체 관람 신청 메일 전송 확인

### 챗봇 기능

1. 챗봇 연결
    - 화면 : 메인페이지
    - 동작: 우측하단 [챗봇] 버튼 클릭 → 챗봇 대화창 열림
2. 경기 조회
    - 화면 : 챗봇 대화창
    - 동작 : 삼성 라이온즈 경기 일정 타이핑 후 질의 → 경기 일정 답변
3. 응모하기
    - 화면 :챗봇 대화창
    - 동작 : 삼성 라이온즈 경기 응모 파이핑 후 질의 → 경기 신청 → 알럿창 [확인] 버튼 클릭 → 나의 응모 내역 조회