<template>
  <div class="bulletin-edit-root">
    <div class="container">
      <div class="header-section">
        <h1 class="page-title">게시글 수정</h1>
        <div class="breadcrumb">
          <router-link to="/bulletin" class="breadcrumb-link">게시판</router-link>
          <span class="breadcrumb-separator">></span>
          <span class="breadcrumb-current">글 수정</span>
        </div>
      </div>

      <!-- 로딩 상태 -->
      <div v-if="isLoading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>게시글을 불러오는 중...</p>
      </div>
      
      <!-- 에러 상태 -->
      <div v-else-if="error" class="error-container">
        <p class="error-message">{{ error }}</p>
        <button @click="loadPostDetail" class="retry-btn">다시 시도</button>
      </div>
      
      <!-- 수정 폼 -->
      <form v-else @submit.prevent="handleSubmit" class="edit-form">
        <!-- 게시판 타입 표시 (수정 불가) -->
        <div class="form-group">
          <label class="form-label">게시판 종류</label>
          <div class="board-type-display">
            {{ boardTypeName }}
          </div>
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

        <!-- 이미지 관리 -->
        <div class="form-group">
          <label class="form-label">이미지</label>
          
          <!-- 기존 이미지가 있는 경우 -->
          <div v-if="postInfo.postImageUrl && !newImageFile" class="existing-image-container">
            <img 
              :src="postInfo.postImageUrl" 
              :alt="postInfo.title"
              class="existing-image"
              @error="handleImageError"
            />
            <div class="image-info">
              <span class="image-note">📷 기존에 첨부된 이미지입니다</span>
              <div class="image-actions">
                <button type="button" class="btn btn-secondary btn-sm" @click="removeImage">
                  🗑️ 이미지 제거
                </button>
                <button type="button" class="btn btn-primary btn-sm" @click="selectNewImage">
                  📤 이미지 변경
                </button>
              </div>
            </div>
          </div>

          <!-- 새로 선택된 이미지 -->
          <div v-if="newImageFile" class="new-image-container">
            <img 
              :src="previewUrl" 
              :alt="newImageFile.name"
              class="new-image"
            />
            <div class="image-info">
              <span class="image-note">📷 새로 선택된 이미지입니다</span>
              <div class="image-actions">
                <button type="button" class="btn btn-secondary btn-sm" @click="cancelImageSelection">
                  ❌ 선택 취소
                </button>
                <button type="button" class="btn btn-primary btn-sm" @click="selectNewImage">
                  📤 다시 선택
                </button>
              </div>
            </div>
          </div>

          <!-- 이미지가 없는 경우 -->
          <div v-if="!postInfo.postImageUrl && !newImageFile" class="no-image-container">
            <div class="no-image-content">
              <span class="no-image-icon">📷</span>
              <span class="no-image-text">첨부된 이미지가 없습니다</span>
              <button type="button" class="btn btn-primary btn-sm" @click="selectNewImage">
                📤 이미지 추가
              </button>
            </div>
          </div>

          <!-- 파일 선택 input (숨김) -->
          <input
            ref="fileInput"
            type="file"
            accept="image/*"
            @change="handleImageUpload"
            class="hidden-file-input"
          />
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
            <span class="btn-icon">❌</span>
            <span class="btn-text">취소</span>
          </button>
          <button
            type="submit"
            :disabled="!isFormValid || isSubmitting"
            class="btn btn-primary"
          >
            <span v-if="isSubmitting" class="btn-icon">⏳</span>
            <span v-else class="btn-icon">✅</span>
            <span class="btn-text">
              <span v-if="isSubmitting">수정 중...</span>
              <span v-else>수정 완료</span>
            </span>
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
import { boardAPI, validatePostData, BOARD_TYPE_NAMES, uploadImageToS3, uploadToS3 } from '@/api/board.js';

const router = useRouter();
const route = useRoute();

// 상태 관리
const isLoading = ref(false);
const error = ref('');
const isSubmitting = ref(false);
const newImageFile = ref(null);
const previewUrl = ref('');
const fileInput = ref(null);
const quillEditor = ref(null);
const isImageRemoved = ref(false); // 이미지 제거 상태 추적

// 폼 데이터
const formData = ref({
  title: '',
  content: ''
});

