<script setup>
import { ref } from 'vue'
import axios from 'axios'

// 반응형 데이터
const selectedFile = ref(null)
const uploadedImageKey = ref('')
const downloadImageUrl = ref('')
const message = ref('')
const imageUrls = ref([])
const imageType = ref('UserProfile')
const refId = ref(1)

// 백엔드 API 기본 URL
const API_BASE_URL = 'http://localhost:8080'

// 파일 선택 핸들러
const handleFileSelect = (event) => {
  selectedFile.value = event.target.files[0]
}

// 이미지 업로드 함수
const uploadImage = async () => {
  if (!selectedFile.value) return

  // 1. Presigned URL 발급 요청
  const presignedResponse = await axios.get(`${API_BASE_URL}/s3/upload-url`, {
    params: {
      type: 'UserProfile',
      refId: 14,  // 일관성을 위해 15로 통일
      fileName: selectedFile.value.name
    }
  })

  const { key, presignedUrl } = presignedResponse.data

  // 2. S3에 직접 업로드
  await axios.put(presignedUrl, selectedFile.value, {
    headers: {
      'Content-Type': selectedFile.value.type
    }
  })

  // 3. 업로드 성공 후 key값을 백엔드에 전송하여 DB에 저장
  const saveResponse = await axios.post(`${API_BASE_URL}/s3/save-key`, {
    key: key,
    type: 'UserProfile',
    refId: 14
  })

  // 저장 성공/실패 확인
  if (saveResponse.data.success) {
    uploadedImageKey.value = key
    message.value = saveResponse.data.message
    console.log('업로드된 이미지 키:', key)
  } else {
    message.value = '업로드 실패: ' + saveResponse.data.message
  }
}

// type과 refId로 이미지 URL들 조회 (실제 사용 사례)
const getImageUrls = async () => {
  try {
    const response = await axios.post(`${API_BASE_URL}/s3/download`, {
      type: imageType.value,  // "UserProfile" 또는 "PostImage"
      refId: refId.value
    })
    
    if (response.data.downloadUrl) {
      imageUrls.value = [response.data.downloadUrl]
      message.value = response.data.message
    } else {
      imageUrls.value = []
      message.value = response.data.message
    }
  } catch (error) {
    message.value = '이미지 URL 조회 실패: ' + error.message
  }
}

// 업로드된 이미지를 type과 refId로 조회하는 함수
const getUploadedImage = async () => {
  if (!uploadedImageKey.value) {
    message.value = '먼저 이미지를 업로드해주세요.'
    return
  }

  try {
    const response = await axios.post(`${API_BASE_URL}/s3/download`, {
      type: 'UserProfile',  // S3Type.UserProfile enum 값
      refId: 14  // 업로드할 때 사용한 refId와 동일하게
    })
    
    if (response.data.downloadUrl) {
      downloadImageUrl.value = response.data.downloadUrl
      message.value = response.data.message
    } else {
      message.value = response.data.message
    }
  } catch (error) {
    message.value = '이미지 조회 실패: ' + error.message
  }
}
</script>

<template>
  <div class="profile-test">
    <h1>사용자 마이페이지 프로필 이미지 테스트</h1>
    
    <!-- 이미지 업로드 섹션 -->
    <div class="section">
      <h2>1. 이미지 업로드</h2>
      <input type="file" @change="handleFileSelect" accept="image/*" />
      <button @click="uploadImage">이미지 업로드</button>
      
      <div v-if="uploadedImageKey">
        <p>업로드된 이미지 키: {{ uploadedImageKey }}</p>
      </div>
    </div>

    <!-- type과 refId로 이미지 URL 조회 (실제 사용 사례) -->
    <div class="section">
      <h2>2. type과 refId로 이미지 URL 조회 (실제 사용 사례)</h2>
      <div class="form-group">
        <label>이미지 타입:</label>
        <select v-model="imageType" class="form-control">
          <option value="UserProfile">사용자 프로필</option>
          <option value="PostImage">게시글 이미지</option>
        </select>
      </div>
      <div class="form-group">
        <label>참조 ID:</label>
        <input type="number" v-model.number="refId" class="form-control" />
      </div>
      <button @click="getImageUrls">이미지 URL 조회</button>
      
      <div v-if="imageUrls.length > 0" class="image-results">
        <h3>조회된 이미지들:</h3>
        <div class="image-grid">
          <div v-for="(url, index) in imageUrls" :key="index" class="image-item">
            <img :src="url" :alt="`이미지 ${index + 1}`" />
            <p>이미지 {{ index + 1 }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 업로드된 이미지 조회 섹션 -->
    <div class="section">
      <h2>3. 업로드된 이미지 조회 (type과 refId로)</h2>
      <button @click="getUploadedImage">업로드된 이미지 조회</button>
      
      <div v-if="downloadImageUrl">
        <p>조회된 이미지 URL:</p>
        <a :href="downloadImageUrl" target="_blank">{{ downloadImageUrl }}</a>
        <div class="image-preview">
          <img :src="downloadImageUrl" alt="업로드된 이미지" />
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
.profile-test {
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

.form-control {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-bottom: 10px;
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

.image-results {
  margin-top: 20px;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
  margin-top: 15px;
}

.image-item {
  text-align: center;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 10px;
  background-color: #f9f9f9;
}

.image-item img {
  max-width: 100%;
  max-height: 150px;
  border-radius: 4px;
  border: 1px solid #ddd;
}

.image-item p {
  margin-top: 8px;
  font-size: 14px;
  color: #666;
}
</style> 