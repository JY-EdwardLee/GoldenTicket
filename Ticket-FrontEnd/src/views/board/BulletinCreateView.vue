<template>
  <div class="bulletin-create-root">
    <div class="container">
      <div class="header-section">
        <h1 class="page-title">게시글 작성</h1>
        <div class="breadcrumb">
          <router-link to="/bulletin" class="breadcrumb-link">게시판</router-link>
          <span class="breadcrumb-separator">></span>
          <span class="breadcrumb-current">글쓰기</span>
        </div>
      </div>

      <form @submit.prevent="handleSubmit" class="create-form">
        <!-- 게시판 타입 선택 -->
        <div class="form-group">
          <label for="boardType" class="form-label">게시판 종류</label>
          <select 
            id="boardType" 
            v-model="formData.boardType" 
            class="form-select"
            required
          >
            <option value="">게시판을 선택하세요</option>
            <option :value="BOARD_TYPES.FREE">자유게시판</option>
            <option :value="BOARD_TYPES.GROUP">단체 관련 게시판</option>
          </select>
        </div>

        <!-- 제목 입력 -->
        <div class="form-group">
          <label for="title" class="form-label">제목</label>
          <input
            id="title"
            type="text"
            v-model="formData.title"
            class="form-input"
            placeholder="제목을 입력하세요"
            required
            maxlength="100"
          />
          <div class="char-count">{{ formData.title.length }}/100</div>
        </div>

        <!-- 에디터 -->
        <div class="form-group">
          <label class="form-label">내용</label>
          
          <div class="editor-wrapper" id="editor-container">
            <QuillEditor
              v-model:content="formData.content"
              theme="snow"
              :options="editorOptions"
              placeholder="내용을 입력하세요..."
              @ready="onEditorReady"
            />
          </div>
          
          <!-- 간단한 사용법 텍스트 -->
          <div class="editor-help-text">
            <p class="help-main">💡 <strong>글 작성 팁:</strong> 굵게(Ctrl+B), 기울임(Ctrl+I), 밑줄(Ctrl+U) | 제목은 드롭다운에서 선택 | 목록, 링크, 이미지 버튼 활용</p>
            <p class="help-sub">📝 중요한 내용은 굵게 표시하고, 여러 항목은 목록으로 정리하세요. 서식 지우기 버튼으로 꾸밈을 제거할 수 있습니다.</p>
          </div>
        </div>

        <!-- 버튼 그룹 -->
        <div class="button-group">
          <button
            type="button"
            @click="handleCancel"
            class="btn btn-secondary"
          >
            취소
          </button>
          <button
            type="submit"
            :disabled="!isFormValid || isSubmitting"
            class="btn btn-primary"
          >
            <span v-if="isSubmitting">작성 중...</span>
            <span v-else>작성 완료</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { QuillEditor } from '@vueup/vue-quill';
import '@vueup/vue-quill/dist/vue-quill.snow.css';
import { boardAPI, validatePostData, BOARD_TYPES } from '@/api/board.js';

const router = useRouter();
const route = useRoute();

// 폼 데이터
const formData = ref({
  boardType: '',
  title: '',
  content: ''
});

const isSubmitting = ref(false);

// 에디터 설정 (시니어 친화적으로 간소화)
const editorOptions = ref({
  modules: {
    toolbar: [
      ['bold', 'italic', 'underline'], // 기본 서식
      [{ 'header': [1, 2, false] }], // 제목 (큰 글씨, 중간 글씨, 일반)
      [{ 'list': 'ordered'}, { 'list': 'bullet' }], // 번호 목록, 글머리 목록
      ['blockquote'], // 인용문
      ['link', 'image'], // 링크, 이미지
      ['clean'] // 서식 지우기
    ]
  },
  placeholder: '여기에 글을 작성해주세요...',
  readOnly: false,
});

// 폼 유효성 검사
const isFormValid = computed(() => {
  const contentText = getContentText(formData.value.content);
  
  return formData.value.boardType && 
         formData.value.title.trim() && 
         contentText.trim().length > 0;
});

// 마운트 시 게시판 타입 자동 설정
onMounted(() => {
  const boardType = route.query.type;
  if (boardType && ['free', 'group'].includes(boardType)) {
    // 쿼리 파라미터의 타입을 BOARD_TYPES 상수에 맞게 변환
    if (boardType === 'free') {
      formData.value.boardType = BOARD_TYPES.FREE;
    } else if (boardType === 'group') {
      formData.value.boardType = BOARD_TYPES.GROUP;
    }
  }
});

