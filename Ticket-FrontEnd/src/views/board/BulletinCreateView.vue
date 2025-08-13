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
            <option v-if="authStore.isAdmin" :value="BOARD_TYPES.NOTICE">공지사항</option>
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
               ref="quillEditor"
               v-model:content="formData.content"
               theme="snow"
               :options="editorOptions"
               placeholder="내용을 입력하세요..."
               @ready="onEditorReady"
             />
          </div>
          
          <!-- 간단한 사용법 텍스트 -->
          <div class="editor-help-text">
            <p class="help-main">💡 <strong>글 작성 팁:</strong> 굵게(Ctrl+B), 기울임(Ctrl+I), 밑줄(Ctrl+U) | 제목은 드롭다운에서 선택 | 목록, 링크 활용</p>
            <p class="help-sub">📝 중요한 내용은 굵게 표시하고, 여러 항목은 목록으로 정리하세요. 서식 지우기 버튼으로 꾸밈을 제거할 수 있습니다.</p>
          </div>
        </div>

        <!-- 이미지 업로드 -->
        <div class="form-group">
          <label class="form-label">이미지 첨부</label>
          <div class="image-upload-section">
            <div class="image-upload-area" @click="triggerFileInput">
              <div v-if="!selectedImage" class="upload-placeholder">
                <div class="upload-icon">📷</div>
                <p class="upload-text">이미지를 선택하세요</p>
                <p class="upload-hint">클릭하여 파일 선택 (최대 1개)</p>
              </div>
              <div v-else class="image-preview">
                <img :src="imagePreviewUrl" alt="미리보기" class="preview-image" />
                <div class="image-info">
                  <p class="image-name">{{ selectedImage.name }}</p>
                  <p class="image-size">{{ formatFileSize(selectedImage.size) }}</p>
                </div>
              </div>
            </div>
            <input
              ref="fileInput"
              type="file"
              accept="image/*"
              @change="handleImageSelect"
              style="display: none;"
            />
            <div class="image-actions">
              <button
                v-if="selectedImage"
                type="button"
                @click="removeImage"
                class="btn btn-danger btn-sm"
              >
                이미지 제거
              </button>
            </div>
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
import { boardAPI, validatePostData, BOARD_TYPES, uploadImageToS3 } from '@/api/board.js';
import { useAuthStore } from '@/stores/auth.js';
import { useTeamThemeStore } from '@/stores/teamTheme.js'

// 1. 로컬 스토리지에서 사용자 정보를 가져옵니다.
const user = JSON.parse(localStorage.getItem('user'))

// 2. 팀 테마 스토어를 가져옵니다.
const themeStore = useTeamThemeStore

// 3. 사용자 정보에 myTeam 값이 있으면 해당 팀으로 테마를 설정합니다.
// 이 코드는 컴포넌트가 생성될 때마다 실행되어 현재 사용자의 팀 테마를 적용합니다.
if (user?.myTeam) {
  themeStore.setSelectedTeam(user.myTeam)
}

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

// 폼 데이터
const formData = ref({
  boardType: '',
  title: '',
  content: ''
});

const isSubmitting = ref(false);

// 이미지 관련 변수들
const selectedImage = ref(null);
const imagePreviewUrl = ref('');
const fileInput = ref(null);
const quillEditor = ref(null);

// 임시 postId 생성 함수
const generateRandomLong = () => {
  return Math.floor(Math.random() * 1000000) + 100000; // 100000 ~ 1099999 범위
};

