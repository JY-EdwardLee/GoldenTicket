# Ticket Frontend

## 🚀 최근 업데이트

### 게시판 컴포넌트 리팩토링 및 글쓰기 기능 추가

#### ✅ 완료된 작업

1. **컴포넌트 분리**
   - `BulletinView.vue` (391줄 → 96줄, 75% 감소)
   - 공통 컴포넌트 분리: `BoardHeader`, `BoardTable`, `BoardPagination`
   - 개별 게시판 컴포넌트: `NoticeBoard`, `FreeBoard`, `GroupBoard`

2. **글쓰기 기능 구현**
   - **경로**: `/bulletin/create`
   - **에디터**: Quill Editor (Vue 3 호환)
   - **기능**: 
     - 게시판 타입 선택 (자유게시판/단체 관련 게시판)
     - 제목 입력 (100자 제한)
     - 리치 텍스트 에디터
     - 실시간 폼 유효성 검사
     - 반응형 디자인

3. **라우터 설정**
   ```javascript
   { path: '/bulletin/create', name: 'BulletinCreate', component: BulletinCreateView }
   ```

4. **글쓰기 버튼 연동**
   - `FreeBoard.vue`: 자유게시판 타입으로 이동
   - `GroupBoard.vue`: 단체 관련 게시판 타입으로 이동
   - 쿼리 파라미터로 게시판 타입 자동 설정

#### 🔧 설치된 패키지
```bash
npm install @vueup/vue-quill quill
```

#### 📁 새로운 파일 구조
```
src/
├── components/
│   └── board/
│       ├── BoardHeader.vue      # 공통 헤더
│       ├── BoardTable.vue       # 공통 테이블
│       ├── BoardPagination.vue  # 공통 페이지네이션
│       ├── NoticeBoard.vue      # 공지사항
│       ├── FreeBoard.vue        # 자유게시판
│       └── GroupBoard.vue       # 단체 관련 게시판
└── views/
    └── board/
        ├── BulletinView.vue      # 게시판 메인 (탭 관리)
        └── BulletinCreateView.vue # 글쓰기 페이지
```

#### 🔮 향후 개발 예정
- [ ] 로그인 인증 연동 (현재는 모든 사용자에게 글쓰기 버튼 표시)
- [ ] 실제 API 연동 (현재는 콘솔 로그)
- [ ] 게시글 상세 페이지
- [ ] 댓글 기능
- [ ] 파일 업로드 기능

## 개발 서버 실행

```bash
npm run dev
```

## 주요 기능

### 게시판
- 공지사항, 자유게시판, 단체 관련 게시판
- 검색 기능
- 페이지네이션
- 글쓰기 (Quill 에디터 사용)

### 글쓰기 페이지 특징
- 📝 리치 텍스트 에디터 (굵게, 기울임, 목록, 링크 등)
- 🎨 직관적인 UI/UX
- 📱 반응형 디자인
- ✅ 실시간 유효성 검사
- 🔄 자동 게시판 타입 선택