// 에디터 준비 완료
const onEditorReady = (quill) => {
  console.log('에디터 준비 완료:', quill);
  
  // 브라우저 경고 억제 (선택사항)
  const originalWarn = console.warn;
  console.warn = (...args) => {
    if (args[0] && typeof args[0] === 'string' && 
        args[0].includes('DOMNodeInserted')) {
      return; // 이 경고는 무시
    }
    originalWarn.apply(console, args);
  };
  
  // 에디터가 준비된 후 한국어 라벨 추가
  setTimeout(() => {
    addKoreanLabels();
  }, 100);
};

// 한국어 라벨 추가 함수
const addKoreanLabels = () => {
  const labels = {
    '.ql-bold': '굵게',
    '.ql-italic': '기울임', 
    '.ql-underline': '밑줄',
    '.ql-header[value="1"]': '큰제목',
    '.ql-header[value="2"]': '작은제목',
    '.ql-list[value="ordered"]': '번호목록',
    '.ql-list[value="bullet"]': '글머리목록',
    '.ql-blockquote': '인용문',
    '.ql-link': '링크',
    '.ql-image': '이미지',
    '.ql-clean': '서식지우기'
  };
  
  Object.entries(labels).forEach(([selector, text]) => {
    const elements = document.querySelectorAll(`#editor-container ${selector}`);
    elements.forEach(el => {
      el.setAttribute('data-label', text);
    });
  });
};

// content를 텍스트로 변환하는 헬퍼 함수
const getContentText = (content) => {
  if (!content) return '';
  
  if (typeof content === 'string') {
    // HTML 문자열인 경우 태그를 제거하고 텍스트만 추출
    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = content;
    return tempDiv.textContent || tempDiv.innerText || '';
  } else {
    // Delta 객체 등 다른 형태인 경우
    return String(content);
  }
};

// 폼 제출
const handleSubmit = async () => {
  if (!isFormValid.value) return;

  // 클라이언트 측 유효성 검사
  const validation = validatePostData(formData.value);
  if (!validation.isValid) {
    alert(validation.errors.join('\n'));
    return;
  }

  isSubmitting.value = true;
  
  try {
    // content를 안전하게 처리
    const contentText = getContentText(formData.value.content);
    
    // API 호출로 게시글 저장
    const result = await boardAPI.createPost({
      boardType: formData.value.boardType,
      title: formData.value.title.trim(),
      content: contentText.trim(),
      imageUrl: null // 현재는 이미지 업로드 기능 없음
    });
    
    if (result.success) {
      alert(result.message || '게시글이 성공적으로 작성되었습니다.');
      // 성공 시 게시판으로 이동
      router.push('/bulletin');
    } else {
      alert(result.message || '게시글 작성에 실패했습니다.');
    }
  } catch (error) {
    console.error('게시글 작성 실패:', error);
    alert('게시글 작성 중 오류가 발생했습니다. 다시 시도해주세요.');
  } finally {
    isSubmitting.value = false;
  }
};

// 취소
const handleCancel = () => {
  if (confirm('작성 중인 내용이 사라집니다. 정말로 취소하시겠습니까?')) {
    router.push('/bulletin');
  }
};
</script>

<style scoped>
.bulletin-create-root {
  min-height: 100vh;
  background: #f8f9fa;
  padding: 20px 0;
}