// 에디터 설정 (시니어 친화적으로 간소화)
const editorOptions = ref({
  modules: {
    toolbar: [
      ['bold', 'italic', 'underline'], // 기본 서식
      [{ 'header': [1, 2, false] }], // 제목 (큰 글씨, 중간 글씨, 일반)
      [{ 'list': 'ordered'}, { 'list': 'bullet' }], // 번호 목록, 글머리 목록
      ['blockquote'], // 인용문
      ['link'], // 링크만 (이미지는 별도 버튼으로)
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

// 마운트 시 인증 상태 확인 및 게시판 타입 자동 설정
onMounted(() => {
  // 인증 상태 확인
  if (!authStore.isAuthenticated) {
    alert('로그인이 필요한 서비스입니다.');
    router.push('/');
    return;
  }
  
  const boardType = route.query.type;
  if (boardType && ['free', 'group', 'notice'].includes(boardType)) {
    // 쿼리 파라미터의 타입을 BOARD_TYPES 상수에 맞게 변환
    if (boardType === 'free') {
      formData.value.boardType = BOARD_TYPES.FREE;
    } else if (boardType === 'group') {
      formData.value.boardType = BOARD_TYPES.GROUP;
    } else if (boardType === 'notice') {
      formData.value.boardType = BOARD_TYPES.NOTICE;
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

// content를 HTML로 변환하는 헬퍼 함수
const getContentHtml = (content) => {
  if (!content) return '';
  
  if (typeof content === 'string') {
    // 이미 HTML 문자열인 경우
    return content;
  } else {
    // Delta 객체인 경우 - Quill 에디터에서 HTML 추출
    try {
      // Quill 에디터 인스턴스를 통해 HTML 추출
      if (quillEditor.value && quillEditor.value.getHTML) {
        return quillEditor.value.getHTML();
      }
      // 대안: DOM에서 직접 추출
      const editorElement = document.querySelector('#editor-container .ql-editor');
      if (editorElement) {
        return editorElement.innerHTML;
      }
      // 마지막 대안: content를 문자열로 변환
      return String(content);
    } catch (error) {
      console.error('HTML 변환 실패:', error);
      return String(content);
    }
  }
};

// content를 텍스트로 변환하는 헬퍼 함수 (유효성 검사용)
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

// 이미지 관련 함수들
const triggerFileInput = () => {
  fileInput.value?.click();
};

const handleImageSelect = (event) => {
  const file = event.target.files[0];
  if (!file) return;

  // 파일 타입 검증
  if (!file.type.startsWith('image/')) {
    alert('이미지 파일만 선택할 수 있습니다.');
    return;
  }

  // 파일 크기 검증 (5MB 제한)
  const maxSize = 5 * 1024 * 1024; // 5MB
  if (file.size > maxSize) {
    alert('파일 크기는 5MB 이하여야 합니다.');
    return;
  }

  selectedImage.value = file;
  
  // 미리보기 URL 생성
  const reader = new FileReader();
  reader.onload = (e) => {
    imagePreviewUrl.value = e.target.result;
  };
  reader.readAsDataURL(file);
};

const removeImage = () => {
  selectedImage.value = null;
  imagePreviewUrl.value = '';
  if (fileInput.value) {
    fileInput.value.value = '';
  }
};

const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes';
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

// 폼 제출
const handleSubmit = async () => {
  // 인증 상태 재확인
  if (!authStore.isAuthenticated) {
    alert('로그인이 필요한 서비스입니다.');
    router.push('/');
    return;
  }
  
  if (!isFormValid.value) return;

  // content를 안전하게 처리
  const contentText = getContentText(formData.value.content);
  
  // 클라이언트 측 유효성 검사 (텍스트로 변환된 content 사용)
  const validation = validatePostData({
    ...formData.value,
    content: contentText
  });
  if (!validation.isValid) {
    alert(validation.errors.join('\n'));
    return;
  }

  isSubmitting.value = true;
  
  try {
    // 임시 postId 생성
    const tempPostId = generateRandomLong();
    console.log('임시 postId 생성:', tempPostId);
    
    // 이미지 업로드 처리
    let imageUrl = null;
    if (selectedImage.value) {
      try {
        console.log('이미지 업로드 시작:', selectedImage.value.name);
        const uploadResult = await uploadImageToS3(selectedImage.value, tempPostId);
        imageUrl = uploadResult.imageUrl;
        console.log('이미지 업로드 완료:', imageUrl);
      } catch (uploadError) {
        console.error('이미지 업로드 실패:', uploadError);
        
        // 인증 관련 오류인 경우
        if (uploadError.message && (
          uploadError.message.includes('인증이 필요합니다') ||
          uploadError.message.includes('로그인이 만료되었습니다')
        )) {
          alert(uploadError.message);
          router.push('/');
          return;
        }
        
        alert('이미지 업로드에 실패했습니다. 다시 시도해주세요.');
        return;
      }
    }
    
    // API 호출로 게시글 저장
    const result = await boardAPI.createPost({
      boardType: formData.value.boardType,
      title: formData.value.title.trim(),
      content: getContentHtml(formData.value.content), // HTML 형태로 변환해서 전송
      imageUrl: imageUrl
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
  background: var(--theme-gradient);
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

/* 이미지 업로드 스타일 */
.image-upload-section {
  margin-top: 8px;
}

.image-upload-area {
  border: 2px dashed #d1d5db;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
  background: #f9fafb;
}

.image-upload-area:hover {
  border-color: #e11d48;
  background: #fef2f2;
}

.upload-placeholder {
  color: #6b7280;
}

.upload-icon {
  font-size: 48px;
  margin-bottom: 12px;
}

.upload-text {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 4px;
  color: #374151;
}

.upload-hint {
  font-size: 14px;
  color: #9ca3af;
  margin: 0;
}

.image-preview {
  display: flex;
  align-items: center;
  gap: 16px;
}

.preview-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  border: 2px solid #e5e7eb;
}

.image-info {
  flex: 1;
  text-align: left;
}

.image-name {
  font-weight: 600;
  color: #374151;
  margin: 0 0 4px 0;
  font-size: 14px;
}

.image-size {
  color: #6b7280;
  margin: 0;
  font-size: 12px;
}

.image-actions {
  margin-top: 12px;
  text-align: center;
}

.btn-sm {
  padding: 6px 12px;
  font-size: 14px;
  min-width: auto;
}

.btn-danger {
  background: #dc2626;
  color: white;
}

.btn-danger:hover:not(:disabled) {
  background: #b91c1c;
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
  background: var(--theme-gradient);
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
  
  /* 모바일에서 이미지 업로드 영역 조정 */
  .image-upload-area {
    padding: 16px;
  }
  
  .upload-icon {
    font-size: 36px;
  }
  
  .upload-text {
    font-size: 14px;
  }
  
  .upload-hint {
    font-size: 12px;
  }
  
  .image-preview {
    flex-direction: column;
    gap: 12px;
  }
  
  .preview-image {
    width: 60px;
    height: 60px;
  }
  
  .image-info {
    text-align: center;
  }
}
</style> 