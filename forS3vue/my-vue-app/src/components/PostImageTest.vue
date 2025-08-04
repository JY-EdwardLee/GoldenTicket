<script setup>
import { ref } from 'vue'
import axios from 'axios'

// UUID 생성 함수 (먼저 정의)
const generateUUID = () => {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    const r = Math.random() * 16 | 0
    const v = c === 'x' ? r : (r & 0x3 | 0x8)
    return v.toString(16)
  })
}

// Long 타입 무작위 숫자 생성 함수
const generateRandomLong = () => {
  return Math.floor(Math.random() * 1000000) + 100000 // 100000 ~ 1099999 범위
}

// 새로운 임시 ID 생성 함수
const generateNewTempId = () => {
  tempRefId.value = generateRandomLong()
  console.log('새로운 임시 ID 생성:', tempRefId.value)
}

// 게시물 이미지 업로드 관련 데이터
const postImageFile = ref(null)
const postImageKey = ref('')
const postImageUrl = ref('')
const tempRefId = ref(generateRandomLong()) // Long 타입 무작위 숫자로 임시 refId 생성
const actualPostId = ref(1) // 실제 postId (게시글 생성 후)
const message = ref('')

// 게시글 작성 폼 데이터
const postForm = ref({
  title: '',
  content: '',
  boardType: 'FREE'
})

// 백엔드 API 기본 URL
const API_BASE_URL = 'http://localhost:8080'

// 게시물 이미지 파일 선택 핸들러
const handlePostImageSelect = (event) => {
  postImageFile.value = event.target.files[0]
}

// 게시물 이미지 업로드 함수
const uploadPostImage = async () => {
  if (!postImageFile.value) return

  try {
    const accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJob25nZ2lsZG9uZ0BleGFtcGxlLmNvbSIsImlhdCI6MTc1Mzg0NjQ1OCwiZXhwIjoxNzUzODUwMDU4fQ.ecGZvJLbAP_JbvrpYmknmxrYCtnUfrn-04fEYcdcYiY"
    
    // 1. 임시 refId로 Presigned URL 발급 요청
    const presignedResponse = await axios.get(`${API_BASE_URL}/s3/upload-url`, {
      params: {
        type: 'PostImage',
        refId: tempRefId.value,
        fileName: postImageFile.value.name
      },
      headers: {
        'Authorization': `Bearer ${accessToken}`
      }
    })

    const { key, presignedUrl } = presignedResponse.data
    postImageKey.value = key

    // 2. S3에 직접 업로드
    await axios.put(presignedUrl, postImageFile.value, {
      headers: {
        'Content-Type': postImageFile.value.type
      }
    })

    message.value = '게시물 이미지 업로드 성공: ' + key
    console.log('업로드 성공 - 키:', key)
    
    // 새로운 임시 ID 생성
    generateNewTempId()
    
    // TODO: 실제 게시글 작성 시 이 key를 imageUrl로 전달
    // 예: 게시글 작성 API 호출 시 { title: "...", content: "...", imageUrl: key }
    
  } catch (error) {
    console.error('업로드 실패:', error.message)
    message.value = '게시물 이미지 업로드 실패: ' + error.message
  }
}

// 게시물 이미지 조회 함수
const getPostImage = async () => {
  try {
    // 토큰 정보 (실제로는 로그인 후 받은 토큰 사용)
    const accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJob25nZ2lsZG9uZ0BleGFtcGxlLmNvbSIsImlhdCI6MTc1Mzg0NjI0NCwiZXhwIjoxNzUzODQ5ODQ0fQ.fWGn4rndNwoHJ9EPHY2wyiPCzc-RIets0hsTg9xeJzk"
    
    const response = await axios.post(`${API_BASE_URL}/s3/download`, {
      type: 'PostImage',
      refId: tempRefId.value  // 임시 refId로 조회
    }, {
      headers: {
        'Authorization': `Bearer ${accessToken}`
      }
    })
    
    if (response.data.downloadUrl) {
      postImageUrl.value = response.data.downloadUrl
      message.value = response.data.message
    } else {
      message.value = response.data.message
    }
  } catch (error) {
    message.value = '게시물 이미지 조회 실패: ' + error.message
  }
}

