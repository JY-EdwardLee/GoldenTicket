<template>
  <div class="payment-container">
    <div class="payment-content">
      <!-- Left Column -->
      <div class="left-column">
        <!-- Order Product Info -->
        <div class="order-section">
          <h3 class="section-title">주문상품 정보</h3>
          <div class="order-detail">
            <div class="order-info">
              <div class="order-title">{{ enumToTeamName[ticket?.game.home] }} VS {{ enumToTeamName[ticket?.game.away] }}</div>
              <div class="order-meta">
                <span>{{ stadiumOfTeam[ticket?.game.stadium] }}</span>
                <span> {{ formatDate(ticket?.game.date) }}</span>
                <span>{{ ticket?.seat }}석 </span>
              </div>
            </div>
            <div class="order-price">{{ ticket?.price }}원</div>
          </div>
        </div>

        <!-- Total Amount -->
        <div class="total-amount-section">
          <div class="total-amount">
            <span>총 주문금액</span>
            <span class="amount">{{ ticket?.price }}원</span>
          </div>
        </div>

        <!-- Orderer Info -->
        <div class="orderer-section">
          <h3 class="section-title">주문자 정보</h3>
          <div class="form-group">
            <label>이름</label>
            <input type="text" v-model="formData.userName" >
          </div>
          <div class="form-group">
            <label>휴대전화</label>
            <input type="tel" v-model="formData.phoneNumber" >
          </div>
          <div class="form-group">
            <label>이메일</label>
            <input type="email" v-model="formData.email" >
          </div>
        </div>
      </div>

      <!-- Right Column -->
      <div class="right-column">
        <!-- Payment Methods -->
        <div class="payment-method-section">
          <h3 class="section-title">결제수단</h3>
          
          <div class="payment-options">
            <label class="payment-option-radio">
              <input 
                type="radio" 
                name="payment-method" 
                value="simple" 
                v-model="selectedPayment"
                @change="togglePaymentSection('simple')"
              >
              <span>간편결제</span>
            </label>
            
            <!-- Simple Payment Section -->
            <div class="payment-details" v-if="showSimplePayment">
                <div class="simple-payment">
                <div class="payment-buttons">
                                        <button type="button" class="payment-button" id="kakao" @click="selectedMethod = 'kakao'" :class="{ 'selected': selectedMethod === 'kakao' }">
                    <img src="@/assets/logo/btn_kakaoye_pay.png" alt="카카오페이">
                    </button>
                                        <button type="button" class="payment-button" id="naver" @click="selectedMethod = 'naver'" :class="{ 'selected': selectedMethod === 'naver' }">
                    <img src="@/assets/logo/btn_npaygr_pay.svg" alt="네이버페이">
                    </button>
                </div>
                </div>
            </div>

            
            <label class="payment-option-radio">
              <input 
                type="radio" 
                name="payment-method" 
                value="bank" 
                v-model="selectedPayment"
                @change="togglePaymentSection('bank')"
              >
              <span>무통장입금</span>
            </label>
          </div>

          <!-- Bank Transfer Section -->
          <div class="payment-details bank-transfer-wrapper" v-if="showBankTransfer">
            <div class="bank-transfer">
              <h4>무통장입금</h4>
              <div class="bank-info">
                <div class="bank-account">
                  <span>국민은행 123-45-6789-0</span>
                  <span>예금주: (주)티켓링크</span>
                </div>
                <p class="notice">* 입금 기한: 2024.07.31 23:59까지</p>
                <p class="notice">* 입금자명과 주문자명이 일치해야 합니다.</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Terms -->
        <div class="terms-section">
          <div class="terms-item">
            <input 
              type="checkbox" 
              id="agree-all" 
              class="custom-checkbox"
              v-model="agreeAll"
              @change="handleAgreeAllChange"
            >
            <label for="agree-all" class="agree-all">전체 동의</label>
          </div>
          <div class="divider"></div>
          <div class="terms-item">
            <input 
              type="checkbox" 
              id="agree-terms" 
              class="custom-checkbox"
              v-model="agreeTerms"
              @change="updateAgreeAll"
            >
            <label for="agree-terms">(필수) 개인정보 수집 및 이용 동의</label>
            <a href="#" class="view-terms">보기</a>
          </div>
          <div class="terms-item">
            <input 
              type="checkbox" 
              id="agree-payment" 
              class="custom-checkbox"
              v-model="agreePayment"
              @change="updateAgreeAll"
            >
            <label for="agree-payment">(필수) 결제대행 서비스 이용약관 동의</label>
            <a href="#" class="view-terms">보기</a>
          </div>
        </div>

      </div>
    </div>
    <!-- Action Buttons -->
    <div class="action-buttons">
      <button class="btn-cancel">취소</button>
      <button 
        class="btn-pay"
        :disabled="!agreeAll"
        @click="handlePayment"
      >결제하기</button>
    </div>
  </div>