// 게시글 정보
const postInfo = ref({});

// 게시판 타입 이름 (읽기 전용)
const boardTypeName = computed(() => {
  // 게시글 데이터에서 게시판 타입 확인
  if (postInfo.value.boardType) {
    return BOARD_TYPE_NAMES[postInfo.value.boardType] || '일반';
  } else if (postInfo.value.boardId) {
    // boardId를 boardType으로 매핑
    let boardType = null;
    if (postInfo.value.boardId === 'FREE') {
      boardType = 'FREE';
    } else if (postInfo.value.boardId === 'GROUP') {
      boardType = 'GROUP';
    } else if (postInfo.value.boardId === 'NOTICE') {
      boardType = 'NOTICE';
    }
    return BOARD_TYPE_NAMES[boardType] || '일반';
  }
  
  return '일반';
});

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
  
  return formData.value.title.trim() && 
         contentText.trim().length > 0;
});

// 게시글 상세 조회
const loadPostDetail = async () => {
  const postId = route.params.id;
  if (!postId) {
    error.value = '게시글 ID가 없습니다.';
    return;
  }

  isLoading.value = true;
  error.value = '';
  
  try {
    const result = await boardAPI.getPostDetail(postId);
    
    // BoardDetailView와 동일한 방식으로 처리
    if (result) {
      postInfo.value = result;
      // 폼 데이터 초기화
      formData.value.title = result.title;
      
      // content 처리 - 실제 내용이 있는지 확인
      let contentToSet = '';
      if (result.content) {
        // HTML 태그가 있는지 확인
        if (result.content.includes('<p>') || result.content.includes('<div>')) {
          contentToSet = result.content;
        } else {
          // 일반 텍스트인 경우 HTML로 감싸기
          contentToSet = `<p>${result.content}</p>`;
        }
      }
      
      formData.value.content = contentToSet;
      
      // 실제 내용이 없는 경우 안내
      if (!result.content || result.content.trim() === '' || result.content === '<p>○○</p>') {
        formData.value.content = ''; // 빈 내용으로 설정
      }
    } else {
      error.value = '게시글을 불러올 수 없습니다.';
    }
  } catch (err) {
    error.value = '게시글을 불러오는 중 오류가 발생했습니다.';
    console.error('게시글 상세 조회 중 오류:', err);
  } finally {
    isLoading.value = false;
  }
};

// 컴포넌트 마운트 시 게시글 조회
onMounted(async () => {
  await loadPostDetail();
});

// 이미지 에러 핸들러
const handleImageError = (event) => {
  console.error('이미지 로드 실패:', event.target.src);
  event.target.style.display = 'none';
};

// 파일 선택 트리거
const selectNewImage = () => {
  fileInput.value?.click();
};

// 이미지 제거
const removeImage = () => {
  postInfo.value.postImageUrl = null;
  newImageFile.value = null;
  previewUrl.value = '';
  isImageRemoved.value = true; // 이미지 제거 상태 설정
};

// 이미지 선택 취소
const cancelImageSelection = () => {
  newImageFile.value = null;
  previewUrl.value = '';
};

// 이미지 파일 선택 처리
const handleImageUpload = async (event) => {
  const file = event.target.files[0];
  if (file) {
    newImageFile.value = file;
    // 미리보기 URL 생성
    previewUrl.value = URL.createObjectURL(file);
    
    // 최초에 1번만 Presigned URL 요청
    try {
      const presignedResponse = await boardAPI.getPresignedUploadUrl(
        'PostImage', 
        route.params.id, 
        file.name
      );
      
      // Presigned URL과 key를 저장
      newImageFile.value.presignedUrl = presignedResponse.presignedUrl;
      newImageFile.value.imageKey = presignedResponse.key;
      
    } catch (error) {
      console.error('Presigned URL 요청 실패:', error);
      alert('이미지 업로드 준비에 실패했습니다. 다시 시도해주세요.');
      // 선택 취소
      newImageFile.value = null;
      previewUrl.value = '';
    }
  }
};