// 게시글 작성 예시 함수
const createPostExample = async () => {
  if (!postImageKey.value) {
    message.value = '먼저 이미지를 업로드해주세요!'
    return
  }

  if (!postForm.value.title.trim() || !postForm.value.content.trim()) {
    message.value = '제목과 내용을 입력해주세요!'
    return
  }

  try {
    const accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJob25nZ2lsZG9uZ0BleGFtcGxlLmNvbSIsImlhdCI6MTc1Mzg0NjQ1OCwiZXhwIjoxNzUzODUwMDU4fQ.ecGZvJLbAP_JbvrpYmknmxrYCtnUfrn-04fEYcdcYiY"
    
    const postData = {
      title: postForm.value.title,
      content: postForm.value.content,
      boardType: postForm.value.boardType,
      imageUrl: postImageKey.value
    }
    
    const postResponse = await axios.post(`${API_BASE_URL}/posts`, postData, {
      headers: {
        'Authorization': `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      }
    })

    if (postResponse.data.success) {
      message.value = '게시글 작성 성공: ' + postResponse.data.message
      console.log('게시글 작성 완료!')
      
      // 폼 초기화
      postForm.value = {
        title: '',
        content: '',
        boardType: 'FREE'
      }
      postImageKey.value = ''
      postImageFile.value = null
      
      // 새로운 임시 ID 생성
      generateNewTempId()
    } else {
      message.value = '게시글 작성 실패: ' + postResponse.data.message
    }
  } catch (error) {
    console.error('게시글 작성 실패:', error.message)
    message.value = '게시글 작성 실패: ' + error.message
  }
}
</script>

<template>
  <div class="post-test">
    <h1>게시물 이미지 업로드 테스트</h1>
    
    <div class="section">
      <h2>게시물 이미지 업로드 흐름 테스트</h2>
      <p><strong>테스트 흐름:</strong></p>
      <ol>
        <li>임시 Long ID로 S3 업로드 URL 요청</li>
        <li>S3에 직접 업로드</li>
        <li>업로드된 key를 게시글 작성 시 imageUrl로 전달</li>
        <li>게시글 작성 완료 시 서비스에서 실제 postId로 키 변경</li>
        <li>임시 Long ID로 이미지 조회 테스트</li>
      </ol>
      <p><strong>참고:</strong> 실제 구현에서는 PostService에서 실제 postId로 key를 재정의합니다.</p>
      <p><strong>현재 임시 ID:</strong> {{ tempRefId }}</p>
      
      <div class="form-group">
        <label>게시물 이미지 파일:</label>
        <input type="file" @change="handlePostImageSelect" accept="image/*" />
        <button @click="uploadPostImage">게시물 이미지 업로드</button>
        <div v-if="postImageKey">
          <p>업로드된 게시물 이미지 키: {{ postImageKey }}</p>
          <p><strong>이 키를 게시글 작성 시 imageUrl로 전달하세요!</strong></p>
        </div>
      </div>
      
      <!-- 실제 게시글 작성 폼 -->
      <div class="section">
        <h2>게시글 작성</h2>
        <div class="form-group">
          <label>제목:</label>
          <input 
            type="text" 
            v-model="postForm.title" 
            placeholder="게시글 제목을 입력하세요"
            class="form-input"
          />
        </div>
        
        <div class="form-group">
          <label>내용:</label>
          <textarea 
            v-model="postForm.content" 
            placeholder="게시글 내용을 입력하세요"
            class="form-textarea"
            rows="5"
          ></textarea>
        </div>
        
        <div class="form-group">
          <label>게시판:</label>
          <select v-model="postForm.boardType" class="form-select">
            <option value="FREE">자유게시판</option>
            <option value="NOTICE">공지사항</option>
            <option value="QNA">Q&A</option>
          </select>
        </div>
        
        <button @click="createPostExample" class="create-post-btn" :disabled="!postImageKey">
          {{ postImageKey ? '게시글 작성' : '먼저 이미지를 업로드해주세요' }}
        </button>
      </div>
      
      <button @click="getPostImage">게시물 이미지 조회</button>
      <div v-if="postImageUrl">
        <p>조회된 게시물 이미지 URL:</p>
        <a :href="postImageUrl" target="_blank">{{ postImageUrl }}</a>
        <div class="image-preview">
          <img :src="postImageUrl" alt="업로드된 게시물 이미지" />
        </div>
      </div>
    </div>

    <!-- 메시지 표시 -->
    <div v-if="message" class="message">
      {{ message }}
    </div>
  </div>
</template>

<style scoped>
.post-test {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.section {
  background: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

input[type="file"] {
  margin-bottom: 10px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 100%;
}

button {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  background-color: #007bff;
  color: white;
  cursor: pointer;
  margin-right: 10px;
}

button:hover {
  background-color: #0056b3;
}

.create-post-btn {
  background-color: #28a745;
  margin-top: 10px;
}

.create-post-btn:hover {
  background-color: #218838;
}

.form-input, .form-textarea, .form-select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-bottom: 10px;
  font-size: 14px;
}

.form-textarea {
  resize: vertical;
  min-height: 100px;
}

.form-select {
  background-color: white;
}

button:disabled {
  background-color: #6c757d;
  cursor: not-allowed;
}

button:disabled:hover {
  background-color: #6c757d;
}

.message {
  padding: 12px 15px;
  border-radius: 4px;
  margin-top: 15px;
  background-color: #d4edda;
  color: #155724;
}

.image-preview img {
  max-width: 100%;
  max-height: 200px;
  border-radius: 4px;
  border: 1px solid #ddd;
  margin-top: 10px;
}

ol {
  margin: 15px 0;
  padding-left: 20px;
}

ol li {
  margin-bottom: 5px;
}
</style> 