</template>

<script setup>
import NavBar from '@/components/common/NavBar.vue';
import FooterBar from '@/components/common/FooterBar.vue';
import { ref, onMounted, reactive } from 'vue';
import { useRoute } from 'vue-router';
import { useAuthStore } from '@/stores/auth';
import { API_CONFIG } from '@/config/api.config';
import { enumToTeamName, getEnumTeamName } from '@/utils/teamNameMap';
import { stadiumOfTeam } from '@/utils/teamStadium';
import { formatDate } from '@/utils/dateUtils';
import http from '@/utils/http';

const route = useRoute()

// 사용자 정보
const user = ref(null)
// 티켓 정보
const ticket = ref(null)


// 폼 데이터
const formData = reactive({
  userName: '',
  phoneNumber: '',
  email: ''
})

onMounted(async () => {
  const authStore = useAuthStore()
  authStore.getUserInfo()
  const userInfo = localStorage.getItem('user')
  if (userInfo) {
    user.value = JSON.parse(userInfo)
    formData.userName = user.value.userName || ''
    formData.phoneNumber = user.value.phoneNumber || ''
    formData.email = user.value.email || ''
  }
  console.log('user : ', user.value)
  try {
    const response = await http.get(API_CONFIG.TICKET.DETAIL(route.params.id))
    ticket.value = response.data
    console.log('ticket : ', ticket.value)
  } catch (error) {
    console.error('티켓 정보 요청 실패:', error)
  }
})

const selectedPayment = ref('simple')
const showSimplePayment = ref(true)
const showBankTransfer = ref(false)

// 체크박스 상태 관리
const agreeAll = ref(false)
const agreeTerms = ref(false)
const agreePayment = ref(false)

// 전체 동의 체크박스 변경 핸들러
const handleAgreeAllChange = () => {
  if (agreeAll.value) {
    agreeTerms.value = true
    agreePayment.value = true
  }
}

// 결제 요청 핸들러
const handlePayment = async () => {
  if (selectedMethod.value === 'kakao') {
  try {
    const paymentData = {
      ticketId: route.params.id,
      itemName: ticket.value.home + ' vs ' + ticket.value.away,
      quantity: 1,
      totalAmount: ticket.value.price
    };
    console.log('결제 데이터:', paymentData);
    // 백엔드에 결제 준비 요청
    const response = await http.post(API_CONFIG.USER.PAYMENT.KAKAO.READY, paymentData);
    console.log('결제 준비 응답:', response.data);
    // 결제 완료 페이지로 리디렉션
    window.location.href = response.data.next_redirect_pc_url;
    // 모바일: response.data.next_redirect_mobile_url
    
  } catch (error) {
    console.error('결제 요청 실패:', error);
    alert('결제 요청에 실패했습니다.');
  }
} else if (selectedMethod.value === 'naver') {
  alert('네이버페이 결제 준비 중입니다.');
  }
}

// 개별 체크박스 변경 시 전체 동의 상태 업데이트
const updateAgreeAll = () => {
  agreeAll.value = agreeTerms.value && agreePayment.value
}

const togglePaymentSection = (type) => {
  showSimplePayment.value = type === 'simple';
  showBankTransfer.value = type === 'bank';
}

const selectedMethod = ref(null)

</script>

<style scoped>
.bank-transfer-wrapper {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  padding: 0 0 0 24px;
  width: 478px;
  height: auto;
  flex: none;
  order: 1;
  flex-grow: 0;
}

.payment-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

.payment-content {
  display: flex;
  gap: 2rem;
  align-items: flex-start;
}