// 에디터 준비 완료
const onEditorReady = (quill) => {
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
  
  // 에디터가 준비된 후 content 설정 (필요한 경우)
  if (formData.value.content) {
    setTimeout(() => {
      quill.root.innerHTML = formData.value.content;
    }, 200);
  }
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

  // 클라이언트 측 유효성 검사 (수정 시에는 제목과 내용만 검사)
  const errors = [];
  
  if (!formData.value.title || formData.value.title.trim() === '') {
    errors.push('제목을 입력해주세요.');
  }
  
  const contentText = getContentText(formData.value.content);
  if (!contentText || contentText.trim() === '') {
    errors.push('내용을 입력해주세요.');
  }
  
  if (errors.length > 0) {
    alert(errors.join('\n'));
    return;
  }

  isSubmitting.value = true;
  
  try {
    // 이미지 KEY 처리
    let imageKey = null;
    
    if (newImageFile.value) {
      // 새 이미지가 있는 경우 S3에 업로드
      try {
        if (!newImageFile.value.presignedUrl) {
          throw new Error('putUrl이 없습니다. 이미지를 다시 선택해주세요.');
        }
        
        // S3에 직접 업로드 (PUT 요청)
        const uploadResult = await uploadToS3(
          newImageFile.value.presignedUrl, 
          newImageFile.value
        );
        
        // 저장된 key 값 사용 (쿼리 파라미터 제거)
        let keyValue = newImageFile.value.imageKey;
        // URL에 쿼리 파라미터가 있는 경우 제거
        if (keyValue && keyValue.includes('?')) {
          keyValue = keyValue.split('?')[0];
        }
        imageKey = keyValue;
        
      } catch (uploadError) {
        console.error('S3 업로드 실패:', uploadError);
        
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
    } else if (postInfo.value.postImageUrl && !isImageRemoved.value) {
      // 기존 이미지가 있지만 수정하지 않는 경우 -> 기존 key 값 사용 (쿼리 파라미터 제거)
      let existingKey = postInfo.value.postImageUrl;
      if (existingKey && existingKey.startsWith('https://')) {
        // URL에서 key 추출: https://bucket.s3.region.amazonaws.com/PostImage/75/uuid.png
        const urlParts = existingKey.split('/');
        if (urlParts.length >= 6) {
          existingKey = urlParts[3] + '/' + urlParts[4] + '/' + urlParts[5];
        }
      }
      // 쿼리 파라미터 제거
      if (existingKey && existingKey.includes('?')) {
        existingKey = existingKey.split('?')[0];
      }
      imageKey = existingKey;
    }
    // 이미지가 없거나 제거된 경우 imageKey은 null
    

    
    // API 호출로 게시글 수정 (key 값 포함)
    const result = await boardAPI.updatePost(route.params.id, {
      postId: route.params.id, // URL 경로의 postId도 포함
      title: formData.value.title.trim(),
      content: getContentHtml(formData.value.content), // HTML 형태로 변환해서 전송
      imageUrl: imageKey // S3 key 값 전송
    });
    

    
    if (result.success) {
      alert(result.message || '게시글이 성공적으로 수정되었습니다.');
      // 성공 시 게시글 상세 페이지로 이동
      router.push(`/bulletin/detail/${route.params.id}`);
    } else {
      alert(result.message || '게시글 수정에 실패했습니다.');
    }
  } catch (error) {
    console.error('게시글 수정 실패:', error);
    alert('게시글 수정 중 오류가 발생했습니다. 다시 시도해주세요.');
  } finally {
    isSubmitting.value = false;
  }
};

// 취소
const handleCancel = () => {
  if (confirm('수정 중인 내용이 사라집니다. 정말로 취소하시겠습니까?')) {
    router.push(`/bulletin/detail/${route.params.id}`);
  }
};
</script>

<style scoped>
.bulletin-edit-root {
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

/* 로딩 스타일 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 20px;
  color: #6b7280;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f4f6;
  border-top: 4px solid #e11d48;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 에러 스타일 */
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 20px;
  color: #dc2626;
}

.error-message {
  margin-bottom: 16px;
  text-align: center;
}

.retry-btn {
  background: #e11d48;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 8px 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.retry-btn:hover {
  background: #be123c;
}

.edit-form {
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

.board-type-display {
  padding: 12px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  background: #f9fafb;
  color: #6b7280;
  font-size: 16px;
}

.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.2s, box-shadow 0.2s;
  background: white !important;
  color: #1f2937 !important;
}

.form-input:focus {
  outline: none;
  border-color: #e11d48;
  box-shadow: 0 0 0 3px rgba(225, 29, 72, 0.1);
}

.form-input::placeholder {
  color: #9ca3af !important;
  opacity: 1;
}

.char-count {
  text-align: right;
  font-size: 12px;
  color: #6b7280;
  margin-top: 4px;
}

/* 이미지 관리 스타일 */
.existing-image-container,
.new-image-container {
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  padding: 16px;
  background: #f9fafb;
  margin-top: 8px;
}

.existing-image,
.new-image {
  max-width: 300px;
  max-height: 200px;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  object-fit: cover;
  display: block;
  margin: 0 auto;
}

/* 새 이미지 컨테이너 */
.new-image-container {
  border-color: #10b981;
  background: rgba(16, 185, 129, 0.05);
}

/* 이미지가 없는 경우 */
.no-image-container {
  border: 2px dashed #e5e7eb;
  border-radius: 8px;
  padding: 20px;
  background: #f9fafb;
  margin-top: 8px;
  text-align: center;
}

.no-image-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.no-image-icon {
  font-size: 24px;
  color: #9ca3af;
}

.no-image-text {
  font-size: 14px;
  color: #6b7280;
}

/* 숨겨진 파일 input */
.hidden-file-input {
  display: none;
}

.image-info {
  text-align: center;
  margin-top: 12px;
}

.image-note {
  font-size: 14px;
  color: #6b7280;
  background: rgba(107, 114, 128, 0.1);
  padding: 6px 12px;
  border-radius: 4px;
  display: inline-block;
  margin-bottom: 8px;
}

.image-actions {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.btn-sm {
  padding: 6px 12px;
  font-size: 12px;
  min-width: auto;
}

.upload-info {
  margin-top: 8px;
}

.upload-note {
  font-size: 12px;
  color: #6b7280;
  background: rgba(107, 114, 128, 0.1);
  padding: 4px 8px;
  border-radius: 4px;
  display: inline-block;
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
  color: #1f2937 !important;
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
  transition: all 0.3s ease;
  border: 2px solid;
  min-width: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  position: relative;
  overflow: hidden;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
}

.btn-secondary {
  background: white;
  color: #374151;
  border-color: #e5e7eb;
  box-shadow: 0 2px 8px rgba(107, 114, 128, 0.1);
}

.btn-secondary:hover:not(:disabled) {
  background: #f3f4f6;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(107, 114, 128, 0.2);
  border-color: #d1d5db;
}

.btn-secondary:active:not(:disabled) {
  transform: translateY(0);
}

.btn-primary {
  background: linear-gradient(135deg, #e11d48 0%, #f97316 100%);
  color: white;
  border-color: #e11d48;
  box-shadow: 0 4px 12px rgba(225, 29, 72, 0.3);
}

.btn-primary:hover:not(:disabled) {
  background: linear-gradient(135deg, #be185d 0%, #ea580c 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(225, 29, 72, 0.4);
  border-color: #be185d;
}

.btn-primary:active:not(:disabled) {
  transform: translateY(0);
}

.btn-icon {
  font-size: 16px;
  transition: transform 0.2s ease;
}

.btn-secondary:hover .btn-icon {
  transform: scale(1.1);
}

.btn-primary:hover .btn-icon {
  transform: rotate(10deg);
}

.btn-text {
  font-weight: 600;
  letter-spacing: 0.5px;
}

/* 반응형에서 도움말 조정 */
@media (max-width: 768px) {
  .container {
    margin: 0 10px;
    border-radius: 0;
  }
  
  .header-section,
  .edit-form {
    padding: 20px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .button-group {
    flex-direction: column;
    gap: 8px;
  }
  
  .btn {
    width: 100%;
    padding: 14px 20px;
    font-size: 16px;
  }
  
  .btn-icon {
    font-size: 18px;
  }
  
  /* 모바일에서 이미지 조정 */
  .existing-image,
  .new-image {
    max-width: 250px;
    max-height: 150px;
  }
  
  .existing-image-container,
  .new-image-container {
    padding: 12px;
  }
  
  .no-image-container {
    padding: 16px;
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