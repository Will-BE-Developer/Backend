[![Willbe](https://user-images.githubusercontent.com/60756023/170631952-4dbb8e21-0636-4980-a9be-67724597bc1d.png)](https://willbedeveloper.com)

## Why Will-Be ?

### 요즘 화상 면접 안보는 곳 있나요? WillBe와 함께 준비해볼까요 👨‍💻

<br />
면접을 연습한 적은 있지만, 본인의 모습을 직접 녹화해 보신 적 있으신가요?<br>
긴장하면 나오는 목소리 톤🗣, 어울리지 않는 제스처 등 소리내어 연습하는 것만으로 알 수 있을까요?<br>
윌비와 함께 내가 모르는 나의 면접 습관을 알고 강점은 더 강하게 약점은 기록하며 보완해보세요💪💪

<br/>

### 📆 프로젝트 기간

- 2022.04.29 ~ 2022.06.03

<br/>

###  🐾 Project Members 

<table>
   <tr>
    <td align="center"><b><a href="https://github.com/llama-ste">🦙 안동현</a></b></td>
    <td align="center"><b><a href="https://github.com/AlgoRoots">🐰 박성혜</a></b></td>
    <td align="center"><b><a href="https://github.com/limjae">🐘 임재현</a></b></td>
    <td align="center"><b><a href="https://github.com/catalinakim">🐩 김경현</a></b></td>
    <td align="center"><b><a href="https://github.com/Juri-Lee">🐬 이주리</a></b></td>
  </tr>
  <tr>
     <td align="center"><a href="https://github.com/llama-ste"><img src="https://user-images.githubusercontent.com/90495580/169259372-a923afea-a898-4bca-9504-7d073d6ffab8.jpeg" width="100px" /></a></td>
    <td align="center"><a href="https://github.com/AlgoRoots"><img src="https://user-images.githubusercontent.com/90495580/169259379-a913dd30-fa7f-4309-af30-9bd94c9608a6.png" width="100px" /></a></td>
    <td align="center"><a href="https://github.com/limjae"><img src="https://user-images.githubusercontent.com/90495580/169259387-0e3b59ad-5882-458a-9a2b-2ccac2d696ae.png" width="100px" /></a></td>
    <td align="center"><a href="https://github.com/catalinakim"><img src="https://user-images.githubusercontent.com/90495580/170095150-bcdacb00-ac2a-42eb-98b5-c67e05352832.jpeg" width="100px" /></a></td>
    <td align="center"><a href="https://github.com/Juri-Lee"><img src="https://user-images.githubusercontent.com/90495580/169259405-ba67e49d-8b01-405f-b0c8-12c6054b7577.png" width="100px" /></a></td>
  </tr>
  <tr>
    <td align="center"><b>🍄 React</b></td>
    <td align="center"><b>🍄 React</b></td>
    <td align="center"><b>🌱 Spring</b></td>
    <td align="center"><b>🌱 Spring</b></td>
    <td align="center"><b>🌱 Spring</b></td>
  </tr>
</table>

<br/>
<br/>


## Service Architecture

![WillBe-service_architecture](https://user-images.githubusercontent.com/88864019/170158157-eb5066ef-93dc-42a4-9407-4cfac15d4b76.jpg)

<br/>
<br/>

## 🛠 Tools

#### Backend

<p>
  <img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
   <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
   <img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
</p>

#### Infrastructure

<p>
  <img src="https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white" > 
   <img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
</p>

#### Dev tools

<p> 
  <img src="https://img.shields.io/badge/git-%23F05033.svg?style=for-the-badge&logo=git&logoColor=white">
  <img src="https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white">
</p>

<br>
<br>

## Backend 주요 기술 및 Flow chart✨ 

#### 🔐 로그인 : JWT 토큰 방식, Spring security, Java Mail sender

- 카카오를 통한 소셜로그인으로 간단하게 가입할 수 있어요. 이메일 회원가입에서는 유효한 이메일만 가입할 수 있도록 중복확인과 더불어 이메일 인증 링크 방식을 도입했어요. 또한 JWT 토큰 인증 방식을 통해 다중서버에서도 유저를 재인증할 필요가 없어요. 

![detail structure](https://user-images.githubusercontent.com/60756023/170812464-34141bc2-0e5e-4aea-a1eb-3cb34742de98.jpg)

#### 📹 📀 동영상 저장,변환 및 업로드 : S3,Lambda, Elastic Load balancer

- 동영상 촬영후 업로드 버튼을 누르면 기다릴 필요없이 다른 서비스 이용이 가능해요. 유저가 기다리는 시간을 최소화하고 동영상 처리시간을 빠르게 할 수 있도록, 여러대의 서버를 loadbalancer를 통해 관리하고 있어요.
![detail structure (1)](https://user-images.githubusercontent.com/60756023/170812477-01999331-e860-4baa-b69f-8e2a516f528a.jpg)

#### 👑 주간 면접왕, 오늘의 질문, 핫한 카테고리 관리 : spring batch

- spring batch를 사용해, 효율적으로 필요한 서비스 데이터를 산출하고 있어요. 데이터들은 히스토리처럼 쌓여 서비스 통계📊에 사용할수 있답니다!


<br/>
<br/>

## ☁️ ERD 

![Will be](https://user-images.githubusercontent.com/60756023/170617716-62ecda77-34cd-4465-948a-18ed707fd0e5.png)

## 🔥 Trouble Shooting

#### Issue 1
### 동영상, 썸네일 변환시 긴 대기시간

1-1. 클라이언트에서 게시글 요청작성 POST /api/interviews 요청이 오고 다시 반환 될 때 까지 너무 긴 시간이 소요되는 문제가 발생 & <br>
동영상이 처리 중일 때 다른 사용자들의 요청이 지연되는 현상이 발생했습니다.<br>
<br>
cause : <br>
- 영상의 확장자를 변환하는 과정이 동기로 처리되고 있어 생기 문제
- 사용중인 EC2 t2.micro의 사양의 한계 

##### solution :
- step 1: 동영상 변환 과정을 @async 어노테이션을 통해 비동기로 처리하여 변환 요청이 올 때 변환 과정을 기다리는 과정 생략
- step 2: 동영상 처리 서버와 API서버를 분리 <br>
<br>
1-2. 썸네일 이미지의 크기가 일정하게 맞추기위해 crop 하는 과정에서 대기시간 발생<br>
cause: <br>
- lambda 사용시 생기는 속도 문제 

#### solution : 
- 썸네일 이미지가 크롭되는 동안 유저 화면에는 로딩이미지를 보여줌

#### Issue 2
### 다중 서버에서 유저 인식 불가
사용자가 비디오를 업로드를 할때 서버가 사용자를 인식하지 못해 업로드 실패 문제 발생 <br>

#### solution :
- 