.left-column {
  flex: 2;
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.right-column {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2rem;
  position: sticky;
  top: 1rem;
}

.section-title {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  color: #333;
  padding-bottom: 0.75rem;
  border-bottom: 2px solid #f0f0f0;
}

/* Order Section */
.order-section {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.order-detail {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.order-title {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  color: #222;
}

.order-meta {
  display: flex;
  gap: 1rem;
  color: #666;
  font-size: 0.9rem;
}

.order-price {
  font-weight: 600;
  color: #1a73e8;
  font-size: 1.1rem;
}

/* Total Amount */
.total-amount-section {
  background: #f8f9fa;
  padding: 2rem;
  border-radius: 8px;
  margin-bottom: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.total-amount {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total-amount span:first-child {
  font-size: 1.5rem;
  font-weight: 600;
  color: #333;
}

.total-amount .amount {
  font-size: 2.25rem;
  font-weight: 700;
  color: #1a73e8;
  letter-spacing: -0.5px;
}

@media (max-width: 768px) {
  .total-amount-section {
    padding: 1.5rem;
  }
  
  .total-amount span:first-child {
    font-size: 1.25rem;
  }
  
  .total-amount .amount {
    font-size: 1.75rem;
  }
}

/* Orderer Info */
.orderer-section {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #444;
}

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  color: #000000;
}

/* Payment Methods */
.payment-method-section {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.simple-payment,
.bank-transfer {
  margin-bottom: 2rem;
}

.simple-payment h4,
.bank-transfer h4 {
  font-size: 1rem;
  margin-bottom: 1rem;
  color: #444;
}

.payment-options {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
}

.payment-option-radio {
  display: flex;
  align-items: center;
  padding: 1rem;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s;
}

.payment-option-radio:hover {
  border-color: #1a73e8;
  background-color: #f8f9fa;
}

.payment-option-radio input[type="radio"] {
  margin-right: 0.75rem;
  width: 1.25rem;
  height: 1.25rem;
  cursor: pointer;
}

.payment-option-radio span {
  font-weight: 500;
  color: #333;
}

.payment-details {
  margin-top: 1rem;
  padding: 1rem;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  background-color: #f8f9fa;
  height: 188px;
}

.payment-buttons {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  margin-top: 1rem;
}

.payment-button {
  border: 0px solid #e0e0e0;
  border-radius: 4px;
  padding: 0.5rem;
  cursor: pointer;
  transition: all 0.2s;
  text-align : center;
  height : 55px;
  width:200px;
}

#naver {
  background-color: #00DE5A;
}

#kakao {
  background-color: #FFEB00;
  width:200px;
}

.payment-button:hover {
  border-color: #1a73e8;
}

.payment-button.selected {
    border: 2px solid #007bff;
}

.payment-button img {
  height: 38px;
  width: auto;
}

#kakao img {
  height: 28px;
  width: auto;
}

.bank-info {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 4px;
  font-size: 0.9rem;
}

.bank-account {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.notice {
  color: #666;
  font-size: 0.85rem;
  margin: 0.25rem 0;
}

/* Terms Section */
.terms-section {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  margin-bottom: 2rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.terms-item {
  display: flex;
  align-items: center;
  padding: 0.75rem 0;
}

.terms-item input {

}

.terms-item label {
  margin-left: 0.5rem;
  color: #444;
  cursor: pointer;
}

.agree-all {
  font-weight: 600;
  color: #222 !important;
}

.view-terms {
  margin-left: auto;
  color: #1a73e8;
  text-decoration: none;
  font-size: 0.9rem;
}

.divider {
  height: 1px;
  background: #eee;
  margin: 0.5rem 0;
}

/* Action Buttons */
.action-buttons {
  display: flex;
  gap: 1rem;
  margin-top: 2rem;
}

.btn-cancel,
.btn-pay {
  padding: 1rem;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  width: 80%;
}

.btn-cancel {
  background: #f0f0f0;
  color: #666666a4;
  width: 20%;
}

.btn-pay {
  background: #1a73e8;
  color: white;
}

.btn-pay:hover {
  background: #1557b0;
}

/* Responsive Design */
@media (max-width: 992px) {
  .payment-content {
    flex-direction: column;
  }
  
  .left-column,
  .right-column {
    width: 100%;
  }
  
  .right-column {
    position: static;
  }
}

@media (max-width: 600px) {
  .order-detail {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }
  
  .order-meta {
    flex-direction: column;
    gap: 0.25rem;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .payment-options {
    flex-wrap: wrap;
  }
}
</style>
