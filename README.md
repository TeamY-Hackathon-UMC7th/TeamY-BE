<div align=center>

# ☕ LastCoffee_Backend ☕

## 👶🏼 Backend Members 👶🏼
<img width="160px" src="https://avatars.githubusercontent.com/u/105594739?v=4"/> | <img width="160px" src="https://avatars.githubusercontent.com/u/106726806?v=4"/> | <img width="160px" src="https://avatars.githubusercontent.com/u/147061193?v=4"/> | 
|:-----:|:-----:|:-----:|
|[김지민](https://github.com/jimin-fundamental)|[정원희](https://github.com/oneeee822)|[이호준](https://github.com/lehojun)|
|팀장 👑|팀원 👨🏻‍💻|팀원 👨🏻‍💻|
</div>
<br/>

---

## 🌟 프로젝트 소개

**LastCoffee_Backend**는 사용자에게 카페인 섭취를 관리하고 음료 추천을 제공하는 기능을 제공합니다. 사용자 친화적인 인터페이스를 통해 기록 관리와 추천 시스템을 활용하여 카페인을 보다 효율적으로 섭취할 수 있도록 도와줍니다.

### **주요 기능**  

1. **회원가입 및 로그인**  
   - 닉네임 및 이메일 기반 회원가입 (닉네임 및 이메일 중복 확인 포함)  
   - Access Token 기반 인증 (JWT 사용)  
   - 로그인 및 로그아웃 기능 제공  
   - 비밀번호 변경 및 계정 탈퇴 지원  
   - 액세스 토큰 만료 시 Refresh Token을 이용한 자동 갱신  

2. **음료 추천 시스템**  
   - 사용자의 **목표 취침 시간**을 설정하면 최적의 음료 추천  
   - 카페인 농도를 고려하여 음료 정렬 및 랜덤 추천 기능  
   - **사용자의 음료 섭취 이력 기반 맞춤형 추천** 기능 추가  
   - **섭취 후 카페인 효과 지속 시간**을 고려한 추천 알고리즘 적용  
   - 최근 추천받은 음료 목록 저장 및 **추천 이력 조회 기능 제공**  

3. **인기 메뉴 제공**  
   - **사용자 데이터 분석 기반 인기 메뉴 5개 제공**  
   - **인기 순위 반영 로직 개선** → 최근 7일간 가장 많이 섭취된 음료 순으로 업데이트  
   - 브랜드별 인기 메뉴 필터링 기능 추가  
   - 상세 정보 제공:  
     - 📷 음료 사진  
     - 🏷 브랜드명 및 음료명  
     - ⚡ **카페인 함량**, 🍭 **당류**, 🔥 **칼로리**, 🥚 **단백질 함량** 등  

4. **기록 관리**  
   - **음료 섭취 기록을 최신순으로 관리** 및 검색 기능 제공  
   - **후기 작성 기능 추가** (최대 200자 제한)  
   - **마신 메뉴와 후기 기록 저장** → 개인화 추천 데이터로 활용  
   - 특정 기록 삭제 기능 지원  
   - **사용자의 음료 섭취 패턴 분석 기능 추가** (향후 과다 섭취 경고 시스템 연동 예정)  
   - **최근 5개의 섭취 기록을 빠르게 확인하는 기능 추가**  

5. **보안 및 HTTPS 적용**  
   - **모든 API 요청을 HTTPS를 통해 암호화하여 전송** (보안 강화)  
   - **AWS Route 53 및 SSL 인증서(ACM) 적용**을 통한 HTTPS 지원  
   - **로그인 및 비밀번호 변경 시 데이터 암호화 적용**  
   - **클라이언트-서버 간 데이터 암호화**를 통해 안전한 정보 전송  

6. **CI/CD 및 배포 자동화**  
   - **GitHub Actions를 활용한 CI/CD 파이프라인 구축**  
   - **테스트 자동화 및 빌드, 배포 자동화 적용**  
   - **AWS EC2 및 S3를 활용한 무중단 배포**  
   - **Docker & Nginx를 활용한 컨테이너 기반 배포 환경 구성**  
   - **배포 단계에서 보안 강화를 위한 환경 변수 관리 및 접근 제한 적용**  

---

## 🛠️ 기술 스택

- **Backend**: Spring Boot(Java)
- **Database**: RDS(MySQL), Redis
- **Cloud**: AWS (EC2, VPC)
- **API Documentation**: Swagger, Notion
- **Version Control**: GitHub

---

## 🖥️ 프로젝트 구조

### ERD
<img width="802" alt="image" src="https://github.com/user-attachments/assets/89ba9a2f-a3ee-4304-9b22-9158336a94f5" />


### 인프라 구성도
<img width="1201" alt="image" src="https://github.com/user-attachments/assets/b0b44c37-343c-437b-8fb0-0db3fe3f3989" />

---

## 📄 주요 기능 설명

### 카페인 섭취 시간 계산

우리 프로젝트는 **카페인 농도의 변화 공식**을 기반으로 합니다. 이는 연구 논문과 임상 데이터를 참고하여 정확도를 높였습니다.

#### 연구 자료
1. **Bennett WA, 1990: Clinical Pharmacology of caffeine**
   - 카페인이 체내에서 반감기(half-life)를 기준으로 감소한다는 사실을 기반으로 계산.

2. **Rogers PJ, 2007: Caffeine: Pharmacology and effects on performance and mood**
   - 카페인 섭취 후 체내에서의 반감기 데이터를 사용하여 추천 음료의 적합성을 계산.

#### 공식

```java
public static double calculateTime(double C0, double T, double target) {
    if (C0 <= 0 || target <= 0 || T <= 0) {
        throw new IllegalArgumentException("C0, T, target 값은 0보다 커야 합니다.");
    }
    return T * (Math.log(target / C0) / Math.log(0.5));
}
```
- 초기 농도(C0), 반감기(T), 목표 농도(target)을 입력받아 카페인 농도가 목표치 이하가 되는 시간을 계산합니다.

#### 예시 데이터
| 카페인 농도 | 시간 |
|-------------|------|
| 200 mg      | 8 시간 |
| 150 mg      | 6.4 시간 |
| 120 mg      | 5.04 시간 |
| 100 mg      | 4 시간 |
| 0 mg        | 0 시간 |

---

### 음료 추천 알고리즘

- 사용자가 설정한 목표 취침 시간(t)에 따라 카페인 함량에 기반하여 음료를 추천합니다.
- 적합한 음료가 여러 개일 경우, 랜덤으로 5개를 선택하여 표시합니다.

#### 카페인 변화 공식

```java
C(t) = C₀ × (1/2)^(t/T)
```
- `C₀`: 초기 카페인 농도 (mg)
- `T`: 카페인의 반감기 (시간)
- `t`: 경과 시간 (시간)
---

## 🌟 프로젝트 배경 및 아이디어

### 프로젝트 기본 아이디어

1. **문제**: 카페인 섭취량 관리와 음료 선택은 많은 사용자들에게 번거롭고 어렵습니다.
2. **목표**: 사용자들이 간편하게 목표 취침 시간에 맞는 음료를 선택하고, 카페인 농도를 예측할 수 있도록 합니다.
3. **해결책**: 과학적인 데이터를 바탕으로 카페인 농도 변화와 음료 정보를 결합하여 사용자 친화적인 서비스를 제공합니다.

---

## 📊 데이터 기준

| 카페인 (mg) | 200 | 150 | 120 | 100 |
|-------------|-----|-----|-----|-----|
| 시간 (시간) | 8   | 6.5 | 5.0 | 4.0 |

---

## 🌐 API 명세서
<img width="992" alt="image" src="https://github.com/user-attachments/assets/e909349d-5ca7-42fa-83cb-4ab861a0d4bd" />



