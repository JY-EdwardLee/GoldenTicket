// 한글 팀명과 API용 영문 ENUM 매핑
export const teamNameToEnum = {
  'KIA타이거즈': 'KIA_TIGERS',
  '삼성라이온즈': 'SAMSUNG_LIONS',
  'LG트윈스': 'LG_TWINS',
  '두산베어스': 'DOOSAN_BEARS',
  'KT위즈': 'KT_WIZ',
  'SSG랜더스': 'SSG_LANDERS',
  '롯데자이언츠': 'LOTTE_GIANTS',
  '한화이글스': 'HANHWA_EAGLES',
  'NC다이노스': 'NC_DINOS',
  '키움히어로즈': 'KIWOOM_HEROES',
};

export function getEnumTeamName(korName) {
  return teamNameToEnum[korName] || korName;
}