.container {
  max-width: 900px;
  margin: 0 auto;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.header-section {
  background: linear-gradient(135deg, #e11d48 0%, #f97316 100%);
  color: white;
  padding: 40px;
  text-align: center;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 10px;
}

.breadcrumb {
  font-size: 14px;
  opacity: 0.9;
}

.breadcrumb-link {
  color: white;
  text-decoration: none;
}

.breadcrumb-link:hover {
  text-decoration: underline;
}

.breadcrumb-separator {
  margin: 0 8px;
}

.breadcrumb-current {
  opacity: 0.8;
}

.create-form {
  padding: 40px;
}

.form-group {
  margin-bottom: 30px;
}

.form-label {
  display: block;
  font-size: 16px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
}

.form-select,
.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.2s, box-shadow 0.2s;
  background: white !important;
  color: #1f2937 !important; /* 텍스트 색상을 검정으로 강제 설정 */
}

.form-select:focus,
.form-input:focus {
  outline: none;
  border-color: #e11d48;
  box-shadow: 0 0 0 3px rgba(225, 29, 72, 0.1);
}

/* placeholder 색상 명시적 설정 */
.form-input::placeholder {
  color: #9ca3af !important;
  opacity: 1;
}

.form-select option {
  color: #1f2937 !important;
  background: white !important;
}

.char-count {
  text-align: right;
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}

/* 에디터 아래 간단한 도움말 스타일 */
.editor-help-text {
  margin-top: 12px;
  padding: 16px;
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 14px;
}

.help-main {
  margin: 0 0 8px 0;
  color: #374151;
  line-height: 1.5;
}

.help-main strong {
  color: #e11d48;
}

.help-sub {
  margin: 0;
  color: #6b7280;
  font-size: 13px;
  line-height: 1.4;
}

.editor-wrapper {
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  overflow: hidden;
  transition: border-color 0.2s;
}

.editor-wrapper:focus-within {
  border-color: #e11d48;
}

/* Quill 에디터 스타일 커스터마이징 */
:deep(.ql-container) {
  min-height: 300px;
  font-size: 16px;
  line-height: 1.6;
}

:deep(.ql-toolbar) {
  border-bottom: 1px solid #e5e7eb;
  background: #f9fafb;
  padding: 12px;
  position: relative;
}

/* 툴바 버튼 크기 확대 (시니어 친화적) */
:deep(.ql-toolbar .ql-formats) {
  margin-right: 20px;
  position: relative;
}

:deep(.ql-toolbar button) {
  width: 32px;
  height: 32px;
  margin: 2px;
  border-radius: 6px;
  transition: all 0.2s;
  position: relative;
}

:deep(.ql-toolbar button:hover) {
  background: #e11d48;
  color: white;
}

:deep(.ql-toolbar button.ql-active) {
  background: #e11d48;
  color: white;
}

/* 드롭다운 스타일 개선 */
:deep(.ql-toolbar .ql-picker) {
  margin: 2px;
  position: relative;
}

:deep(.ql-toolbar .ql-picker-label) {
  border-radius: 6px;
  padding: 6px 8px;
  font-size: 14px;
  transition: all 0.2s;
}

:deep(.ql-toolbar .ql-picker-label:hover) {
  background: #e11d48;
  color: white;
}

/* 버튼 아래 한국어 라벨 추가 */
:deep(.ql-toolbar button::after) {
  content: attr(data-label);
  position: absolute;
  bottom: -18px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 9px;
  color: #6b7280;
  white-space: nowrap;
  font-weight: 500;
  opacity: 0.8;
  pointer-events: none;
}

/* 드롭다운 아래 라벨 */
:deep(.ql-toolbar .ql-picker::after) {
  content: attr(data-label);
  position: absolute;
  bottom: -18px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 9px;
  color: #6b7280;
  white-space: nowrap;
  font-weight: 500;
  opacity: 0.8;
  pointer-events: none;
}

/* 툴바 높이 조정 (라벨 공간 확보) */
:deep(.ql-toolbar) {
  padding-bottom: 28px;
}

:deep(.ql-editor) {
  padding: 20px;
  color: #1f2937 !important; /* 에디터 텍스트 색상을 검정으로 설정 */
  font-size: 16px;
  line-height: 1.8;
}

:deep(.ql-editor.ql-blank::before) {
  color: #9ca3af;
  font-style: normal;
  font-size: 16px;
}

/* 에디터 내 모든 텍스트 요소의 색상 강제 설정 */
:deep(.ql-editor p),
:deep(.ql-editor h1),
:deep(.ql-editor h2),
:deep(.ql-editor h3),
:deep(.ql-editor h4),
:deep(.ql-editor h5),
:deep(.ql-editor h6),
:deep(.ql-editor li),
:deep(.ql-editor span),
:deep(.ql-editor div) {
  color: #1f2937 !important;
}

/* 선택된 텍스트 색상 */
:deep(.ql-editor ::selection) {
  background: #e11d48;
  color: white;
}

.button-group {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

.btn {
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
  min-width: 120px;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: #f3f4f6;
  color: #374151;
  border: 2px solid #e5e7eb;
}

.btn-secondary:hover:not(:disabled) {
  background: #e5e7eb;
}

.btn-primary {
  background: linear-gradient(135deg, #e11d48 0%, #f97316 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(225, 29, 72, 0.3);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(225, 29, 72, 0.4);
}

/* 반응형에서 도움말 조정 */
@media (max-width: 768px) {
  .container {
    margin: 0 10px;
    border-radius: 0;
  }
  
  .header-section,
  .create-form {
    padding: 20px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .button-group {
    flex-direction: column;
  }
  
  .btn {
    width: 100%;
  }

  .editor-help-text {
    padding: 12px;
    font-size: 13px;
  }
  
  .help-main {
    font-size: 13px;
  }
  
  .help-sub {
    font-size: 12px;
  }
  
  /* 모바일에서 툴바 버튼 크기 조정 */
  :deep(.ql-toolbar button) {
    width: 28px;
    height: 28px;
  }
  
  /* 모바일에서 라벨 크기 조정 */
  :deep(.ql-toolbar button::after),
  :deep(.ql-toolbar .ql-picker::after) {
    font-size: 8px;
    bottom: -16px;
  }
  
  /* 모바일에서 툴바 높이 조정 */
  :deep(.ql-toolbar) {
    padding-bottom: 24px;
  }
}
</style> 