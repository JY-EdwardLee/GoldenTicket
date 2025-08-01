/**
 * 날짜를 'YYYY-MM-DD HH시 mm분' 형식으로 포맷합니다.
 * @param {string} dateString - 변환할 날짜 문자열
 * @returns {string} 포맷된 날짜 문자열
 */
export const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}시 ${String(date.getMinutes()).padStart(2, '0')}분`
}

/**
 * 날짜를 'YYYY-MM-DD' 형식으로 포맷합니다.
 * @param {string} dateString - 변환할 날짜 문자열
 * @returns {string} 포맷된 날짜 문자열 (YYYY-MM-DD)
 */
export const formatDateOnly = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

/**
 * 날짜를 'HH:mm' 형식으로 포맷합니다.
 * @param {string} dateString - 변환할 날짜 문자열
 * @returns {string} 포맷된 시간 문자열 (HH:mm)
 */
export const formatTimeOnly = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

/**
 * 날짜에 분을 더한 후 'YYYY-MM-DD HH시 mm분' 형식으로 포맷합니다.
 * @param {string} dateString - 기준 날짜 문자열
 * @param {number} minutes - 더할 분 (기본값: 10분)
 * @returns {string} 포맷된 날짜 문자열
 */
export const formatDateWithAddedMinutes = (dateString, minutes = 10) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  date.setMinutes(date.getMinutes() + minutes)
  return formatDate(date)
}

// 이전 함수들과의 호환성을 위한 별칭
export const formatCancelDate = formatDateWithAddedMinutes
