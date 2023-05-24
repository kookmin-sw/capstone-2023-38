import React, { useCallback, useEffect, useRef } from "react";
import styled, { keyframes } from "styled-components";
import mainImg from "../src/img/exampleImg/main.jpeg";
import ImageSlider from "./components/imgSlider";
import exampleImg1 from "../src/img/exampleImg/codymap1.jpeg";
import exampleImg2 from "../src/img/exampleImg/codymap2.jpeg";
import exampleImg3 from "../src/img/exampleImg/codymap3.jpeg";
import GoogleLoginButton from "./google";
import Button from "./components/button";

const FadeIn = keyframes`
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0px);
  }
`;
const MainWrapper = styled.div`
  display: flex;
  flex-direction: column;
`;

const FirstContent = styled.div`
  width: 100%;
  height: 680px;
  background-image: url(${mainImg});
  background-repeat: no-repeat;
  background-size: 100% 100%;
  animation: ${FadeIn} 1s ease-out forwards;

  .titlebox {
    margin-top: 3rem;
    margin-left: 2rem;
    font-family: "NanumSquareRound";
    font-style: normal;
    color: #ffffff;
  }

  .titlebox > .subtitle {
    font-size: 32px;
  }
  .titlebox > .aco {
    font-size: 128px;
  }
`;

const SecondContent = styled.div`
  margin-top: 3rem;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;

  .titlebox {
    font-family: "NanumSquareRound";
    font-style: normal;
    font-size: 32px;
    text-align: center;
  }
`;

const Googlebtn = styled.div`
  margin: 3rem;
  display: flex;
  flex-direction: column;
  align-items: center;

  span {
    font-size: 32px;
    margin: 1rem;
  }
`;

function MainPage() {
  const handleLogin = () => {
    window.location.href = "http://localhost:3000/myCloset"; // 네이버 URL
  };

  const exampleImg = [
    { id: 1, imgsrc: exampleImg1 },
    { id: 2, imgsrc: exampleImg2 },
    { id: 3, imgsrc: exampleImg3 },
  ];
  return (
    <MainWrapper>
      <FirstContent>
        <div className="titlebox">
          <span className="subtitle">나만의 작은 코디네이터</span>
          <br></br>
          <span className="aco">에코</span>
        </div>
      </FirstContent>
      <SecondContent>
        <div className="titlebox">
          <span>인공지능 기반 코디 추천 서비스</span>
          <br></br>
          <span>
            바쁜 하루, <b className="myStyle">'나만의 스타일'</b> 을 찾아보세요
          </span>
        </div>
        <ImageSlider images={exampleImg}></ImageSlider>
      </SecondContent>
      <Googlebtn>
        {/* <Button fontcolor={"skyblue"} onClick={handleLogin}>
          에코 시작하기
        </Button> */}
        <span>에코 시작하기</span>
        <GoogleLoginButton></GoogleLoginButton>
      </Googlebtn>
    </MainWrapper>
  );
}

export default MainPage;
