import React, { useCallback } from "react";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";
import logoImg from "../img/logo.png";

const Navigationbar = styled.div`
  max-width: 100%;
  margin: 0 auto;
  display: flex;
  align-items: flex-end;
  border-bottom: 1px solid black;

  .logo {
    max-width: 250px;
    transition: max-width 0.3s ease-in-out;

    @media screen and (min-width: 915px) {
      &:hover {
        max-width: 270px;
        border-bottom: 1px solid black;
        padding-bottom: 2px;
      }
    }

    /* 모바일 화면에 대한 스타일 */
    @media screen and (max-width: 768px) {
      &:hover {
        border-bottom: 2px solid black;
        padding-bottom: 2px;
      }

      &:active {
        max-width: 150px;
        border-bottom: 1px solid black;
        padding-bottom: 2px;
      }
    }
  }

  .menu {
    margin: 2rem auto 2rem auto;
    color: black;
    cursor: pointer;
    transition: font-size 0.3s ease-in-out;
    font-size: 1.3rem;
    font-weight: bold;
    font-family: "NanumSquareRound";
    font-style: normal;

    @media screen and (min-width: 915px) {
      &:hover {
        font-size: 1.6rem;
        border-bottom: 1px solid black;
        padding-bottom: 2px;
      }
    }

    /* 모바일 화면에 대한 스타일 */
    @media screen and (max-width: 768px) {
      &:hover {
        border-bottom: 2px solid black;
        padding-bottom: 2px;
      }

      &:active {
        font-size: 0.5rem;
        border-bottom: 1px solid black;
        padding-bottom: 2px;
      }
    }
  }

  /* 모바일 화면에 대한 전체 스타일 */
  @media screen and (max-width: 768px) {
    display: flex;
    align-items: center;
    .logo {
      max-width: 130px;
    }
    .menu {
      margin: 0rem 0.5rem 0 0.5rem;
      font-size: 0.6rem;
      padding-bottom: 5px;
      font-family: "NanumSquareRound";
      font-style: normal;
    }
  }
`;

function Header() {
  const navigate = useNavigate();

  const moveMainPage = useCallback(() => {
    navigate("/main");
  }, [navigate]);

  const moveImgUpload = useCallback(() => {
    navigate("/imgUpload");
  }, [navigate]);

  const moveMyCloset = useCallback(() => {
    navigate("/myCloset");
  }, [navigate]);

  const moveMyWishList = useCallback(() => {
    navigate("/myWishList");
  }, [navigate]);

  const moveFeedList = useCallback(() => {
    navigate("/FeedList");
  }, [navigate]);

  return (
    <Navigationbar>
      <img src={logoImg} className="logo" onClick={moveMainPage} />
      <div className="menu" onClick={moveImgUpload}>
        코디 추천
      </div>
      <span className="menu" onClick={moveMyCloset}>
        내 옷장
      </span>
      <span className="menu" onClick={moveMyWishList}>
        나의 위시리스트
      </span>
      <span className="menu" onClick={moveFeedList}>
        스타일
      </span>
    </Navigationbar>
  );
}

export default Header;
