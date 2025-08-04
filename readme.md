# A109

### 그라운드 룰

✅ 프로젝트

- 코딩 스타일 통일하기
    - Commit 메시지 작성
        
        
        | 타입 이름 | 내용 |
        | --- | --- |
        | feat | 새로운 기능에 대한 커밋 |
        | fix | 버그 수정에 대한 커밋 |
        | build | 빌드 관련 파일 수정 / 모듈 설치 또는 삭제에 대한 커밋 |
        | chore | 그 외 자잘한 수정에 대한 커밋 |
        | ci | CI 관련 설정 수정에 대한 커밋 |
        | docs | 문서 수정에 대한 커밋 |
        | style | 코드 스타일 혹은 포맷 등에 관한 커밋 |
        | refactor | 코드 리팩토링에 대한 커밋 |
        | test | 테스트 코드 수정에 대한 커밋 |
        | perf | 성능 개선에 대한 커밋 |
        - 작성 예시
            
            ```
               <type>(<scope>): <간단한 설명>
            feat(auth): 로그인 기능 추가
            fix(login): 로그인 실패 시 예외 처리 누락 수정
            ```
            
    - Branch
        
        
        | 브랜치 종류 | 접두사(prefix) | 용도 설명 | 예시 |
        | --- | --- | --- | --- |
        | **메인 브랜치** | `main` | 운영 환경에 배포되는 최종 코드 | `main` |
        | **개발 브랜치** | `develop` | 기능 개발 브랜치가 병합되는 통합 개발 브랜치 | `develop` |
        | **기능 개발** | `feature/` | 새로운 기능 개발 | `feature/login`, `feature/user-page` |
        | **버그 수정** | `bugfix/` 또는 `fix/` | 개발 중 발견된 버그 수정 | `bugfix/login-error`, `fix/crash` |
        | **긴급 수정** | `hotfix/` | 운영 중 긴급 수정 | `hotfix/typo-fix`, `hotfix/payment` |
        | **코드 리팩토링** | `refactor/` | 기능 변경 없이 코드 구조 개선 | `refactor/user-service` |
        | **문서 작업** | `docs/` | README, 문서, 위키 수정 | `docs/update-readme` |
        | **테스트 코드** | `test/` | 테스트 코드 작성 또는 수정 | `test/login-test`, `test/api-call` |
        | **환경 설정** | `chore/` | 빌드 설정, 패키지 관리 등 잡무성 작업 | `chore/init-project`, `chore/lint` |
    - [Java(BE)](https://www.notion.so/BE-23915952f338805cb00fea2f797cf5d6?pvs=21)
        - 링크 참고
        - Intellij 추천 Plugin
            - Atom Material Icons
            - CodeGlance
            - Key Promoter X
            - Rainbow Brackets
- GIT 협업 룰
    - 아침에 pull (프론트/백 각자)
    - 개인 branch 생성
    - 작업 완료 후 퇴근 30분 전에 무조건 PR
        - 하루 이틀정도 개발 부진해서 밀리는 건 가능
    - 충돌 발생 시 각 팀 리드가 조율
- 스크럼
    
    진행 사항 및 그 날 할 업무 공유
    

✅ 생활수칙

- 스크럼 지각 시 커피 사기
- 소통은 무조건 단톡방
- 공지 확인 하면 ✅ 이모티콘 달기

✅ 마인드셋

- 불만 있으면 말하고, 감정적으로 받아들이지 않기
- 대충하지 않기 끝까지 마무리 하기


### 주간 JIRA 스프린트(일정)

| 시간 | 활동 |
| --- | --- |
| **월요일 09:00 ~ 12:00** | 주간 계획 회의 |
| **월요일 12:00** | 스프린트 시작 |
| **월 ~ 금 17:00** | 스프린트 진행 |
| **금요일 17:00** | 스프린트 종료 |


### **Role**

- 활성 스프린트 **진행 중** 표시하기
- 진행중인 Task는 1개를 권장
- 개인 일정 캘린더에 공유